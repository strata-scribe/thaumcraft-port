package thaumcraft.common.blocks.essentia.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JarInteractionLogic Unit Tests")
public class JarInteractionLogicTest {

    @Nested
    @DisplayName("calculateComparatorSignal")
    class ComparatorTests {

        @Test
        @DisplayName("Zero or negative essentia returns 0")
        void testZeroOrNegativeAmount() {
            assertEquals(0, JarInteractionLogic.calculateComparatorSignal(0, 250));
            assertEquals(0, JarInteractionLogic.calculateComparatorSignal(-1, 250));
            assertEquals(0, JarInteractionLogic.calculateComparatorSignal(-100, 250));
        }

        @Test
        @DisplayName("Zero or negative capacity returns 0")
        void testZeroOrNegativeCapacity() {
            assertEquals(0, JarInteractionLogic.calculateComparatorSignal(100, 0));
            assertEquals(0, JarInteractionLogic.calculateComparatorSignal(100, -250));
            assertEquals(0, JarInteractionLogic.calculateComparatorSignal(0, 0));
        }

        @Test
        @DisplayName("Minimal amount (1/250) returns 0 under floor division")
        void testMinimalAmount() {
            // (1 * 15) / 250 = 0.06 -> floor = 0
            assertEquals(0, JarInteractionLogic.calculateComparatorSignal(1, 250));
        }

        @Test
        @DisplayName("Half capacity (125/250) returns 7")
        void testHalfCapacity() {
            // (125 * 15) / 250 = 7.5 -> floor = 7
            assertEquals(7, JarInteractionLogic.calculateComparatorSignal(125, 250));
        }

        @Test
        @DisplayName("Full capacity (250/250) returns 15")
        void testFullCapacity() {
            assertEquals(15, JarInteractionLogic.calculateComparatorSignal(250, 250));
        }

        @Test
        @DisplayName("Overfilled essentia clamps to 15")
        void testOverfillClamping() {
            assertEquals(15, JarInteractionLogic.calculateComparatorSignal(251, 250));
            assertEquals(15, JarInteractionLogic.calculateComparatorSignal(300, 250));
            assertEquals(15, JarInteractionLogic.calculateComparatorSignal(1000, 250));
        }

        @Test
        @DisplayName("Signal scales monotonically across full capacity range [0, 250]")
        void testMonotonicity() {
            int prev = 0;
            for (int amount = 0; amount <= 250; amount++) {
                int signal = JarInteractionLogic.calculateComparatorSignal(amount, 250);
                assertTrue(signal >= prev, "Comparator signal must be monotonically non-decreasing");
                assertTrue(signal >= 0 && signal <= 15, "Signal must be in range [0, 15]");
                prev = signal;
            }
        }
    }

    @Nested
    @DisplayName("canApplyLabel")
    class ApplyLabelTests {

        @Test
        @DisplayName("No filter present and label item held returns true")
        void testValidApplyLabel() {
            assertTrue(JarInteractionLogic.canApplyLabel(false, true));
        }

        @Test
        @DisplayName("Already filtered jar returns false")
        void testAlreadyFilteredReturnsFalse() {
            assertFalse(JarInteractionLogic.canApplyLabel(true, true));
        }

        @Test
        @DisplayName("Item is not a label returns false")
        void testNotLabelReturnsFalse() {
            assertFalse(JarInteractionLogic.canApplyLabel(false, false));
        }

        @Test
        @DisplayName("Already filtered and not a label returns false")
        void testFilteredAndNotLabelReturnsFalse() {
            assertFalse(JarInteractionLogic.canApplyLabel(true, false));
        }
    }

    @Nested
    @DisplayName("canRemoveLabel")
    class RemoveLabelTests {

        @Test
        @DisplayName("Sneaking player and filter present returns true")
        void testValidRemoveLabel() {
            assertTrue(JarInteractionLogic.canRemoveLabel(true, true));
        }

        @Test
        @DisplayName("Not sneaking returns false")
        void testNotSneakingReturnsFalse() {
            assertFalse(JarInteractionLogic.canRemoveLabel(false, true));
        }

        @Test
        @DisplayName("No filter present returns false")
        void testNoFilterReturnsFalse() {
            assertFalse(JarInteractionLogic.canRemoveLabel(true, false));
        }

        @Test
        @DisplayName("Not sneaking and no filter returns false")
        void testNotSneakingNoFilterReturnsFalse() {
            assertFalse(JarInteractionLogic.canRemoveLabel(false, false));
        }
    }

    @Nested
    @DisplayName("canFillFromPhial")
    class FillFromPhialTests {

        @Test
        @DisplayName("Normal jar accepts phial when room is available")
        void testNormalJarWithRoom() {
            assertTrue(JarInteractionLogic.canFillFromPhial(true, true, 0, 250, 10, false));
            assertTrue(JarInteractionLogic.canFillFromPhial(true, true, 100, 250, 10, false));
            assertTrue(JarInteractionLogic.canFillFromPhial(true, true, 240, 250, 10, false));
        }

        @Test
        @DisplayName("Normal jar rejects phial when insufficient room (< phialAmount)")
        void testNormalJarOverflowRejected() {
            assertFalse(JarInteractionLogic.canFillFromPhial(true, true, 241, 250, 10, false));
            assertFalse(JarInteractionLogic.canFillFromPhial(true, true, 245, 250, 10, false));
            assertFalse(JarInteractionLogic.canFillFromPhial(true, true, 250, 250, 10, false));
        }

        @Test
        @DisplayName("Void jar accepts phial even when full or beyond capacity")
        void testVoidJarAcceptsBeyondCapacity() {
            assertTrue(JarInteractionLogic.canFillFromPhial(true, true, 240, 250, 10, true));
            assertTrue(JarInteractionLogic.canFillFromPhial(true, true, 241, 250, 10, true));
            assertTrue(JarInteractionLogic.canFillFromPhial(true, true, 250, 250, 10, true));
            assertTrue(JarInteractionLogic.canFillFromPhial(true, true, 300, 250, 10, true));
        }

        @Test
        @DisplayName("Aspect mismatch rejected for both normal and void jars")
        void testAspectMismatchRejected() {
            assertFalse(JarInteractionLogic.canFillFromPhial(true, false, 0, 250, 10, false));
            assertFalse(JarInteractionLogic.canFillFromPhial(true, false, 250, 250, 10, true));
        }

        @Test
        @DisplayName("Unfilled phial rejected")
        void testUnfilledPhialRejected() {
            assertFalse(JarInteractionLogic.canFillFromPhial(false, true, 0, 250, 10, false));
            assertFalse(JarInteractionLogic.canFillFromPhial(false, true, 0, 250, 10, true));
        }
    }

    @Nested
    @DisplayName("canDrainToPhial")
    class DrainToPhialTests {

        @Test
        @DisplayName("Empty phial can drain when stored amount >= phial amount")
        void testValidDrain() {
            assertTrue(JarInteractionLogic.canDrainToPhial(true, 10, 10));
            assertTrue(JarInteractionLogic.canDrainToPhial(true, 50, 10));
            assertTrue(JarInteractionLogic.canDrainToPhial(true, 250, 10));
        }

        @Test
        @DisplayName("Drain rejected when stored amount < phial amount")
        void testInsufficientStoredEssentia() {
            assertFalse(JarInteractionLogic.canDrainToPhial(true, 9, 10));
            assertFalse(JarInteractionLogic.canDrainToPhial(true, 5, 10));
            assertFalse(JarInteractionLogic.canDrainToPhial(true, 1, 10));
        }

        @Test
        @DisplayName("Drain rejected when jar is empty (0 stored)")
        void testEmptyJarRejected() {
            assertFalse(JarInteractionLogic.canDrainToPhial(true, 0, 10));
            assertFalse(JarInteractionLogic.canDrainToPhial(true, -10, 10));
        }

        @Test
        @DisplayName("Drain rejected when phial is not empty")
        void testNonEmptyPhialRejected() {
            assertFalse(JarInteractionLogic.canDrainToPhial(false, 50, 10));
            assertFalse(JarInteractionLogic.canDrainToPhial(false, 250, 10));
        }
    }
}
