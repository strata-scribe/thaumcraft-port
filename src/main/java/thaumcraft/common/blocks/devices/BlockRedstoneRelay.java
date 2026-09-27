package thaumcraft.common.blocks.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.redstone.Orientation;
import org.jetbrains.annotations.Nullable;
import thaumcraft.common.blocks.entities.RedstoneRelayBlockEntity;
import com.mojang.serialization.MapCodec;

public class BlockRedstoneRelay extends BaseEntityBlock {
    public static final MapCodec<BlockRedstoneRelay> CODEC = simpleCodec(BlockRedstoneRelay::new);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final IntegerProperty POWER = BlockStateProperties.POWER;

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    public BlockRedstoneRelay(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false).setValue(POWER, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED, POWER);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, orientation, isMoving);
        if (!level.isClientSide()) {
            boolean isPowered = state.getValue(POWERED);
            int powerReceived = level.getBestNeighborSignal(pos);

            if (powerReceived > 0 && !isPowered) {
                // If it wasn't powered but receives power, turn it on and transmit
                level.setBlockAndUpdate(pos, state.setValue(POWERED, true).setValue(POWER, powerReceived));
                transmitPower(level, pos, powerReceived);
            } else if (powerReceived == 0 && isPowered) {
                // If it was powered but receives no power, turn it off and update transmission
                level.setBlockAndUpdate(pos, state.setValue(POWERED, false).setValue(POWER, 0));
                transmitPower(level, pos, 0);
            }
        }
    }

    private void transmitPower(Level level, BlockPos pos, int power) {
        // Transmit power wirelessly across line of sight to another relay
        for (Direction dir : Direction.values()) {
            for (int i = 1; i <= 16; i++) {
                BlockPos targetPos = pos.relative(dir, i);
                BlockState targetState = level.getBlockState(targetPos);

                if (targetState.getBlock() instanceof BlockRedstoneRelay) {
                    if (targetState.getValue(POWER) != power) {
                        level.setBlockAndUpdate(targetPos, targetState.setValue(POWER, power).setValue(POWERED, power > 0));
                    }
                    break;
                } else if (!targetState.isAir() && targetState.isRedstoneConductor(level, targetPos)) {
                    // Line of sight blocked
                    break;
                }
            }
        }
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(POWER);
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(POWER);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public net.minecraft.world.level.block.entity.BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RedstoneRelayBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends net.minecraft.world.level.block.entity.BlockEntity> net.minecraft.world.level.block.entity.BlockEntityTicker<T> getTicker(Level level, BlockState state, net.minecraft.world.level.block.entity.BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, thaumcraft.common.blocks.entities.ThaumcraftBlockEntities.REDSTONE_RELAY.get(), RedstoneRelayBlockEntity::tick);
    }

}
