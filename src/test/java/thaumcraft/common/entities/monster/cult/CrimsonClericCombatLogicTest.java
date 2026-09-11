package thaumcraft.common.entities.monster.cult;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CrimsonClericCombatLogicTest {

    @Test
    public void testCalculateFireOrbTrajectory() {
        double[] trajectory = CrimsonClericCombatLogic.calculateFireOrbTrajectory(0, 0, 0, 10, 0, 0, 2.0);
        assertEquals(2.0, trajectory[0], 0.001);
        assertEquals(0.0, trajectory[1], 0.001);
        assertEquals(0.0, trajectory[2], 0.001);

        trajectory = CrimsonClericCombatLogic.calculateFireOrbTrajectory(0, 0, 0, 0, 10, 0, 1.5);
        assertEquals(0.0, trajectory[0], 0.001);
        assertEquals(1.5, trajectory[1], 0.001);
        assertEquals(0.0, trajectory[2], 0.001);

        trajectory = CrimsonClericCombatLogic.calculateFireOrbTrajectory(0, 0, 0, 0, 0, 0, 1.5);
        assertEquals(0.0, trajectory[0], 0.001);
        assertEquals(0.0, trajectory[1], 0.001);
        assertEquals(0.0, trajectory[2], 0.001);

        // 45 degrees 2D test
        trajectory = CrimsonClericCombatLogic.calculateFireOrbTrajectory(0, 0, 0, 10, 10, 0, 1.0);
        assertEquals(Math.sqrt(0.5), trajectory[0], 0.001);
        assertEquals(Math.sqrt(0.5), trajectory[1], 0.001);
        assertEquals(0.0, trajectory[2], 0.001);
    }

    @Test
    public void testCalculateExplosionRadius() {
        assertEquals(4.0f, CrimsonClericCombatLogic.calculateExplosionRadius(2.0f, 2.0f), 0.001f);
        assertEquals(1.5f, CrimsonClericCombatLogic.calculateExplosionRadius(1.5f, 1.0f), 0.001f);
    }

    @Test
    public void testShouldBloodPrayerPulse() {
        assertFalse(CrimsonClericCombatLogic.shouldBloodPrayerPulse(0));
        assertFalse(CrimsonClericCombatLogic.shouldBloodPrayerPulse(10));
        assertFalse(CrimsonClericCombatLogic.shouldBloodPrayerPulse(99));
        assertTrue(CrimsonClericCombatLogic.shouldBloodPrayerPulse(100));
        assertFalse(CrimsonClericCombatLogic.shouldBloodPrayerPulse(101));
        assertTrue(CrimsonClericCombatLogic.shouldBloodPrayerPulse(200));
        assertFalse(CrimsonClericCombatLogic.shouldBloodPrayerPulse(-100));
    }

    @Test
    public void testCalculateBloodPrayerHealing() {
        assertEquals(25.0f, CrimsonClericCombatLogic.calculateBloodPrayerHealing(100.0f), 0.001f);
        assertEquals(12.5f, CrimsonClericCombatLogic.calculateBloodPrayerHealing(50.0f), 0.001f);
        assertEquals(0.0f, CrimsonClericCombatLogic.calculateBloodPrayerHealing(0.0f), 0.001f);
    }
}
