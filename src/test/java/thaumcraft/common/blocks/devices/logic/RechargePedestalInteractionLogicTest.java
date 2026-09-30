package thaumcraft.common.blocks.devices.logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RechargePedestalInteractionLogicTest {

    @Test
    public void testCanInsert() {
        // Valid insertion: pedestal empty, rechargeable item, positive count
        assertTrue(RechargePedestalInteractionLogic.canInsert(false, true, 1));
        assertTrue(RechargePedestalInteractionLogic.canInsert(false, true, 64));

        // Invalid: pedestal already occupied
        assertFalse(RechargePedestalInteractionLogic.canInsert(true, true, 1));
        assertFalse(RechargePedestalInteractionLogic.canInsert(true, true, 64));

        // Invalid: item not rechargeable
        assertFalse(RechargePedestalInteractionLogic.canInsert(false, false, 1));
        assertFalse(RechargePedestalInteractionLogic.canInsert(false, false, 64));

        // Invalid: zero or negative stack count
        assertFalse(RechargePedestalInteractionLogic.canInsert(false, true, 0));
        assertFalse(RechargePedestalInteractionLogic.canInsert(false, true, -1));
        assertFalse(RechargePedestalInteractionLogic.canInsert(false, true, -100));

        // All invalid
        assertFalse(RechargePedestalInteractionLogic.canInsert(true, false, 0));
    }

    @Test
    public void testCanExtract() {
        // Can extract only when pedestal has an item
        assertTrue(RechargePedestalInteractionLogic.canExtract(true));
        assertFalse(RechargePedestalInteractionLogic.canExtract(false));
    }

    @Test
    public void testCanDeliverExtractedItem() {
        // Can deliver if player exists (to inventory)
        assertTrue(RechargePedestalInteractionLogic.canDeliverExtractedItem(true, false));
        // Can deliver if world location exists (dropped to ground)
        assertTrue(RechargePedestalInteractionLogic.canDeliverExtractedItem(false, true));
        // Can deliver if both exist
        assertTrue(RechargePedestalInteractionLogic.canDeliverExtractedItem(true, true));
        // Cannot deliver if neither exists (would permanently delete item)
        assertFalse(RechargePedestalInteractionLogic.canDeliverExtractedItem(false, false));
    }

    @Test
    public void testCalculateRemainingStackCount() {
        assertEquals(63, RechargePedestalInteractionLogic.calculateRemainingStackCount(64));
        assertEquals(1, RechargePedestalInteractionLogic.calculateRemainingStackCount(2));
        assertEquals(0, RechargePedestalInteractionLogic.calculateRemainingStackCount(1));
        assertEquals(0, RechargePedestalInteractionLogic.calculateRemainingStackCount(0));
        assertEquals(0, RechargePedestalInteractionLogic.calculateRemainingStackCount(-5));
    }

    @Test
    public void testCalculateComparatorSignal() {
        // Empty pedestal always emits 0 signal
        assertEquals(0, RechargePedestalInteractionLogic.calculateComparatorSignal(false, 0, 100));
        assertEquals(0, RechargePedestalInteractionLogic.calculateComparatorSignal(false, 50, 100));
        assertEquals(0, RechargePedestalInteractionLogic.calculateComparatorSignal(false, 100, 100));

        // Occupied pedestal with maxCharge <= 0 emits full signal (15)
        assertEquals(15, RechargePedestalInteractionLogic.calculateComparatorSignal(true, 0, 0));
        assertEquals(15, RechargePedestalInteractionLogic.calculateComparatorSignal(true, 50, -10));

        // Proportional signal calculation
        assertEquals(0, RechargePedestalInteractionLogic.calculateComparatorSignal(true, 0, 100));
        assertEquals(0, RechargePedestalInteractionLogic.calculateComparatorSignal(true, -10, 100));
        assertEquals(1, RechargePedestalInteractionLogic.calculateComparatorSignal(true, 10, 100));
        assertEquals(7, RechargePedestalInteractionLogic.calculateComparatorSignal(true, 50, 100));
        assertEquals(14, RechargePedestalInteractionLogic.calculateComparatorSignal(true, 95, 100));
        assertEquals(15, RechargePedestalInteractionLogic.calculateComparatorSignal(true, 100, 100));
        assertEquals(15, RechargePedestalInteractionLogic.calculateComparatorSignal(true, 200, 100));

        // 64-bit integer overflow protection with large numbers
        assertEquals(15, RechargePedestalInteractionLogic.calculateComparatorSignal(true, Integer.MAX_VALUE, Integer.MAX_VALUE));
        assertEquals(7, RechargePedestalInteractionLogic.calculateComparatorSignal(true, Integer.MAX_VALUE / 2, Integer.MAX_VALUE));
    }
}
