package thaumcraft.common.blocks.research;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import thaumcraft.common.tiles.research.TileResearchTable;
import javax.annotation.Nullable;

public class BlockResearchTable extends HorizontalDirectionalBlock implements EntityBlock {

    public static final com.mojang.serialization.MapCodec<BlockResearchTable> CODEC = simpleCodec(BlockResearchTable::new);

    @Override
    public com.mojang.serialization.MapCodec<BlockResearchTable> codec() {
        return CODEC;
    }

    public BlockResearchTable(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileResearchTable(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        net.minecraft.world.MenuProvider menuProvider = state.getMenuProvider(level, pos);
        if (menuProvider != null) {
            if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                serverPlayer.openMenu(menuProvider, buf -> buf.writeBlockPos(pos));
            } else {
                player.openMenu(menuProvider);
            }
        }
        return InteractionResult.CONSUME;
    }

    @Nullable
    @Override
    protected net.minecraft.world.MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        return be instanceof net.minecraft.world.MenuProvider menuProvider ? menuProvider : null;
    }
}
