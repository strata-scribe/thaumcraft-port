package thaumcraft.common.blocks.essentia;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.common.blocks.essentia.logic.TubeConnectionLogic;
import thaumcraft.common.tiles.essentia.TileTubeFilter;

public class BlockTubeFilter extends BlockTube {

    public static final MapCodec<BlockTubeFilter> CODEC = simpleCodec(BlockTubeFilter::new);

    public BlockTubeFilter(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileTubeFilter(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof TileTubeFilter filterTile) {
            ItemStack heldItem = player.getMainHandItem();
            Aspect currentFilter = filterTile.getFilteredLogic().getAspectFilter();
            boolean hasFilter = currentFilter != null;
            boolean isHandEmpty = heldItem.isEmpty();
            boolean isCrouching = player.isCrouching();

            if (TubeConnectionLogic.canClearFilter(isCrouching, isHandEmpty, hasFilter)) {
                if (level.isClientSide()) return InteractionResult.SUCCESS;

                filterTile.getFilteredLogic().setAspectFilter(null);
                filterTile.setChanged();
                level.sendBlockUpdated(pos, state, state, 3);
                return InteractionResult.CONSUME;
            } else if (!heldItem.isEmpty()) {
                Aspect aspectToFilter = null;
                if (heldItem.getItem() instanceof IEssentiaContainerItem container) {
                    AspectList aspects = container.getAspects(heldItem);
                    if (aspects != null && aspects.size() > 0) {
                        aspectToFilter = aspects.getAspects()[0];
                    }
                }

                if (TubeConnectionLogic.canApplyFilter(hasFilter, aspectToFilter != null)) {
                    if (level.isClientSide()) return InteractionResult.SUCCESS;
                    filterTile.getFilteredLogic().setAspectFilter(aspectToFilter);
                    filterTile.setChanged();
                    level.sendBlockUpdated(pos, state, state, 3);
                    return InteractionResult.CONSUME;
                }
            }
        }

        // Delegate to base tube interaction (e.g., wrenching to open/close)
        return super.useWithoutItem(state, level, pos, player, hit);
    }
}
