package thaumcraft.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import thaumcraft.common.tiles.ArcaneWorkbenchBlockEntity;

import javax.annotation.Nullable;

/**
 * The Arcane Workbench block — an EntityBlock that hosts an ArcaneWorkbenchBlockEntity
 * and opens the arcane crafting menu when right-clicked.
 */
public class BlockArcaneWorkbench extends Block implements EntityBlock {

    public BlockArcaneWorkbench(BlockBehaviour.Properties properties) {
        super(properties);
    }

    // -------------------------------------------------------------------------
    // EntityBlock
    // -------------------------------------------------------------------------

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ArcaneWorkbenchBlockEntity(pos, state);
    }

    // -------------------------------------------------------------------------
    // MenuProvider lookup
    // -------------------------------------------------------------------------

    @Nullable
    @Override
    protected net.minecraft.world.MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        return be instanceof net.minecraft.world.MenuProvider menuProvider ? menuProvider : null;
    }

    // -------------------------------------------------------------------------
    // Player interaction — handles right-click with empty hand or holding item
    // -------------------------------------------------------------------------

    @Override
    protected InteractionResult useItemOn(net.minecraft.world.item.ItemStack stack, BlockState state,
                                          Level level, BlockPos pos, Player player,
                                          net.minecraft.world.InteractionHand hand, BlockHitResult hit) {
        if (player.isShiftKeyDown()) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        return useWithoutItem(state, level, pos, player, hit);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hit) {
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
}

