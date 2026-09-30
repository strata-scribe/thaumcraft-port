package thaumcraft.common.blocks.essentia;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
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
import thaumcraft.common.blocks.essentia.logic.AlembicInteractionLogic;
import thaumcraft.common.tiles.essentia.AlembicBlockEntity;

import javax.annotation.Nullable;

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
 * Delegated to {@link AlembicInteractionLogic#calculateComparatorSignal(int, int)}.
 *
 * <p>MC 1.21.4 / NeoForge 26.2 port of
 * {@code thaumcraft.common.blocks.essentia.BlockAlembic}.
 */
public class BlockAlembic extends BaseEntityBlock {

    public static final MapCodec<BlockAlembic> CODEC = simpleCodec(BlockAlembic::new);

    // -------------------------------------------------------------------------
    // VoxelShape
    // -------------------------------------------------------------------------

    /** Cylindrical alembic vessel: 12 px wide × 16 px tall. */
    public static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 16, 14);

    /** Amount of essentia per phial interaction. */
    public static final int PHIAL_AMOUNT = 8;

    // -------------------------------------------------------------------------
    // Constructor & Codec
    // -------------------------------------------------------------------------

    public BlockAlembic(BlockBehaviour.Properties properties) {
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
    // Placement validation
    // -------------------------------------------------------------------------

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (level == null || pos == null) return false;
        BlockState below = level.getBlockState(pos.below());
        String belowId = (below != null && below.getBlock() != null) ? String.valueOf(below.getBlock()) : null;
        return AlembicInteractionLogic.canSurviveOn(belowId);
    }

    // -------------------------------------------------------------------------
    // Removal & Inventory Drop
    // -------------------------------------------------------------------------

    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state != null && (newState == null || !state.is(newState.getBlock()))) {
            AlembicBlockEntity alembic = getAlembic(level, pos);
            if (alembic != null) {
                if (alembic.getAmount() > 0) {
                    AuraHelper.polluteAura(level, pos, (float) alembic.getAmount(), true);
                    alembic.clearEssentia();
                }
                if (alembic.getFacing() != Direction.DOWN.ordinal() && alembic.getAspectFilter() != null) {
                    Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            new ItemStack(ThaumcraftItems.label.get()));
                    alembic.setAspectFilter(null);
                    alembic.setFacing(Direction.DOWN.ordinal());
                }
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        if (level instanceof Level world && !world.isClientSide()) {
            AlembicBlockEntity alembic = getAlembic(world, pos);
            if (alembic != null) {
                if (alembic.getAmount() > 0) {
                    AuraHelper.polluteAura(world, pos, (float) alembic.getAmount(), true);
                    alembic.clearEssentia();
                }
                if (alembic.getFacing() != Direction.DOWN.ordinal() && alembic.getAspectFilter() != null) {
                    Containers.dropItemStack(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            new ItemStack(ThaumcraftItems.label.get()));
                    alembic.setAspectFilter(null);
                    alembic.setFacing(Direction.DOWN.ordinal());
                }
            }
        }
        super.destroy(level, pos, state);
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean isMoving) {
        Containers.updateNeighboursAfterDestroy(state, level, pos);
    }

    // -------------------------------------------------------------------------
    // Interaction — item in hand
    // -------------------------------------------------------------------------

    @Override
    protected InteractionResult useItemOn(
            ItemStack stack, BlockState state, Level level,
            BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {

        if (level.isClientSide()) return InteractionResult.SUCCESS;

        AlembicBlockEntity alembic = getAlembic(level, pos);
        if (alembic == null) return InteractionResult.PASS;

        Direction hitFace = hit.getDirection();
        boolean isSneaking = player != null && player.isShiftKeyDown();

        // --- Label removal: sneak + click on labeled face ---
        if (AlembicInteractionLogic.canRemoveLabel(isSneaking, alembic.getAspectFilter() != null,
                hitFace.ordinal(), alembic.getFacing())) {
            alembic.setAspectFilter(null);
            alembic.setFacing(Direction.DOWN.ordinal());
            alembic.setChanged();
            // Drop label item
            Containers.dropItemStack(level,
                    pos.getX() + 0.5 + hitFace.getStepX() / 3.0,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5 + hitFace.getStepZ() / 3.0,
                    new ItemStack(ThaumcraftItems.label.get()));
            level.playSound(null, pos, SoundEvents.BOOK_PAGE_TURN,
                    SoundSource.BLOCKS, 1.0f, 1.0f);
            return InteractionResult.CONSUME;
        }

        Item heldItem = stack.getItem();

        // --- Phial interaction ---
        if (heldItem == ThaumcraftItems.phial.get()) {
            if (heldItem instanceof IEssentiaContainerItem phialContainer) {
                AspectList phialAspects = phialContainer.getAspects(stack);
                boolean isFilled = phialAspects != null && phialAspects.size() > 0;
                boolean isEmpty = !isFilled;

                if (isFilled) {
                    Aspect aspect = phialAspects.getAspects()[0];
                    int amt = phialAspects.getAmount(aspect);
                    if (AlembicInteractionLogic.canDepositPhial(true, alembic.getAmount(), alembic.getMaxAmount(), amt)) {
                        if (alembic.addToContainer(aspect, amt) == 0) {
                            if (player == null || !player.getAbilities().instabuild) {
                                stack.shrink(1);
                            }
                            giveOrDrop(level, pos, player,
                                    new ItemStack(ThaumcraftItems.phial.get()));
                            level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY,
                                    SoundSource.BLOCKS, 0.5f, 1.0f);
                            return InteractionResult.CONSUME;
                        }
                    }
                } else if (AlembicInteractionLogic.canDrainPhial(isEmpty, alembic.getAmount(), PHIAL_AMOUNT)) {
                    Aspect stored = alembic.getStoredAspect();
                    if (stored != null) {
                        alembic.takeFromContainer(stored, PHIAL_AMOUNT);
                        ItemStack filled = new ItemStack(ThaumcraftItems.phial.get());
                        phialContainer.setAspects(filled,
                                new AspectList().add(stored, PHIAL_AMOUNT));
                        if (player == null || !player.getAbilities().instabuild) {
                            stack.shrink(1);
                        }
                        giveOrDrop(level, pos, player, filled);
                        level.playSound(null, pos, SoundEvents.BOTTLE_FILL,
                                SoundSource.BLOCKS, 0.5f, 1.0f);
                        return InteractionResult.CONSUME;
                    }
                }
            }
        }

        // --- Label application ---
        boolean isLabel = heldItem == ThaumcraftItems.label.get();
        if (AlembicInteractionLogic.canApplyLabel(alembic.getAspectFilter() != null, hitFace.getAxis().isHorizontal(), isLabel)) {
            if (heldItem instanceof IEssentiaContainerItem labelContainer) {
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
                    if (player == null || !player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
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

        boolean isSneaking = player != null && player.isShiftKeyDown();

        // Sneak + empty hand → vent essentia as flux and clear
        if (AlembicInteractionLogic.canVentEssentia(isSneaking, alembic.getAmount())) {
            AuraHelper.polluteAura(level, pos,
                    (float) alembic.getAmount(), true);
            alembic.clearEssentia();
            level.playSound(null, pos, SoundEvents.BOTTLE_FILL,
                    SoundSource.BLOCKS, 0.5f, 1.0f);
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    // -------------------------------------------------------------------------
    // Comparator
    // -------------------------------------------------------------------------

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level,
                                        BlockPos pos, Direction direction) {
        AlembicBlockEntity alembic = getAlembic(level, pos);
        if (alembic == null) return 0;
        return AlembicInteractionLogic.calculateComparatorSignal(alembic.getAmount(), alembic.getMaxAmount());
    }

    // -------------------------------------------------------------------------
    // Utility
    // -------------------------------------------------------------------------

    @Nullable
    private static AlembicBlockEntity getAlembic(Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        return be instanceof AlembicBlockEntity a ? a : null;
    }

    private static void giveOrDrop(Level level, BlockPos pos, @Nullable Player player, ItemStack stack) {
        if (player == null || !player.getInventory().add(stack)) {
            Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.75, pos.getZ() + 0.5, stack);
        }
    }
}
