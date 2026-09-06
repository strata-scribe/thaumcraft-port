package thaumcraft.common.blocks.crafting;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumcraft.common.blocks.entities.ThaumcraftBlockEntities;
import thaumcraft.common.tiles.crafting.CrucibleBlockEntity;

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
public class BlockCrucible extends Block implements EntityBlock {

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
    // Constructor
    // -------------------------------------------------------------------------

    public BlockCrucible(BlockBehaviour.Properties properties) {
        super(properties);
    }

    // -------------------------------------------------------------------------
    // EntityBlock
    // -------------------------------------------------------------------------

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CrucibleBlockEntity(pos, state);
    }

    /**
     * Returns a server-side-only ticker.  The crucible heat, aspect decay, and
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

    /** Safe cast helper — returns null when {@code actual} != {@code expected}. */
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

        // --- Water bucket fill ---
        if (stack.is(Items.WATER_BUCKET)) {
            if (tile.getWater() < CrucibleBlockEntity.TANK_CAPACITY) {
                tile.setWater(CrucibleBlockEntity.TANK_CAPACITY);
                tile.setChanged();
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                    player.getInventory().add(new ItemStack(Items.BUCKET));
                }
                level.playSound(null, pos,
                        SoundEvents.BUCKET_EMPTY,
                        SoundSource.BLOCKS, 1.0f, 1.0f);
                return InteractionResult.CONSUME;
            }
            return InteractionResult.FAIL;
        }

        // --- Alchemy smelt with held item (non-sneaking, boiling) ---
        if (!player.isShiftKeyDown()
                && tile.getHeat() > 150
                && tile.getWater() > 0) {
            ItemStack single = stack.copyWithCount(1);
            ItemStack remainder = tile.attemptSmelt(single, player);
            if (remainder == null) {
                // Item was fully consumed by a recipe or dissolved
                stack.shrink(1);
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
            if (tile.getHeat() > 150 && tile.getWater() > 0) {
                tile.attemptSmelt(itemEntity);
            }
        } else if (entity instanceof LivingEntity living) {
            if (tile.getHeat() > 150 && tile.getWater() > 0) {
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
