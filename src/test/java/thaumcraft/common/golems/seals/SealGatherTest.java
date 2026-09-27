package thaumcraft.common.golems.seals;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SealGatherTest {

    @Test
    public void testBoundingBoxIntersection() {
        SealGatherLogic.BoundingBox area = new SealGatherLogic.BoundingBox(0, 0, 0, 10, 10, 10);

        // Fully inside
        assertTrue(area.intersects(2, 2, 2, 8, 8, 8), "Item inside area should intersect");

        // Overlapping
        assertTrue(area.intersects(8, 8, 8, 12, 12, 12), "Item overlapping area should intersect");

        // Outside completely
        assertFalse(area.intersects(11, 11, 11, 15, 15, 15), "Item outside area should not intersect");
    }

    @Test
    public void testValidItemDetectionAndTaskGeneration() {
        SealGatherLogic.BoundingBox area = new SealGatherLogic.BoundingBox(0, 0, 0, 10, 10, 10);

        Object mockValidItem = new Object();
        SealGatherLogic.ItemAdapter item = new SealGatherLogic.ItemAdapter() {
            @Override public Object getEntityReference() { return mockValidItem; }
            @Override public double getMinX() { return 2; }
            @Override public double getMinY() { return 2; }
            @Override public double getMinZ() { return 2; }
            @Override public double getMaxX() { return 3; }
            @Override public double getMaxY() { return 3; }
            @Override public double getMaxZ() { return 3; }
            @Override public boolean isAlive() { return true; }
            @Override public boolean hasPickUpDelay() { return false; }
            @Override public int getStackSize() { return 5; }
            @Override public boolean matchesFilter() { return true; }
        };

        SealGatherLogic.GatherTask task = SealGatherLogic.evaluateItemAndCreateTask(area, item, (byte) 5);

        assertNotNull(task, "Task should be generated for a valid item");
        assertEquals(mockValidItem, task.entityReference, "Task should target the correct item");
        assertEquals((byte) 5, task.priority, "Task priority should match seal priority");
        assertEquals((short) 300, task.lifespan, "Task lifespan should be 300 ticks");
    }

    @Test
    public void testItemStateFiltering() {
        SealGatherLogic.BoundingBox area = new SealGatherLogic.BoundingBox(0, 0, 0, 10, 10, 10);

        // Helper to create configurable mock item adapter
        class MockItemAdapter implements SealGatherLogic.ItemAdapter {
            boolean isAlive = true;
            boolean hasPickUpDelay = false;
            int stackSize = 1;
            boolean matchesFilter = true;

            @Override public Object getEntityReference() { return new Object(); }
            @Override public double getMinX() { return 2; }
            @Override public double getMinY() { return 2; }
            @Override public double getMinZ() { return 2; }
            @Override public double getMaxX() { return 3; }
            @Override public double getMaxY() { return 3; }
            @Override public double getMaxZ() { return 3; }
            @Override public boolean isAlive() { return isAlive; }
            @Override public boolean hasPickUpDelay() { return hasPickUpDelay; }
            @Override public int getStackSize() { return stackSize; }
            @Override public boolean matchesFilter() { return matchesFilter; }
        }

        // Dead item
        MockItemAdapter deadItem = new MockItemAdapter();
        deadItem.isAlive = false;
        assertNull(SealGatherLogic.evaluateItemAndCreateTask(area, deadItem, (byte) 5), "Task should not be generated for a dead item");

        // Item with pickup delay
        MockItemAdapter delayedItem = new MockItemAdapter();
        delayedItem.hasPickUpDelay = true;
        assertNull(SealGatherLogic.evaluateItemAndCreateTask(area, delayedItem, (byte) 5), "Task should not be generated for an item with pickup delay");

        // Item with 0 stack size
        MockItemAdapter emptyStackItem = new MockItemAdapter();
        emptyStackItem.stackSize = 0;
        assertNull(SealGatherLogic.evaluateItemAndCreateTask(area, emptyStackItem, (byte) 5), "Task should not be generated for an empty stack");

        // Item failing filter
        MockItemAdapter unfilteredItem = new MockItemAdapter();
        unfilteredItem.matchesFilter = false;
        assertNull(SealGatherLogic.evaluateItemAndCreateTask(area, unfilteredItem, (byte) 5), "Task should not be generated for an item that doesn't match the filter");
    }
}
