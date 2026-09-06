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
 *   <li>If aspects exceed {@link #MAX_ASPECTS} or the {@code spillCounter} reaches 100 → one
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
     * Ticks since last spill.  When this reaches 100 on a boiling crucible,
     * one random aspect is spilled to the aura.
     */
    private long spillCounter = -100L;

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
        spillCounter++;

        // --- Heat logic ---
        short prevHeat = heat;
        BlockState below = level.getBlockState(pos.below());

        if (water > 0) {
            if (isHeatSource(below)) {
                if (heat < 200) {
                    heat++;
                    if (prevHeat < 151 && heat >= 151) {
                        setChanged();
                    }
                }
            } else {
                if (heat > 0) {
                    heat--;
                    if (heat == 149) {
                        setChanged();
                    }
                }
            }
        } else if (heat > 0) {
            heat--;
        }

        // --- Overflow & timed spill ---
        if (aspects.visSize() > MAX_ASPECTS) {
            spillRandom(level, pos);
        }
        if (spillCounter >= 100L && heat > 150) {
            spillRandom(level, pos);
            spillCounter = 0L;
        }
    }

    /**
     * Returns {@code true} if the given block state qualifies as a heat source
     * for the crucible (fire, lava, campfire, soul_campfire, magma_block).
     */
    private static boolean isHeatSource(BlockState state) {
        return state.is(Blocks.FIRE)
                || state.is(Blocks.LAVA)
                || state.is(Blocks.MAGMA_BLOCK)
                || state.is(Blocks.CAMPFIRE)
                || state.is(Blocks.SOUL_CAMPFIRE);
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
                spillCounter = -250L;
                crafted = true;
                item.shrink(1);
            } else {
                // Dissolve
                AspectList objectAspects = AspectHelper.getObjectAspects(single);
                if (objectAspects != null && objectAspects.size() > 0) {
                    for (Aspect tag : objectAspects.getAspects()) {
                        aspects.add(tag, objectAspects.getAmount(tag));
                    }
                    dissolved = true;
                    spillCounter = -150L;
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
        // Aspect.FLUX not yet accessible in port — treat all as minor pollution
        AuraHelper.polluteAura(level, pos, 0.25f, true);
        setChanged();
    }

    /**
     * Clears all aspects and water, dumping everything.  Used on sneak+right-click
     * with an empty hand, or when the block is broken.
     */
    public void spillRemnants() {
        if (level != null && aspects.visSize() > 0) {
            AuraHelper.polluteAura(level, worldPosition, aspects.visSize() * 0.1f, true);
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
