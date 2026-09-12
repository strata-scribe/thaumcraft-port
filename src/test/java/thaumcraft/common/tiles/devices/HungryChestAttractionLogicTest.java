package thaumcraft.common.tiles.devices;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HungryChestAttractionLogicTest {

    @Test
    public void testDistanceDecay() {
        double decay0 = HungryChestAttractionLogic.calculateDistanceDecay(0.0, 5.0);
        assertEquals(1.0, decay0, 0.0001);

        double decayMid = HungryChestAttractionLogic.calculateDistanceDecay(2.5, 5.0);
        assertEquals(0.5, decayMid, 0.0001);

        double decayMax = HungryChestAttractionLogic.calculateDistanceDecay(5.0, 5.0);
        assertEquals(0.0, decayMax, 0.0001);

        double decayOut = HungryChestAttractionLogic.calculateDistanceDecay(10.0, 5.0);
        assertEquals(0.0, decayOut, 0.0001);
    }

    @Test
    public void testPullSpeed() {
        double speedFull = HungryChestAttractionLogic.calculatePullSpeed(1.0, 0.05);
        assertEquals(0.05, speedFull, 0.0001);

        double speedHalf = HungryChestAttractionLogic.calculatePullSpeed(0.5, 0.05);
        assertEquals(0.025, speedHalf, 0.0001);

        double speedZero = HungryChestAttractionLogic.calculatePullSpeed(0.0, 0.05);
        assertEquals(0.0, speedZero, 0.0001);
    }

    @Test
    public void testAttractionVectorDirection() {
        // Item at (1, 0, 0), Chest at (0, 0, 0)
        // Vector should point towards chest: (-1, 0, 0)
        double itemX = 1.0, itemY = 0.0, itemZ = 0.0;
        double chestX = 0.0, chestY = 0.0, chestZ = 0.0;
        double maxRadius = 5.0;
        double absorbDistance = 0.5;
        double baseSpeed = 0.1;

        HungryChestAttractionLogic.AttractionResult result = HungryChestAttractionLogic.calculateAttractionVector(
            itemX, itemY, itemZ, chestX, chestY, chestZ, maxRadius, absorbDistance, baseSpeed
        );

        assertTrue(result.vx < 0);
        assertEquals(0.0, result.vy, 0.0001);
        assertEquals(0.0, result.vz, 0.0001);
        assertFalse(result.shouldAbsorb);

        // Exact distance is 1.0, decay is 1 - 1/5 = 0.8. Speed = 0.8 * 0.1 = 0.08
        // Unit vector is (-1, 0, 0), so vx should be -0.08
        assertEquals(-0.08, result.vx, 0.0001);
    }

    @Test
    public void testOutOFRadius() {
        HungryChestAttractionLogic.AttractionResult result = HungryChestAttractionLogic.calculateAttractionVector(
            10.0, 0.0, 0.0, 0.0, 0.0, 0.0, 5.0, 0.5, 0.1
        );
        assertEquals(0.0, result.vx, 0.0001);
        assertEquals(0.0, result.vy, 0.0001);
        assertEquals(0.0, result.vz, 0.0001);
        assertFalse(result.shouldAbsorb);
    }

    @Test
    public void testAbsorption() {
        HungryChestAttractionLogic.AttractionResult result = HungryChestAttractionLogic.calculateAttractionVector(
            0.2, 0.0, 0.0, 0.0, 0.0, 0.0, 5.0, 0.5, 0.1
        );
        assertTrue(result.shouldAbsorb);
    }

    @Test
    public void testExactlyOnChest() {
        HungryChestAttractionLogic.AttractionResult result = HungryChestAttractionLogic.calculateAttractionVector(
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 5.0, 0.5, 0.1
        );
        assertEquals(0.0, result.vx, 0.0001);
        assertEquals(0.0, result.vy, 0.0001);
        assertEquals(0.0, result.vz, 0.0001);
        assertTrue(result.shouldAbsorb);
    }
}
