package thaumcraft.common.tiles.crafting;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.blocks.ThaumcraftBlocks;
import thaumcraft.api.crafting.IInfusionStabiliser;
import thaumcraft.api.crafting.IInfusionStabiliserExt;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.common.blocks.crafting.BlockPedestal;
import thaumcraft.common.blocks.entities.ThaumcraftBlockEntities;

/**
 * Block entity for the Infusion Matrix — orchestrates the infusion altar crafting cycle.
 *
 * <h3>Server Tick Lifecycle</h3>
 * <ol>
 *   <li>Scan surroundings: central pedestal at {@code pos.below(2)}, surrounding
 *       pedestals within an 8-block horizontal radius.</li>
 *   <li>Symmetry calculation: balance between opposing stabiliser positions.</li>
 *   <li>Crafting lifecycle: match {@link InfusionRecipe}, drain essentia,
 *       absorb pedestal items, handle instability risk, complete output on
 *       central pedestal.</li>
 * </ol>
 *
 * <p>MC 26.1.2 / NeoForge 26.2 port of
 * {@code thaumcraft.common.tiles.crafting.TileInfusionMatrix}.</p>
 */
public class InfusionMatrixBlockEntity extends BlockEntity implements IAspectContainer {

    // -------------------------------------------------------------------------
    // Stability classification
    // -------------------------------------------------------------------------

    private enum EnumStability {
        VERY_STABLE, STABLE, UNSTABLE, VERY_UNSTABLE
    }

    // -------------------------------------------------------------------------
    // State fields
    // -------------------------------------------------------------------------

    private ArrayList<BlockPos> pedestals = new ArrayList<>();
    public boolean active = false;
    public boolean crafting = false;
    public boolean checkSurroundings = true;

    public float costMult = 0.0f;
    private int cycleTime = 20;
    public int stabilityCap = 25;
    public float stability = 0.0f;
    public float stabilityReplenish = 0.0f;

    private AspectList recipeEssentia = new AspectList();
    private ArrayList<ItemStack> recipeIngredients = null;
    private ItemStack recipeOutput = null;
    private ItemStack recipeInput = null;
    private String recipePlayer = null;
    private int recipeInstability = 0;

    public int count = 0;
    private int countDelay = cycleTime / 2;
    int itemCount = 0;

    private ArrayList<BlockPos> problemBlocks = new ArrayList<>();
    HashMap<Block, Integer> tempBlockCount = new HashMap<>();
    static final DecimalFormat myFormatter = new DecimalFormat("#######.##");

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public InfusionMatrixBlockEntity(BlockPos pos, BlockState state) {
        super(ThaumcraftBlockEntities.INFUSION_MATRIX.get(), pos, state);
    }

    // -------------------------------------------------------------------------
    // Server tick
    // -------------------------------------------------------------------------

    public static void tick(Level level, BlockPos pos, BlockState state,
                            InfusionMatrixBlockEntity tile) {
        if (level.isClientSide()) return;
        tile.serverTick(level, pos);
    }

    private void serverTick(Level level, BlockPos pos) {
        ++count;
        if (checkSurroundings) {
            checkSurroundings = false;
            getSurroundings(level, pos);
        }
        // Periodic location validation
        if (count % (crafting ? 20 : 100) == 0 && !validLocation(level, pos)) {
            active = false;
            setChanged();
            return;
        }
        // Stability regeneration when idle
        if (active && !crafting && stability < stabilityCap && count % Math.max(5, countDelay) == 0) {
            stability += Math.max(0.1f, stabilityReplenish);
            if (stability > stabilityCap) stability = stabilityCap;
            setChanged();
        }
        // Crafting cycle
        if (active && crafting && count % countDelay == 0) {
            craftCycle(level, pos);
            setChanged();
        }
    }

    // -------------------------------------------------------------------------
    // Location validation
    // -------------------------------------------------------------------------

    /**
     * Checks that the multiblock structure is intact: central pedestal at y-2
     * and four pillars at the diagonal corners of y-2.
     */
    public boolean validLocation(Level level, BlockPos pos) {
        boolean hasCentral = level.getBlockState(pos.offset(0, -2, 0)).getBlock() instanceof BlockPedestal;
        boolean nw = isPillar(level, pos.offset(-1, -2, -1));
        boolean ne = isPillar(level, pos.offset(1, -2, -1));
        boolean sw = isPillar(level, pos.offset(-1, -2, 1));
        boolean se = isPillar(level, pos.offset(1, -2, 1));
        return hasCentral && nw && ne && sw && se;
    }

    private boolean isPillar(Level level, BlockPos bp) {
        Block b = level.getBlockState(bp).getBlock();
        return b == ThaumcraftBlocks.pillarArcane.get()
                || b == ThaumcraftBlocks.pillarAncient.get()
                || b == ThaumcraftBlocks.pillarEldritch.get();
    }

    // -------------------------------------------------------------------------
    // Crafting start — activated by right-click
    // -------------------------------------------------------------------------

    public void craftingStart(Player player) {
        Level level = getLevel();
        BlockPos pos = getBlockPos();
        if (level == null) return;

        if (!validLocation(level, pos)) {
            active = false;
            setChanged();
            return;
        }

        getSurroundings(level, pos);

        // Read central pedestal item
        recipeInput = ItemStack.EMPTY;
        BlockEntity centralTE = level.getBlockEntity(pos.below(2));
        if (centralTE instanceof PedestalBlockEntity centralPed && centralPed.hasItem()) {
            recipeInput = centralPed.getItem().copy();
        }
        if (recipeInput.isEmpty()) return;

        // Gather component items from surrounding pedestals
        ArrayList<ItemStack> components = new ArrayList<>();
        for (BlockPos cc : pedestals) {
            BlockEntity te = level.getBlockEntity(cc);
            if (te instanceof PedestalBlockEntity ped && ped.hasItem()) {
                components.add(ped.getItem().copy());
            }
        }
        if (components.isEmpty()) return;

        // Try to find a matching recipe
        InfusionRecipe recipe = findMatchingInfusionRecipe(components, recipeInput, player, level);
        if (costMult < 0.5f) costMult = 0.5f;

        if (recipe != null) {
            recipeIngredients = components;
            Object rawOutput = recipe.getRecipeOutput(player, recipeInput, components);
            if (rawOutput instanceof ItemStack is) {
                recipeOutput = is.copy();
            }
            recipeInstability = recipe.getInstability(player, recipeInput, components);

            // Calculate scaled essentia cost
            AspectList al = recipe.getAspects(player, recipeInput, components);
            AspectList al2 = new AspectList();
            for (Aspect as : al.getAspects()) {
                int amt = (int) (al.getAmount(as) * costMult);
                if (amt > 0) al2.add(as, amt);
            }
            recipeEssentia = al2;
            recipePlayer = player.getName().getString();
            crafting = true;
            level.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE,
                    SoundSource.BLOCKS, 0.5f, 1.0f);
            setChanged();
        }
    }

    @SuppressWarnings("unchecked")
    private InfusionRecipe findMatchingInfusionRecipe(
            List<ItemStack> components, ItemStack central, Player player, Level level) {
        for (Object r : ThaumcraftApi.getCraftingRecipes().values()) {
            if (r instanceof InfusionRecipe recipe) {
                if (recipe.matches(components, central, level, player)) {
                    return recipe;
                }
            }
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // Stability helpers
    // -------------------------------------------------------------------------

    private EnumStability getStability() {
        if (stability > stabilityCap / 2f) return EnumStability.VERY_STABLE;
        if (stability >= 0.0f) return EnumStability.STABLE;
        if (stability > -25.0f) return EnumStability.UNSTABLE;
        return EnumStability.VERY_UNSTABLE;
    }

    private float getModFromCurrentStability() {
        return switch (getStability()) {
            case VERY_STABLE -> 5.0f;
            case STABLE -> 6.0f;
            case UNSTABLE -> 7.0f;
            case VERY_UNSTABLE -> 8.0f;
        };
    }

    private float getLossPerCycle() {
        return recipeInstability / getModFromCurrentStability();
    }

    // -------------------------------------------------------------------------
    // Craft cycle
    // -------------------------------------------------------------------------

    private void craftCycle(Level level, BlockPos pos) {
        // Apply instability-based stability loss
        float ff = level.getRandom().nextFloat() * getLossPerCycle();
        stability -= ff;
        stability += stabilityReplenish;
        stability = Math.max(-100.0f, Math.min(stabilityCap, stability));

        // Validate central pedestal still has original input
        boolean valid = false;
        BlockEntity centralTE = level.getBlockEntity(pos.below(2));
        if (centralTE instanceof PedestalBlockEntity centralPed && centralPed.hasItem()) {
            ItemStack i2 = centralPed.getItem().copy();
            if (ItemStack.isSameItemSameComponents(i2, recipeInput)) {
                valid = true;
            }
        }

        // Instability events
        if (!valid || (stability < 0.0f && level.getRandom().nextInt(1500) <= Math.abs(stability))) {
            handleInstabilityEvent(level, pos);
            stability += 5.0f + level.getRandom().nextFloat() * 5.0f;
            if (valid) return;
        }

        if (!valid) {
            // Crafting fails — input removed
            crafting = false;
            recipeEssentia = new AspectList();
            recipeInstability = 0;
            level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH,
                    SoundSource.BLOCKS, 1.0f, 0.6f);
            setChanged();
            return;
        }

        if (countDelay < 1) countDelay = 1;

        // Phase 1: Drain essentia
        if (recipeEssentia.visSize() > 0) {
            // Simplified: skip 1 essentia per cycle (full EssentiaHandler not yet ported)
            for (Aspect aspect : recipeEssentia.getAspects()) {
                int na = recipeEssentia.getAmount(aspect);
                if (na > 0) {
                    recipeEssentia.reduce(aspect, 1);
                    setChanged();
                    return;
                }
            }
            checkSurroundings = true;
            return;
        }

        // Phase 2: Absorb pedestal items
        if (recipeIngredients != null && !recipeIngredients.isEmpty()) {
            for (int a = 0; a < recipeIngredients.size(); ++a) {
                for (BlockPos cc : pedestals) {
                    BlockEntity te = level.getBlockEntity(cc);
                    if (te instanceof PedestalBlockEntity ped && ped.hasItem()
                            && ItemStack.isSameItemSameComponents(ped.getItem(), recipeIngredients.get(a))) {
                        if (itemCount == 0) {
                            itemCount = 5;
                        } else if (--itemCount <= 1) {
                            // Consume item from pedestal
                            ped.setItem(ItemStack.EMPTY);
                            recipeIngredients.remove(a);
                            setChanged();
                        }
                        return;
                    }
                }
            }
            return;
        }

        // Phase 3: Crafting complete — place output on central pedestal
        crafting = false;
        craftingFinish(level, pos);
        recipeOutput = null;
        setChanged();
    }

    // -------------------------------------------------------------------------
    // Instability events
    // -------------------------------------------------------------------------

    private void handleInstabilityEvent(Level level, BlockPos pos) {
        int event = level.getRandom().nextInt(6);
        switch (event) {
            case 0 -> ejectRandomPedestalItem(level);
            case 1 -> zapNearbyEntity(level, pos);
            case 2, 3 -> {} // placeholder for warp / harm
            case 4 -> ejectRandomPedestalItem(level);
            case 5 -> level.explode(null,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    1.5f + level.getRandom().nextFloat(),
                    Level.ExplosionInteraction.NONE);
        }
    }

    private void ejectRandomPedestalItem(Level level) {
        for (int retries = 0; retries < 25 && !pedestals.isEmpty(); ++retries) {
            BlockPos cc = pedestals.get(level.getRandom().nextInt(pedestals.size()));
            BlockEntity te = level.getBlockEntity(cc);
            if (te instanceof PedestalBlockEntity ped && ped.hasItem()) {
                net.minecraft.world.Containers.dropItemStack(level,
                        cc.getX() + 0.5, cc.getY() + 1.0, cc.getZ() + 0.5,
                        ped.getItem());
                ped.setItem(ItemStack.EMPTY);
                return;
            }
        }
    }

    private void zapNearbyEntity(Level level, BlockPos pos) {
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class,
                new AABB(pos).inflate(10.0));
        if (!targets.isEmpty()) {
            LivingEntity target = targets.get(level.getRandom().nextInt(targets.size()));
            target.hurt(level.damageSources().magic(), 4 + level.getRandom().nextInt(4));
        }
    }

    // -------------------------------------------------------------------------
    // Crafting finish
    // -------------------------------------------------------------------------

    private void craftingFinish(Level level, BlockPos pos) {
        BlockEntity centralTE = level.getBlockEntity(pos.below(2));
        if (centralTE instanceof PedestalBlockEntity centralPed) {
            if (recipeOutput != null && !recipeOutput.isEmpty()) {
                centralPed.setItemFromInfusion(recipeOutput.copy());
            }
            recipeEssentia = new AspectList();
            recipeInstability = 0;
            level.playSound(null, pos, SoundEvents.PLAYER_LEVELUP,
                    SoundSource.BLOCKS, 0.5f, 1.0f);
            setChanged();
        }
    }

    // -------------------------------------------------------------------------
    // Surroundings scan — symmetry & stabilisers
    // -------------------------------------------------------------------------

    private void getSurroundings(Level level, BlockPos pos) {
        Set<Long> stabPositions = new HashSet<>();
        pedestals.clear();
        tempBlockCount.clear();
        problemBlocks.clear();
        cycleTime = 10;
        stabilityReplenish = 0.0f;
        costMult = 1.0f;

        try {
            // Scan area: ±8 horizontal, -3..+7 vertical
            for (int xx = -8; xx <= 8; ++xx) {
                for (int zz = -8; zz <= 8; ++zz) {
                    for (int yy = -3; yy <= 7; ++yy) {
                        if (xx == 0 && zz == 0) continue;
                        BlockPos bp = new BlockPos(pos.getX() + xx, pos.getY() - yy, pos.getZ() + zz);
                        Block bi = level.getBlockState(bp).getBlock();

                        if (bi instanceof BlockPedestal) {
                            pedestals.add(bp);
                        }
                        if (bi instanceof IInfusionStabiliser stab
                                && stab.canStabaliseInfusion(level, bp)) {
                            stabPositions.add(bp.asLong());
                        }
                    }
                }
            }

            // Symmetry evaluation
            while (!stabPositions.isEmpty()) {
                Long[] posArray = stabPositions.toArray(new Long[0]);
                if (posArray[0] == null) break;
                long lp = posArray[0];
                BlockPos c1 = BlockPos.of(lp);

                // Mirror position across the matrix center
                int dx = pos.getX() - c1.getX();
                int dz = pos.getZ() - c1.getZ();
                BlockPos c2 = new BlockPos(pos.getX() + dx, c1.getY(), pos.getZ() + dz);

                Block sb1 = level.getBlockState(c1).getBlock();
                Block sb2 = level.getBlockState(c2).getBlock();

                float amt1 = 0.1f;
                float amt2 = 0.1f;
                if (sb1 instanceof IInfusionStabiliserExt ext1)
                    amt1 = ext1.getStabilizationAmount(level, c1);
                if (sb2 instanceof IInfusionStabiliserExt ext2)
                    amt2 = ext2.getStabilizationAmount(level, c2);

                if (sb1 == sb2 && amt1 == amt2) {
                    if (sb1 instanceof IInfusionStabiliserExt ext
                            && ext.hasSymmetryPenalty(level, c1, c2)) {
                        stabilityReplenish -= ext.getSymmetryPenalty(level, c1);
                        problemBlocks.add(c1);
                    } else {
                        stabilityReplenish += calcDiminishingReturns(sb1, amt1);
                    }
                } else {
                    stabilityReplenish -= Math.max(amt1, amt2);
                    problemBlocks.add(c1);
                }

                stabPositions.remove(c2.asLong());
                stabPositions.remove(lp);
            }

            // Pillar type bonuses
            Block pillarAncient = ThaumcraftBlocks.pillarAncient.get();
            Block pillarEldritch = ThaumcraftBlocks.pillarEldritch.get();
            if (isPillar(level, pos.offset(-1, -2, -1)) && isPillar(level, pos.offset(1, -2, -1))
                    && isPillar(level, pos.offset(1, -2, 1)) && isPillar(level, pos.offset(-1, -2, 1))) {
                if (allPillarsMatch(level, pos, pillarAncient)) {
                    --cycleTime;
                    costMult -= 0.1f;
                    stabilityReplenish -= 0.1f;
                }
                if (allPillarsMatch(level, pos, pillarEldritch)) {
                    cycleTime -= 3;
                    costMult += 0.05f;
                    stabilityReplenish += 0.2f;
                }
            }

            // Matrix speed/cost augments
            int[] xm = {-1, 1, 1, -1};
            int[] zm = {-1, -1, 1, 1};
            for (int a = 0; a < 4; ++a) {
                Block b = level.getBlockState(pos.offset(xm[a], -3, zm[a])).getBlock();
                if (b == ThaumcraftBlocks.matrixSpeed.get()) {
                    --cycleTime;
                    costMult += 0.01f;
                }
                if (b == ThaumcraftBlocks.matrixCost.get()) {
                    ++cycleTime;
                    costMult -= 0.02f;
                }
            }

            countDelay = Math.max(1, cycleTime / 2);

            // Pedestal type bonuses
            Block pedestalEldritch = ThaumcraftBlocks.pedestalEldritch.get();
            Block pedestalAncient = ThaumcraftBlocks.pedestalAncient.get();
            for (BlockPos cc : pedestals) {
                Block bb = level.getBlockState(cc).getBlock();
                if (bb == pedestalEldritch) costMult += 0.0025f;
                if (bb == pedestalAncient) costMult -= 0.01f;
            }
        } catch (Exception ignored) {}
    }

    private boolean allPillarsMatch(Level level, BlockPos matrixPos, Block pillarBlock) {
        return level.getBlockState(matrixPos.offset(-1, -2, -1)).getBlock() == pillarBlock
                && level.getBlockState(matrixPos.offset(1, -2, -1)).getBlock() == pillarBlock
                && level.getBlockState(matrixPos.offset(1, -2, 1)).getBlock() == pillarBlock
                && level.getBlockState(matrixPos.offset(-1, -2, 1)).getBlock() == pillarBlock;
    }

    private float calcDiminishingReturns(Block b, float base) {
        float bb = base;
        int c = tempBlockCount.getOrDefault(b, 0);
        if (c > 0) bb *= (float) Math.pow(0.75, c);
        tempBlockCount.put(b, c + 1);
        return bb;
    }

    // -------------------------------------------------------------------------
    // NBT — MC 26.1.2 ValueOutput / ValueInput
    // -------------------------------------------------------------------------

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("active", com.mojang.serialization.Codec.BOOL, active);
        output.store("crafting", com.mojang.serialization.Codec.BOOL, crafting);
        output.store("stability", com.mojang.serialization.Codec.FLOAT, stability);
        output.store("recipeinst", com.mojang.serialization.Codec.INT, recipeInstability);

        CompoundTag aspectTag = new CompoundTag();
        recipeEssentia.writeToNBT(aspectTag);
        output.store("Essentia", CompoundTag.CODEC, aspectTag);

        if (recipeIngredients != null && !recipeIngredients.isEmpty()) {
            ValueOutput.TypedOutputList<ItemStack> itemsOut = output.list("RecipeIn", ItemStack.CODEC);
            for (ItemStack stack : recipeIngredients) {
                if (!stack.isEmpty()) {
                    itemsOut.add(stack);
                }
            }
        }

        if (recipeOutput != null) {
            output.store("RecipeOut", ItemStack.OPTIONAL_CODEC, recipeOutput);
        }

        if (recipeInput != null) {
            output.store("RecipeInput", ItemStack.OPTIONAL_CODEC, recipeInput);
        }

        output.store("RecipePlayer", com.mojang.serialization.Codec.STRING,
                recipePlayer != null ? recipePlayer : "");
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        active = input.read("active", com.mojang.serialization.Codec.BOOL).orElse(false);
        crafting = input.read("crafting", com.mojang.serialization.Codec.BOOL).orElse(false);
        stability = input.read("stability", com.mojang.serialization.Codec.FLOAT).orElse(0.0f);
        recipeInstability = input.read("recipeinst", com.mojang.serialization.Codec.INT).orElse(0);

        input.read("Essentia", CompoundTag.CODEC).ifPresent(tag -> recipeEssentia.readFromNBT(tag));

        ValueInput.TypedInputList<ItemStack> loaded = input.listOrEmpty("RecipeIn", ItemStack.CODEC);
        if (!loaded.isEmpty()) {
            recipeIngredients = new ArrayList<>(loaded.stream().toList());
        }

        recipeOutput = input.read("RecipeOut", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        recipeInput = input.read("RecipeInput", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);

        String rp = input.read("RecipePlayer", com.mojang.serialization.Codec.STRING).orElse("");
        recipePlayer = rp.isEmpty() ? null : rp;
    }

    // -------------------------------------------------------------------------
    // IAspectContainer
    // -------------------------------------------------------------------------

    @Override
    public AspectList getAspects() { return recipeEssentia; }

    @Override
    public void setAspects(AspectList aspects) {}

    @Override
    public int addToContainer(Aspect tag, int amount) { return 0; }

    @Override
    public boolean takeFromContainer(Aspect tag, int amount) { return false; }

    @Override
    public boolean takeFromContainer(AspectList ot) { return false; }

    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amount) { return false; }

    @Override
    public boolean doesContainerContain(AspectList ot) { return false; }

    @Override
    public int containerContains(Aspect tag) { return 0; }

    @Override
    public boolean doesContainerAccept(Aspect tag) { return true; }

    // -------------------------------------------------------------------------
    // Accessors for testing
    // -------------------------------------------------------------------------

    /** @return the discovered pedestal positions (read-only view). */
    public List<BlockPos> getPedestals() { return pedestals; }

    /** @return the current stability replenish rate. */
    public float getStabilityReplenish() { return stabilityReplenish; }

    /** @return the computed cost multiplier. */
    public float getCostMult() { return costMult; }
}
