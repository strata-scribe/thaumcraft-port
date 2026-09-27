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
import thaumcraft.common.tiles.essentia.logic.AlembicLogic;

/**
 * Block entity for the Distillation Alembic — single-aspect essentia
 * receiver that stacks on top of an Essentia Smelter or another Alembic.
 *
 * <h3>Storage</h3>
 * <ul>
 *   <li>Holds one {@link Aspect} type at a time, up to {@link AlembicLogic#MAX_AMOUNT}.</li>
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
    // State
    // -------------------------------------------------------------------------

    public final AlembicLogic logic = new AlembicLogic();

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
        return (logic.getAspect() != null && logic.getAmount() > 0)
                ? new AspectList().add(logic.getAspect(), logic.getAmount())
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
        int remainder = logic.addToContainer(tag, am);
        if (remainder != am) {
            setChanged();
        }
        return remainder;
    }

    @Override
    public boolean takeFromContainer(Aspect tag, int am) {
        boolean success = logic.takeFromContainer(tag, am);
        if (success) {
            setChanged();
        }
        return success;
    }

    @Deprecated
    @Override
    public boolean takeFromContainer(AspectList ot) {
        return false;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect tag, int am) {
        return logic.doesContainerContainAmount(tag, am);
    }

    @Deprecated
    @Override
    public boolean doesContainerContain(AspectList ot) {
        return logic.getAmount() > 0 && logic.getAspect() != null && ot.getAmount(logic.getAspect()) > 0;
    }

    @Override
    public int containerContains(Aspect tag) {
        return logic.containerContains(tag);
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
        return logic.getAspect();
    }

    @Override
    public int getEssentiaAmount(Direction face) {
        return logic.getAmount();
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

    public Aspect getStoredAspect() { return logic.getAspect(); }
    public int getAmount() { return logic.getAmount(); }
    public int getMaxAmount() { return AlembicLogic.MAX_AMOUNT; }

    public Aspect getAspectFilter() { return logic.getAspectFilter(); }
    public void setAspectFilter(Aspect filter) { logic.setAspectFilter(filter); setChanged(); }

    public int getFacing() { return facing; }
    public void setFacing(int facing) { this.facing = facing; setChanged(); }

    /** Clears all stored essentia without producing flux (for internal use). */
    public void clearEssentia() {
        logic.clearEssentia();
        setChanged();
    }

    // -------------------------------------------------------------------------
    // Serialisation — MC 1.21.4 ValueOutput / ValueInput
    // -------------------------------------------------------------------------

    @Override
    protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput output) {
        super.saveAdditional(output);
        if (logic.getAspect() != null) {
            output.store("Aspect", com.mojang.serialization.Codec.STRING, logic.getAspect().getTag());
        }
        if (logic.getAspectFilter() != null) {
            output.store("AspectFilter", com.mojang.serialization.Codec.STRING, logic.getAspectFilter().getTag());
        }
        output.store("Amount", com.mojang.serialization.Codec.INT, logic.getAmount());
        output.store("Facing", com.mojang.serialization.Codec.INT, facing);
    }

    @Override
    protected void loadAdditional(net.minecraft.world.level.storage.ValueInput input) {
        super.loadAdditional(input);
        logic.setAspect(input.read("Aspect", com.mojang.serialization.Codec.STRING)
                .map(Aspect::getAspect).orElse(null));
        logic.setAspectFilter(input.read("AspectFilter", com.mojang.serialization.Codec.STRING)
                .map(Aspect::getAspect).orElse(null));
        logic.setAmount(input.read("Amount", com.mojang.serialization.Codec.INT).orElse(0));
        facing = input.read("Facing", com.mojang.serialization.Codec.INT)
                .orElse(Direction.DOWN.ordinal());
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
                if (alembic.getAmount() > 0 && alembic.getStoredAspect() == aspect
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
                if ((alembic.getAspectFilter() == null || alembic.getAspectFilter() == aspect)
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
