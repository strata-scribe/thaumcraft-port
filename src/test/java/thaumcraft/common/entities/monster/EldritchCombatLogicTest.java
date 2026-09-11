package thaumcraft.common.entities.monster;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EldritchCombatLogicTest {

    @Test
    @DisplayName("calculateLifeDrainHealing should return 50% of the damage dealt")
    public void testCalculateLifeDrainHealing() {
        assertEquals(5.0f, EldritchCombatLogic.calculateLifeDrainHealing(10.0f), 0.001f, "10.0 damage should heal for 5.0");
        assertEquals(2.5f, EldritchCombatLogic.calculateLifeDrainHealing(5.0f), 0.001f, "5.0 damage should heal for 2.5");
        assertEquals(0.0f, EldritchCombatLogic.calculateLifeDrainHealing(0.0f), 0.001f, "0.0 damage should heal for 0.0");
        assertEquals(0.0f, EldritchCombatLogic.calculateLifeDrainHealing(-5.0f), 0.001f, "Negative damage should heal for 0.0");
    }

    @Test
    @DisplayName("shouldShadowStep should return true for heavy physical damage (>= 8.0f)")
    public void testShouldShadowStep() {
        // Physical damage
        assertTrue(EldritchCombatLogic.shouldShadowStep(8.0f, true), "8.0 physical damage should trigger shadow step");
        assertTrue(EldritchCombatLogic.shouldShadowStep(10.0f, true), "10.0 physical damage should trigger shadow step");
        assertFalse(EldritchCombatLogic.shouldShadowStep(7.9f, true), "7.9 physical damage should NOT trigger shadow step");

        // Magical damage
        assertFalse(EldritchCombatLogic.shouldShadowStep(8.0f, false), "8.0 magical damage should NOT trigger shadow step");
        assertFalse(EldritchCombatLogic.shouldShadowStep(10.0f, false), "10.0 magical damage should NOT trigger shadow step");
    }

    @Test
    @DisplayName("calculateShadowStepCoordinates should place entity behind target based on yaw")
    public void testCalculateShadowStepCoordinates() {
        double x = 100.0;
        double y = 64.0;
        double z = 200.0;
        double dist = 2.0;

        // Yaw 0: South (+Z). Behind is North (-Z)
        double[] coords0 = EldritchCombatLogic.calculateShadowStepCoordinates(x, y, z, 0.0f, dist);
        assertEquals(100.0, coords0[0], 0.001, "Yaw 0 X coordinate should remain unchanged");
        assertEquals(64.0, coords0[1], 0.001, "Yaw 0 Y coordinate should remain unchanged");
        assertEquals(198.0, coords0[2], 0.001, "Yaw 0 Z coordinate should be -2");

        // Yaw 90: West (-X). Behind is East (+X)
        double[] coords90 = EldritchCombatLogic.calculateShadowStepCoordinates(x, y, z, 90.0f, dist);
        assertEquals(102.0, coords90[0], 0.001, "Yaw 90 X coordinate should be +2");
        assertEquals(64.0, coords90[1], 0.001, "Yaw 90 Y coordinate should remain unchanged");
        assertEquals(200.0, coords90[2], 0.001, "Yaw 90 Z coordinate should remain unchanged");

        // Yaw 180: North (-Z). Behind is South (+Z)
        double[] coords180 = EldritchCombatLogic.calculateShadowStepCoordinates(x, y, z, 180.0f, dist);
        assertEquals(100.0, coords180[0], 0.001, "Yaw 180 X coordinate should remain unchanged");
        assertEquals(64.0, coords180[1], 0.001, "Yaw 180 Y coordinate should remain unchanged");
        assertEquals(202.0, coords180[2], 0.001, "Yaw 180 Z coordinate should be +2");

        // Yaw 270: East (+X). Behind is West (-X)
        double[] coords270 = EldritchCombatLogic.calculateShadowStepCoordinates(x, y, z, 270.0f, dist);
        assertEquals(98.0, coords270[0], 0.001, "Yaw 270 X coordinate should be -2");
        assertEquals(64.0, coords270[1], 0.001, "Yaw 270 Y coordinate should remain unchanged");
        assertEquals(200.0, coords270[2], 0.001, "Yaw 270 Z coordinate should remain unchanged");
    }
}
