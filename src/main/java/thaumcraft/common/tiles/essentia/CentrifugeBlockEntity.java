package thaumcraft.common.tiles.essentia;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.blocks.entities.ThaumcraftBlockEntities;

public class CentrifugeBlockEntity extends BlockEntity implements IEssentiaTransport {

    public final CentrifugeLogic logic;

    public CentrifugeBlockEntity(BlockPos pos, BlockState state) {
        super(ThaumcraftBlockEntities.CENTRIFUGE.get(), pos, state);
        this.logic = new CentrifugeLogic(this::setChanged);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CentrifugeBlockEntity be) {
        be.logic.tick();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        logic.save(output);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        logic.load(input);
    }

    @Override
    public boolean isConnectable(Direction face) {
        return logic.canInputFrom(face) || logic.canOutputTo(face);
    }

    @Override
    public boolean canInputFrom(Direction face) {
        return logic.canInputFrom(face);
    }

    @Override
    public boolean canOutputTo(Direction face) {
        return logic.canOutputTo(face);
    }

    @Override
    public void setSuction(Aspect aspect, int amount) {
        // usually ignored for machines
    }

    @Override
    public Aspect getSuctionType(Direction face) {
        return logic.getSuctionType(face);
    }

    @Override
    public int getSuctionAmount(Direction face) {
        return logic.getSuctionAmount(face);
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, Direction face) {
        return logic.takeEssentia(aspect, amount, face);
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, Direction face) {
        return logic.addEssentia(aspect, amount, face);
    }

    @Override
    public Aspect getEssentiaType(Direction face) {
        return logic.getEssentiaType(face);
    }

    @Override
    public int getEssentiaAmount(Direction face) {
        return logic.getEssentiaAmount(face);
    }

    @Override
    public int getMinimumSuction() {
        return 0;
    }
}
