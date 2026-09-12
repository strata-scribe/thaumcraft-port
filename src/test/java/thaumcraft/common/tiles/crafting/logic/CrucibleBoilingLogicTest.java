package thaumcraft.common.tiles.crafting.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CrucibleBoilingLogicTest {

    @Test
    public void testIsHeatSource() {
        assertTrue(CrucibleBoilingLogic.isHeatSource("minecraft:fire"));
        assertTrue(CrucibleBoilingLogic.isHeatSource("minecraft:lava"));
        assertTrue(CrucibleBoilingLogic.isHeatSource("thaumcraft:nitor"));
        assertFalse(CrucibleBoilingLogic.isHeatSource("minecraft:dirt"));
        assertFalse(CrucibleBoilingLogic.isHeatSource(null));
    }

    @Test
    public void testCalculateHeatHeatingUp() {
        // Has water, has heat source
        assertEquals(1, CrucibleBoilingLogic.calculateHeat((short) 0, true, true));
        assertEquals(CrucibleBoilingLogic.MAX_HEAT, CrucibleBoilingLogic.calculateHeat(CrucibleBoilingLogic.MAX_HEAT, true, true));
    }

    @Test
    public void testCalculateHeatCoolingDownNoHeatSource() {
        // Has water, no heat source
        assertEquals(100, CrucibleBoilingLogic.calculateHeat((short) 101, true, false));
        assertEquals(0, CrucibleBoilingLogic.calculateHeat((short) 0, true, false));
    }

    @Test
    public void testCalculateHeatCoolingDownNoWater() {
        // No water, has heat source (should cool down)
        assertEquals(100, CrucibleBoilingLogic.calculateHeat((short) 101, false, true));
        // No water, no heat source
        assertEquals(100, CrucibleBoilingLogic.calculateHeat((short) 101, false, false));
        assertEquals(0, CrucibleBoilingLogic.calculateHeat((short) 0, false, false));
    }

    @Test
    public void testDidBoilingStateChange() {
        assertTrue(CrucibleBoilingLogic.didBoilingStateChange((short) 150, (short) 151)); // Reached boiling
        assertTrue(CrucibleBoilingLogic.didBoilingStateChange((short) 151, (short) 150)); // Dropped from boiling
        assertFalse(CrucibleBoilingLogic.didBoilingStateChange((short) 100, (short) 101)); // Still cold
        assertFalse(CrucibleBoilingLogic.didBoilingStateChange((short) 160, (short) 161)); // Still boiling
    }

    @Test
    public void testTicksUntilBoiling() {
        assertEquals(151, CrucibleBoilingLogic.ticksUntilBoiling((short) 0));
        assertEquals(1, CrucibleBoilingLogic.ticksUntilBoiling((short) 150));
        assertEquals(0, CrucibleBoilingLogic.ticksUntilBoiling((short) 151));
        assertEquals(0, CrucibleBoilingLogic.ticksUntilBoiling((short) 200));
    }
}
