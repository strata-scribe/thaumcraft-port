package thaumcraft.common.blocks.essentia;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.blocks.essentia.logic.TubeConnectionLogic;
import thaumcraft.common.tiles.essentia.TileTube;

/**
 * Essentia Tube block — conduits for transporting liquid essentia between containers and apparatus.
 */
public class BlockTube extends BaseEntityBlock {

    public static final MapCodec<BlockTube> CODEC = simpleCodec(BlockTube::new);

    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");

    public BlockTube(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST, false)
                .setValue(WEST, false)
                .setValue(UP, false)
                .setValue(DOWN, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileTube(pos, state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        return this.defaultBlockState()
                .setValue(NORTH, canConnect(level, pos, Direction.NORTH))
                .setValue(SOUTH, canConnect(level, pos, Direction.SOUTH))
                .setValue(EAST, canConnect(level, pos, Direction.EAST))
                .setValue(WEST, canConnect(level, pos, Direction.WEST))
                .setValue(UP, canConnect(level, pos, Direction.UP))
                .setValue(DOWN, canConnect(level, pos, Direction.DOWN));
    }

    @Override
    protected BlockState updateShape(BlockState state, net.minecraft.world.level.LevelReader level, net.minecraft.world.level.ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, net.minecraft.util.RandomSource random) {
        if (level instanceof LevelAccessor accessor) {
            boolean connected = canConnect(accessor, pos, direction);
            return state.setValue(getPropertyForDirection(direction), connected);
        }
        return state;
    }

    private boolean canConnect(LevelAccessor level, BlockPos pos, Direction dir) {
        BlockEntity self = level.getBlockEntity(pos);
        boolean isOpen = !(self instanceof TileTube tube) || tube.logic.isOpen(dir);
        BlockEntity be = level.getBlockEntity(pos.relative(dir));
        boolean neighborConnectable = (be instanceof IEssentiaTransport transport) && transport.isConnectable(dir.getOpposite());
        return TubeConnectionLogic.canConnect(isOpen, neighborConnectable);
    }

    public static BooleanProperty getPropertyForDirection(Direction dir) {
        return switch (dir) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
            case UP -> UP;
            case DOWN -> DOWN;
        };
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof TileTube tube) {
            Direction face = hit.getDirection();
            boolean newOpen = TubeConnectionLogic.toggleFace(tube.logic.isOpen(face));
            tube.logic.setOpenFace(face, newOpen);
            tube.setChanged();

            if (!newOpen) {
                level.setBlock(pos, state.setValue(getPropertyForDirection(face), false), 3);
            } else {
                level.setBlock(pos, state.setValue(getPropertyForDirection(face), canConnect(level, pos, face)), 3);
            }

            BlockPos neighborPos = pos.relative(face);
            level.neighborChanged(neighborPos, this, null);

            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = Block.box(4.0D, 4.0D, 4.0D, 12.0D, 12.0D, 12.0D);
        if (state == null) {
            return shape;
        }
        if (state.hasProperty(NORTH) && state.getValue(NORTH)) shape = Shapes.or(shape, Block.box(4.0D, 4.0D, 0.0D, 12.0D, 12.0D, 4.0D));
        if (state.hasProperty(SOUTH) && state.getValue(SOUTH)) shape = Shapes.or(shape, Block.box(4.0D, 4.0D, 12.0D, 12.0D, 12.0D, 16.0D));
        if (state.hasProperty(EAST) && state.getValue(EAST)) shape = Shapes.or(shape, Block.box(12.0D, 4.0D, 4.0D, 16.0D, 12.0D, 12.0D));
        if (state.hasProperty(WEST) && state.getValue(WEST)) shape = Shapes.or(shape, Block.box(0.0D, 4.0D, 4.0D, 4.0D, 12.0D, 12.0D));
        if (state.hasProperty(UP) && state.getValue(UP)) shape = Shapes.or(shape, Block.box(4.0D, 12.0D, 4.0D, 12.0D, 16.0D, 12.0D));
        if (state.hasProperty(DOWN) && state.getValue(DOWN)) shape = Shapes.or(shape, Block.box(4.0D, 0.0D, 4.0D, 12.0D, 4.0D, 12.0D));
        return shape;
    }
}
