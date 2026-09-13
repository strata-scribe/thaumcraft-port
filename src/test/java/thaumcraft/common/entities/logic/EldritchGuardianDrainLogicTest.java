package thaumcraft.common.entities.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EldritchGuardianDrainLogicTest {

    @Test
    void testIsDrainBeamConnected() {
        assertTrue(EldritchGuardianDrainLogic.isDrainBeamConnected(0.0, 10.0));
        assertTrue(EldritchGuardianDrainLogic.isDrainBeamConnected(5.0, 10.0));
        assertTrue(EldritchGuardianDrainLogic.isDrainBeamConnected(10.0, 10.0));

        assertFalse(EldritchGuardianDrainLogic.isDrainBeamConnected(10.1, 10.0));
        assertFalse(EldritchGuardianDrainLogic.isDrainBeamConnected(15.0, 10.0));
        assertFalse(EldritchGuardianDrainLogic.isDrainBeamConnected(-1.0, 10.0));
    }

    @Test
    void testCalculateLifeDrainDamage() {
        // baseDamage, distance, maxRange

        // Point-blank range
        assertEquals(10.0f, EldritchGuardianDrainLogic.calculateLifeDrainDamage(10.0f, 0.0, 10.0), 0.001f);

        // Mid range (50% falloff)
        assertEquals(5.0f, EldritchGuardianDrainLogic.calculateLifeDrainDamage(10.0f, 5.0, 10.0), 0.001f);

        // Quarter range (75% falloff)
        assertEquals(2.5f, EldritchGuardianDrainLogic.calculateLifeDrainDamage(10.0f, 7.5, 10.0), 0.001f);

        // Beyond max range
        assertEquals(0.0f, EldritchGuardianDrainLogic.calculateLifeDrainDamage(10.0f, 10.0, 10.0), 0.001f);
        assertEquals(0.0f, EldritchGuardianDrainLogic.calculateLifeDrainDamage(10.0f, 15.0, 10.0), 0.001f);

        // Invalid max range
        assertEquals(0.0f, EldritchGuardianDrainLogic.calculateLifeDrainDamage(10.0f, 5.0, 0.0), 0.001f);
        assertEquals(0.0f, EldritchGuardianDrainLogic.calculateLifeDrainDamage(10.0f, 5.0, -5.0), 0.001f);

        // Negative distance (should cap to baseDamage)
        assertEquals(10.0f, EldritchGuardianDrainLogic.calculateLifeDrainDamage(10.0f, -2.0, 10.0), 0.001f);
    }

    @Test
    void testCalculateHealthConversion() {
        // damageDealt, conversionRate

        // 50% conversion
        assertEquals(5.0f, EldritchGuardianDrainLogic.calculateHealthConversion(10.0f, 0.5f), 0.001f);

        // 100% conversion
        assertEquals(10.0f, EldritchGuardianDrainLogic.calculateHealthConversion(10.0f, 1.0f), 0.001f);

        // 0% conversion
        assertEquals(0.0f, EldritchGuardianDrainLogic.calculateHealthConversion(10.0f, 0.0f), 0.001f);

        // Negative conversion rate (invalid, should return 0)
        assertEquals(0.0f, EldritchGuardianDrainLogic.calculateHealthConversion(10.0f, -0.5f), 0.001f);

        // Negative damage (invalid, should return 0)
        assertEquals(0.0f, EldritchGuardianDrainLogic.calculateHealthConversion(-5.0f, 0.5f), 0.001f);

        // Zero damage
        assertEquals(0.0f, EldritchGuardianDrainLogic.calculateHealthConversion(0.0f, 0.5f), 0.001f);
    }
}
