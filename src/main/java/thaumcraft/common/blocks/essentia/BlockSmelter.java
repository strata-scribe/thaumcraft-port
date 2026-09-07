package thaumcraft.common.blocks.essentia;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
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
public class BlockSmelter extends Block implements EntityBlock {

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public BlockSmelter(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
                .setValue(BlockStateProperties.LIT, false));
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
        if (type != ThaumcraftBlockEntities.SMELTER.get()) return null;
        return (lvl, p, s, be) -> SmelterBlockEntity.serverTick(lvl, p, s, (SmelterBlockEntity) be);
    }

    // -------------------------------------------------------------------------
    // Light emission — lit smelters glow
    // -------------------------------------------------------------------------

    /**
     * Emits light level 13 when lit, matching furnace behaviour.
     * Note: registered via {@code BlockBehaviour.Properties.lightLevel()} in
     * the block registration, but this override ensures correctness.
     */
    // Light level is controlled via the Properties builder at registration time:
    // .lightLevel(state -> state.getValue(BlockStateProperties.LIT) ? 13 : 0)

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
            int vis = smelter.getVis();
            int cap = smelter.getTier().getCapacity();
            if (vis <= 0 || cap <= 0) return 0;
            return Math.min(15, (vis * 15) / cap);
        }
        return 0;
    }

    // -------------------------------------------------------------------------
    // Block events — neighbour notification
    // -------------------------------------------------------------------------

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos,
                                   Block block, @javax.annotation.Nullable net.minecraft.world.level.redstone.Orientation orientation, boolean isMoving) {
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
