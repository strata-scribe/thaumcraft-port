package thaumcraft.common.blocks.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.redstone.Orientation;
import org.jetbrains.annotations.Nullable;

public class BlockRedstoneRelay extends Block implements net.minecraft.world.level.block.EntityBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final IntegerProperty POWER = BlockStateProperties.POWER;

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
        thaumcraft.common.lib.RedstoneRelayLogic.transmitPower(new thaumcraft.common.lib.RedstoneRelayLogic.IEnvironment() {
            @Override
            public boolean isRedstoneRelay(int x, int y, int z) {
                return level.getBlockState(new BlockPos(x, y, z)).getBlock() instanceof BlockRedstoneRelay;
            }

            @Override
            public int getPower(int x, int y, int z) {
                return level.getBlockState(new BlockPos(x, y, z)).getValue(POWER);
            }

            @Override
            public void setPower(int x, int y, int z, int p) {
                BlockPos pPos = new BlockPos(x, y, z);
                BlockState targetState = level.getBlockState(pPos);
                level.setBlockAndUpdate(pPos, targetState.setValue(POWER, p).setValue(POWERED, p > 0));
            }

            @Override
            public boolean isLineOfSightBlocked(int x, int y, int z) {
                BlockPos pPos = new BlockPos(x, y, z);
                BlockState targetState = level.getBlockState(pPos);
                return !targetState.isAir() && targetState.isRedstoneConductor(level, pPos);
            }
        }, pos.getX(), pos.getY(), pos.getZ(), power);
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

    @Nullable
    @Override
    public net.minecraft.world.level.block.entity.BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new thaumcraft.common.tiles.devices.RedstoneRelayBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends net.minecraft.world.level.block.entity.BlockEntity> net.minecraft.world.level.block.entity.BlockEntityTicker<T> getTicker(Level level, BlockState state, net.minecraft.world.level.block.entity.BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, thaumcraft.common.blocks.entities.ThaumcraftBlockEntities.REDSTONE_RELAY.get(), thaumcraft.common.tiles.devices.RedstoneRelayBlockEntity::tick);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    protected static <E extends net.minecraft.world.level.block.entity.BlockEntity, A extends net.minecraft.world.level.block.entity.BlockEntity> net.minecraft.world.level.block.entity.BlockEntityTicker<A> createTickerHelper(net.minecraft.world.level.block.entity.BlockEntityType<A> type, net.minecraft.world.level.block.entity.BlockEntityType<E> targetType, net.minecraft.world.level.block.entity.BlockEntityTicker<? super E> ticker) {
        return targetType == type ? (net.minecraft.world.level.block.entity.BlockEntityTicker<A>) ticker : null;
    }

}
