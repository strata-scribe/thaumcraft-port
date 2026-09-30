package thaumcraft.common.tiles.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ArcaneWorkbenchLogic}.
 */
public class ArcaneWorkbenchLogicTest {

    @Nested
    @DisplayName("Redstone Comparator Signal Calculation")
    class ComparatorSignalTests {

        @Test
        @DisplayName("Empty container returns 0 signal")
        void testEmptyContainerReturnsZero() {
            assertEquals(0, ArcaneWorkbenchLogic.calculateComparatorSignal(15, 0, 0.0));
            assertEquals(0, ArcaneWorkbenchLogic.calculateComparatorSignal(9, 0, 0.0));
        }

        @Test
        @DisplayName("Invalid or non-positive total slots returns 0 signal")
        void testInvalidTotalSlotsReturnsZero() {
            assertEquals(0, ArcaneWorkbenchLogic.calculateComparatorSignal(0, 5, 5.0));
            assertEquals(0, ArcaneWorkbenchLogic.calculateComparatorSignal(-1, 5, 5.0));
            assertEquals(0, ArcaneWorkbenchLogic.calculateComparatorSignal(-15, 1, 1.0));
        }

        @Test
        @DisplayName("Non-positive filled slots returns 0 signal")
        void testNonPositiveFilledSlotsReturnsZero() {
            assertEquals(0, ArcaneWorkbenchLogic.calculateComparatorSignal(15, -1, 5.0));
            assertEquals(0, ArcaneWorkbenchLogic.calculateComparatorSignal(15, 0, 5.0));
        }

        @Test
        @DisplayName("Zero, negative, or NaN fill ratio returns 0 signal")
        void testInvalidFillRatioReturnsZero() {
            assertEquals(0, ArcaneWorkbenchLogic.calculateComparatorSignal(15, 5, 0.0));
            assertEquals(0, ArcaneWorkbenchLogic.calculateComparatorSignal(15, 5, -0.5));
            assertEquals(0, ArcaneWorkbenchLogic.calculateComparatorSignal(15, 5, Double.NaN));
        }

        @Test
        @DisplayName("Single item in container produces at least signal 1 (vanilla parity)")
        void testSingleItemProducesSignalOne() {
            // 1 item with stack size 64 in a 15-slot container
            double singleItemRatio = 1.0 / 64.0;
            int signal = ArcaneWorkbenchLogic.calculateComparatorSignal(15, 1, singleItemRatio);
            assertEquals(1, signal, "A single item in a container must output comparator signal 1");

            // 1 item with stack size 64 in a 9-slot container
            int signal9 = ArcaneWorkbenchLogic.calculateComparatorSignal(9, 1, singleItemRatio);
            assertEquals(1, signal9, "A single item in a 9-slot container must output comparator signal 1");
        }

        @Test
        @DisplayName("Single half-stack in container produces signal 1")
        void testHalfStackInOneSlotProducesSignalOne() {
            // 32 items with stack size 64 in a 15-slot container -> fill ratio 0.5
            // factor = 0.5 / 15 = 1/30; floor(1/30 * 14) + 1 = 0 + 1 = 1
            int signal = ArcaneWorkbenchLogic.calculateComparatorSignal(15, 1, 0.5);
            assertEquals(1, signal);
        }

        @Test
        @DisplayName("Half full container produces signal 8")
        void testHalfFullContainerProducesSignalEight() {
            // Half-filled: factor = 0.5
            // factor * 14 = 7.0; floor(7.0) + 1 = 8
            int signal15 = ArcaneWorkbenchLogic.calculateComparatorSignal(15, 15, 7.5);
            assertEquals(8, signal15, "Half full 15-slot container must output signal 8");

            int signal9 = ArcaneWorkbenchLogic.calculateComparatorSignal(9, 9, 4.5);
            assertEquals(8, signal9, "Half full 9-slot container must output signal 8");
        }

        @Test
        @DisplayName("Fully filled container produces maximum signal 15")
        void testFullyFilledContainerProducesSignalFifteen() {
            // Full: factor = 1.0 -> factor * 14 = 14; floor(14) + 1 = 15
            int signal15 = ArcaneWorkbenchLogic.calculateComparatorSignal(15, 15, 15.0);
            assertEquals(15, signal15, "Completely full container must output signal 15");

            int signal9 = ArcaneWorkbenchLogic.calculateComparatorSignal(9, 9, 9.0);
            assertEquals(15, signal9, "Completely full 9-slot container must output signal 15");
        }

        @Test
        @DisplayName("Nearly full container produces signal 14")
        void testNearlyFullContainerProducesSignalFourteen() {
            // factor = 14.0 / 15.0 -> factor * 14 = 13.066... -> floor + 1 = 14
            int signal = ArcaneWorkbenchLogic.calculateComparatorSignal(15, 15, 14.0);
            assertEquals(14, signal);
        }

        @Test
        @DisplayName("Over-filled container is safely clamped to 15")
        void testOverflowClampsToFifteen() {
            int signal = ArcaneWorkbenchLogic.calculateComparatorSignal(15, 15, 25.0);
            assertEquals(15, signal, "Over-filled container ratio must clamp to 15");
        }

        @Test
        @DisplayName("Comparator signal increases monotonically with fill ratio")
        void testMonotonicity() {
            int previousSignal = 0;
            for (double ratio = 0.0; ratio <= 15.0; ratio += 0.25) {
                int filledSlots = (ratio > 0.0) ? (int) Math.ceil(ratio) : 0;
                int signal = ArcaneWorkbenchLogic.calculateComparatorSignal(15, filledSlots, ratio);
                assertTrue(signal >= previousSignal, "Signal should be monotonically non-decreasing: at ratio " + ratio);
                assertTrue(signal >= 0 && signal <= 15, "Signal must remain in range [0, 15]");
                previousSignal = signal;
            }
            assertEquals(15, previousSignal);
        }

        @Test
        @DisplayName("All signal levels 0 through 15 are attainable")
        void testAllSignalLevelsAttainable() {
            boolean[] reached = new boolean[16];
            for (int step = 0; step <= 1000; step++) {
                double ratio = (step / 1000.0) * 15.0;
                int filled = (step == 0) ? 0 : 15;
                int signal = ArcaneWorkbenchLogic.calculateComparatorSignal(15, filled, ratio);
                reached[signal] = true;
            }
            for (int s = 0; s <= 15; s++) {
                assertTrue(reached[s], "Signal level " + s + " must be attainable");
            }
        }
    }

    @Nested
    @DisplayName("Content Drop Conditions")
    class ShouldDropContentsTests {

        @Test
        @DisplayName("Different block IDs trigger item drops")
        void testDifferentBlockIdDropsContents() {
            assertTrue(ArcaneWorkbenchLogic.shouldDropContents("thaumcraft:arcane_workbench", "minecraft:air"));
            assertTrue(ArcaneWorkbenchLogic.shouldDropContents("thaumcraft:arcane_workbench", "minecraft:dirt"));
            assertTrue(ArcaneWorkbenchLogic.shouldDropContents("thaumcraft:arcane_workbench", "thaumcraft:crucible"));
        }

        @Test
        @DisplayName("Identical block IDs do not trigger item drops (e.g. blockstate property change)")
        void testSameBlockIdPreservesContents() {
            assertFalse(ArcaneWorkbenchLogic.shouldDropContents("thaumcraft:arcane_workbench", "thaumcraft:arcane_workbench"));
            assertFalse(ArcaneWorkbenchLogic.shouldDropContents("minecraft:air", "minecraft:air"));
        }

        @Test
        @DisplayName("Null oldBlockId safely returns false without exception")
        void testNullOldBlockIdReturnsFalse() {
            assertFalse(ArcaneWorkbenchLogic.shouldDropContents(null, "thaumcraft:arcane_workbench"));
            assertFalse(ArcaneWorkbenchLogic.shouldDropContents(null, null));
        }

        @Test
        @DisplayName("Null newBlockId with valid oldBlockId returns true")
        void testNullNewBlockIdReturnsTrue() {
            assertTrue(ArcaneWorkbenchLogic.shouldDropContents("thaumcraft:arcane_workbench", null));
        }
    }

    @Nested
    @DisplayName("Workbench Interaction & Open Rules")
    class CanOpenWorkbenchTests {

        @Test
        @DisplayName("Normal right click allows opening workbench")
        void testNormalClickOpensWorkbench() {
            assertTrue(ArcaneWorkbenchLogic.canOpenWorkbench(false),
                    "Workbench should open when sneak/shift is not held");
        }

        @Test
        @DisplayName("Shift/sneak right click prevents opening workbench")
        void testSneakClickDoesNotOpenWorkbench() {
            assertFalse(ArcaneWorkbenchLogic.canOpenWorkbench(true),
                    "Workbench should not open when sneaking (allows block placement on it)");
        }
    }

    @Nested
    @DisplayName("Inventory Slot Classification")
    class SlotClassificationTests {

        @Test
        @DisplayName("Slots 0 through 8 are classified as crafting slots")
        void testCraftingSlotsValid() {
            for (int slot = 0; slot <= 8; slot++) {
                assertTrue(ArcaneWorkbenchLogic.isCraftingSlot(slot), "Slot " + slot + " should be a crafting slot");
                assertFalse(ArcaneWorkbenchLogic.isCrystalSlot(slot), "Slot " + slot + " should not be a crystal slot");
            }
        }

        @Test
        @DisplayName("Slots 9 through 14 are classified as crystal slots")
        void testCrystalSlotsValid() {
            for (int slot = 9; slot <= 14; slot++) {
                assertTrue(ArcaneWorkbenchLogic.isCrystalSlot(slot), "Slot " + slot + " should be a crystal slot");
                assertFalse(ArcaneWorkbenchLogic.isCraftingSlot(slot), "Slot " + slot + " should not be a crafting slot");
            }
        }

        @Test
        @DisplayName("Out of bounds slot indices are rejected by both classifications")
        void testOutOfBoundsSlots() {
            int[] outOfBounds = {-10, -1, 15, 16, 20, 100};
            for (int slot : outOfBounds) {
                assertFalse(ArcaneWorkbenchLogic.isCraftingSlot(slot), "Slot " + slot + " should not be crafting slot");
                assertFalse(ArcaneWorkbenchLogic.isCrystalSlot(slot), "Slot " + slot + " should not be crystal slot");
            }
        }

        @Test
        @DisplayName("Inventory slots 0..14 form an exact disjoint partition")
        void testExactDisjointPartition() {
            int craftCount = 0;
            int crystalCount = 0;

            for (int i = 0; i < 15; i++) {
                boolean isCraft = ArcaneWorkbenchLogic.isCraftingSlot(i);
                boolean isCrystal = ArcaneWorkbenchLogic.isCrystalSlot(i);

                assertTrue(isCraft ^ isCrystal, "Slot " + i + " must be either crafting or crystal, never both or neither");
                if (isCraft) craftCount++;
                if (isCrystal) crystalCount++;
            }

            assertEquals(9, craftCount, "Must have exactly 9 crafting slots");
            assertEquals(6, crystalCount, "Must have exactly 6 crystal slots");
        }
    }
}
