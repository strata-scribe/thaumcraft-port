package thaumcraft.common.blocks.crafting;

import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
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
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumcraft.common.blocks.crafting.logic.InfusionMatrixLogic;
import thaumcraft.common.blocks.entities.ThaumcraftBlockEntities;
import thaumcraft.common.tiles.crafting.InfusionMatrixBlockEntity;

/**
 * The Infusion Matrix block — the floating runic cube at the centre of the
 * Infusion Altar.
 *
 * <p>Custom shape: 3..13 on all axes (10px cube, centred).</p>
 * <p>Right-click triggers crafting via the {@link InfusionMatrixBlockEntity}.</p>
 *
 * <p>MC 26.1.2 / NeoForge 26.2 port of
 * {@code thaumcraft.common.blocks.crafting.BlockInfusionMatrix}.</p>
 */
public class BlockInfusionMatrix extends BaseEntityBlock {

    public static final MapCodec<BlockInfusionMatrix> CODEC = simpleCodec(BlockInfusionMatrix::new);

    // -------------------------------------------------------------------------
    // VoxelShape
    // -------------------------------------------------------------------------

    private static final VoxelShape SHAPE = Block.box(3, 3, 3, 13, 13, 13);

    // -------------------------------------------------------------------------
    // Constructor & Codec
    // -------------------------------------------------------------------------

    public BlockInfusionMatrix(BlockBehaviour.Properties properties) {
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
    // EntityBlock
    // -------------------------------------------------------------------------

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new InfusionMatrixBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return createTickerHelper(type,
                ThaumcraftBlockEntities.INFUSION_MATRIX.get(),
                InfusionMatrixBlockEntity::tick);
    }

    // -------------------------------------------------------------------------
    // Shape
    // -------------------------------------------------------------------------

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level,
                                  BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    // -------------------------------------------------------------------------
    // Interaction — right-click starts crafting
    // -------------------------------------------------------------------------

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state, Level level,
            BlockPos pos, Player player, BlockHitResult hit) {

        if (level.isClientSide()) return InteractionResult.SUCCESS;

        boolean isSneaking = player != null && player.isShiftKeyDown();
        if (!InfusionMatrixLogic.canActivateMatrix(isSneaking)) {
            return InteractionResult.PASS;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof InfusionMatrixBlockEntity matrix) {
            if (!matrix.active && matrix.validLocation(level, pos)) {
                // Activate the matrix
                matrix.active = true;
                level.playSound(null, pos, net.minecraft.sounds.SoundEvents.ENCHANTMENT_TABLE_USE,
                        net.minecraft.sounds.SoundSource.BLOCKS, 0.5f, 1.0f);
                matrix.setChanged();
            } else if (matrix.active && !matrix.crafting) {
                // Start crafting cycle
                matrix.craftingStart(player);
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected InteractionResult useItemOn(
            ItemStack stack, BlockState state, Level level,
            BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        boolean isSneaking = player != null && player.isShiftKeyDown();
        if (!InfusionMatrixLogic.canActivateMatrix(isSneaking)) {
            return InteractionResult.PASS;
        }
        // Delegate all interactions through the empty-hand path
        return InteractionResult.TRY_WITH_EMPTY_HAND;
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
        if (be instanceof InfusionMatrixBlockEntity matrix) {
            return InfusionMatrixLogic.calculateComparatorSignal(matrix.isCrafting());
        }
        return 0;
    }
}
