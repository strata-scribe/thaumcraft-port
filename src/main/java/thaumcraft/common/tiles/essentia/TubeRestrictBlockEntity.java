package thaumcraft.common.tiles.essentia;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.blocks.entities.ThaumcraftBlockEntities;

public class TubeRestrictBlockEntity extends TileTube {

    public TubeRestrictBlockEntity(BlockPos pos, BlockState state) {
        super(ThaumcraftBlockEntities.TUBE_RESTRICT.get(), pos, state, logicFactory());
    }

    private static java.util.function.Function<Runnable, TubeLogic> logicFactory() {
        return setChanged -> new TubeRestrictLogic(setChanged);
    }
}
