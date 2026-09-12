package thaumcraft.common.golems.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GolemBiomeModifierLogicTest {

    @Test
    public void testNormalTerrainAndTemperature() {
        // Temperature between 0.2 and 1.5, not ice/sand/snow
        assertEquals(1.0f, GolemBiomeModifierLogic.getMovementMultiplier("DIRT", 0.5f), 0.001f);
        assertEquals(1.0f, GolemBiomeModifierLogic.getMovementMultiplier("STONE", 1.0f), 0.001f);
        assertEquals(1.0f, GolemBiomeModifierLogic.getMovementMultiplier(null, 1.0f), 0.001f);
    }

    @Test
    public void testIceTerrain() {
        assertEquals(1.2f, GolemBiomeModifierLogic.getMovementMultiplier("ICE", 0.5f), 0.001f);
        assertEquals(1.2f, GolemBiomeModifierLogic.getMovementMultiplier("ice", 0.5f), 0.001f);
    }

    @Test
    public void testSandAndSnowTerrain() {
        assertEquals(0.8f, GolemBiomeModifierLogic.getMovementMultiplier("SAND", 0.5f), 0.001f);
        assertEquals(0.8f, GolemBiomeModifierLogic.getMovementMultiplier("sand", 0.5f), 0.001f);
        assertEquals(0.8f, GolemBiomeModifierLogic.getMovementMultiplier("SNOW", 0.5f), 0.001f);
        assertEquals(0.8f, GolemBiomeModifierLogic.getMovementMultiplier("snow", 0.5f), 0.001f);
    }

    @Test
    public void testHotTemperature() {
        // Temperature >= 1.5
        assertEquals(0.9f, GolemBiomeModifierLogic.getMovementMultiplier("DIRT", 1.5f), 0.001f);
        assertEquals(0.9f, GolemBiomeModifierLogic.getMovementMultiplier("DIRT", 2.0f), 0.001f);
    }

    @Test
    public void testColdTemperature() {
        // Temperature <= 0.2
        assertEquals(0.9f, GolemBiomeModifierLogic.getMovementMultiplier("DIRT", 0.2f), 0.001f);
        assertEquals(0.9f, GolemBiomeModifierLogic.getMovementMultiplier("DIRT", -0.5f), 0.001f);
    }

    @Test
    public void testCombinedTerrainAndTemperature() {
        // Ice + Cold
        assertEquals(1.2f * 0.9f, GolemBiomeModifierLogic.getMovementMultiplier("ICE", 0.1f), 0.001f);
        // Sand + Hot
        assertEquals(0.8f * 0.9f, GolemBiomeModifierLogic.getMovementMultiplier("SAND", 1.6f), 0.001f);
        // Snow + Cold
        assertEquals(0.8f * 0.9f, GolemBiomeModifierLogic.getMovementMultiplier("SNOW", -0.1f), 0.001f);
    }
}
