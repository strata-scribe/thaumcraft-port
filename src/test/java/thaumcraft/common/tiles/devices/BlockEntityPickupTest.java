package thaumcraft.common.tiles.devices;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class BlockEntityPickupTest {

    static class MockItem {
        String name;
        int count;
        int maxStackSize;
        String aspectTag;

        MockItem(String name, int count, int maxStackSize) {
            this(name, count, maxStackSize, null);
        }

        MockItem(String name, int count, int maxStackSize, String aspectTag) {
            this.name = name;
            this.count = count;
            this.maxStackSize = maxStackSize;
            this.aspectTag = aspectTag;
        }

        MockItem copy() {
            return new MockItem(name, count, maxStackSize, aspectTag);
        }

        boolean isEmpty() {
            return count <= 0;
        }

        void grow(int amount) {
            this.count += amount;
        }

        void shrink(int amount) {
            this.count -= amount;
        }
    }

    static class MockItemEntity {
        double x, y, z;
        MockItem item;
        boolean alive = true;

        MockItemEntity(double x, double y, double z, MockItem item) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.item = item;
        }

        void discard() {
            alive = false;
        }
    }

    // Hungry Chest Helper
    static MockItem tryInsert(MockItem[] container, MockItem stack) {
        MockItem currentStack = stack.copy();
        for (int i = 0; i < container.length; i++) {
            MockItem inSlot = container[i];
            if (inSlot == null || inSlot.isEmpty()) {
                container[i] = currentStack.copy();
                return new MockItem("", 0, 0); // Empty
            } else if (inSlot.name.equals(currentStack.name)) {
                int space = inSlot.maxStackSize - inSlot.count;
                if (space > 0) {
                    int toAdd = Math.min(space, currentStack.count);
                    inSlot.grow(toAdd);
                    currentStack.shrink(toAdd);
                    if (currentStack.isEmpty()) {
                        return new MockItem("", 0, 0);
                    }
                }
            }
        }
        return currentStack;
    }

    static void tickHungryChest(double chestX, double chestY, double chestZ, List<MockItemEntity> entities, MockItem[] inventory) {
        for (MockItemEntity entity : entities) {
            if (!entity.alive) continue;

            // Check distance (AABB inflate 5.0) -> roughly bounds checking
            // Chest is at chestX, chestY, chestZ. Inflate 5 means +/- 5 from center.
            double dx = Math.abs(entity.x - chestX);
            double dy = Math.abs(entity.y - chestY);
            double dz = Math.abs(entity.z - chestZ);

            if (dx <= 5.0 && dy <= 5.0 && dz <= 5.0) {
                MockItem remaining = tryInsert(inventory, entity.item);
                if (remaining.isEmpty()) {
                    entity.discard();
                } else {
                    entity.item = remaining;
                }
            }
        }
    }

    @Test
    public void testHungryChestPickup() {
        MockItem[] inventory = new MockItem[27];
        List<MockItemEntity> entities = new ArrayList<>();

        // Entity within 5 blocks
        entities.add(new MockItemEntity(3, 0, 0, new MockItem("apple", 10, 64)));
        // Entity outside 5 blocks
        entities.add(new MockItemEntity(6, 0, 0, new MockItem("sword", 1, 1)));

        tickHungryChest(0, 0, 0, entities, inventory);

        assertFalse(entities.get(0).alive, "Apple should be picked up and discarded");
        assertTrue(entities.get(1).alive, "Sword is outside range and should not be picked up");

        assertNotNull(inventory[0]);
        assertEquals("apple", inventory[0].name);
        assertEquals(10, inventory[0].count);
    }

    // Void Siphon Helper
    static void tickVoidSiphon(double siphonX, double siphonY, double siphonZ, List<MockItemEntity> entities, String filterAspect) {
        for (MockItemEntity entity : entities) {
            if (!entity.alive) continue;

            double dx = Math.abs(entity.x - siphonX);
            double dy = Math.abs(entity.y - siphonY);
            double dz = Math.abs(entity.z - siphonZ);

            if (dx <= 3.0 && dy <= 3.0 && dz <= 3.0) {
                if (filterAspect != null) {
                    if (entity.item.aspectTag == null || !entity.item.aspectTag.equals(filterAspect)) {
                        continue;
                    }
                }
                entity.discard();
            }
        }
    }

    @Test
    public void testVoidSiphonDestroy() {
        List<MockItemEntity> entities = new ArrayList<>();

        // Entity within 3 blocks
        entities.add(new MockItemEntity(2, 0, 0, new MockItem("dirt", 64, 64)));
        // Entity outside 3 blocks
        entities.add(new MockItemEntity(4, 0, 0, new MockItem("stone", 64, 64)));

        tickVoidSiphon(0, 0, 0, entities, null);

        assertFalse(entities.get(0).alive, "Dirt should be destroyed");
        assertTrue(entities.get(1).alive, "Stone is outside range and should remain");
    }

    @Test
    public void testVoidSiphonFilter() {
        List<MockItemEntity> entities = new ArrayList<>();

        // Entity within 3 blocks, matching filter
        entities.add(new MockItemEntity(1, 0, 0, new MockItem("ignis_crystal", 1, 64, "ignis")));
        // Entity within 3 blocks, not matching filter
        entities.add(new MockItemEntity(1, 0, 0, new MockItem("aqua_crystal", 1, 64, "aqua")));

        tickVoidSiphon(0, 0, 0, entities, "ignis");

        assertFalse(entities.get(0).alive, "Ignis crystal matches filter and should be destroyed");
        assertTrue(entities.get(1).alive, "Aqua crystal does not match filter and should remain");
    }
}
