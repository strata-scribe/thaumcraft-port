package thaumcraft.common.blocks.essentia;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import thaumcraft.common.tiles.essentia.TileVoidTube;

public class BlockVoidTube extends BlockTube {

    public static final MapCodec<BlockVoidTube> CODEC = simpleCodec(BlockVoidTube::new);

    public BlockVoidTube(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<BlockVoidTube> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileVoidTube(pos, state);
    }
}
