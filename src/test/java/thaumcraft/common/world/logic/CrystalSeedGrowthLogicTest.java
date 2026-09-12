package thaumcraft.common.world.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CrystalSeedGrowthLogicTest {

    private CrystalSeedGrowthLogic logic;

    @BeforeEach
    void setUp() {
        logic = new CrystalSeedGrowthLogic();
    }

    @Test
    void testNegativeVis_returnsStalledGrowth() {
        // Growth should stall (-1) when vis is negative
        assertEquals(-1, logic.calculateGrowthTicks(-50.0));
    }

    @Test
    void testZeroVis_returnsStalledGrowth() {
        // Growth should stall (-1) when vis is exactly 0
        assertEquals(-1, logic.calculateGrowthTicks(0.0));
    }

    @Test
    void testLowVis_returnsMaxTicks() {
        // With very low vis (e.g. 5), the inverse calculation 12000 * (100 / 5) = 240000
        // This should be capped at maxTicks (48000)
        assertEquals(48000, logic.calculateGrowthTicks(5.0));
    }

    @Test
    void testNormalVis_returnsCalculatedTicks() {
        // At exactly standard vis (100.0), it should return base ticks (12000)
        assertEquals(12000, logic.calculateGrowthTicks(100.0));

        // At 50 vis, calculation is 12000 * (100 / 50) = 24000
        assertEquals(24000, logic.calculateGrowthTicks(50.0));

        // At 200 vis, calculation is 12000 * (100 / 200) = 6000
        assertEquals(6000, logic.calculateGrowthTicks(200.0));
    }

    @Test
    void testHighVis_returnsMinTicks() {
        // With extremely high vis (e.g. 3000), calculation is 12000 * (100 / 3000) = 400
        // This should be capped at minTicks (600)
        assertEquals(600, logic.calculateGrowthTicks(3000.0));
    }
}
