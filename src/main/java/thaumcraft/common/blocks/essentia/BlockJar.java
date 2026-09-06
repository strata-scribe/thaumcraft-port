package thaumcraft.common.blocks.essentia;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.items.ThaumcraftItems;
import thaumcraft.common.tiles.essentia.JarBlockEntity;

/**
 * Warded Jar block — a single-aspect essentia storage vessel.
 *
 * <h3>VoxelShape (px = 1/16 block)</h3>
 * <ul>
 *   <li>Body: x=3..13, y=0..12, z=3..13</li>
 *   <li>Neck:  x=5..11, y=12..14, z=5..11</li>
 *   <li>Lid:   x=4..12, y=14..16, z=4..12</li>
 * </ul>
 *
 * <h3>Interaction</h3>
 * <ul>
 *   <li>Empty phial → drain {@value #PHIAL_AMOUNT} essentia; give back filled phial.</li>
 *   <li>Filled phial → pour {@value #PHIAL_AMOUNT} essentia into jar.</li>
 *   <li>Label item → apply aspect filter from label's contained aspect.</li>
 *   <li>Sneak + empty hand → clear aspect filter.</li>
 * </ul>
 *
 * <p>MC 1.21.4 / NeoForge 26.2 port of
 * {@code thaumcraft.common.blocks.essentia.BlockJar}.
 */
public class BlockJar extends Block implements EntityBlock {

    // -------------------------------------------------------------------------
    // VoxelShape definitions — coordinates in 1/16-block pixels
    // -------------------------------------------------------------------------

    /** Jar body: 10×12×10 px column */
    private static final VoxelShape SHAPE_BODY =
            Block.box(3, 0, 3, 13, 12, 13);

    /** Narrow neck: 6×2×6 px */
    private static final VoxelShape SHAPE_NECK =
            Block.box(5, 12, 5, 11, 14, 11);

    /** Lid cap: 8×2×8 px */
    private static final VoxelShape SHAPE_LID =
            Block.box(4, 14, 4, 12, 16, 12);

    /** Union of body + neck + lid. */
    private static final VoxelShape SHAPE =
            Shapes.or(SHAPE_BODY, SHAPE_NECK, SHAPE_LID);

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    /** Amount of essentia transferred per phial interaction. */
    public static final int PHIAL_AMOUNT = 8;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public BlockJar(BlockBehaviour.Properties properties) {
        super(properties);
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
    // Player interaction — item in hand
    // -------------------------------------------------------------------------

    /**
     * Handles phial fill/drain and label application.
     *
     * <ul>
     *   <li>Filled phial → pour essentia into jar (if jar accepts that aspect and has room).</li>
     *   <li>Empty phial → draw {@value #PHIAL_AMOUNT} essentia out of jar into a new filled phial.</li>
     *   <li>Label → set the jar's aspect filter to the label's contained aspect.</li>
     * </ul>
     */
    @Override
    protected InteractionResult useItemOn(
            ItemStack stack, BlockState state, Level level,
            BlockPos pos, Player player,
            net.minecraft.world.InteractionHand hand, BlockHitResult hit) {

        if (level.isClientSide()) return InteractionResult.SUCCESS;

        JarBlockEntity jar = getJar(level, pos);
        if (jar == null) return InteractionResult.PASS;

        net.minecraft.world.item.Item heldItem = stack.getItem();

        // --- Phial interaction ---
        if (heldItem == ThaumcraftItems.phial.get()) {
            boolean isFilledPhial = stack.has(DataComponents.CUSTOM_DATA);

            if (isFilledPhial) {
                // Filled phial → pour into jar
                IEssentiaContainerItem phialContainer = (IEssentiaContainerItem) heldItem;
                AspectList phialAspects = phialContainer.getAspects(stack);
                if (phialAspects != null && phialAspects.size() > 0) {
                    Aspect aspect = phialAspects.getAspects()[0];
                    int amt = phialAspects.getAmount(aspect);
                    if (jar.doesContainerAccept(aspect)
                            && (jar.getStoredAspect() == null || jar.getStoredAspect() == aspect)
                            && jar.getAmount() + amt <= JarBlockEntity.CAPACITY) {
                        jar.addToContainer(aspect, amt);
                        jar.setChanged();
                        // Replace filled phial with empty phial
                        stack.shrink(1);
                        giveOrDrop(level, pos, player, new ItemStack(ThaumcraftItems.phial.get()));
                        level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY,
                                SoundSource.BLOCKS, 0.5f, 1.0f);
                        return InteractionResult.CONSUME;
                    }
                }
            } else {
                // Empty phial → drain from jar
                Aspect stored = jar.getStoredAspect();
                if (stored != null && jar.getAmount() >= PHIAL_AMOUNT) {
                    jar.takeFromContainer(stored, PHIAL_AMOUNT);
                    jar.setChanged();
                    ItemStack filledPhial = new ItemStack(ThaumcraftItems.phial.get());
                    IEssentiaContainerItem phialContainer = (IEssentiaContainerItem) ThaumcraftItems.phial.get();
                    phialContainer.setAspects(filledPhial, new AspectList().add(stored, PHIAL_AMOUNT));
                    stack.shrink(1);
                    giveOrDrop(level, pos, player, filledPhial);
                    level.playSound(null, pos, SoundEvents.BOTTLE_FILL,
                            SoundSource.BLOCKS, 0.5f, 1.0f);
                    return InteractionResult.CONSUME;
                }
            }
        }

        // --- Label interaction: apply aspect filter ---
        if (heldItem == ThaumcraftItems.label.get()
                && heldItem instanceof IEssentiaContainerItem labelContainer) {
            if (jar.getAspectFilter() == null) {
                AspectList labelAspects = labelContainer.getAspects(stack);
                if (labelAspects != null && labelAspects.size() > 0) {
                    Aspect filterAspect = labelAspects.getAspects()[0];
                    // Only apply filter if jar is empty or already holds that aspect
                    if (jar.getAmount() == 0 || jar.getStoredAspect() == filterAspect) {
                        if (jar.getAmount() == 0) {
                            jar.setStoredAspect(filterAspect);
                        }
                        jar.setAspectFilter(filterAspect);
                        jar.setChanged();
                        level.playSound(null, pos, SoundEvents.BOOK_PAGE_TURN,
                                SoundSource.BLOCKS, 0.4f, 1.0f);
                        return InteractionResult.CONSUME;
                    }
                }
            }
        }

        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    // -------------------------------------------------------------------------
    // Player interaction — empty hand
    // -------------------------------------------------------------------------

    /**
     * Sneak + empty-hand right-click → clears the aspect filter.
     */
    @Override
    protected InteractionResult useWithoutItem(
            BlockState state, Level level,
            BlockPos pos, Player player, BlockHitResult hit) {

        if (level.isClientSide()) return InteractionResult.SUCCESS;

        if (player.isShiftKeyDown()) {
            JarBlockEntity jar = getJar(level, pos);
            if (jar != null && jar.getAspectFilter() != null) {
                jar.setAspectFilter(null);
                jar.setChanged();
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

    /**
     * Returns 0–15 based on stored essentia:
     * {@code (amount * 15) / CAPACITY}.
     * This maps 0 → 0 and CAPACITY → 15.
     */
    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos,
                                        net.minecraft.core.Direction direction) {
        JarBlockEntity jar = getJar(level, pos);
        if (jar == null || jar.getAmount() <= 0) return 0;
        return (jar.getAmount() * 15) / JarBlockEntity.CAPACITY;
    }

    // -------------------------------------------------------------------------
    // Utility
    // -------------------------------------------------------------------------

    /** Returns the jar entity at {@code pos}, or {@code null}. */
    @Nullable
    private static JarBlockEntity getJar(Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        return be instanceof JarBlockEntity j ? j : null;
    }

    /**
     * Gives {@code stack} to {@code player}'s inventory; drops it near the block
     * if the inventory is full.
     */
    private static void giveOrDrop(Level level, BlockPos pos, Player player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            ItemEntity entity = new ItemEntity(
                    level, pos.getX() + 0.5, pos.getY() + 0.75, pos.getZ() + 0.5, stack);
            level.addFreshEntity(entity);
        }
    }
}
