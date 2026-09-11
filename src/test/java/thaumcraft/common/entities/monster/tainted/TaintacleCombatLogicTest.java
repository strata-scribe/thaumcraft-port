package thaumcraft.common.entities.monster.tainted;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TaintacleCombatLogicTest {

    @Test
    public void testIsWithinMeleeReach() {
        // Distance <= 5 blocks (distance squared <= 25)
        assertTrue(TaintacleCombatLogic.isWithinMeleeReach(16.0)); // 4 blocks
        assertTrue(TaintacleCombatLogic.isWithinMeleeReach(25.0)); // 5 blocks
        assertFalse(TaintacleCombatLogic.isWithinMeleeReach(36.0)); // 6 blocks
    }

    @Test
    public void testCalculateWhipKnockback() {
        double[] knockback = TaintacleCombatLogic.calculateWhipKnockback(3.0, 4.0, 1.0);
        // Distance is 5. dx/dist = 3/5 = 0.6, dz/dist = 4/5 = 0.8
        assertEquals(0.6, knockback[0], 1e-4);
        assertEquals(0.5, knockback[1], 1e-4);
        assertEquals(0.8, knockback[2], 1e-4);

        // Zero distance case
        double[] knockbackZero = TaintacleCombatLogic.calculateWhipKnockback(0.0, 0.0, 1.0);
        assertEquals(0.0, knockbackZero[0], 1e-4);
        assertEquals(0.5, knockbackZero[1], 1e-4);
        assertEquals(0.0, knockbackZero[2], 1e-4);
    }

    @Test
    public void testCalculateSpitTrajectory() {
        // dx=3, dy=0, dz=4, velocity=1.0
        // horizontalDistance = 5
        // adjustedDy = 0 + 5 * 0.2 = 1.0
        // dist = sqrt(3^2 + 1^2 + 4^2) = sqrt(9 + 1 + 16) = sqrt(26) = 5.0990195

        double[] trajectory = TaintacleCombatLogic.calculateSpitTrajectory(3.0, 0.0, 4.0, 1.0);
        double dist = Math.sqrt(26.0);

        assertEquals(3.0 / dist, trajectory[0], 1e-4);
        assertEquals(1.0 / dist, trajectory[1], 1e-4);
        assertEquals(4.0 / dist, trajectory[2], 1e-4);

        // Zero distance case
        double[] trajectoryZero = TaintacleCombatLogic.calculateSpitTrajectory(0.0, 0.0, 0.0, 2.0);
        assertEquals(0.0, trajectoryZero[0], 1e-4);
        assertEquals(2.0, trajectoryZero[1], 1e-4);
        assertEquals(0.0, trajectoryZero[2], 1e-4);
    }

    @Test
    public void testShouldInflictFluxTaint() {
        assertTrue(TaintacleCombatLogic.shouldInflictFluxTaint());
    }
}
