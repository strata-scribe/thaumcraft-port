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
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.items.ThaumcraftItems;
import thaumcraft.common.blocks.essentia.logic.JarInteractionLogic;
import thaumcraft.common.tiles.essentia.JarBlockEntity;
import thaumcraft.common.tiles.essentia.JarLogic;

import javax.annotation.Nullable;

/**
 * Warded Jar block — a single-aspect essentia storage vessel.
 *
 * <h3>VoxelShape (px = 1/16 block)</h3>
 * <ul>
 *   <li>Body: x=3..13, y=0..12, z=3..13</li>
 *   <li>Neck: x=5..11, y=12..14, z=5..11</li>
 *   <li>Lid:  x=4..12, y=14..16, z=4..12</li>
 * </ul>
 *
 * <h3>Interaction</h3>
 * <ul>
 *   <li>Empty phial → drain {@value #PHIAL_AMOUNT} essentia; give back filled phial.</li>
 *   <li>Filled phial → pour {@value #PHIAL_AMOUNT} essentia into jar.</li>
 *   <li>Label item → apply aspect filter from label's contained aspect or current jar aspect.</li>
 *   <li>Sneak + empty hand → clear aspect filter and return label item.</li>
 * </ul>
 *
 * <p>MC 1.21.4 / NeoForge 26.2 port of
 * {@code thaumcraft.common.blocks.essentia.BlockJar}.
 */
public class BlockJar extends BaseEntityBlock {

    public static final MapCodec<BlockJar> CODEC = simpleCodec(BlockJar::new);

    // -------------------------------------------------------------------------
    // VoxelShape definitions — coordinates in 1/16-block pixels
    // -------------------------------------------------------------------------

    /** Jar body: 10×12×10 px column */
    public static final VoxelShape SHAPE_BODY =
            Block.box(3, 0, 3, 13, 12, 13);

    /** Narrow neck: 6×2×6 px */
    public static final VoxelShape SHAPE_NECK =
            Block.box(5, 12, 5, 11, 14, 11);

    /** Lid cap: 8×2×8 px */
    public static final VoxelShape SHAPE_LID =
            Block.box(4, 14, 4, 12, 16, 12);

    /** Union of body + neck + lid. */
    public static final VoxelShape SHAPE =
            Shapes.or(SHAPE_BODY, SHAPE_NECK, SHAPE_LID);

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    /** Amount of essentia transferred per phial interaction. */
    public static final int PHIAL_AMOUNT = 10;

    // -------------------------------------------------------------------------
    // Constructor & Codec
    // -------------------------------------------------------------------------

    public BlockJar(BlockBehaviour.Properties properties) {
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
        return new JarBlockEntity(pos, state);
    }

    // -------------------------------------------------------------------------
    // Shape
    // -------------------------------------------------------------------------

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level,
                                  BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level,
                                           BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    // -------------------------------------------------------------------------
    // Block removal & Item dropping
    // -------------------------------------------------------------------------

    /**
     * Drops label item if filtered when jar block is replaced,
     * and updates neighbouring redstone comparator listeners.
     */
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state != null && (newState == null || !state.is(newState.getBlock()))) {
            JarBlockEntity jar = getJar(level, pos);
            if (jar != null) {
                if (jar.logic.getAspectFilter() != null) {
                    Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            new ItemStack(ThaumcraftItems.label.get()));
                    jar.logic.setAspectFilter(null);
                }
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        if (level instanceof Level world && !world.isClientSide()) {
            JarBlockEntity jar = getJar(world, pos);
            if (jar != null && jar.logic.getAspectFilter() != null) {
                Containers.dropItemStack(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        new ItemStack(ThaumcraftItems.label.get()));
                jar.logic.setAspectFilter(null);
            }
        }
        super.destroy(level, pos, state);
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean isMoving) {
        Containers.updateNeighboursAfterDestroy(state, level, pos);
    }

    // -------------------------------------------------------------------------
    // Player interaction — item in hand
    // -------------------------------------------------------------------------

    @Override
    protected InteractionResult useItemOn(
            ItemStack stack, BlockState state, Level level,
            BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {

        if (level.isClientSide()) return InteractionResult.SUCCESS;

        JarBlockEntity jar = getJar(level, pos);
        if (jar == null) return InteractionResult.PASS;

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
                    boolean acceptsAspect = jar.doesContainerAccept(aspect)
                            && (jar.getStoredAspect() == null || jar.getStoredAspect() == aspect);

                    if (JarInteractionLogic.canFillFromPhial(true, acceptsAspect,
                            jar.getAmount(), JarBlockEntity.CAPACITY, amt, jar.logic.isVoid())) {
                        if (jar.logic.tryFillFromPhial(aspect, amt) == JarLogic.InteractionResult.SUCCESS) {
                            jar.setChanged();
                            if (player == null || !player.getAbilities().instabuild) {
                                stack.shrink(1);
                            }
                            giveOrDrop(level, pos, player, new ItemStack(ThaumcraftItems.phial.get()));
                            level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY,
                                    SoundSource.BLOCKS, 0.5f, 1.0f);
                            return InteractionResult.CONSUME;
                        }
                    }
                } else if (JarInteractionLogic.canDrainToPhial(isEmpty, jar.getAmount(), PHIAL_AMOUNT)) {
                    Aspect stored = jar.getStoredAspect();
                    if (stored != null) {
                        if (jar.logic.tryDrainToPhial() == JarLogic.InteractionResult.SUCCESS) {
                            jar.setChanged();
                            ItemStack filledPhial = new ItemStack(ThaumcraftItems.phial.get());
                            phialContainer.setAspects(filledPhial, new AspectList().add(stored, PHIAL_AMOUNT));
                            if (player == null || !player.getAbilities().instabuild) {
                                stack.shrink(1);
                            }
                            giveOrDrop(level, pos, player, filledPhial);
                            level.playSound(null, pos, SoundEvents.BOTTLE_FILL,
                                    SoundSource.BLOCKS, 0.5f, 1.0f);
                            return InteractionResult.CONSUME;
                        }
                    }
                }
            }
        }

        // --- Label interaction: apply aspect filter ---
        boolean isLabel = heldItem == ThaumcraftItems.label.get();
        if (JarInteractionLogic.canApplyLabel(jar.getAspectFilter() != null, isLabel)) {
            if (heldItem instanceof IEssentiaContainerItem labelContainer) {
                AspectList labelAspects = labelContainer.getAspects(stack);
                Aspect filterAspect = null;
                if (labelAspects != null && labelAspects.size() > 0) {
                    filterAspect = labelAspects.getAspects()[0];
                } else if (jar.getAmount() > 0) {
                    filterAspect = jar.getStoredAspect();
                }

                if (filterAspect != null && jar.logic.applyLabel(filterAspect) == JarLogic.InteractionResult.SUCCESS) {
                    jar.setChanged();
                    if (player == null || !player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    level.playSound(null, pos, SoundEvents.BOOK_PAGE_TURN,
                            SoundSource.BLOCKS, 0.4f, 1.0f);
                    return InteractionResult.CONSUME;
                }
            }
        }

        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    // -------------------------------------------------------------------------
    // Player interaction — empty hand
    // -------------------------------------------------------------------------

    /**
     * Sneak + empty-hand right-click → clears the aspect filter and returns the label.
     */
    @Override
    protected InteractionResult useWithoutItem(
            BlockState state, Level level,
            BlockPos pos, Player player, BlockHitResult hit) {

        if (level.isClientSide()) return InteractionResult.SUCCESS;

        JarBlockEntity jar = getJar(level, pos);
        if (jar == null) return InteractionResult.PASS;

        boolean isSneaking = player != null && player.isShiftKeyDown();
        boolean hasFilter = jar.logic.getAspectFilter() != null;

        if (JarInteractionLogic.canRemoveLabel(isSneaking, hasFilter)) {
            if (jar.logic.removeLabel() == JarLogic.InteractionResult.SUCCESS) {
                jar.setChanged();
                giveOrDrop(level, pos, player, new ItemStack(ThaumcraftItems.label.get()));
                level.playSound(null, pos, SoundEvents.BOOK_PAGE_TURN,
                        SoundSource.BLOCKS, 0.5f, 1.0f);
                return InteractionResult.CONSUME;
            }
        }

        return InteractionResult.PASS;
    }

    // -------------------------------------------------------------------------
    // Redstone comparator — essentia fill level (0–15)
    // -------------------------------------------------------------------------

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos,
                                        Direction direction) {
        JarBlockEntity jar = getJar(level, pos);
        if (jar == null) return 0;
        return JarInteractionLogic.calculateComparatorSignal(jar.getAmount(), JarBlockEntity.CAPACITY);
    }

    // -------------------------------------------------------------------------
    // Utility
    // -------------------------------------------------------------------------

    /** Returns the jar entity at {@code pos}, or {@code null}. */
    @Nullable
    private static JarBlockEntity getJar(@Nullable LevelAccessor level, @Nullable BlockPos pos) {
        if (level == null || pos == null) return null;
        BlockEntity be = level.getBlockEntity(pos);
        return be instanceof JarBlockEntity j ? j : null;
    }

    /**
     * Gives {@code stack} to {@code player}'s inventory; drops it near the block
     * if the inventory is full or player is null.
     */
    private static void giveOrDrop(Level level, BlockPos pos, @Nullable Player player, ItemStack stack) {
        if (player == null || !player.getInventory().add(stack)) {
            Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.75, pos.getZ() + 0.5, stack);
        }
    }
}
