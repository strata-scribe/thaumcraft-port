package thaumcraft.common.blocks.essentia;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.items.ThaumcraftItems;
import thaumcraft.common.tiles.essentia.AlembicBlockEntity;

/**
 * Distillation Alembic block — stacks on top of a smelter or another alembic
 * to receive boiled essentia.
 *
 * <h3>VoxelShape</h3>
 * Cylindrical vessel: 2..14 x, 0..16 y, 2..14 z (12×16×12 px).
 *
 * <h3>Interactions</h3>
 * <ul>
 *   <li>Empty phial → drain 8 essentia into filled phial.</li>
 *   <li>Filled phial → deposit 8 essentia into alembic.</li>
 *   <li>Label → set aspect filter.</li>
 *   <li>Sneak + empty hand on label face → remove label, drop it.</li>
 *   <li>Sneak + empty hand elsewhere → vent essentia as flux, clear contents.</li>
 * </ul>
 *
 * <h3>Comparator</h3>
 * {@code (amount * 14 / maxAmount) + (amount > 0 ? 1 : 0)}
 *
 * <p>MC 1.21.4 / NeoForge 26.2 port of
 * {@code thaumcraft.common.blocks.essentia.BlockAlembic}.
 */
public class BlockAlembic extends Block implements EntityBlock {

    // -------------------------------------------------------------------------
    // VoxelShape
    // -------------------------------------------------------------------------

    /** Cylindrical alembic vessel: 12 px wide × 16 px tall. */
    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 16, 14);

    /** Amount of essentia per phial interaction. */
    public static final int PHIAL_AMOUNT = 8;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public BlockAlembic(BlockBehaviour.Properties properties) {
        super(properties);
    }

    // -------------------------------------------------------------------------
    // EntityBlock
    // -------------------------------------------------------------------------

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AlembicBlockEntity(pos, state);
    }

    // -------------------------------------------------------------------------
    // Shape
    // -------------------------------------------------------------------------

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level,
                                  BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level,
                                           BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    // -------------------------------------------------------------------------
    // Interaction — item in hand
    // -------------------------------------------------------------------------

    @Override
    protected InteractionResult useItemOn(
            ItemStack stack, BlockState state, Level level,
            BlockPos pos, Player player,
            net.minecraft.world.InteractionHand hand, BlockHitResult hit) {

        if (level.isClientSide()) return InteractionResult.SUCCESS;

        AlembicBlockEntity alembic = getAlembic(level, pos);
        if (alembic == null) return InteractionResult.PASS;

        Direction hitFace = hit.getDirection();

        // --- Label removal: sneak + click on labeled face ---
        if (player.isShiftKeyDown() && alembic.getAspectFilter() != null
                && hitFace.ordinal() == alembic.getFacing()) {
            alembic.setAspectFilter(null);
            alembic.setFacing(Direction.DOWN.ordinal());
            alembic.setChanged();
            // Drop label item
            level.addFreshEntity(new ItemEntity(level,
                    pos.getX() + 0.5 + hitFace.getStepX() / 3.0,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5 + hitFace.getStepZ() / 3.0,
                    new ItemStack(ThaumcraftItems.label.get())));
            level.playSound(null, pos, SoundEvents.BOOK_PAGE_TURN,
                    SoundSource.BLOCKS, 1.0f, 1.0f);
            return InteractionResult.CONSUME;
        }

        net.minecraft.world.item.Item heldItem = stack.getItem();

        // --- Phial interaction ---
        if (heldItem == ThaumcraftItems.phial.get()) {
            // Check if filled phial
            if (heldItem instanceof IEssentiaContainerItem phialContainer) {
                AspectList phialAspects = phialContainer.getAspects(stack);
                if (phialAspects != null && phialAspects.size() > 0) {
                    // Filled phial → deposit
                    Aspect aspect = phialAspects.getAspects()[0];
                    int amt = phialAspects.getAmount(aspect);
                    if (alembic.addToContainer(aspect, amt) == 0) {
                        stack.shrink(1);
                        giveOrDrop(level, pos, player,
                                new ItemStack(ThaumcraftItems.phial.get()));
                        level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY,
                                SoundSource.BLOCKS, 0.5f, 1.0f);
                        return InteractionResult.CONSUME;
                    }
                } else {
                    // Empty phial → drain
                    Aspect stored = alembic.getStoredAspect();
                    if (stored != null && alembic.getAmount() >= PHIAL_AMOUNT) {
                        alembic.takeFromContainer(stored, PHIAL_AMOUNT);
                        ItemStack filled = new ItemStack(ThaumcraftItems.phial.get());
                        phialContainer.setAspects(filled,
                                new AspectList().add(stored, PHIAL_AMOUNT));
                        stack.shrink(1);
                        giveOrDrop(level, pos, player, filled);
                        level.playSound(null, pos, SoundEvents.BOTTLE_FILL,
                                SoundSource.BLOCKS, 0.5f, 1.0f);
                        return InteractionResult.CONSUME;
                    }
                }
            }
        }

        // --- Label application ---
        if (heldItem == ThaumcraftItems.label.get()
                && heldItem instanceof IEssentiaContainerItem labelContainer) {
            if (alembic.getAspectFilter() == null && hitFace.getAxis().isHorizontal()) {
                AspectList labelAspects = labelContainer.getAspects(stack);
                Aspect labelAspect = null;
                if (labelAspects != null && labelAspects.size() > 0) {
                    labelAspect = labelAspects.getAspects()[0];
                }
                // Determine filter aspect: from label, or from current contents
                Aspect filterAspect = null;
                if (alembic.getAmount() == 0 && labelAspect != null) {
                    filterAspect = labelAspect;
                } else if (alembic.getAmount() > 0) {
                    filterAspect = alembic.getStoredAspect();
                }
                if (filterAspect != null) {
                    alembic.setAspectFilter(filterAspect);
                    alembic.setFacing(hitFace.ordinal());
                    alembic.setChanged();
                    stack.shrink(1);
                    level.playSound(null, pos, SoundEvents.BOOK_PAGE_TURN,
                            SoundSource.BLOCKS, 1.0f, 1.0f);
                    return InteractionResult.CONSUME;
                }
            }
        }

        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    // -------------------------------------------------------------------------
    // Interaction — empty hand
    // -------------------------------------------------------------------------

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state, Level level, BlockPos pos,
            Player player, BlockHitResult hit) {

        if (level.isClientSide()) return InteractionResult.SUCCESS;

        AlembicBlockEntity alembic = getAlembic(level, pos);
        if (alembic == null) return InteractionResult.PASS;

        // Sneak + empty hand → vent essentia as flux and clear
        if (player.isShiftKeyDown()) {
            if (alembic.getAmount() > 0) {
                AuraHelper.polluteAura(level, pos,
                        (float) alembic.getAmount(), true);
            }
            alembic.clearEssentia();
            level.playSound(null, pos, SoundEvents.BOTTLE_FILL,
                    SoundSource.BLOCKS, 0.5f, 1.0f);
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    // -------------------------------------------------------------------------
    // Comparator — (amount * 14 / maxAmount) + (amount > 0 ? 1 : 0)
    // -------------------------------------------------------------------------

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level,
                                        BlockPos pos, Direction direction) {
        AlembicBlockEntity alembic = getAlembic(level, pos);
        if (alembic == null || alembic.getAmount() <= 0) return 0;
        float ratio = alembic.getAmount() / (float) alembic.getMaxAmount();
        return Mth.floor(ratio * 14.0f) + (alembic.getAmount() > 0 ? 1 : 0);
    }

    // -------------------------------------------------------------------------
    // Utility
    // -------------------------------------------------------------------------

    @Nullable
    private static AlembicBlockEntity getAlembic(Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        return be instanceof AlembicBlockEntity a ? a : null;
    }

    private static void giveOrDrop(Level level, BlockPos pos, Player player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            level.addFreshEntity(new ItemEntity(level,
                    pos.getX() + 0.5, pos.getY() + 0.75, pos.getZ() + 0.5, stack));
        }
    }
}
