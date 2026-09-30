package thaumcraft.common.blocks.essentia;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
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
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.blocks.entities.ThaumcraftBlockEntities;
import thaumcraft.common.tiles.essentia.SmelterBlockEntity;
import thaumcraft.common.tiles.essentia.logic.SmelterLogic;

import javax.annotation.Nullable;

/**
 * Essentia Smelter block — decomposes items into their component aspects.
 *
 * <h3>BlockState Properties</h3>
 * <ul>
 *   <li>{@link BlockStateProperties#HORIZONTAL_FACING HORIZONTAL_FACING} — placement direction (default NORTH).</li>
 *   <li>{@link BlockStateProperties#LIT LIT} — whether the smelter is actively burning fuel.</li>
 * </ul>
 *
 * <p>Three separate block instances exist (basic, thaumium, void), registered
 * in {@link thaumcraft.api.blocks.ThaumcraftBlocks}. The tier is determined
 * by the block entity at construction time.
 *
 * <p>MC 1.21.4 / NeoForge 26.2 port of
 * {@code thaumcraft.common.blocks.essentia.BlockSmelter}.
 */
public class BlockSmelter extends BaseEntityBlock {

    public static final MapCodec<BlockSmelter> CODEC = simpleCodec(BlockSmelter::new);

    // -------------------------------------------------------------------------
    // Constructor & Codec
    // -------------------------------------------------------------------------

    public BlockSmelter(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
                .setValue(BlockStateProperties.LIT, false));
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
    // BlockState
    // -------------------------------------------------------------------------

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.LIT);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING,
                        context.getHorizontalDirection().getOpposite())
                .setValue(BlockStateProperties.LIT, false);
    }

    // -------------------------------------------------------------------------
    // EntityBlock — block entity creation and ticker
    // -------------------------------------------------------------------------

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SmelterBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return createTickerHelper(type, ThaumcraftBlockEntities.SMELTER.get(), SmelterBlockEntity::serverTick);
    }

    // -------------------------------------------------------------------------
    // Interaction
    // -------------------------------------------------------------------------

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state, Level level, BlockPos pos,
            Player player, BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        // TODO: Open smelter GUI / container screen
        // player.openMenu(...)
        return InteractionResult.CONSUME;
    }

    // -------------------------------------------------------------------------
    // Removal & Inventory Drop
    // -------------------------------------------------------------------------

    /**
     * Drops inventory when the block is replaced by another block type,
     * and updates neighbouring redstone comparator listeners.
     */
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        String oldId = state != null ? String.valueOf(state.getBlock()) : null;
        String newId = newState != null ? String.valueOf(newState.getBlock()) : null;
        if (SmelterLogic.shouldDropInventory(oldId, newId)) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof SmelterBlockEntity smelter) {
                if (smelter.getItemInput() != null && !smelter.getItemInput().isEmpty()) {
                    Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, smelter.getItemInput());
                    smelter.setItemInput(ItemStack.EMPTY);
                }
                if (smelter.getFuelInput() != null && !smelter.getFuelInput().isEmpty()) {
                    Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, smelter.getFuelInput());
                    smelter.setFuelInput(ItemStack.EMPTY);
                }
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        if (level instanceof Level world && !world.isClientSide()) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof SmelterBlockEntity smelter) {
                if (smelter.getItemInput() != null && !smelter.getItemInput().isEmpty()) {
                    Containers.dropItemStack(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, smelter.getItemInput());
                    smelter.setItemInput(ItemStack.EMPTY);
                }
                if (smelter.getFuelInput() != null && !smelter.getFuelInput().isEmpty()) {
                    Containers.dropItemStack(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, smelter.getFuelInput());
                    smelter.setFuelInput(ItemStack.EMPTY);
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
    // Comparator
    // -------------------------------------------------------------------------

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level,
                                        BlockPos pos, Direction direction) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof SmelterBlockEntity smelter) {
            return SmelterLogic.calculateComparatorSignal(smelter.getVis(), smelter.getTier().getCapacity());
        }
        return 0;
    }

    // -------------------------------------------------------------------------
    // Block events — neighbour notification
    // -------------------------------------------------------------------------

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos,
                                   Block block, @Nullable net.minecraft.world.level.redstone.Orientation orientation, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, orientation, isMoving);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof SmelterBlockEntity smelter) {
            smelter.checkNeighbours();
        }
    }

    // -------------------------------------------------------------------------
    // Break — release stored essentia as flux
    // -------------------------------------------------------------------------

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos,
                                        BlockState state, Player player) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof SmelterBlockEntity smelter && smelter.getVis() > 0) {
                AuraHelper.polluteAura(level, pos, (float) smelter.getVis(), true);
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }
}
