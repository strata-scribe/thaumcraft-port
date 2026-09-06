package thaumcraft.common.tiles.essentia;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.blocks.entities.ThaumcraftBlockEntities;

public class TileTube extends BlockEntity implements IEssentiaTransport {

    public final TubeLogic logic;

    public TileTube(BlockPos pos, BlockState state) {
        super(ThaumcraftBlockEntities.TUBE.get(), pos, state);
        this.logic = new TubeLogic(this::setChanged);
    }

    @Override
    public boolean isConnectable(Direction face) { return logic.isConnectable(face); }
    @Override
    public boolean canInputFrom(Direction face) { return logic.canInputFrom(face); }
    @Override
    public boolean canOutputTo(Direction face) { return logic.canOutputTo(face); }
    @Override
    public void setSuction(Aspect aspect, int amount) { logic.setSuction(aspect, amount); }
    @Override
    public Aspect getSuctionType(Direction face) { return logic.getSuctionType(face); }
    @Override
    public int getSuctionAmount(Direction face) { return logic.getSuctionAmount(face); }
    @Override
    public int takeEssentia(Aspect aspect, int amount, Direction face) { return logic.takeEssentia(aspect, amount, face); }
    @Override
    public int addEssentia(Aspect aspect, int amount, Direction face) { return logic.addEssentia(aspect, amount, face); }
    @Override
    public Aspect getEssentiaType(Direction face) { return logic.getEssentiaType(face); }
    @Override
    public int getEssentiaAmount(Direction face) { return logic.getEssentiaAmount(face); }
    @Override
    public int getMinimumSuction() { return logic.getMinimumSuction(); }

    public boolean isOpen(Direction face) { return logic.isOpen(face); }
    public void toggleOpenFace(Direction face) { logic.toggleOpenFace(face); }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (logic.getEssentiaType(null) != null) {
            output.store("essentiaType", com.mojang.serialization.Codec.STRING, logic.getEssentiaType(null).getTag());
        }
        output.store("essentiaAmount", com.mojang.serialization.Codec.INT, logic.getEssentiaAmount(null));

        if (logic.getSuctionType(null) != null) {
            output.store("suctionType", com.mojang.serialization.Codec.STRING, logic.getSuctionType(null).getTag());
        }
        output.store("suctionAmount", com.mojang.serialization.Codec.INT, logic.getSuctionAmount(null));

        for (Direction dir : Direction.values()) {
            output.store("openFace_" + dir.name(), com.mojang.serialization.Codec.BOOL, logic.isOpen(dir));
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        Aspect essentiaType = input.read("essentiaType", com.mojang.serialization.Codec.STRING).map(Aspect::getAspect).orElse(null);
        int essentiaAmount = input.read("essentiaAmount", com.mojang.serialization.Codec.INT).orElse(0);
        if (essentiaAmount == 0) essentiaType = null;
        logic.setEssentia(essentiaType, essentiaAmount);

        Aspect suctionType = input.read("suctionType", com.mojang.serialization.Codec.STRING).map(Aspect::getAspect).orElse(null);
        int suctionAmount = input.read("suctionAmount", com.mojang.serialization.Codec.INT).orElse(0);
        logic.setSuction(suctionType, suctionAmount);

        for (Direction dir : Direction.values()) {
            logic.setOpenFace(dir, input.read("openFace_" + dir.name(), com.mojang.serialization.Codec.BOOL).orElse(true));
        }
    }
}
