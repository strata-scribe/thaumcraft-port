package thaumcraft.common.blocks.essentia;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.items.ThaumcraftItems;
import thaumcraft.common.tiles.essentia.TileTubeFilter;

public class BlockTubeFilter extends BlockTube {

    public BlockTubeFilter(Properties properties) {
        super(properties);
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
            if (heldItem.isEmpty() && player.isCrouching()) {
                if (level.isClientSide()) return InteractionResult.SUCCESS;

                Aspect filter = filterTile.getFilteredLogic().getAspectFilter();
                if (filter != null) {
                    filterTile.getFilteredLogic().setAspectFilter(null);
                    filterTile.setChanged();
                    level.sendBlockUpdated(pos, state, state, 3);
                    return InteractionResult.CONSUME;
                }
            } else if (!heldItem.isEmpty()) {
                Aspect aspectToFilter = null;
                if (heldItem.getItem() instanceof IEssentiaContainerItem container) {
                    AspectList aspects = container.getAspects(heldItem);
                    if (aspects != null && aspects.size() > 0) {
                        aspectToFilter = aspects.getAspects()[0];
                    }
                }

                if (aspectToFilter != null) {
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
