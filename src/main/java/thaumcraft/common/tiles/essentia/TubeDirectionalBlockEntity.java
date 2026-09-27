package thaumcraft.common.tiles.essentia;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import thaumcraft.common.tiles.essentia.logic.TubeDirectionalLogic;

public class TubeDirectionalBlockEntity extends TileTube {

    public TubeDirectionalBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, TubeDirectionalLogic::new);
    }

    public Direction getFacing() {
        return ((TubeDirectionalLogic) this.logic).getFacing();
    }

    public void setFacing(Direction facing) {
        ((TubeDirectionalLogic) this.logic).setFacing(facing);
        setChanged();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("facing", com.mojang.serialization.Codec.INT, getFacing().get3DDataValue());
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        setFacing(Direction.from3DDataValue(input.read("facing", com.mojang.serialization.Codec.INT).orElse(Direction.NORTH.get3DDataValue())));
    }
}
