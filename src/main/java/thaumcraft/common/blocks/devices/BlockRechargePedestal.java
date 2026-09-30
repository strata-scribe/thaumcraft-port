package thaumcraft.common.blocks.devices;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;
import thaumcraft.common.blocks.devices.logic.RechargePedestalInteractionLogic;
import thaumcraft.common.blocks.entities.ThaumcraftBlockEntities;
import thaumcraft.common.tiles.devices.RechargePedestalBlockEntity;

/**
 * Recharge Pedestal block — holds and charges rechargeable items (e.g. wands, gauntlets) using local aura vis.
 */
public class BlockRechargePedestal extends BaseEntityBlock {

    public static final MapCodec<BlockRechargePedestal> CODEC = simpleCodec(BlockRechargePedestal::new);

    private static final VoxelShape SHAPE_BASE = Block.box(0, 0, 0, 16, 4, 16);
    private static final VoxelShape SHAPE_MID = Block.box(2, 4, 2, 14, 8, 14);
    private static final VoxelShape SHAPE_TOP = Block.box(4, 8, 4, 12, 16, 12);
    private static final VoxelShape SHAPE_GEM_NS = Block.box(7, 10, 3, 9, 12, 13);
    private static final VoxelShape SHAPE_GEM_EW = Block.box(3, 10, 7, 13, 12, 9);
    private static final VoxelShape SHAPE = Shapes.or(SHAPE_BASE, SHAPE_MID, SHAPE_TOP, SHAPE_GEM_NS, SHAPE_GEM_EW);

    public BlockRechargePedestal(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<BlockRechargePedestal> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RechargePedestalBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ? null : createTickerHelper(blockEntityType, ThaumcraftBlockEntities.RECHARGE_PEDESTAL.get(), RechargePedestalBlockEntity::serverTick);
    }

    @Override
    protected InteractionResult useItemOn(
            ItemStack stack, BlockState state, Level level,
            BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof RechargePedestalBlockEntity ped)) {
            return InteractionResult.PASS;
        }

        // If player holds a rechargeable item and pedestal is empty, insert 1 item
        if (!stack.isEmpty() && stack.getItem() instanceof IRechargable && !ped.hasItem()) {
            if (level.isClientSide()) return InteractionResult.SUCCESS;
            insertItem(ped, level, pos, stack);
            return InteractionResult.CONSUME;
        }

        // Otherwise defer to useWithoutItem (extracts if occupied, or allows block placement if empty)
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state, Level level,
            BlockPos pos, Player player, BlockHitResult hit) {

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof RechargePedestalBlockEntity ped)) {
            return InteractionResult.PASS;
        }

        // Pedestal has item -> extract it
        if (ped.hasItem()) {
            if (level.isClientSide()) return InteractionResult.SUCCESS;
            extractItem(ped, player, level, pos);
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    public static boolean extractItem(RechargePedestalBlockEntity ped, @Nullable Player player, @Nullable Level level, @Nullable BlockPos pos) {
        if (ped == null || !RechargePedestalInteractionLogic.canExtract(ped.hasItem())) return false;
        if (!RechargePedestalInteractionLogic.canDeliverExtractedItem(player != null, level != null && pos != null)) {
            return false;
        }
        ItemStack extracted = ped.getItem().copy();
        ped.setItem(ItemStack.EMPTY);
        if (player != null) {
            if (!player.getInventory().add(extracted)) {
                if (level != null && pos != null) {
                    Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, extracted);
                }
            }
        } else {
            Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, extracted);
        }
        if (level != null && pos != null) {
            level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.2f,
                    ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.7f + 1.0f) * 1.5f);
        }
        return true;
    }

    public static boolean insertItem(RechargePedestalBlockEntity ped, @Nullable Level level, @Nullable BlockPos pos, ItemStack stack) {
        if (ped == null || stack == null || stack.isEmpty()) return false;
        boolean isRechargeable = stack.getItem() instanceof IRechargable;
        if (!RechargePedestalInteractionLogic.canInsert(ped.hasItem(), isRechargeable, stack.getCount())) {
            return false;
        }
        ItemStack toPlace = stack.copyWithCount(1);
        ped.setItem(toPlace);
        stack.shrink(1);
        if (level != null && pos != null) {
            level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.2f,
                    ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.7f + 1.0f) * 1.6f);
        }
        return true;
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        if (level instanceof Level world && !world.isClientSide()) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof RechargePedestalBlockEntity ped && ped.hasItem()) {
                Containers.dropItemStack(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, ped.getItem());
                ped.setItem(ItemStack.EMPTY);
            }
        }
        super.destroy(level, pos, state);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof RechargePedestalBlockEntity ped && ped.hasItem()) {
            ItemStack stack = ped.getItem();
            if (stack.getItem() instanceof IRechargable rechargable) {
                int max = rechargable.getMaxCharge(stack, null);
                int charge = RechargeHelper.getCharge(stack);
                return RechargePedestalInteractionLogic.calculateComparatorSignal(true, charge, max);
            }
            return 15;
        }
        return 0;
    }
}
