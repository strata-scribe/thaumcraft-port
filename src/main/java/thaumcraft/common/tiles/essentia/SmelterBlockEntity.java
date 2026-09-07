package thaumcraft.common.tiles.essentia;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.ThaumcraftBlocks;
import thaumcraft.api.items.ThaumcraftItems;
import thaumcraft.common.blocks.entities.ThaumcraftBlockEntities;
import thaumcraft.common.blocks.essentia.SmelterTier;

/**
 * Block entity for the Essentia Smelter — decomposes items into their
 * component aspects and pushes essentia upward into stacked alembics.
 *
 * <h3>Inventory</h3>
 * <ul>
 *   <li>Slot 0: Item input — any item with aspect tags.</li>
 *   <li>Slot 1: Fuel — standard furnace fuels, with Alumentum providing speed boost.</li>
 * </ul>
 *
 * <h3>Mechanics</h3>
 * <ul>
 *   <li>Burns fuel to enter "lit" state, incrementing cook time.</li>
 *   <li>Once cook time reaches smelt time, decomposes the input item
 *       into its aspects, adding them to the internal buffer.</li>
 *   <li>Periodically pushes 1 essentia from the buffer into stacked alembics.</li>
 *   <li>Flux is generated based on tier efficiency and vented through
 *       adjacent {@code BlockSmelterVent} blocks or released into the aura.</li>
 * </ul>
 *
 * <p>MC 1.21.4 / NeoForge 26.2 port of
 * {@code thaumcraft.common.tiles.essentia.TileSmelter}.
 */
public class SmelterBlockEntity extends BlockEntity {

    // -------------------------------------------------------------------------
    // Tier
    // -------------------------------------------------------------------------

    private final SmelterTier tier;

    // -------------------------------------------------------------------------
    // Internal essentia buffer
    // -------------------------------------------------------------------------

    /** Multi-aspect buffer holding decomposed essentia awaiting alembic push. */
    private final AspectList aspects = new AspectList();
    /** Cached total essentia in the buffer. */
    private int vis = 0;

    // -------------------------------------------------------------------------
    // Burning engine
    // -------------------------------------------------------------------------

    /** Remaining burn ticks of the current fuel item. */
    private int furnaceBurnTime = 0;
    /** Total burn time of the last consumed fuel item (for GUI scaling). */
    private int currentItemBurnTime = 0;
    /** Cook progress ticks for the current input item. */
    private int furnaceCookTime = 0;
    /** Computed smelt duration for the current input item (ticks). */
    private int smeltTime = 100;
    /** True when Alumentum was used as fuel — multiplies push speed by 0.8. */
    private boolean speedBoost = false;

    // -------------------------------------------------------------------------
    // Tick counter & cached neighbour data
    // -------------------------------------------------------------------------

    private int tickCount = 0;
    /** Cached bellows count (-1 = not yet scanned). */
    private int bellows = 0;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public SmelterBlockEntity(BlockPos pos, BlockState state) {
        super(ThaumcraftBlockEntities.SMELTER.get(), pos, state);
        // Determine tier from the block that created this entity
        Block block = state.getBlock();
        if (block == ThaumcraftBlocks.smelterThaumium.get()) {
            this.tier = SmelterTier.THAUMIUM;
        } else if (block == ThaumcraftBlocks.smelterVoid.get()) {
            this.tier = SmelterTier.VOID;
        } else {
            this.tier = SmelterTier.BASIC;
        }
    }

    // -------------------------------------------------------------------------
    // Tick — called every game tick on the server side
    // -------------------------------------------------------------------------

    /**
     * Static ticker method for use with
     * {@link net.minecraft.world.level.block.entity.BlockEntityTicker}.
     */
    public static void serverTick(Level level, BlockPos pos, BlockState state,
                                  SmelterBlockEntity smelter) {
        smelter.tick(level, pos, state);
    }

    private void tick(Level level, BlockPos pos, BlockState state) {
        if (level == null || level.isClientSide()) return;

        boolean wasLit = furnaceBurnTime > 0;
        boolean dirty = false;
        tickCount++;

        // Decrement burn time
        if (furnaceBurnTime > 0) {
            furnaceBurnTime--;
        }

        // --- Push essentia into alembics ---
        int pushInterval = getPushSpeed();
        if (speedBoost) {
            pushInterval = Math.max(1, (int)(pushInterval * 0.8));
        }
        if (tickCount % pushInterval == 0 && aspects.size() > 0) {
            pushEssentiaToAlembics(level, pos, state);
        }

        // --- Fuel consumption ---
        // (Inventory interaction is deferred to GUI/container — here we just
        //  track the burn state. In a full implementation, fuel is consumed from
        //  slot 1 when furnaceBurnTime reaches 0 and canSmelt() is true.)

        // --- Cooking ---
        if (isLit() && furnaceCookTime > 0) {
            furnaceCookTime++;
            if (furnaceCookTime >= smeltTime) {
                furnaceCookTime = 0;
                dirty = true;
            }
        }

        // Update LIT blockstate if changed
        boolean nowLit = furnaceBurnTime > 0;
        if (wasLit != nowLit) {
            level.setBlock(pos, state.setValue(BlockStateProperties.LIT, nowLit), Block.UPDATE_ALL);
            dirty = true;
        }

        if (dirty) {
            setChanged();
        }
    }

    // -------------------------------------------------------------------------
    // Essentia push
    // -------------------------------------------------------------------------

    private void pushEssentiaToAlembics(Level level, BlockPos pos, BlockState state) {
        // Push from direct column above smelter
        for (Aspect aspect : aspects.getAspects()) {
            if (aspects.getAmount(aspect) > 0
                    && AlembicBlockEntity.processAlembics(level, pos, aspect)) {
                takeFromBuffer(aspect, 1);
                break;
            }
        }

        // Also push from auxiliary columns (BlockSmelterAux on sides)
        Direction smelterFacing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        for (Direction face : Direction.Plane.HORIZONTAL) {
            if (face == smelterFacing) continue;
            BlockPos auxPos = pos.relative(face);
            BlockState auxState = level.getBlockState(auxPos);
            if (auxState.is(ThaumcraftBlocks.smelterAux.get())
                    && auxState.getValue(BlockStateProperties.HORIZONTAL_FACING) == face.getOpposite()) {
                for (Aspect aspect : aspects.getAspects()) {
                    if (aspects.getAmount(aspect) > 0
                            && AlembicBlockEntity.processAlembics(level, auxPos, aspect)) {
                        takeFromBuffer(aspect, 1);
                        break;
                    }
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Smelting API (called from container/menu or test harness)
    // -------------------------------------------------------------------------

    /**
     * Tests whether the current input item can be smelted.
     *
     * @param inputAspects the aspects of the item in slot 0
     * @return {@code true} if there is room in the buffer
     */
    public boolean canSmelt(AspectList inputAspects) {
        if (inputAspects == null || inputAspects.size() == 0) return false;
        int totalVis = inputAspects.visSize();
        return totalVis <= (tier.getCapacity() - vis);
    }

    /**
     * Computes the smelt duration based on the input item's total vis
     * and the current bellows count.
     *
     * @param inputAspects the aspects of the item in slot 0
     * @return smelt duration in ticks
     */
    public int computeSmeltTime(AspectList inputAspects) {
        int totalVis = inputAspects.visSize();
        return Math.max(1, (int)(totalVis * 2 * (1.0f - 0.125f * bellows)));
    }

    /**
     * Decomposes an item's aspects into the internal buffer, generating
     * flux based on tier efficiency.
     *
     * <p>For each point of each aspect, a random roll determines whether
     * that point survives (added to buffer) or is destroyed (becomes flux).
     * The FLUX aspect itself has a harsher threshold: {@code efficiency * 0.66}.
     *
     * @param inputAspects the aspects of the item being smelted
     * @param level the world (for flux pollution)
     * @param pos the smelter position
     */
    public void smeltItem(AspectList inputAspects, Level level, BlockPos pos) {
        if (inputAspects == null) return;
        AspectList working = inputAspects.copy();
        int flux = 0;

        for (Aspect a : working.getAspects()) {
            float eff = tier.getEfficiency();
            int qty = working.getAmount(a);
            for (int i = 0; i < qty; i++) {
                float threshold = (a == Aspect.FLUX) ? (eff * 0.66f) : eff;
                if (level.getRandom().nextFloat() > threshold) {
                    working.reduce(a, 1);
                    flux++;
                }
            }
            aspects.add(a, working.getAmount(a));
        }

        vis = aspects.visSize();

        // Vent flux through BlockSmelterVent blocks, remainder goes to aura
        if (flux > 0 && level != null) {
            int vented = ventFlux(level, pos, flux);
            int remainder = flux - vented;
            if (remainder > 0) {
                AuraHelper.polluteAura(level, pos, (float) remainder, true);
            }
        }
    }

    /**
     * Attempts to vent flux through adjacent {@link BlockSmelterVent} blocks.
     *
     * @return the number of flux points successfully vented (absorbed by vents)
     */
    private int ventFlux(Level level, BlockPos pos, int flux) {
        int vented = 0;
        BlockState state = level.getBlockState(pos);
        Direction smelterFacing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);

        for (int i = 0; i < flux; i++) {
            boolean found = false;
            for (Direction face : Direction.Plane.HORIZONTAL) {
                if (face == smelterFacing) continue;
                BlockPos ventPos = pos.relative(face);
                BlockState ventState = level.getBlockState(ventPos);
                if (ventState.is(ThaumcraftBlocks.smelterVent.get())
                        && ventState.getValue(BlockStateProperties.HORIZONTAL_FACING) == face.getOpposite()
                        && level.getRandom().nextFloat() < 0.333f) {
                    vented++;
                    found = true;
                    break;
                }
            }
            // If no vent absorbed this point, it will go to aura (handled by caller)
        }
        return vented;
    }

    // -------------------------------------------------------------------------
    // Fuel API
    // -------------------------------------------------------------------------

    /**
     * Starts burning the given fuel item for {@code burnTime} ticks.
     *
     * @param burnTime ticks this fuel will burn
     * @param isAlumentum whether the fuel item is Alumentum (grants speed boost)
     */
    public void consumeFuel(int burnTime, boolean isAlumentum) {
        this.furnaceBurnTime = burnTime;
        this.currentItemBurnTime = burnTime;
        this.speedBoost = isAlumentum;
        setChanged();
    }

    /** @return {@code true} if the smelter is currently burning fuel. */
    public boolean isLit() {
        return furnaceBurnTime > 0;
    }

    // -------------------------------------------------------------------------
    // Buffer access
    // -------------------------------------------------------------------------

    /**
     * Removes {@code amount} of {@code aspect} from the internal buffer.
     *
     * @return {@code true} if successfully removed
     */
    public boolean takeFromBuffer(Aspect aspect, int amount) {
        if (aspects.getAmount(aspect) >= amount) {
            aspects.remove(aspect, amount);
            vis = aspects.visSize();
            setChanged();
            return true;
        }
        return false;
    }

    /** @return the internal essentia buffer (read-only snapshot). */
    public AspectList getAspects() { return aspects; }

    /** @return total essentia in the buffer. */
    public int getVis() { return vis; }

    /** @return the tier of this smelter. */
    public SmelterTier getTier() { return tier; }

    /** @return current burn time remaining. */
    public int getFurnaceBurnTime() { return furnaceBurnTime; }

    /** @return total burn time of last consumed fuel. */
    public int getCurrentItemBurnTime() { return currentItemBurnTime; }

    /** @return current cook progress. */
    public int getFurnaceCookTime() { return furnaceCookTime; }

    /** @return computed smelt time for current item. */
    public int getSmeltTime() { return smeltTime; }

    /** @return whether alumentum speed boost is active. */
    public boolean isSpeedBoost() { return speedBoost; }

    /** @return the push speed in ticks (before speed boost). */
    public int getPushSpeed() { return tier.getPushSpeed(); }

    /** @return bellows count (0 = none detected). */
    public int getBellows() { return bellows; }

    public void setSmeltTime(int smeltTime) { this.smeltTime = smeltTime; }
    public void setFurnaceCookTime(int cookTime) { this.furnaceCookTime = cookTime; }
    public void setBellows(int bellows) { this.bellows = bellows; }

    // -------------------------------------------------------------------------
    // Neighbour scan
    // -------------------------------------------------------------------------

    /**
     * Scans horizontal neighbours for bellows, aux blocks, and vents.
     * Called when the smelter is first placed and on neighbour block changes.
     */
    public void checkNeighbours() {
        // Bellows detection is deferred to TileBellows integration.
        // For now, bellows defaults to 0.
        bellows = 0;
    }

    // -------------------------------------------------------------------------
    // Serialisation — MC 1.21.4 ValueOutput / ValueInput
    // -------------------------------------------------------------------------

    @Override
    protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput output) {
        super.saveAdditional(output);
        output.store("BurnTime", com.mojang.serialization.Codec.INT, furnaceBurnTime);
        output.store("CurrentItemBurnTime", com.mojang.serialization.Codec.INT, currentItemBurnTime);
        output.store("CookTime", com.mojang.serialization.Codec.INT, furnaceCookTime);
        output.store("SmeltTime", com.mojang.serialization.Codec.INT, smeltTime);
        output.store("SpeedBoost", com.mojang.serialization.Codec.BOOL, speedBoost);
        output.store("Vis", com.mojang.serialization.Codec.INT, vis);
        // Serialize the multi-aspect buffer using AspectList's Codec
        output.store("Aspects", AspectList.CODEC, aspects);
    }

    @Override
    protected void loadAdditional(net.minecraft.world.level.storage.ValueInput input) {
        super.loadAdditional(input);
        furnaceBurnTime = input.read("BurnTime", com.mojang.serialization.Codec.INT).orElse(0);
        currentItemBurnTime = input.read("CurrentItemBurnTime", com.mojang.serialization.Codec.INT).orElse(0);
        furnaceCookTime = input.read("CookTime", com.mojang.serialization.Codec.INT).orElse(0);
        smeltTime = input.read("SmeltTime", com.mojang.serialization.Codec.INT).orElse(100);
        speedBoost = input.read("SpeedBoost", com.mojang.serialization.Codec.BOOL).orElse(false);
        vis = input.read("Vis", com.mojang.serialization.Codec.INT).orElse(0);

        AspectList loaded = input.read("Aspects", AspectList.CODEC).orElse(null);
        aspects.aspects.clear();
        if (loaded != null) {
            for (Aspect a : loaded.getAspects()) {
                aspects.add(a, loaded.getAmount(a));
            }
        }
        vis = aspects.visSize();
    }
}
