package thaumcraft.common.lib;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SunScornedLogicTest {

    @Test
    public void testHeatBuildupInSunlight() {
        int currentHeat = 50;
        int expectedHeat = currentHeat + SunScornedLogic.HEAT_RATE_SUN;
        assertEquals(expectedHeat, SunScornedLogic.calculateHeat(currentHeat, true), "Heat should increase by HEAT_RATE_SUN when in sunlight");
    }

    @Test
    public void testCoolingInShade() {
        int currentHeat = 50;
        int expectedHeat = currentHeat - SunScornedLogic.COOL_RATE_SHADE;
        assertEquals(expectedHeat, SunScornedLogic.calculateHeat(currentHeat, false), "Heat should decrease by COOL_RATE_SHADE when in shade");
    }

    @Test
    public void testHeatCappedAtMax() {
        int currentHeat = SunScornedLogic.MAX_HEAT;
        assertEquals(SunScornedLogic.MAX_HEAT, SunScornedLogic.calculateHeat(currentHeat, true), "Heat should not exceed MAX_HEAT in sunlight");

        currentHeat = SunScornedLogic.MAX_HEAT - SunScornedLogic.HEAT_RATE_SUN + 1;
        assertEquals(SunScornedLogic.MAX_HEAT, SunScornedLogic.calculateHeat(currentHeat, true), "Heat should be capped at MAX_HEAT in sunlight");
    }

    @Test
    public void testHeatFlooredAtZero() {
        int currentHeat = 0;
        assertEquals(0, SunScornedLogic.calculateHeat(currentHeat, false), "Heat should not drop below 0 in shade");

        currentHeat = SunScornedLogic.COOL_RATE_SHADE - 1;
        assertEquals(0, SunScornedLogic.calculateHeat(currentHeat, false), "Heat should be floored at 0 in shade");
    }
}
