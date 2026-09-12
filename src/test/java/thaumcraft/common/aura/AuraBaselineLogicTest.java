package thaumcraft.common.aura;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import thaumcraft.common.aura.AuraBaselineLogic.AuraCapacities;

public class AuraBaselineLogicTest {

    @Test
    public void testStandardEnvironment() {
        // Average temperature and rainfall, neutral magic affinity
        AuraCapacities capacities = AuraBaselineLogic.calculateCapacities(0.5f, 0.5f, 0.0f);
        // baseVis = 100 + (0.5 * 20) + (0.5 * 30) = 100 + 10 + 15 = 125
        assertEquals(125, capacities.baseVisCapacity());
        // fluxCeiling = 50 + (0.5 * 10) - (0.5 * 20) = 50 + 5 - 10 = 45
        assertEquals(45, capacities.fluxCeiling());
    }

    @Test
    public void testHighTempLowRainfall() {
        // High temp (desert), low rainfall
        AuraCapacities capacities = AuraBaselineLogic.calculateCapacities(1.5f, 0.0f, 0.0f);
        // baseVis = 100 + 30 + 0 = 130
        assertEquals(130, capacities.baseVisCapacity());
        // fluxCeiling = 50 + 15 - 0 = 65
        assertEquals(65, capacities.fluxCeiling());
    }

    @Test
    public void testLowTempHighRainfall() {
        // Low temp (tundra/snow), high rainfall (snow)
        AuraCapacities capacities = AuraBaselineLogic.calculateCapacities(0.0f, 1.0f, 0.0f);
        // baseVis = 100 + 0 + 30 = 130
        assertEquals(130, capacities.baseVisCapacity());
        // fluxCeiling = 50 + 0 - 20 = 30
        assertEquals(30, capacities.fluxCeiling());
    }

    @Test
    public void testHighMagicAffinity() {
        // Magical forest
        AuraCapacities capacities = AuraBaselineLogic.calculateCapacities(0.5f, 0.5f, 1.5f);
        // baseVis = 100 + 10 + 15 + 150 = 275
        assertEquals(275, capacities.baseVisCapacity());
        // fluxCeiling = 50 + 5 - 10 + 75 = 120
        assertEquals(120, capacities.fluxCeiling());
    }

    @Test
    public void testNegativeMagicAffinityAndMinimumBounds() {
        // Very hostile to magic, should hit minimums
        AuraCapacities capacities = AuraBaselineLogic.calculateCapacities(-1.0f, 1.0f, -2.0f);
        // baseVis = 100 - 20 + 30 - 200 = -90 => Math.max(10, -90) = 10
        assertEquals(10, capacities.baseVisCapacity());
        // fluxCeiling = 50 - 10 - 20 - 100 = -80 => Math.max(5, -80) = 5
        assertEquals(5, capacities.fluxCeiling());
    }
}
