package thaumcraft.common.blocks.essentia;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Smelter Vent block — attaches to the side of a smelter to filter
 * flux pollution during smelting.
 *
 * <p>When the smelter generates flux, each point has a 33% chance of
 * being absorbed by an adjacent vent instead of polluting the aura.
 *
 * <p>MC 1.21.4 / NeoForge 26.2 port of
 * {@code thaumcraft.common.blocks.essentia.BlockSmelterVent}.
 */
public class BlockSmelterVent extends Block {

    // -------------------------------------------------------------------------
    // Directional VoxelShapes (half-block depth on the attached face)
    // -------------------------------------------------------------------------

    private static final VoxelShape SHAPE_NORTH = Block.box(2, 2, 0, 14, 14, 8);
    private static final VoxelShape SHAPE_SOUTH = Block.box(2, 2, 8, 14, 14, 16);
    private static final VoxelShape SHAPE_WEST  = Block.box(0, 2, 2, 8, 14, 14);
    private static final VoxelShape SHAPE_EAST  = Block.box(8, 2, 2, 16, 14, 14);

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public BlockSmelterVent(BlockBehaviour.Properties properties) {
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
        return defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, clickedFace.getOpposite());
    }

    // -------------------------------------------------------------------------
    // Placement validation
    // -------------------------------------------------------------------------

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        BlockPos smelterPos = pos.relative(facing.getOpposite());
        BlockState smelterState = level.getBlockState(smelterPos);

        if (!(smelterState.getBlock() instanceof BlockSmelter)) return false;

        Direction smelterFacing = smelterState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        return facing != smelterFacing;
    }

    // -------------------------------------------------------------------------
    // Shape
    // -------------------------------------------------------------------------

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level,
                                  BlockPos pos, CollisionContext ctx) {
        return switch (state.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
            case SOUTH -> SHAPE_SOUTH;
            case WEST  -> SHAPE_WEST;
            case EAST  -> SHAPE_EAST;
            default    -> SHAPE_NORTH;
        };
    }

}
