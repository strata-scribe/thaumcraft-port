package thaumcraft.common.blocks.essentia.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AlembicInteractionLogic Unit Tests")
public class AlembicInteractionLogicTest {

    @Nested
    @DisplayName("calculateComparatorSignal")
    class ComparatorTests {

        @Test
        @DisplayName("Zero or negative essentia returns 0")
        void testZeroOrNegativeAmount() {
            assertEquals(0, AlembicInteractionLogic.calculateComparatorSignal(0, 32));
            assertEquals(0, AlembicInteractionLogic.calculateComparatorSignal(-1, 32));
            assertEquals(0, AlembicInteractionLogic.calculateComparatorSignal(-50, 32));
        }

        @Test
        @DisplayName("Invalid or non-positive capacity returns 0")
        void testInvalidCapacity() {
            assertEquals(0, AlembicInteractionLogic.calculateComparatorSignal(10, 0));
            assertEquals(0, AlembicInteractionLogic.calculateComparatorSignal(10, -32));
            assertEquals(0, AlembicInteractionLogic.calculateComparatorSignal(0, 0));
        }

        @Test
        @DisplayName("Single unit of essentia returns 1")
        void testSingleUnitReturnsOne() {
            assertEquals(1, AlembicInteractionLogic.calculateComparatorSignal(1, 32));
        }

        @Test
        @DisplayName("Half capacity returns expected signal (8 for 16/32)")
        void testHalfCapacity() {
            assertEquals(8, AlembicInteractionLogic.calculateComparatorSignal(16, 32));
        }

        @Test
        @DisplayName("Full capacity returns 15")
        void testFullCapacity() {
            assertEquals(15, AlembicInteractionLogic.calculateComparatorSignal(32, 32));
        }

        @Test
        @DisplayName("Overfilled essentia clamps to 15")
        void testOverfillClampsTo15() {
            assertEquals(15, AlembicInteractionLogic.calculateComparatorSignal(33, 32));
            assertEquals(15, AlembicInteractionLogic.calculateComparatorSignal(64, 32));
            assertEquals(15, AlembicInteractionLogic.calculateComparatorSignal(1000, 32));
        }

        @Test
        @DisplayName("Intermediate amounts scale monotonically")
        void testIntermediateValues() {
            int prev = 0;
            for (int i = 0; i <= 32; i++) {
                int signal = AlembicInteractionLogic.calculateComparatorSignal(i, 32);
                assertTrue(signal >= prev, "Signal should be monotonically non-decreasing");
                assertTrue(signal >= 0 && signal <= 15, "Signal must be between 0 and 15");
                prev = signal;
            }
        }
    }

    @Nested
    @DisplayName("canSurviveOn")
    class CanSurviveOnTests {

        @Test
        @DisplayName("Smelter variants return true")
        void testSmelterVariants() {
            assertTrue(AlembicInteractionLogic.canSurviveOn("thaumcraft:smelter_basic"));
            assertTrue(AlembicInteractionLogic.canSurviveOn("thaumcraft:smelter_thaumium"));
            assertTrue(AlembicInteractionLogic.canSurviveOn("thaumcraft:smelter_void"));
            assertTrue(AlembicInteractionLogic.canSurviveOn("Block{thaumcraft:smelter_basic}"));
            assertTrue(AlembicInteractionLogic.canSurviveOn("SMELTER_BASIC"));
        }

        @Test
        @DisplayName("Alembic returns true (stacking)")
        void testAlembic() {
            assertTrue(AlembicInteractionLogic.canSurviveOn("alembic"));
            assertTrue(AlembicInteractionLogic.canSurviveOn("thaumcraft:alembic"));
            assertTrue(AlembicInteractionLogic.canSurviveOn("Block{thaumcraft:alembic}"));
            assertTrue(AlembicInteractionLogic.canSurviveOn("ALEMBIC"));
        }

        @Test
        @DisplayName("Other blocks return false")
        void testNonSupportingBlocks() {
            assertFalse(AlembicInteractionLogic.canSurviveOn("minecraft:dirt"));
            assertFalse(AlembicInteractionLogic.canSurviveOn("minecraft:stone"));
            assertFalse(AlembicInteractionLogic.canSurviveOn("minecraft:air"));
            assertFalse(AlembicInteractionLogic.canSurviveOn("thaumcraft:crucible"));
            assertFalse(AlembicInteractionLogic.canSurviveOn("thaumcraft:arcane_workbench"));
        }

        @Test
        @DisplayName("Null returns false")
        void testNullBlockId() {
            assertFalse(AlembicInteractionLogic.canSurviveOn(null));
        }
    }

    @Nested
    @DisplayName("canRemoveLabel")
    class CanRemoveLabelTests {

        @Test
        @DisplayName("Sneak + filter present + hit face matches label face returns true")
        void testValidLabelRemoval() {
            assertTrue(AlembicInteractionLogic.canRemoveLabel(true, true, 2, 2));
            assertTrue(AlembicInteractionLogic.canRemoveLabel(true, true, 5, 5));
        }

        @Test
        @DisplayName("Wrong face returns false")
        void testWrongFaceReturnsFalse() {
            assertFalse(AlembicInteractionLogic.canRemoveLabel(true, true, 2, 3));
            assertFalse(AlembicInteractionLogic.canRemoveLabel(true, true, 4, 5));
        }

        @Test
        @DisplayName("No filter present returns false")
        void testNoFilterReturnsFalse() {
            assertFalse(AlembicInteractionLogic.canRemoveLabel(true, false, 2, 2));
        }

        @Test
        @DisplayName("Not sneaking returns false")
        void testNotSneakingReturnsFalse() {
            assertFalse(AlembicInteractionLogic.canRemoveLabel(false, true, 2, 2));
        }

        @Test
        @DisplayName("All negative conditions return false")
        void testAllFalseReturnsFalse() {
            assertFalse(AlembicInteractionLogic.canRemoveLabel(false, false, 1, 2));
        }
    }

    @Nested
    @DisplayName("canApplyLabel")
    class CanApplyLabelTests {

        @Test
        @DisplayName("No filter + horizontal face + label item returns true")
        void testValidLabelApplication() {
            assertTrue(AlembicInteractionLogic.canApplyLabel(false, true, true));
        }

        @Test
        @DisplayName("Already has filter returns false")
        void testAlreadyHasFilterReturnsFalse() {
            assertFalse(AlembicInteractionLogic.canApplyLabel(true, true, true));
        }

        @Test
        @DisplayName("Vertical face (UP or DOWN) returns false")
        void testVerticalFaceReturnsFalse() {
            assertFalse(AlembicInteractionLogic.canApplyLabel(false, false, true));
        }

        @Test
        @DisplayName("Item is not a label returns false")
        void testNotLabelItemReturnsFalse() {
            assertFalse(AlembicInteractionLogic.canApplyLabel(false, true, false));
        }

        @Test
        @DisplayName("All negative conditions return false")
        void testAllFalseReturnsFalse() {
            assertFalse(AlembicInteractionLogic.canApplyLabel(true, false, false));
        }
    }

    @Nested
    @DisplayName("canVentEssentia")
    class CanVentEssentiaTests {

        @Test
        @DisplayName("Sneak + amount > 0 returns true")
        void testValidVenting() {
            assertTrue(AlembicInteractionLogic.canVentEssentia(true, 1));
            assertTrue(AlembicInteractionLogic.canVentEssentia(true, 16));
            assertTrue(AlembicInteractionLogic.canVentEssentia(true, 32));
        }

        @Test
        @DisplayName("Empty alembic (amount == 0) returns false")
        void testEmptyAlembicReturnsFalse() {
            assertFalse(AlembicInteractionLogic.canVentEssentia(true, 0));
        }

        @Test
        @DisplayName("Negative amount returns false")
        void testNegativeAmountReturnsFalse() {
            assertFalse(AlembicInteractionLogic.canVentEssentia(true, -5));
        }

        @Test
        @DisplayName("Not sneaking returns false")
        void testNotSneakingReturnsFalse() {
            assertFalse(AlembicInteractionLogic.canVentEssentia(false, 10));
            assertFalse(AlembicInteractionLogic.canVentEssentia(false, 0));
        }
    }

    @Nested
    @DisplayName("canDrainPhial")
    class CanDrainPhialTests {

        @Test
        @DisplayName("Empty phial with stored >= phial capacity returns true")
        void testValidDrain() {
            assertTrue(AlembicInteractionLogic.canDrainPhial(true, 8, 8));
            assertTrue(AlembicInteractionLogic.canDrainPhial(true, 16, 8));
            assertTrue(AlembicInteractionLogic.canDrainPhial(true, 32, 8));
        }

        @Test
        @DisplayName("Empty phial with stored < phial capacity returns false")
        void testInsufficientStoredEssentiaReturnsFalse() {
            assertFalse(AlembicInteractionLogic.canDrainPhial(true, 7, 8));
            assertFalse(AlembicInteractionLogic.canDrainPhial(true, 0, 8));
            assertFalse(AlembicInteractionLogic.canDrainPhial(true, -1, 8));
        }

        @Test
        @DisplayName("Non-empty phial returns false")
        void testNonEmptyPhialReturnsFalse() {
            assertFalse(AlembicInteractionLogic.canDrainPhial(false, 32, 8));
            assertFalse(AlembicInteractionLogic.canDrainPhial(false, 0, 8));
        }
    }

    @Nested
    @DisplayName("canDepositPhial")
    class CanDepositPhialTests {

        @Test
        @DisplayName("Filled phial with room in alembic returns true")
        void testValidDeposit() {
            assertTrue(AlembicInteractionLogic.canDepositPhial(true, 0, 32, 8));
            assertTrue(AlembicInteractionLogic.canDepositPhial(true, 16, 32, 8));
            assertTrue(AlembicInteractionLogic.canDepositPhial(true, 24, 32, 8));
        }

        @Test
        @DisplayName("Filled phial exceeding alembic capacity returns false")
        void testInsufficientRoomReturnsFalse() {
            assertFalse(AlembicInteractionLogic.canDepositPhial(true, 25, 32, 8));
            assertFalse(AlembicInteractionLogic.canDepositPhial(true, 30, 32, 8));
            assertFalse(AlembicInteractionLogic.canDepositPhial(true, 32, 32, 8));
        }

        @Test
        @DisplayName("Non-filled phial returns false")
        void testNonFilledPhialReturnsFalse() {
            assertFalse(AlembicInteractionLogic.canDepositPhial(false, 0, 32, 8));
            assertFalse(AlembicInteractionLogic.canDepositPhial(false, 16, 32, 8));
        }
    }
}
