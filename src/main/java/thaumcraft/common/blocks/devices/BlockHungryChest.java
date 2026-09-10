package thaumcraft.common.blocks.devices;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SoundType;
import thaumcraft.common.blocks.entities.ThaumcraftBlockEntities;
import thaumcraft.common.tiles.devices.HungryChestBlockEntity;

import java.util.function.Supplier;

public class BlockHungryChest extends ChestBlock {
    public static final MapCodec<BlockHungryChest> CODEC = simpleCodec(properties -> new BlockHungryChest(properties, () -> ThaumcraftBlockEntities.HUNGRY_CHEST.get()));

    public BlockHungryChest(BlockBehaviour.Properties properties, Supplier<BlockEntityType<? extends ChestBlockEntity>> blockEntityTypeSupplier) {
        super(blockEntityTypeSupplier, net.minecraft.sounds.SoundEvents.CHEST_OPEN, net.minecraft.sounds.SoundEvents.CHEST_CLOSE, properties);
    }

    @Override
    public MapCodec<BlockHungryChest> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HungryChestBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type, ThaumcraftBlockEntities.HUNGRY_CHEST.get(), HungryChestBlockEntity::serverTick);
    }
}
