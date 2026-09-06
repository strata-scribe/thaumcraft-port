package thaumcraft.common.blocks.crafting;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
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
public class BlockInfusionMatrix extends Block implements EntityBlock {

    // -------------------------------------------------------------------------
    // VoxelShape
    // -------------------------------------------------------------------------

    private static final VoxelShape SHAPE = Block.box(3, 3, 3, 13, 13, 13);

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public BlockInfusionMatrix(BlockBehaviour.Properties properties) {
        super(properties);
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

    @SuppressWarnings("unchecked")
    private static <E extends BlockEntity, A extends BlockEntity>
    BlockEntityTicker<A> createTickerHelper(
            BlockEntityType<A> actual,
            BlockEntityType<E> expected,
            BlockEntityTicker<? super E> ticker) {
        return expected == actual ? (BlockEntityTicker<A>) ticker : null;
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
            net.minecraft.world.InteractionHand hand, BlockHitResult hit) {
        // Delegate all interactions through the empty-hand path
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }
}
