package thaumcraft.common.items.casters.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FocusPouchInventoryLogicTest {

    @Test
    public void testCalculatePouchCapacity() {
        assertEquals(18, FocusPouchInventoryLogic.calculatePouchCapacity(18, 0));
        assertEquals(36, FocusPouchInventoryLogic.calculatePouchCapacity(18, 1));
        assertEquals(54, FocusPouchInventoryLogic.calculatePouchCapacity(18, 2));
    }

    @Test
    public void testGetNextFocusIndexForward() {
        boolean[] populated = {false, true, false, true, false};
        assertEquals(1, FocusPouchInventoryLogic.getNextFocusIndex(-1, 5, false, populated));
        assertEquals(3, FocusPouchInventoryLogic.getNextFocusIndex(1, 5, false, populated));
        assertEquals(1, FocusPouchInventoryLogic.getNextFocusIndex(3, 5, false, populated));
        assertEquals(1, FocusPouchInventoryLogic.getNextFocusIndex(4, 5, false, populated));
    }

    @Test
    public void testGetNextFocusIndexBackward() {
        boolean[] populated = {false, true, false, true, false};
        assertEquals(3, FocusPouchInventoryLogic.getNextFocusIndex(-1, 5, true, populated));
        assertEquals(3, FocusPouchInventoryLogic.getNextFocusIndex(0, 5, true, populated));
        assertEquals(1, FocusPouchInventoryLogic.getNextFocusIndex(3, 5, true, populated));
        assertEquals(3, FocusPouchInventoryLogic.getNextFocusIndex(1, 5, true, populated));
    }

    @Test
    public void testGetNextFocusIndexEmpty() {
        boolean[] populated = {false, false, false};
        assertEquals(-1, FocusPouchInventoryLogic.getNextFocusIndex(0, 3, false, populated));
        assertEquals(-1, FocusPouchInventoryLogic.getNextFocusIndex(0, 3, true, populated));

        boolean[] empty = {};
        assertEquals(-1, FocusPouchInventoryLogic.getNextFocusIndex(0, 3, false, empty));

        assertEquals(-1, FocusPouchInventoryLogic.getNextFocusIndex(0, 3, false, null));
    }

    @Test
    public void testGetNextFocusIndexSingle() {
        boolean[] populated = {false, true, false};
        assertEquals(1, FocusPouchInventoryLogic.getNextFocusIndex(1, 3, false, populated));
        assertEquals(1, FocusPouchInventoryLogic.getNextFocusIndex(1, 3, true, populated));
        assertEquals(1, FocusPouchInventoryLogic.getNextFocusIndex(0, 3, false, populated));
        assertEquals(1, FocusPouchInventoryLogic.getNextFocusIndex(2, 3, true, populated));
    }

    @Test
    public void testGetNextFocusIndexCapacityLimit() {
        boolean[] populated = {false, true, false, true, true};
        // Capacity is only 3, so index 3 and 4 are ignored
        assertEquals(1, FocusPouchInventoryLogic.getNextFocusIndex(-1, 3, false, populated));
        assertEquals(1, FocusPouchInventoryLogic.getNextFocusIndex(1, 3, false, populated));
        assertEquals(1, FocusPouchInventoryLogic.getNextFocusIndex(1, 3, true, populated));
        assertEquals(1, FocusPouchInventoryLogic.getNextFocusIndex(2, 3, false, populated));
    }

    @Test
    public void testCalculateInventoryPersistence() {
        String[] inv = {"ItemA", "ItemB", "ItemC", "ItemD"};
        int[] keep = {1, 3};

        String[] result = FocusPouchInventoryLogic.calculateInventoryPersistence(inv, keep);

        assertNotNull(result);
        assertEquals(4, result.length);
        assertNull(result[0]);
        assertEquals("ItemB", result[1]);
        assertNull(result[2]);
        assertEquals("ItemD", result[3]);
    }

    @Test
    public void testCalculateInventoryPersistenceEdgeCases() {
        assertNull(FocusPouchInventoryLogic.calculateInventoryPersistence(null, new int[]{1}));

        String[] inv = {"ItemA"};
        String[] result = FocusPouchInventoryLogic.calculateInventoryPersistence(inv, null);
        assertNotNull(result);
        assertEquals(1, result.length);
        assertNull(result[0]);

        String[] result2 = FocusPouchInventoryLogic.calculateInventoryPersistence(inv, new int[]{5});
        assertNotNull(result2);
        assertEquals(1, result2.length);
        assertNull(result2[0]);
    }
}
