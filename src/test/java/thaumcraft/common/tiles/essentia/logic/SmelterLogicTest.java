package thaumcraft.common.tiles.essentia.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SmelterLogic Unit Tests")
class SmelterLogicTest {

    @Nested
    @DisplayName("calculateComparatorSignal")
    class ComparatorSignalTests {

        @Test
        @DisplayName("Zero or negative vis produces signal 0")
        void zeroOrNegativeVis() {
            assertEquals(0, SmelterLogic.calculateComparatorSignal(0, 100));
            assertEquals(0, SmelterLogic.calculateComparatorSignal(-10, 100));
        }

        @Test
        @DisplayName("Zero or negative capacity produces signal 0")
        void zeroOrNegativeCapacity() {
            assertEquals(0, SmelterLogic.calculateComparatorSignal(50, 0));
            assertEquals(0, SmelterLogic.calculateComparatorSignal(50, -100));
            assertEquals(0, SmelterLogic.calculateComparatorSignal(0, 0));
        }

        @Test
        @DisplayName("Half capacity produces expected floor signal")
        void halfCapacitySignal() {
            // 50 * 15.0 / 100 = 7.5 -> floor = 7
            assertEquals(7, SmelterLogic.calculateComparatorSignal(50, 100));
            // 128 * 15.0 / 256 = 7.5 -> floor = 7
            assertEquals(7, SmelterLogic.calculateComparatorSignal(128, 256));
        }

        @Test
        @DisplayName("Full capacity produces signal 15")
        void fullCapacitySignal() {
            assertEquals(15, SmelterLogic.calculateComparatorSignal(100, 100));
            assertEquals(15, SmelterLogic.calculateComparatorSignal(256, 256));
            assertEquals(15, SmelterLogic.calculateComparatorSignal(384, 384));
            assertEquals(15, SmelterLogic.calculateComparatorSignal(512, 512));
        }

        @Test
        @DisplayName("Overfill clamps signal to 15")
        void overfillClampsToFifteen() {
            assertEquals(15, SmelterLogic.calculateComparatorSignal(150, 100));
            assertEquals(15, SmelterLogic.calculateComparatorSignal(600, 256));
        }

        @Test
        @DisplayName("Small amounts produce correct proportion")
        void smallAmountsProportion() {
            // 1 * 15.0 / 100 = 0.15 -> floor = 0
            assertEquals(0, SmelterLogic.calculateComparatorSignal(1, 100));
            // 7 * 15.0 / 100 = 1.05 -> floor = 1
            assertEquals(1, SmelterLogic.calculateComparatorSignal(7, 100));
        }
    }

    @Nested
    @DisplayName("shouldDropInventory")
    class ShouldDropInventoryTests {

        @Test
        @DisplayName("Same block ID does not drop inventory")
        void sameBlockDoesNotDrop() {
            assertFalse(SmelterLogic.shouldDropInventory("thaumcraft:smelter_basic", "thaumcraft:smelter_basic"));
            assertFalse(SmelterLogic.shouldDropInventory("thaumcraft:smelter_thaumium", "thaumcraft:smelter_thaumium"));
        }

        @Test
        @DisplayName("Different block ID drops inventory")
        void differentBlockDrops() {
            assertTrue(SmelterLogic.shouldDropInventory("thaumcraft:smelter_basic", "thaumcraft:smelter_thaumium"));
            assertTrue(SmelterLogic.shouldDropInventory("thaumcraft:smelter_basic", "minecraft:air"));
        }

        @Test
        @DisplayName("Null old block ID returns false")
        void nullOldBlockDoesNotDrop() {
            assertFalse(SmelterLogic.shouldDropInventory(null, "thaumcraft:smelter_basic"));
            assertFalse(SmelterLogic.shouldDropInventory(null, null));
        }

        @Test
        @DisplayName("Null new block ID with non-null old block ID drops inventory")
        void nullNewBlockDrops() {
            assertTrue(SmelterLogic.shouldDropInventory("thaumcraft:smelter_basic", null));
        }
    }

    @Nested
    @DisplayName("calculateCookTime")
    class CalculateCookTimeTests {

        @Test
        @DisplayName("0 bellows and 0 aux pumps returns unmodified baseCookTime")
        void zeroModifiersReturnsBaseCookTime() {
            assertEquals(100, SmelterLogic.calculateCookTime(100, 0, 0));
            assertEquals(200, SmelterLogic.calculateCookTime(200, 0, 0));
        }

        @Test
        @DisplayName("Bellows speedup reduces base cook time by 25% per bellows, max 2")
        void bellowsSpeedup() {
            // 1 bellows: 100 * (1 - 0.25) = 75
            assertEquals(75, SmelterLogic.calculateCookTime(100, 1, 0));
            // 2 bellows: 100 * (1 - 0.50) = 50
            assertEquals(50, SmelterLogic.calculateCookTime(100, 2, 0));
            // 3 bellows: capped at 2 bellows -> 50
            assertEquals(50, SmelterLogic.calculateCookTime(100, 3, 0));
            // negative bellows: clamped to 0 bellows -> 100
            assertEquals(100, SmelterLogic.calculateCookTime(100, -1, 0));
        }

        @Test
        @DisplayName("Aux pumps speedup applies SmelterAuxLogic reductions")
        void auxPumpsSpeedup() {
            // 1 aux pump: 100 * 0.80 = 80
            assertEquals(80, SmelterLogic.calculateCookTime(100, 0, 1));
            // 2 aux pumps: 100 * 0.60 = 60
            assertEquals(60, SmelterLogic.calculateCookTime(100, 0, 2));
            // 3 aux pumps: capped at 2 pumps -> 60
            assertEquals(60, SmelterLogic.calculateCookTime(100, 0, 3));
        }

        @Test
        @DisplayName("Combined bellows and aux pumps stack correctly")
        void combinedBellowsAndAuxPumps() {
            // 1 bellows (75) + 1 aux pump (75 * 0.80 = 60)
            assertEquals(60, SmelterLogic.calculateCookTime(100, 1, 1));
            // 2 bellows (50) + 2 aux pumps (50 * 0.60 = 30)
            assertEquals(30, SmelterLogic.calculateCookTime(100, 2, 2));
        }

        @Test
        @DisplayName("Cook time clamps to minimum of 1")
        void minimumClamp() {
            // Low base cook time with max reductions
            assertEquals(1, SmelterLogic.calculateCookTime(1, 2, 2));
            assertEquals(1, SmelterLogic.calculateCookTime(0, 0, 0));
            assertEquals(1, SmelterLogic.calculateCookTime(-50, 0, 0));
        }
    }
}
