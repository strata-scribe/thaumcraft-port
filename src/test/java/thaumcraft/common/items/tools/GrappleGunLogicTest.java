package thaumcraft.common.items.tools;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import thaumcraft.common.items.tools.GrappleGunLogic.Vector3d;
import thaumcraft.common.items.tools.GrappleGunLogic.ProjectileState;

public class GrappleGunLogicTest {

    @Test
    public void testCalculateProjectileFlight() {
        Vector3d pos = new Vector3d(0, 0, 0);
        Vector3d vel = new Vector3d(10, 10, 0);
        double gravity = 0.5;
        double drag = 0.1; // 10% drag

        ProjectileState nextState = GrappleGunLogic.calculateProjectileFlight(pos, vel, gravity, drag);

        // new position should be pos + vel = (10, 10, 0)
        assertEquals(10.0, nextState.position().x(), 0.001);
        assertEquals(10.0, nextState.position().y(), 0.001);
        assertEquals(0.0, nextState.position().z(), 0.001);

        // new velocity should be (vel * 0.9) - (0, 0.5, 0)
        // x: 10 * 0.9 = 9.0
        // y: (10 * 0.9) - 0.5 = 9.0 - 0.5 = 8.5
        // z: 0 * 0.9 = 0
        assertEquals(9.0, nextState.velocity().x(), 0.001);
        assertEquals(8.5, nextState.velocity().y(), 0.001);
        assertEquals(0.0, nextState.velocity().z(), 0.001);
    }

    @Test
    public void testCalculateImpactCoordinate() {
        Vector3d startPos = new Vector3d(0, 0, 0);
        Vector3d initialVel = new Vector3d(1, 0, 0); // straight line
        double gravity = 0.0; // no gravity for simplicity
        double drag = 0.0;    // no drag for simplicity

        // Solid block at x >= 5
        Vector3d impact = GrappleGunLogic.calculateImpactCoordinate(
                startPos, initialVel, gravity, drag, 10,
                pos -> pos.x() >= 5.0
        );

        assertNotNull(impact, "Impact should not be null");
        assertEquals(5.0, impact.x(), 0.001);
        assertEquals(0.0, impact.y(), 0.001);
        assertEquals(0.0, impact.z(), 0.001);
    }

    @Test
    public void testCalculateImpactCoordinate_NoImpact() {
        Vector3d startPos = new Vector3d(0, 0, 0);
        Vector3d initialVel = new Vector3d(1, 0, 0);
        double gravity = 0.0;
        double drag = 0.0;

        // Solid block at x >= 20, but we only simulate 10 steps
        Vector3d impact = GrappleGunLogic.calculateImpactCoordinate(
                startPos, initialVel, gravity, drag, 10,
                pos -> pos.x() >= 20.0
        );

        assertNull(impact, "Impact should be null since maxSteps was reached before hitting solid");
    }

    @Test
    public void testCalculatePullingAcceleration() {
        Vector3d playerPos = new Vector3d(0, 0, 0);
        Vector3d playerVel = new Vector3d(2, 0, 0); // moving towards hook
        Vector3d hookPos = new Vector3d(10, 0, 0);

        double springStiffness = 0.5; // pull force = 0.5 * displacement
        double dampingCoefficient = 0.2; // damping force = 0.2 * velocity

        Vector3d accel = GrappleGunLogic.calculatePullingAcceleration(
                playerPos, playerVel, hookPos, springStiffness, dampingCoefficient
        );

        // Displacement = 10 - 0 = 10
        // Spring force = 10 * 0.5 = 5.0
        // Damping force = 2 * 0.2 = 0.4
        // Accel = 5.0 - 0.4 = 4.6
        assertEquals(4.6, accel.x(), 0.001);
        assertEquals(0.0, accel.y(), 0.001);
        assertEquals(0.0, accel.z(), 0.001);
    }
}
