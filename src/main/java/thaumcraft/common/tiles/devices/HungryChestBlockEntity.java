package thaumcraft.common.tiles.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import thaumcraft.common.blocks.entities.ThaumcraftBlockEntities;

import java.util.List;

public class HungryChestBlockEntity extends ChestBlockEntity {
    private int tickCount = 0;

    public HungryChestBlockEntity(BlockPos pos, BlockState state) {
        super(ThaumcraftBlockEntities.HUNGRY_CHEST.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, HungryChestBlockEntity blockEntity) {
        blockEntity.tickCount++;
        if (blockEntity.tickCount % 20 == 0) {
            AABB aabb = new AABB(pos).inflate(5.0);
            List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, aabb);

            for (ItemEntity itemEntity : items) {
                if (!itemEntity.isAlive()) continue;

                ItemStack stack = itemEntity.getItem();
                ItemStack remaining = tryInsert(blockEntity, stack);

                if (remaining.isEmpty()) {
                    itemEntity.discard();
                } else {
                    itemEntity.setItem(remaining);
                }
            }
        }
    }

    public static ItemStack tryInsert(Container container, ItemStack stack) {
        ItemStack currentStack = stack.copy();
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack inSlot = container.getItem(i);
            if (inSlot.isEmpty()) {
                container.setItem(i, currentStack.copy());
                return ItemStack.EMPTY;
            } else if (ItemStack.isSameItemSameComponents(inSlot, currentStack)) {
                int space = inSlot.getMaxStackSize() - inSlot.getCount();
                if (space > 0) {
                    int toAdd = Math.min(space, currentStack.getCount());
                    inSlot.grow(toAdd);
                    currentStack.shrink(toAdd);
                    if (currentStack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                }
            }
        }
        return currentStack;
    }
}
