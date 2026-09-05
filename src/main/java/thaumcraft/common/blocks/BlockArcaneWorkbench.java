package thaumcraft.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import thaumcraft.common.tiles.ArcaneWorkbenchBlockEntity;

import javax.annotation.Nullable;

/**
 * The Arcane Workbench block — an EntityBlock that hosts an ArcaneWorkbenchBlockEntity
 * and opens the arcane crafting menu when right-clicked.
 */
public class BlockArcaneWorkbench extends Block implements EntityBlock {

    public BlockArcaneWorkbench(BlockBehaviour.Properties properties) {
        super(properties);
    }

    // -------------------------------------------------------------------------
    // EntityBlock
    // -------------------------------------------------------------------------

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ArcaneWorkbenchBlockEntity(pos, state);
    }

    // -------------------------------------------------------------------------
    // Player interaction — NeoForge 26.1.x uses useWithoutItem for "bare hand"
    // activation; it is called for both hands when no item triggers first.
    // -------------------------------------------------------------------------

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        // Delegate to the block entity which implements MenuProvider
        player.openMenu(state.getMenuProvider(level, pos), pos);
        return InteractionResult.CONSUME;
    }
}
