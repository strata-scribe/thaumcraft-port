package thaumcraft.common.entities.monster.wisp;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WispCombatLogicTest {

    @Test
    public void testFlightWanderAvoidsObstacles() {
        double[] wispPos = {0, 0, 0};
        List<double[]> obstacles = new ArrayList<>();
        obstacles.add(new double[]{1, 0, 0}); // Obstacle very close on +X

        // Not attacked, no player nearby
        double[] flightVector = WispCombatLogic.calculateWanderFlightVector(wispPos, null, null, false, obstacles);

        // Should repulse away from +X, meaning vector X component should be negative
        assertTrue(flightVector[0] < 0, "Wisp should repulse away from the obstacle on +X");
    }

    @Test
    public void testFlightWanderAvoidsLineOfSight() {
        double[] wispPos = {5, 0, 0};
        double[] playerPos = {0, 0, 0};
        double[] playerLook = {1, 0, 0}; // Player looking directly at Wisp (+X)

        // No obstacles, not attacked
        List<double[]> obstacles = new ArrayList<>();
        double[] flightVector = WispCombatLogic.calculateWanderFlightVector(wispPos, playerPos, playerLook, false, obstacles);

        // Vector should have a significant perpendicular component (Y or Z) relative to X
        assertTrue(Math.abs(flightVector[1]) > 0.1 || Math.abs(flightVector[2]) > 0.1, "Wisp should move perpendicular to avoid line of sight");

        // It shouldn't move directly toward or away on X without moving perpendicular
        double dotProduct = flightVector[0] * playerLook[0] + flightVector[1] * playerLook[1] + flightVector[2] * playerLook[2];
        assertTrue(Math.abs(dotProduct) < 0.9, "Wisp should not fly directly along the line of sight");
    }

    @Test
    public void testFlightWanderTowardsAttacker() {
        double[] wispPos = {10, 0, 0};
        double[] playerPos = {0, 0, 0};
        double[] playerLook = {1, 0, 0};

        // No obstacles, is attacked
        List<double[]> obstacles = new ArrayList<>();
        double[] flightVector = WispCombatLogic.calculateWanderFlightVector(wispPos, playerPos, playerLook, true, obstacles);

        // Should fly towards player on -X
        assertTrue(flightVector[0] < -0.5, "Wisp should fly towards the attacker");
    }

    @Test
    public void testSparkDamageMultipliers() {
        float baseDamage = 10.0f;

        assertEquals(15.0f, WispCombatLogic.calculateSparkDamage(baseDamage, "ignis"));
        assertEquals(20.0f, WispCombatLogic.calculateSparkDamage(baseDamage, "perditio"));
        assertEquals(8.0f, WispCombatLogic.calculateSparkDamage(baseDamage, "aer"));
        assertEquals(12.0f, WispCombatLogic.calculateSparkDamage(baseDamage, "terra"));
        assertEquals(10.0f, WispCombatLogic.calculateSparkDamage(baseDamage, "aqua"));
        assertEquals(11.0f, WispCombatLogic.calculateSparkDamage(baseDamage, "ordo"));

        // Test compound aspect default
        assertEquals(10.5f, WispCombatLogic.calculateSparkDamage(baseDamage, "lux"));

        // Test null/empty
        assertEquals(10.0f, WispCombatLogic.calculateSparkDamage(baseDamage, null));
        assertEquals(10.0f, WispCombatLogic.calculateSparkDamage(baseDamage, ""));
    }
}
