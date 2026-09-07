package thaumcraft.common.tiles.essentia;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.blocks.entities.ThaumcraftBlockEntities;

/**
 * Block entity for the Distillation Alembic — single-aspect essentia
 * receiver that stacks on top of an Essentia Smelter or another Alembic.
 *
 * <h3>Storage</h3>
 * <ul>
 *   <li>Holds one {@link Aspect} type at a time, up to {@value #MAX_AMOUNT}.</li>
 *   <li>An optional {@link #aspectFilter} restricts which aspect is accepted.</li>
 * </ul>
 *
 * <h3>Essentia Transport</h3>
 * <ul>
 *   <li>Connectable on horizontal faces and UP (not DOWN).</li>
 *   <li>Output only — does not accept external input via tubes.</li>
 *   <li>Receives boiled essentia exclusively from the smelter below
 *       via the static {@link #processAlembics} helper.</li>
 * </ul>
 *
 * <p>MC 1.21.4 / NeoForge 26.2 port of
 * {@code thaumcraft.common.tiles.essentia.TileAlembic}.
 */
public class AlembicBlockEntity extends BlockEntity
        implements IAspectContainer, IEssentiaTransport {

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    /** Maximum essentia capacity. */
    public static final int MAX_AMOUNT = 128;

    // -------------------------------------------------------------------------
    // State
    // -------------------------------------------------------------------------

    private Aspect aspect = null;
    private Aspect aspectFilter = null;
    private int amount = 0;

    /**
     * Direction ordinal of the face where a label is attached.
     * {@link Direction#DOWN} ordinal (0) means "no label".
     */
    private int facing = Direction.DOWN.ordinal();

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public AlembicBlockEntity(BlockPos pos, BlockState state) {
        super(ThaumcraftBlockEntities.ALEMBIC.get(), pos, state);
    }

    // -------------------------------------------------------------------------
    // IAspectContainer
    // -------------------------------------------------------------------------

    @Override
    public AspectList getAspects() {
        return (aspect != null && amount > 0)
                ? new AspectList().add(aspect, amount)
                : new AspectList();
    }

    @Override
    public void setAspects(AspectList aspects) {
        // Not used externally; alembics receive via addToContainer only.
    }

    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return true;
    }

    @Override
    public int addToContainer(Aspect tag, int am) {
        if (aspectFilter != null && tag != aspectFilter) return am;
        if ((amount < MAX_AMOUNT && tag == aspect) || amount == 0) {
            aspect = tag;
            int added = Math.min(am, MAX_AMOUNT - amount);
            amount += added;
            am -= added;
        }
        setChanged();
        return am;
    }

    @Override
    public boolean takeFromContainer(Aspect tag, int am) {
        if (amount == 0 || aspect == null) {
            aspect = null;
            amount = 0;
            return false;
        }
        if (aspect == tag && amount >= am) {
            amount -= am;
            if (amount <= 0) {
                aspect = null;
                amount = 0;
            }
            setChanged();
            return true;
        }
        return false;
    }

    @Deprecated
    @Override
    public boolean takeFromContainer(AspectList ot) {
        return false;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect tag, int am) {
        return tag == aspect && amount >= am;
    }

    @Deprecated
    @Override
    public boolean doesContainerContain(AspectList ot) {
        return amount > 0 && aspect != null && ot.getAmount(aspect) > 0;
    }

    @Override
    public int containerContains(Aspect tag) {
        return tag == aspect ? amount : 0;
    }

    // -------------------------------------------------------------------------
    // IEssentiaTransport
    // -------------------------------------------------------------------------

    @Override
    public boolean isConnectable(Direction face) {
        return face != Direction.DOWN && face.ordinal() != facing;
    }

    @Override
    public boolean canInputFrom(Direction face) {
        return false; // Only receives from smelter below via processAlembics
    }

    @Override
    public boolean canOutputTo(Direction face) {
        return face != Direction.DOWN && face.ordinal() != facing;
    }

    @Override
    public void setSuction(Aspect aspect, int amount) {
        // Alembics do not generate suction
    }

    @Override
    public Aspect getSuctionType(Direction face) {
        return null;
    }

    @Override
    public int getSuctionAmount(Direction face) {
        return 0;
    }

    @Override
    public Aspect getEssentiaType(Direction face) {
        return aspect;
    }

    @Override
    public int getEssentiaAmount(Direction face) {
        return amount;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, Direction face) {
        return (canOutputTo(face) && takeFromContainer(aspect, amount)) ? amount : 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, Direction face) {
        return 0; // External tube input not supported
    }

    @Override
    public int getMinimumSuction() {
        return 0;
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    public Aspect getStoredAspect() { return aspect; }
    public int getAmount() { return amount; }
    public int getMaxAmount() { return MAX_AMOUNT; }

    public Aspect getAspectFilter() { return aspectFilter; }
    public void setAspectFilter(Aspect filter) { this.aspectFilter = filter; setChanged(); }

    public int getFacing() { return facing; }
    public void setFacing(int facing) { this.facing = facing; setChanged(); }

    /** Clears all stored essentia without producing flux (for internal use). */
    public void clearEssentia() {
        this.aspect = null;
        this.amount = 0;
        setChanged();
    }

    // -------------------------------------------------------------------------
    // Serialisation — MC 1.21.4 ValueOutput / ValueInput
    // -------------------------------------------------------------------------

    @Override
    protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput output) {
        super.saveAdditional(output);
        if (aspect != null) {
            output.store("Aspect", com.mojang.serialization.Codec.STRING, aspect.getTag());
        }
        if (aspectFilter != null) {
            output.store("AspectFilter", com.mojang.serialization.Codec.STRING, aspectFilter.getTag());
        }
        output.store("Amount", com.mojang.serialization.Codec.INT, amount);
        output.store("Facing", com.mojang.serialization.Codec.INT, facing);
    }

    @Override
    protected void loadAdditional(net.minecraft.world.level.storage.ValueInput input) {
        super.loadAdditional(input);
        aspect = input.read("Aspect", com.mojang.serialization.Codec.STRING)
                .map(Aspect::getAspect).orElse(null);
        aspectFilter = input.read("AspectFilter", com.mojang.serialization.Codec.STRING)
                .map(Aspect::getAspect).orElse(null);
        amount = input.read("Amount", com.mojang.serialization.Codec.INT).orElse(0);
        facing = input.read("Facing", com.mojang.serialization.Codec.INT)
                .orElse(Direction.DOWN.ordinal());
        // Guard: clamp
        amount = Math.max(0, Math.min(MAX_AMOUNT, amount));
        if (amount == 0) aspect = null;
    }

    // -------------------------------------------------------------------------
    // Multi-pass Alembic Resolution
    // -------------------------------------------------------------------------

    /**
     * Attempts to push 1 unit of {@code aspect} upward into a column of
     * stacked alembics above {@code pos}.
     *
     * <p><b>Pass 1:</b> Find an alembic that already holds the same aspect
     * and has room — this keeps identical essentia concentrated.<br>
     * <b>Pass 2:</b> Find the first empty (or filter-matching) alembic that
     * can accept the aspect.
     *
     * @param level the world
     * @param pos   the position of the smelter (or aux block)
     * @param aspect the aspect to push
     * @return {@code true} if 1 unit was successfully placed
     */
    public static boolean processAlembics(Level level, BlockPos pos, Aspect aspect) {
        // Pass 1: look for an alembic already holding this aspect
        int deep = 1;
        while (true) {
            BlockEntity te = level.getBlockEntity(pos.above(deep));
            if (te instanceof AlembicBlockEntity alembic) {
                if (alembic.amount > 0 && alembic.aspect == aspect
                        && alembic.addToContainer(aspect, 1) == 0) {
                    return true;
                }
                deep++;
            } else {
                break;
            }
        }

        // Pass 2: look for any empty/filtered alembic that accepts this aspect
        deep = 1;
        while (true) {
            BlockEntity te = level.getBlockEntity(pos.above(deep));
            if (te instanceof AlembicBlockEntity alembic) {
                if ((alembic.aspectFilter == null || alembic.aspectFilter == aspect)
                        && alembic.addToContainer(aspect, 1) == 0) {
                    return true;
                }
                deep++;
            } else {
                break;
            }
        }

        return false;
    }
}
