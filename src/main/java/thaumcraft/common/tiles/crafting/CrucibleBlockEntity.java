package thaumcraft.common.tiles.crafting;

import java.util.Comparator;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.nbt.NbtOps;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.common.tiles.crafting.logic.CrucibleBoilingLogic;
import net.minecraft.core.registries.BuiltInRegistries;
import thaumcraft.common.blocks.entities.ThaumcraftBlockEntities;

/**
 * Block entity for the Thaumcraft Crucible.
 *
 * <h3>Lifecycle</h3>
 * <ol>
 *   <li>Player fills with a water bucket → {@code water = 1000}.</li>
 *   <li>Fire / lava / magma below → heat rises up to 200.  Heat ≥ 151 = boiling.</li>
 *   <li>Items dropped in (or right-clicked):
 *       <ul>
 *         <li>If a {@link CrucibleRecipe} matches the aspects + catalyst → craft result ejected.</li>
 *         <li>Otherwise → item's aspects dissolved into the aspect pool.</li>
 *       </ul>
 *   </li>
 *   <li>If aspects exceed {@link #MAX_ASPECTS} or 400 ticks pass without crafting while boiling → one
 *       random aspect is purged to the aura as flux.</li>
 * </ol>
 *
 * <p>MC 26.1.2 / NeoForge 26.1.x port of
 * {@code thaumcraft.common.tiles.crafting.TileCrucible}.
 */
public class CrucibleBlockEntity extends BlockEntity implements IAspectContainer {

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    /** Maximum water capacity in mB. */
    public static final int TANK_CAPACITY = 1000;

    /** Maximum total vis (aspect points) before overflow spill begins. */
    public static final int MAX_ASPECTS = 500;

    /** Water drained per successful crafting operation (mB). */
    private static final int WATER_PER_CRAFT = 50;

    // -------------------------------------------------------------------------
    // State fields
    // -------------------------------------------------------------------------

    /** Current heat level (0–200). Boiling threshold = 151. */
    private short heat = 0;

    /** Water stored in mB (0–1000). */
    private int water = 0;

    /** Aspect (vis) pool stored in the crucible. */
    private AspectList aspects = new AspectList();

    /**
     * Ticks since last craft. Used for decay logic,
     * one random aspect is spilled to the aura.
     */
    private long ticksWithoutCrafting = 0L;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public CrucibleBlockEntity(BlockPos pos, BlockState state) {
        super(ThaumcraftBlockEntities.CRUCIBLE.get(), pos, state);
    }

    // -------------------------------------------------------------------------
    // Server tick
    // -------------------------------------------------------------------------

    /**
     * Static tick method called by {@code BlockCrucible.getTicker()} every game tick
     * on the server side.
     */
    public static void tick(Level level, BlockPos pos, BlockState state,
                            CrucibleBlockEntity tile) {
        tile.serverTick(level, pos);
    }

    private void serverTick(Level level, BlockPos pos) {
        // --- Heat logic ---
        short prevHeat = heat;
        BlockState below = level.getBlockState(pos.below());

        boolean hasHeatSource = isHeatSource(below);
        heat = CrucibleBoilingLogic.calculateHeat(heat, water > 0, hasHeatSource);

        if (CrucibleBoilingLogic.didBoilingStateChange(prevHeat, heat) || (prevHeat != heat && (heat == 149 || heat == 151))) {
            setChanged();
        }

        // --- Overflow & timed spill ---
        if (aspects.visSize() > MAX_ASPECTS) {
            spillRandom(level, pos);
        }

        if (heat > 150 && aspects.visSize() > 0) {
            ticksWithoutCrafting++;
            if (CrucibleDecayLogic.shouldDecayThisTick(ticksWithoutCrafting)) {
                spillRandom(level, pos);
            }
        } else {
            ticksWithoutCrafting = 0L;
        }
    }

    /**
     * Returns {@code true} if the given block state qualifies as a heat source
     * for the crucible (fire, lava, campfire, soul_campfire, magma_block).
     */
    private static boolean isHeatSource(BlockState state) {
        String blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
        return CrucibleBoilingLogic.isHeatSource(blockId);
    }

    // -------------------------------------------------------------------------
    // Alchemy / Smelting
    // -------------------------------------------------------------------------

    /**
     * Called when an {@link ItemEntity} collides with the crucible block.
     * Delegates to {@link #attemptSmelt(ItemStack, Player)} then updates or
     * removes the entity.
     */
    public void attemptSmelt(ItemEntity entity) {
        ItemStack item = entity.getItem();
        // Try to determine the thrower — best effort, no player lookup in BE context
        ItemStack remainder = attemptSmelt(item.copy(), null);

        if (remainder == null || remainder.isEmpty()) {
            entity.discard();
        } else {
            entity.setItem(remainder);
        }
    }

    /**
     * Core alchemy logic.  Processes up to {@code item.getCount()} units.
     *
     * <ol>
     *   <li>If a {@link CrucibleRecipe} matches: craft, eject output, drain 50 mB water.</li>
     *   <li>Otherwise: dissolve item's aspect tags into the pool.</li>
     * </ol>
     *
     * @param item   The catalyst / input item (may be shrunk in-place).
     * @param player The player performing the action (may be {@code null} for dropped items).
     * @return The remainder stack after processing, or {@code null} if fully consumed.
     */
    @Nullable
    public ItemStack attemptSmelt(ItemStack item, @Nullable Player player) {
        if (level == null) return item;
        boolean crafted = false;
        boolean dissolved = false;
        int stackSize = item.getCount();

        for (int i = 0; i < stackSize; i++) {
            ItemStack single = item.copyWithCount(1);
            CrucibleRecipe recipe = findMatchingRecipe(single, player);

            if (recipe != null && water > 0) {
                // Craft
                ItemStack output = recipe.getRecipeOutput().copy();
                aspects = recipe.removeMatching(aspects);
                water = Math.max(0, water - WATER_PER_CRAFT);
                ejectItem(output);
                ticksWithoutCrafting = 0L;
                crafted = true;
                item.shrink(1);
            } else {
                // Dissolve
                AspectList objectAspects = AspectHelper.getObjectAspects(single);

                java.util.Map<String, Integer> inputMap = new java.util.LinkedHashMap<>();
                if (objectAspects != null) {
                    for (Aspect tag : objectAspects.getAspects()) {
                        if (tag != null) {
                            inputMap.put(tag.getTag(), objectAspects.getAmount(tag));
                        }
                    }
                }

                java.util.Map<String, Integer> dissolvedMap = CrucibleDecompositionLogic.calculateDecomposition(inputMap);

                if (!dissolvedMap.isEmpty()) {
                    for (java.util.Map.Entry<String, Integer> entry : dissolvedMap.entrySet()) {
                        Aspect tag = Aspect.getAspect(entry.getKey());
                        if (tag != null) {
                            aspects.add(tag, entry.getValue());
                        }
                    }
                    dissolved = true;
                    ticksWithoutCrafting = 0L;
                    item.shrink(1);
                }
            }

            if (item.isEmpty()) break;
        }

        if (dissolved || crafted) {
            if (level != null) {
                level.playSound(null, worldPosition,
                        crafted ? SoundEvents.EXPERIENCE_ORB_PICKUP : SoundEvents.BUCKET_FILL,
                        SoundSource.BLOCKS,
                        0.2f, 1.0f + level.getRandom().nextFloat() * 0.4f);
            }
            setChanged();
        }

        return item.isEmpty() ? null : item;
    }

    /**
     * Searches all registered {@link CrucibleRecipe}s for the best match
     * (highest aspect cost wins, to avoid partial-match stealing).
     *
     * <p>Research check is skipped here because the crucible currently has no
     * player reference from item-drop context; the block's {@code useItemOn}
     * path passes the player so recipes will be gated properly when you add
     * research checking to this method later.
     */
    @Nullable
    private CrucibleRecipe findMatchingRecipe(ItemStack catalyst, @Nullable Player player) {
        CrucibleRecipe best = null;
        int bestCost = -1;
        for (Object r : ThaumcraftApi.getCraftingRecipes().values()) {
            if (!(r instanceof CrucibleRecipe recipe)) continue;
            if (!recipe.matches(aspects, catalyst)) continue;
            // Prefer recipe with highest vis cost (specificity tie-breaker)
            int cost = recipe.getAspects().visSize();
            if (cost > bestCost) {
                bestCost = cost;
                best = recipe;
            }
        }
        return best;
    }

    /**
     * Spawns an item-entity floating just above the crucible's opening.
     */
    private void ejectItem(ItemStack stack) {
        if (level == null || stack.isEmpty()) return;
        ItemEntity entity = new ItemEntity(level,
                worldPosition.getX() + 0.5,
                worldPosition.getY() + 0.75,
                worldPosition.getZ() + 0.5,
                stack);
        entity.setDeltaMovement(0, 0.075, 0);
        level.addFreshEntity(entity);
    }

    // -------------------------------------------------------------------------
    // Spill / Overflow
    // -------------------------------------------------------------------------

    /**
     * Removes one random aspect from the pool and injects it as flux into the
     * local aura chunk.
     */
    public void spillRandom(Level level, BlockPos pos) {
        if (aspects.size() == 0) return;
        Aspect[] arr = aspects.getAspects();
        Aspect tag = arr[level.getRandom().nextInt(arr.length)];
        aspects.remove(tag, 1);

        if (CrucibleDecayLogic.shouldSpillAsGoo(level.getRandom().nextFloat())) {
            BlockPos abovePos = pos.above();
            if (level.getBlockState(abovePos).isAir()) {
                level.setBlockAndUpdate(abovePos, thaumcraft.api.blocks.ThaumcraftBlocks.fluxGoo.get().defaultBlockState());
            } else {
                AuraHelper.polluteAura(level, pos, CrucibleDecayLogic.getAuraFluxPollution(), true);
            }
        } else {
            AuraHelper.polluteAura(level, pos, CrucibleDecayLogic.getAuraFluxPollution(), true);
        }
        setChanged();
    }

    /**
     * Clears all aspects and water, dumping everything.  Used on sneak+right-click
     * with an empty hand, or when the block is broken.
     */
    public void spillRemnants() {
        if (level != null && aspects.visSize() > 0) {
            AuraHelper.polluteAura(level, worldPosition, CruciblePollutionLogic.calculateSpilloverPollution(aspects.visSize()), true);
        }
        aspects = new AspectList();
        water = 0;
        heat = 0;
        setChanged();
    }

    // -------------------------------------------------------------------------
    // NBT Serialisation — MC 26.1.2 ValueOutput / ValueInput
    // -------------------------------------------------------------------------

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("Heat", com.mojang.serialization.Codec.SHORT, heat);
        output.store("Water", com.mojang.serialization.Codec.INT, water);

        // AspectList still uses CompoundTag internally; bridge via codec
        CompoundTag aspectTag = new CompoundTag();
        aspects.writeToNBT(aspectTag);
        output.store("Aspects", CompoundTag.CODEC, aspectTag);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        heat = input.read("Heat", com.mojang.serialization.Codec.SHORT).orElse((short) 0);
        water = input.read("Water", com.mojang.serialization.Codec.INT).orElse(0);
        input.read("Aspects", CompoundTag.CODEC)
                .ifPresent(tag -> aspects.readFromNBT(tag));
    }

    // -------------------------------------------------------------------------
    // IAspectContainer
    // -------------------------------------------------------------------------

    @Override
    public AspectList getAspects() {
        return aspects;
    }

    @Override
    public void setAspects(AspectList aspects) {
        // Direct mutation intentionally unsupported; use attemptSmelt / spillRemnants
    }

    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return true;
    }

    @Override
    public int addToContainer(Aspect tag, int amount) {
        aspects.add(tag, amount);
        setChanged();
        return amount;
    }

    @Override
    public boolean takeFromContainer(Aspect tag, int amount) {
        if (aspects.getAmount(tag) >= amount) {
            aspects.remove(tag, amount);
            setChanged();
            return true;
        }
        return false;
    }

    @Override
    public boolean takeFromContainer(AspectList ot) {
        for (Aspect a : ot.getAspects()) {
            if (aspects.getAmount(a) < ot.getAmount(a)) return false;
        }
        for (Aspect a : ot.getAspects()) {
            aspects.remove(a, ot.getAmount(a));
        }
        setChanged();
        return true;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amount) {
        return aspects.getAmount(tag) >= amount;
    }

    @Override
    public boolean doesContainerContain(AspectList ot) {
        for (Aspect a : ot.getAspects()) {
            if (aspects.getAmount(a) < ot.getAmount(a)) return false;
        }
        return true;
    }

    @Override
    public int containerContains(Aspect tag) {
        return aspects.getAmount(tag);
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    /** @return current heat (0–200). */
    public short getHeat() { return heat; }

    /** @return water stored in mB (0–1000). */
    public int getWater() { return water; }

    /**
     * Sets water level directly (e.g., from bucket fill).
     * Clamped to [0, {@link #TANK_CAPACITY}].
     */
    public void setWater(int water) {
        this.water = Math.max(0, Math.min(TANK_CAPACITY, water));
    }

    /** @return {@code true} if heat is at or above the boiling threshold (151). */
    public boolean isBoiling() { return heat >= 151; }
}
