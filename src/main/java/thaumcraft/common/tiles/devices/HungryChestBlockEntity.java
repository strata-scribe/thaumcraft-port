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

        AABB aabb = new AABB(pos).inflate(5.0);
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, aabb);

        double chestX = pos.getX() + 0.5;
        double chestY = pos.getY() + 0.5;
        double chestZ = pos.getZ() + 0.5;

        double maxRadius = 5.0;
        double absorbDistance = 1.0;
        double baseSpeed = 0.05;

        for (ItemEntity itemEntity : items) {
            if (!itemEntity.isAlive()) continue;

            double itemX = itemEntity.getX();
            double itemY = itemEntity.getY();
            double itemZ = itemEntity.getZ();

            HungryChestAttractionLogic.AttractionResult result = HungryChestAttractionLogic.calculateAttractionVector(
                    itemX, itemY, itemZ, chestX, chestY, chestZ, maxRadius, absorbDistance, baseSpeed
            );

            if (result.shouldAbsorb) {
                ItemStack stack = itemEntity.getItem();
                ItemStack remaining = tryInsert(blockEntity, stack);

                if (remaining.isEmpty()) {
                    itemEntity.discard();
                } else {
                    itemEntity.setItem(remaining);
                }
            } else {
                itemEntity.setDeltaMovement(
                        itemEntity.getDeltaMovement().add(result.vx, result.vy, result.vz)
                );
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
