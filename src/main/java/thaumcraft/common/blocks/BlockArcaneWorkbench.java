package thaumcraft.common.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import thaumcraft.common.tiles.ArcaneWorkbenchBlockEntity;
import thaumcraft.common.tiles.logic.ArcaneWorkbenchLogic;

import javax.annotation.Nullable;

/**
 * The Arcane Workbench block — a BaseEntityBlock hosting an ArcaneWorkbenchBlockEntity,
 * opening the arcane crafting menu on interaction and exposing redstone comparator signals.
 */
public class BlockArcaneWorkbench extends BaseEntityBlock {

    public static final MapCodec<BlockArcaneWorkbench> CODEC = simpleCodec(BlockArcaneWorkbench::new);

    public BlockArcaneWorkbench(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    // -------------------------------------------------------------------------
    // EntityBlock / BaseEntityBlock
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
    protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        return be instanceof MenuProvider menuProvider ? menuProvider : null;
    }

    // -------------------------------------------------------------------------
    // Player interaction — handles right-click with empty hand or holding item
    // -------------------------------------------------------------------------

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state,
                                          Level level, BlockPos pos, Player player,
                                          InteractionHand hand, BlockHitResult hit) {
        if (!ArcaneWorkbenchLogic.canOpenWorkbench(player.isShiftKeyDown())) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        return useWithoutItem(state, level, pos, player, hit);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hit) {
        if (!ArcaneWorkbenchLogic.canOpenWorkbench(player.isShiftKeyDown())) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        MenuProvider menuProvider = state.getMenuProvider(level, pos);
        if (menuProvider != null) {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.openMenu(menuProvider, buf -> buf.writeBlockPos(pos));
            } else {
                player.openMenu(menuProvider);
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    // -------------------------------------------------------------------------
    // Block removal & Item dropping
    // -------------------------------------------------------------------------

    /**
     * Drops container contents when the block is replaced by another block type,
     * and updates neighbouring redstone comparator listeners.
     */
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state != null && (newState == null || !state.is(newState.getBlock()))) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ArcaneWorkbenchBlockEntity workbench) {
                Containers.dropContents(level, pos, workbench.items);
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        if (level instanceof Level world && !world.isClientSide()) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof ArcaneWorkbenchBlockEntity workbench) {
                Containers.dropContents(world, pos, workbench.items);
            }
        }
        super.destroy(level, pos, state);
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean isMoving) {
        Containers.updateNeighboursAfterDestroy(state, level, pos);
    }

    // -------------------------------------------------------------------------
    // Redstone comparator output
    // -------------------------------------------------------------------------

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ArcaneWorkbenchBlockEntity workbench) {
            int totalSlots = workbench.items.size();
            int filledSlots = 0;
            double totalFillRatio = 0.0;
            for (ItemStack stack : workbench.items) {
                if (!stack.isEmpty()) {
                    filledSlots++;
                    int maxStack = Math.max(1, stack.getMaxStackSize());
                    totalFillRatio += (double) stack.getCount() / (double) maxStack;
                }
            }
            return ArcaneWorkbenchLogic.calculateComparatorSignal(totalSlots, filledSlots, totalFillRatio);
        }
        return 0;
    }
}
