package thaumcraft.common.blocks.essentia;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import thaumcraft.common.tiles.essentia.TubeRestrictBlockEntity;

/**
 * Essentia Restrictor Tube — adds artificial resistance (suction penalty) to essentia flow,
 * prioritizing routes through normal tubes.
 */
public class BlockTubeRestrict extends BlockTube {

    public static final MapCodec<BlockTubeRestrict> CODEC = simpleCodec(BlockTubeRestrict::new);

    public BlockTubeRestrict(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TubeRestrictBlockEntity(pos, state);
    }
}
