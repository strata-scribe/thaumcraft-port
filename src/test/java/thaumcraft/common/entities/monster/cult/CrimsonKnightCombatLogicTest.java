package thaumcraft.common.entities.monster.cult;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CrimsonKnightCombatLogicTest {

    @Test
    public void testCalculatePhalanxDamage() {
        // Base damage 10
        float baseDamage = 10.0f;

        // Shield not raised, frontal -> full damage
        assertEquals(10.0f, CrimsonKnightCombatLogic.calculatePhalanxDamage(baseDamage, false, true), 0.001f);

        // Shield raised, not frontal -> full damage
        assertEquals(10.0f, CrimsonKnightCombatLogic.calculatePhalanxDamage(baseDamage, true, false), 0.001f);

        // Shield raised, frontal -> 25% damage (75% mitigation)
        assertEquals(2.5f, CrimsonKnightCombatLogic.calculatePhalanxDamage(baseDamage, true, true), 0.001f);

        // Zero damage
        assertEquals(0.0f, CrimsonKnightCombatLogic.calculatePhalanxDamage(0.0f, true, true), 0.001f);
    }

    @Test
    public void testIsFrontal() {
        // Knight at (0, 0)
        float kx = 0;
        float kz = 0;

        // Attacker directly in front (+Z axis, which is 0 yaw in standard MC convention)
        float ax = 0;
        float az = 10;

        // Since we are using standard atan2, angle to attacker = atan2(10, 0) * 180/PI - 90 = 90 - 90 = 0 degrees.
        // So a yaw of 0 should be frontal.
        assertTrue(CrimsonKnightCombatLogic.isFrontal(0.0f, kx, kz, ax, az));

        // A yaw of 45 should still be frontal
        assertTrue(CrimsonKnightCombatLogic.isFrontal(45.0f, kx, kz, ax, az));

        // A yaw of -45 should still be frontal
        assertTrue(CrimsonKnightCombatLogic.isFrontal(-45.0f, kx, kz, ax, az));

        // A yaw of 50 should NOT be frontal (45 degree cone)
        assertFalse(CrimsonKnightCombatLogic.isFrontal(50.0f, kx, kz, ax, az));

        // A yaw of 180 should NOT be frontal (opposite direction)
        assertFalse(CrimsonKnightCombatLogic.isFrontal(180.0f, kx, kz, ax, az));

        // Attacker directly to the right (+X axis, which would give angle -90 in our calculation)
        ax = 10;
        az = 0;
        // atan2(0, 10) * 180/PI - 90 = 0 - 90 = -90
        assertTrue(CrimsonKnightCombatLogic.isFrontal(-90.0f, kx, kz, ax, az));
        assertFalse(CrimsonKnightCombatLogic.isFrontal(0.0f, kx, kz, ax, az));
    }

    @Test
    public void testCalculateCleaveDamage() {
        assertEquals(5.0f, CrimsonKnightCombatLogic.calculateCleaveDamage(10.0f), 0.001f);
        assertEquals(0.0f, CrimsonKnightCombatLogic.calculateCleaveDamage(0.0f), 0.001f);
    }

    @Test
    public void testIsWithinCleave() {
        float kx = 0;
        float kz = 0;
        float cleaveRange = 5.0f;

        // Target directly in front, distance 4
        float tx = 0;
        float tz = 4;

        // Distance 4, yaw 0 -> angle is 0, dist is 16 < 25
        assertTrue(CrimsonKnightCombatLogic.isWithinCleave(0.0f, kx, kz, tx, tz, cleaveRange));

        // Target outside range (distance 6)
        tz = 6;
        assertFalse(CrimsonKnightCombatLogic.isWithinCleave(0.0f, kx, kz, tx, tz, cleaveRange));

        // Target at distance 4, but behind knight
        tz = -4;
        // Angle is 180
        assertFalse(CrimsonKnightCombatLogic.isWithinCleave(0.0f, kx, kz, tx, tz, cleaveRange));

        // Target in wider cleave arc (e.g. angle 80)
        // dx = 4, dz = 4, distSq = 32 > 25 (out of range)
        tx = 3;
        tz = 3;
        // distSq = 18, angle to target = atan2(3, 3) * 180/PI - 90 = 45 - 90 = -45
        // Yaw 0 -> angleDiff = -45. Within 90? Yes.
        assertTrue(CrimsonKnightCombatLogic.isWithinCleave(0.0f, kx, kz, tx, tz, cleaveRange));

        // What if yaw is 90? angleDiff = -135, which is outside 90.
        assertFalse(CrimsonKnightCombatLogic.isWithinCleave(90.0f, kx, kz, tx, tz, cleaveRange));
    }
}
