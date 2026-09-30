package thaumcraft.common.blocks.crafting;

import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
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
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumcraft.common.blocks.entities.ThaumcraftBlockEntities;
import thaumcraft.common.tiles.crafting.CrucibleBlockEntity;
import thaumcraft.common.tiles.crafting.logic.CrucibleEnvironmentLogic;

/**
 * The Crucible block — a cauldron-like vessel that dissolves items with magical aspects
 * and performs alchemy when provided with sufficient heat and water.
 *
 * <p>Shape: bottom plate (5px tall) + four 2px-thick walls, mirroring the original
 * 1.12 AABB definitions from {@code BlockCrucible}.
 *
 * <p>MC 26.1.2 / NeoForge 26.1.x port of
 * {@code thaumcraft.common.blocks.crafting.BlockCrucible}.
 */
public class BlockCrucible extends BaseEntityBlock {

    public static final MapCodec<BlockCrucible> CODEC = simpleCodec(BlockCrucible::new);

    // -------------------------------------------------------------------------
    // VoxelShape definitions
    // -------------------------------------------------------------------------

    /** Bottom plate: y = 0..5 px (0..0.3125 block) */
    private static final VoxelShape SHAPE_LEGS =
            Block.box(0, 0, 0, 16, 5, 16);

    /** North wall: z = 0..2 px */
    private static final VoxelShape SHAPE_WALL_NORTH =
            Block.box(0, 0, 0, 16, 16, 2);

    /** South wall: z = 14..16 px */
    private static final VoxelShape SHAPE_WALL_SOUTH =
            Block.box(0, 0, 14, 16, 16, 16);

    /** East wall: x = 14..16 px */
    private static final VoxelShape SHAPE_WALL_EAST =
            Block.box(14, 0, 0, 16, 16, 16);

    /** West wall: x = 0..2 px */
    private static final VoxelShape SHAPE_WALL_WEST =
            Block.box(0, 0, 0, 2, 16, 16);

    /** Union of all sub-shapes — used for both collision and outline rendering. */
    private static final VoxelShape SHAPE = Shapes.or(
            SHAPE_LEGS,
            SHAPE_WALL_NORTH,
            SHAPE_WALL_SOUTH,
            SHAPE_WALL_EAST,
            SHAPE_WALL_WEST
    );

    // -------------------------------------------------------------------------
    // Constructor & Codec
    // -------------------------------------------------------------------------

    public BlockCrucible(BlockBehaviour.Properties properties) {
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
        return new CrucibleBlockEntity(pos, state);
    }

    /**
     * Returns a server-side-only ticker. The crucible heat, aspect decay, and
     * overflow logic all run server-side; client effects are handled by the
     * engine's random display-tick budget.
     */
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return createTickerHelper(type,
                ThaumcraftBlockEntities.CRUCIBLE.get(),
                CrucibleBlockEntity::tick);
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
     * Handles:
     * <ul>
     *   <li>Water bucket → fills crucible to 1000 mB, gives back empty bucket.</li>
     *   <li>Empty bucket → drains 1000 mB water, gives back water bucket.</li>
     *   <li>Water bottle → fills crucible by 333 mB, gives back glass bottle.</li>
     *   <li>Glass bottle → drains 333 mB water, gives back water bottle.</li>
     *   <li>Any item above boiling water → attempts alchemy smelt.</li>
     * </ul>
     */
    @Override
    protected InteractionResult useItemOn(
            ItemStack stack, BlockState state, Level level,
            BlockPos pos, Player player,
            net.minecraft.world.InteractionHand hand, BlockHitResult hit) {

        if (level.isClientSide()) return InteractionResult.SUCCESS;

        CrucibleBlockEntity tile = getCrucible(level, pos);
        if (tile == null) return InteractionResult.PASS;

        // 1. Water bucket fill
        if (stack.is(Items.WATER_BUCKET)) {
            if (CrucibleEnvironmentLogic.canFillWithBucket(tile.getWater())) {
                tile.setWater(CrucibleEnvironmentLogic.fillWithBucket(tile.getWater()));
                tile.setChanged();
                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.BUCKET)));
                level.playSound(null, pos,
                        SoundEvents.BUCKET_EMPTY,
                        SoundSource.BLOCKS, 1.0f, 1.0f);
                return InteractionResult.CONSUME;
            }
            return InteractionResult.FAIL;
        }

        // 2. Empty bucket drain
        if (stack.is(Items.BUCKET)) {
            if (CrucibleEnvironmentLogic.canDrainWithBucket(tile.getWater(), tile.getAspects().visSize())) {
                tile.setWater(CrucibleEnvironmentLogic.drainWithBucket(tile.getWater()));
                tile.setChanged();
                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.WATER_BUCKET)));
                level.playSound(null, pos,
                        SoundEvents.BUCKET_FILL,
                        SoundSource.BLOCKS, 1.0f, 1.0f);
                return InteractionResult.CONSUME;
            }
            return InteractionResult.FAIL;
        }

        // 3. Water bottle fill
        boolean isWaterBottle = stack.is(Items.POTION)
                && stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).is(Potions.WATER);
        if (isWaterBottle) {
            if (CrucibleEnvironmentLogic.canFillWithBottle(tile.getWater())) {
                tile.setWater(CrucibleEnvironmentLogic.fillWithBottle(tile.getWater()));
                tile.setChanged();
                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
                level.playSound(null, pos,
                        SoundEvents.BOTTLE_EMPTY,
                        SoundSource.BLOCKS, 1.0f, 1.0f);
                return InteractionResult.CONSUME;
            }
            return InteractionResult.FAIL;
        }

        // 4. Glass bottle drain
        if (stack.is(Items.GLASS_BOTTLE)) {
            if (CrucibleEnvironmentLogic.canDrainWithBottle(tile.getWater(), tile.getAspects().visSize())) {
                tile.setWater(CrucibleEnvironmentLogic.drainWithBottle(tile.getWater()));
                tile.setChanged();
                ItemStack waterBottle = PotionContents.createItemStack(Items.POTION, Potions.WATER);
                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, waterBottle));
                level.playSound(null, pos,
                        SoundEvents.BOTTLE_FILL,
                        SoundSource.BLOCKS, 1.0f, 1.0f);
                return InteractionResult.CONSUME;
            }
            return InteractionResult.FAIL;
        }

        // 5. Catalyst item alchemy smelt with held item (non-sneaking, boiling)
        if (!player.isShiftKeyDown()
                && CrucibleEnvironmentLogic.canSmelt(tile.getHeat(), tile.getWater())) {
            ItemStack single = stack.copyWithCount(1);
            ItemStack remainder = tile.attemptSmelt(single, player);
            if (remainder == null) {
                // Item was fully consumed by a recipe or dissolved
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                return InteractionResult.CONSUME;
            }
        }

        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    // -------------------------------------------------------------------------
    // Player interaction — empty hand
    // -------------------------------------------------------------------------

    /**
     * Sneak + empty-hand right-click → spill all water and aspects.
     */
    @Override
    protected InteractionResult useWithoutItem(
            BlockState state, Level level,
            BlockPos pos, Player player, BlockHitResult hit) {

        if (level.isClientSide()) return InteractionResult.SUCCESS;

        if (player.isShiftKeyDown()) {
            CrucibleBlockEntity tile = getCrucible(level, pos);
            if (tile != null) {
                tile.spillRemnants();
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.PASS;
    }

    // -------------------------------------------------------------------------
    // Entity collision
    // -------------------------------------------------------------------------

    /**
     * Server-side collision handler.
     * <ul>
     *   <li>ItemEntity falling in boiling water → alchemy smelt attempt.</li>
     *   <li>LivingEntity standing in boiling water → 1 fire-damage tick.</li>
     * </ul>
     */
    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity,
                                net.minecraft.world.entity.InsideBlockEffectApplier effectApplier, boolean moved) {
        if (level.isClientSide()) return;

        CrucibleBlockEntity tile = getCrucible(level, pos);
        if (tile == null) return;

        if (entity instanceof ItemEntity itemEntity) {
            if (CrucibleEnvironmentLogic.canSmelt(tile.getHeat(), tile.getWater())) {
                tile.attemptSmelt(itemEntity);
            }
        } else if (entity instanceof LivingEntity living) {
            if (CrucibleEnvironmentLogic.shouldHurtLivingEntity(tile.getHeat(), tile.getWater(), living.fireImmune())) {
                living.hurt(level.damageSources().inFire(), 1.0f);
                level.playSound(null, pos,
                        SoundEvents.FIRE_EXTINGUISH,
                        SoundSource.BLOCKS,
                        0.4f, 2.0f + level.getRandom().nextFloat() * 0.4f);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Block removal — spill remnants before the BE is removed
    // -------------------------------------------------------------------------

    @Override
    public void destroy(net.minecraft.world.level.LevelAccessor level, BlockPos pos, BlockState state) {
        CrucibleBlockEntity tile = level.getBlockEntity(pos) instanceof CrucibleBlockEntity c ? c : null;
        if (tile != null) {
            tile.spillRemnants();
        }
        super.destroy(level, pos, state);
    }

    // -------------------------------------------------------------------------
    // Redstone comparator — aspect fill (0-15)
    // -------------------------------------------------------------------------

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, net.minecraft.core.Direction direction) {
        CrucibleBlockEntity tile = getCrucible(level, pos);
        if (tile == null) return 0;
        int vis = tile.getAspects().visSize();
        if (vis <= 0) return 0;
        float ratio = vis / (float) CrucibleBlockEntity.MAX_ASPECTS;
        return Math.min(15, (int) (ratio * 14.0f) + 1);
    }

    // -------------------------------------------------------------------------
    // Utility
    // -------------------------------------------------------------------------

    @Nullable
    private static CrucibleBlockEntity getCrucible(Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        return be instanceof CrucibleBlockEntity c ? c : null;
    }
}
