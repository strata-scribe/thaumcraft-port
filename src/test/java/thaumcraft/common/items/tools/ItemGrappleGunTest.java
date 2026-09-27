package thaumcraft.common.items.tools;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import thaumcraft.common.items.tools.logic.GrappleGunLogic;

public class ItemGrappleGunTest {

    @Test
    public void testVelocityCalculationsForGrappleGun() {
        // Testing that the logic GrappleGunLogic provides correct inputs
        // to what ItemGrappleGun simulates in its use() method.

        GrappleGunLogic.Vector3d playerPos = new GrappleGunLogic.Vector3d(0, 64, 0);
        GrappleGunLogic.Vector3d playerLook = new GrappleGunLogic.Vector3d(1, 0, 0); // Looking directly east

        // Mock ItemGrappleGun starting vel
        GrappleGunLogic.Vector3d pos = new GrappleGunLogic.Vector3d(playerPos.x(), playerPos.y() + 1.62, playerPos.z());
        GrappleGunLogic.Vector3d vel = new GrappleGunLogic.Vector3d(playerLook.x() * 2.0, playerLook.y() * 2.0, playerLook.z() * 2.0);

        assertEquals(2.0, vel.x(), 0.001);
        assertEquals(0.0, vel.y(), 0.001);
        assertEquals(0.0, vel.z(), 0.001);

        double gravity = 0.05;
        double drag = 0.01;

        GrappleGunLogic.Vector3d impact = GrappleGunLogic.calculateImpactCoordinate(
                pos, vel, gravity, drag, 5,
                v -> v.x() >= 5.0
        );

        assertNotNull(impact);
        assertTrue(impact.x() >= 5.0);

        GrappleGunLogic.Vector3d playerVel = new GrappleGunLogic.Vector3d(0, 0, 0);

        double springStiffness = 0.5;
        double dampingCoefficient = 0.2;

        GrappleGunLogic.Vector3d accel = GrappleGunLogic.calculatePullingAcceleration(
                playerPos, playerVel, impact, springStiffness, dampingCoefficient
        );

        // Ensure acceleration pulls player towards impact coordinate
        assertTrue(accel.x() > 0);
    }
}
