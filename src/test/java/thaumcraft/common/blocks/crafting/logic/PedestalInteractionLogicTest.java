package thaumcraft.common.blocks.crafting.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PedestalInteractionLogicTest {

    @Test
    @DisplayName("canPlaceItem: allows placing only when pedestal empty, hand not empty, and not sneaking")
    public void testCanPlaceItem() {
        // Empty pedestal with item in hand and not sneaking -> allowed
        assertTrue(PedestalInteractionLogic.canPlaceItem(false, false, false));

        // Pedestal already has an item -> forbidden
        assertFalse(PedestalInteractionLogic.canPlaceItem(true, false, false));

        // Empty hand -> forbidden
        assertFalse(PedestalInteractionLogic.canPlaceItem(false, true, false));

        // Sneaking while placing -> forbidden (allows block placement / other interactions)
        assertFalse(PedestalInteractionLogic.canPlaceItem(false, false, true));

        // All false/edge cases
        assertFalse(PedestalInteractionLogic.canPlaceItem(true, true, true));
    }

    @Test
    @DisplayName("calculateRemainingHeld: decrements in survival, preserves count in creative")
    public void testCalculateRemainingHeld() {
        // Survival mode: count - 1
        assertEquals(63, PedestalInteractionLogic.calculateRemainingHeld(64, false));
        assertEquals(0, PedestalInteractionLogic.calculateRemainingHeld(1, false));
        assertEquals(0, PedestalInteractionLogic.calculateRemainingHeld(0, false));

        // Creative mode: count unchanged
        assertEquals(64, PedestalInteractionLogic.calculateRemainingHeld(64, true));
        assertEquals(1, PedestalInteractionLogic.calculateRemainingHeld(1, true));
        assertEquals(0, PedestalInteractionLogic.calculateRemainingHeld(0, true));
    }

    @Test
    @DisplayName("canExtractItem: allows extracting when pedestal has item and hand is empty or not sneaking")
    public void testCanExtractItem() {
        // Occupied pedestal, empty hand, not sneaking -> allowed
        assertTrue(PedestalInteractionLogic.canExtractItem(true, true, false));

        // Occupied pedestal, empty hand, sneaking -> allowed
        assertTrue(PedestalInteractionLogic.canExtractItem(true, true, true));

        // Occupied pedestal, held item, not sneaking -> allowed (extracts item without consuming held item)
        assertTrue(PedestalInteractionLogic.canExtractItem(true, false, false));

        // Occupied pedestal, held item, sneaking -> forbidden (allows block placement)
        assertFalse(PedestalInteractionLogic.canExtractItem(true, false, true));

        // Empty pedestal -> always false
        assertFalse(PedestalInteractionLogic.canExtractItem(false, true, false));
        assertFalse(PedestalInteractionLogic.canExtractItem(false, true, true));
        assertFalse(PedestalInteractionLogic.canExtractItem(false, false, false));
        assertFalse(PedestalInteractionLogic.canExtractItem(false, false, true));
    }

    @Test
    @DisplayName("calculateComparatorSignal: returns 15 if holding item, 0 if empty")
    public void testCalculateComparatorSignal() {
        assertEquals(15, PedestalInteractionLogic.calculateComparatorSignal(true));
        assertEquals(0, PedestalInteractionLogic.calculateComparatorSignal(false));
    }

    @Test
    @DisplayName("hasSymmetryPenalty: triggers penalty only if item presence is mismatched")
    public void testHasSymmetryPenalty() {
        // Both pedestals have items -> symmetrical (false)
        assertFalse(PedestalInteractionLogic.hasSymmetryPenalty(true, true));

        // Both pedestals empty -> symmetrical (false)
        assertFalse(PedestalInteractionLogic.hasSymmetryPenalty(false, false));

        // One has item, other does not -> asymmetrical (true)
        assertTrue(PedestalInteractionLogic.hasSymmetryPenalty(true, false));
        assertTrue(PedestalInteractionLogic.hasSymmetryPenalty(false, true));
    }

    @Test
    @DisplayName("shouldDropItemOnRemoval: drops only if block replaced by different block and has item")
    public void testShouldDropItemOnRemoval() {
        String pedestalId = "thaumcraft:pedestal_arcane";
        String airId = "minecraft:air";
        String differentId = "minecraft:stone";

        // Replaced by air with item -> true
        assertTrue(PedestalInteractionLogic.shouldDropItemOnRemoval(pedestalId, airId, true));

        // Replaced by stone with item -> true
        assertTrue(PedestalInteractionLogic.shouldDropItemOnRemoval(pedestalId, differentId, true));

        // Replaced by same block type (state update) with item -> false
        assertFalse(PedestalInteractionLogic.shouldDropItemOnRemoval(pedestalId, pedestalId, true));

        // Empty pedestal -> false
        assertFalse(PedestalInteractionLogic.shouldDropItemOnRemoval(pedestalId, airId, false));
        assertFalse(PedestalInteractionLogic.shouldDropItemOnRemoval(pedestalId, pedestalId, false));

        // Null checks
        assertFalse(PedestalInteractionLogic.shouldDropItemOnRemoval(null, airId, true));
        assertFalse(PedestalInteractionLogic.shouldDropItemOnRemoval(pedestalId, null, false));
        assertTrue(PedestalInteractionLogic.shouldDropItemOnRemoval(pedestalId, null, true));
    }
}
