package thaumcraft.common.blocks.essentia;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Smelter Auxiliary Pump block — attaches to the side of a smelter to
 * provide additional distillation columns.
 *
 * <p>Can only be placed on horizontal faces of a {@link BlockSmelter},
 * and only on sides that are not the smelter's front face.
 *
 * <p>MC 1.21.4 / NeoForge 26.2 port of
 * {@code thaumcraft.common.blocks.essentia.BlockSmelterAux}.
 */
public class BlockSmelterAux extends Block {

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public BlockSmelterAux(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
    }

    // -------------------------------------------------------------------------
    // BlockState
    // -------------------------------------------------------------------------

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clickedFace = context.getClickedFace();
        if (!clickedFace.getAxis().isHorizontal()) {
            clickedFace = Direction.NORTH;
        }
        // The aux block faces toward the smelter (opposite of the clicked face)
        return defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, clickedFace.getOpposite());
    }

    // -------------------------------------------------------------------------
    // Placement validation
    // -------------------------------------------------------------------------

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        // The block this aux is attached to (opposite of its facing)
        BlockPos smelterPos = pos.relative(facing.getOpposite());
        BlockState smelterState = level.getBlockState(smelterPos);

        if (!(smelterState.getBlock() instanceof BlockSmelter)) return false;

        // Cannot attach to the smelter's front face
        Direction smelterFacing = smelterState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        return facing != smelterFacing;
    }

}

