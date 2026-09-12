package thaumcraft.common.entities.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DeathGazeLogicTest {

    @Test
    public void testCalculateGazeAngle_ZeroDegrees() {
        // Look vector pointing directly at target direction vector
        double lookX = 1.0, lookY = 0.0, lookZ = 0.0;
        double dirX = 1.0, dirY = 0.0, dirZ = 0.0;

        double angle = DeathGazeLogic.calculateGazeAngle(lookX, lookY, lookZ, dirX, dirY, dirZ);

        assertEquals(0.0, angle, 0.001, "Angle should be 0 when vectors are parallel and in same direction");
    }

    @Test
    public void testCalculateGazeAngle_90Degrees() {
        // Look vector pointing along X, target direction along Y
        double lookX = 1.0, lookY = 0.0, lookZ = 0.0;
        double dirX = 0.0, dirY = 1.0, dirZ = 0.0;

        double angle = DeathGazeLogic.calculateGazeAngle(lookX, lookY, lookZ, dirX, dirY, dirZ);

        assertEquals(90.0, angle, 0.001, "Angle should be 90 when vectors are perpendicular");
    }

    @Test
    public void testCalculateGazeAngle_180Degrees() {
        // Look vector pointing opposite to target direction
        double lookX = 1.0, lookY = 0.0, lookZ = 0.0;
        double dirX = -1.0, dirY = 0.0, dirZ = 0.0;

        double angle = DeathGazeLogic.calculateGazeAngle(lookX, lookY, lookZ, dirX, dirY, dirZ);

        assertEquals(180.0, angle, 0.001, "Angle should be 180 when vectors are opposite");
    }

    @Test
    public void testCalculateGazeAngle_ZeroVector() {
        // Look vector is zero length
        double lookX = 0.0, lookY = 0.0, lookZ = 0.0;
        double dirX = 1.0, dirY = 0.0, dirZ = 0.0;

        double angle = DeathGazeLogic.calculateGazeAngle(lookX, lookY, lookZ, dirX, dirY, dirZ);

        assertEquals(0.0, angle, 0.001, "Angle should default to 0 when a vector has zero magnitude");
    }

    @Test
    public void testIsTargetInGazeCone_InsideCone() {
        // Target is 45 degrees off, cone is 60 degrees
        double lookX = 1.0, lookY = 0.0, lookZ = 0.0;
        double dirX = 1.0, dirY = 1.0, dirZ = 0.0; // 45 degrees

        boolean inside = DeathGazeLogic.isTargetInGazeCone(lookX, lookY, lookZ, dirX, dirY, dirZ, 60.0);

        assertTrue(inside, "Target at 45 degrees should be inside a 60 degree cone");
    }

    @Test
    public void testIsTargetInGazeCone_OutsideCone() {
        // Target is 45 degrees off, cone is 30 degrees
        double lookX = 1.0, lookY = 0.0, lookZ = 0.0;
        double dirX = 1.0, dirY = 1.0, dirZ = 0.0; // 45 degrees

        boolean inside = DeathGazeLogic.isTargetInGazeCone(lookX, lookY, lookZ, dirX, dirY, dirZ, 30.0);

        assertFalse(inside, "Target at 45 degrees should be outside a 30 degree cone");
    }

    @Test
    public void testIsTargetInGazeCone_ExactEdge() {
        // Target is exactly 90 degrees off, cone is 90 degrees
        double lookX = 1.0, lookY = 0.0, lookZ = 0.0;
        double dirX = 0.0, dirY = 1.0, dirZ = 0.0; // 90 degrees

        boolean inside = DeathGazeLogic.isTargetInGazeCone(lookX, lookY, lookZ, dirX, dirY, dirZ, 90.0);

        assertTrue(inside, "Target exactly on the edge of the cone should be considered inside");
    }

    @Test
    public void testCalculateWitherDamage() {
        assertEquals(1.0f, DeathGazeLogic.calculateWitherDamage(0), 0.001f);
        assertEquals(1.5f, DeathGazeLogic.calculateWitherDamage(20), 0.001f);
        assertEquals(2.0f, DeathGazeLogic.calculateWitherDamage(40), 0.001f);
        assertEquals(3.5f, DeathGazeLogic.calculateWitherDamage(100), 0.001f);
    }

    @Test
    public void testCalculateWitherDamage_NegativeTicks() {
        assertEquals(0.0f, DeathGazeLogic.calculateWitherDamage(-10), 0.001f, "Damage should be 0 for negative ticks");
    }

    @Test
    public void testCalculateWitherDuration() {
        assertEquals(100, DeathGazeLogic.calculateWitherDuration(0));
        assertEquals(200, DeathGazeLogic.calculateWitherDuration(10));
        assertEquals(600, DeathGazeLogic.calculateWitherDuration(50));
    }

    @Test
    public void testCalculateWitherDuration_NegativeTicks() {
        assertEquals(0, DeathGazeLogic.calculateWitherDuration(-5), "Duration should be 0 for negative ticks");
    }
}
