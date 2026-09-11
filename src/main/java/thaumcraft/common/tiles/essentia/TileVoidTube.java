package thaumcraft.common.tiles.essentia;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.blocks.entities.ThaumcraftBlockEntities;
import thaumcraft.common.lib.SoundsTC;

public class TileVoidTube extends TileTube {

    public TileVoidTube(BlockPos pos, BlockState state) {
        super(ThaumcraftBlockEntities.VOID_TUBE.get(), pos, state, logicFactory());
        ((VoidTubeLogic) this.logic).setOnVoidCallback(this::playVoidSound);
    }

    private static java.util.function.Function<Runnable, TubeLogic> logicFactory() {
        return setChanged -> new VoidTubeLogic(setChanged, null);
    }

    public void playVoidSound() {
        if (level != null && !level.isClientSide()) {
            level.playSound(null, getBlockPos(), SoundsTC.PUMP.get(), SoundSource.BLOCKS, 0.1f, 1.0f);
        }
    }
}
