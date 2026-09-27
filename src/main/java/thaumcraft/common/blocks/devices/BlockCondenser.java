package thaumcraft.common.blocks.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import thaumcraft.common.blocks.entities.ThaumcraftBlockEntities;
import thaumcraft.common.tiles.devices.FluxCondenserBlockEntity;

public class BlockCondenser extends Block implements EntityBlock {

    public BlockCondenser(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FluxCondenserBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) return null;
        return blockEntityType == ThaumcraftBlockEntities.CONDENSER.get() ?
                (lvl, pos, st, be) -> FluxCondenserBlockEntity.tick(lvl, pos, st, (FluxCondenserBlockEntity) be) : null;
    }
}
