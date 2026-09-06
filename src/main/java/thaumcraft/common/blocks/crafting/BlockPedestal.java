package thaumcraft.common.blocks.crafting;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumcraft.api.crafting.IInfusionStabiliserExt;
import thaumcraft.common.tiles.crafting.PedestalBlockEntity;

/**
 * The Pedestal block — holds one item for the Infusion Altar and acts as
 * an infusion stabiliser.
 *
 * <h3>Shape</h3>
 * <ul>
 *   <li>Base slab (0..16, 0..4, 0..16)</li>
 *   <li>Stem    (3..13, 4..12, 3..13)</li>
 *   <li>Dish    (1..15, 12..16, 1..15)</li>
 * </ul>
 *
 * <p>MC 26.1.2 / NeoForge 26.2 port of
 * {@code thaumcraft.common.blocks.devices.BlockPedestal}.</p>
 */
public class BlockPedestal extends Block implements EntityBlock, IInfusionStabiliserExt {

    // -------------------------------------------------------------------------
    // VoxelShape
    // -------------------------------------------------------------------------

    private static final VoxelShape SHAPE_BASE = Block.box(0, 0, 0, 16, 4, 16);
    private static final VoxelShape SHAPE_STEM = Block.box(3, 4, 3, 13, 12, 13);
    private static final VoxelShape SHAPE_DISH = Block.box(1, 12, 1, 15, 16, 15);
    private static final VoxelShape SHAPE = Shapes.or(SHAPE_BASE, SHAPE_STEM, SHAPE_DISH);

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public BlockPedestal(BlockBehaviour.Properties properties) {
        super(properties);
    }

    // -------------------------------------------------------------------------
    // EntityBlock
    // -------------------------------------------------------------------------

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PedestalBlockEntity(pos, state);
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
    // Player interaction — item in hand → place item
    // -------------------------------------------------------------------------

    @Override
    protected InteractionResult useItemOn(
            ItemStack stack, BlockState state, Level level,
            BlockPos pos, Player player,
            net.minecraft.world.InteractionHand hand, BlockHitResult hit) {

        if (level.isClientSide()) return InteractionResult.SUCCESS;

        PedestalBlockEntity ped = getPedestal(level, pos);
        if (ped == null) return InteractionResult.PASS;

        // Player has item and pedestal is empty → place 1 item
        if (!stack.isEmpty() && !ped.hasItem()) {
            ItemStack toPlace = stack.copyWithCount(1);
            ped.setItem(toPlace);
            stack.shrink(1);
            level.playSound(null, pos, SoundEvents.ITEM_PICKUP,
                    SoundSource.BLOCKS, 0.2f,
                    ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.7f + 1.0f) * 1.6f);
            return InteractionResult.CONSUME;
        }

        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    // -------------------------------------------------------------------------
    // Player interaction — empty hand → extract item
    // -------------------------------------------------------------------------

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state, Level level,
            BlockPos pos, Player player, BlockHitResult hit) {

        if (level.isClientSide()) return InteractionResult.SUCCESS;

        PedestalBlockEntity ped = getPedestal(level, pos);
        if (ped == null) return InteractionResult.PASS;

        if (ped.hasItem()) {
            ItemStack extracted = ped.getItem().copy();
            ped.setItem(ItemStack.EMPTY);
            // Give to player or spawn in world
            if (!player.getInventory().add(extracted)) {
                Containers.dropItemStack(level,
                        pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                        extracted);
            }
            level.playSound(null, pos, SoundEvents.ITEM_PICKUP,
                    SoundSource.BLOCKS, 0.2f,
                    ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.7f + 1.0f) * 1.5f);
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    // -------------------------------------------------------------------------
    // Block removal — drop stored item
    // -------------------------------------------------------------------------

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        if (level instanceof Level world && !world.isClientSide()) {
            PedestalBlockEntity ped = getPedestal(world, pos);
            if (ped != null && ped.hasItem()) {
                Containers.dropItemStack(world,
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        ped.getItem());
                ped.setItem(ItemStack.EMPTY);
            }
        }
        super.destroy(level, pos, state);
    }

    // -------------------------------------------------------------------------
    // IInfusionStabiliserExt
    // -------------------------------------------------------------------------

    @Override
    public boolean canStabaliseInfusion(Level world, BlockPos pos) {
        return true;
    }

    @Override
    public float getStabilizationAmount(Level world, BlockPos pos) {
        // Only eldritch pedestals provide stabilization by default
        return 0.0f;
    }

    @Override
    public boolean hasSymmetryPenalty(Level world, BlockPos pos1, BlockPos pos2) {
        PedestalBlockEntity ped1 = getPedestal(world, pos1);
        PedestalBlockEntity ped2 = getPedestal(world, pos2);
        if (ped1 != null && ped2 != null) {
            return ped1.hasItem() != ped2.hasItem();
        }
        return false;
    }

    @Override
    public float getSymmetryPenalty(Level world, BlockPos pos) {
        return 0.1f;
    }

    // -------------------------------------------------------------------------
    // Utility
    // -------------------------------------------------------------------------

    @Nullable
    private static PedestalBlockEntity getPedestal(Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        return be instanceof PedestalBlockEntity p ? p : null;
    }
}
