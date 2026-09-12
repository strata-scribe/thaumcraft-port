package thaumcraft.common.tiles.crafting.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CrucibleBubblePhysicsLogicTest {

    @Test
    public void testCalculateBubbleBurstCoordinates() {
        double minBound = 0.125;
        double maxBound = 0.875;
        double fluidHeight = 0.75;

        // Test lower bounds (randomX = 0.0, randomZ = 0.0)
        double[] coordsMin = CrucibleBubblePhysicsLogic.calculateBubbleBurstCoordinates(0.0, 0.0, fluidHeight, minBound, maxBound);
        assertArrayEquals(new double[]{minBound, fluidHeight, minBound}, coordsMin, 0.0001, "Lower bound coords failed");

        // Test upper bounds (randomX = 1.0, randomZ = 1.0)
        double[] coordsMax = CrucibleBubblePhysicsLogic.calculateBubbleBurstCoordinates(1.0, 1.0, fluidHeight, minBound, maxBound);
        assertArrayEquals(new double[]{maxBound, fluidHeight, maxBound}, coordsMax, 0.0001, "Upper bound coords failed");

        // Test center/midpoint (randomX = 0.5, randomZ = 0.5)
        double center = minBound + (maxBound - minBound) * 0.5;
        double[] coordsMid = CrucibleBubblePhysicsLogic.calculateBubbleBurstCoordinates(0.5, 0.5, fluidHeight, minBound, maxBound);
        assertArrayEquals(new double[]{center, fluidHeight, center}, coordsMid, 0.0001, "Midpoint coords failed");
    }

    @Test
    public void testCalculateSteamDispersionVector() {
        double horizontalSpread = 0.1;
        double upwardVelocity = 0.5;

        // Angle 0: cos(0)=1, sin(0)=0 -> [horizontalSpread, upwardVelocity, 0]
        double[] vec0 = CrucibleBubblePhysicsLogic.calculateSteamDispersionVector(0.0, horizontalSpread, upwardVelocity);
        assertEquals(horizontalSpread, vec0[0], 0.0001, "dx failed at angle 0");
        assertEquals(upwardVelocity, vec0[1], 0.0001, "dy failed at angle 0");
        assertEquals(0.0, vec0[2], 0.0001, "dz failed at angle 0");

        // Angle PI/2 (90 deg): cos(PI/2)=0, sin(PI/2)=1 -> [0, upwardVelocity, horizontalSpread]
        double[] vec90 = CrucibleBubblePhysicsLogic.calculateSteamDispersionVector(Math.PI / 2, horizontalSpread, upwardVelocity);
        assertEquals(0.0, vec90[0], 0.0001, "dx failed at angle PI/2");
        assertEquals(upwardVelocity, vec90[1], 0.0001, "dy failed at angle PI/2");
        assertEquals(horizontalSpread, vec90[2], 0.0001, "dz failed at angle PI/2");

        // Angle PI (180 deg): cos(PI)=-1, sin(PI)=0 -> [-horizontalSpread, upwardVelocity, 0]
        double[] vec180 = CrucibleBubblePhysicsLogic.calculateSteamDispersionVector(Math.PI, horizontalSpread, upwardVelocity);
        assertEquals(-horizontalSpread, vec180[0], 0.0001, "dx failed at angle PI");
        assertEquals(upwardVelocity, vec180[1], 0.0001, "dy failed at angle PI");
        assertEquals(0.0, vec180[2], 0.0001, "dz failed at angle PI");
    }
}
