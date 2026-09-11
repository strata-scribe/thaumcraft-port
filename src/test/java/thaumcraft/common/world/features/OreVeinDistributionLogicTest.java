package thaumcraft.common.world.features;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OreVeinDistributionLogicTest {

    @Test
    public void testValidCinnabarY() {
        assertTrue(OreVeinDistributionLogic.isValidCinnabarY(-64));
        assertTrue(OreVeinDistributionLogic.isValidCinnabarY(0));
        assertTrue(OreVeinDistributionLogic.isValidCinnabarY(-32));
        assertFalse(OreVeinDistributionLogic.isValidCinnabarY(-65));
        assertFalse(OreVeinDistributionLogic.isValidCinnabarY(1));
    }

    @Test
    public void testValidAmberY() {
        assertTrue(OreVeinDistributionLogic.isValidAmberY(0));
        assertTrue(OreVeinDistributionLogic.isValidAmberY(80));
        assertTrue(OreVeinDistributionLogic.isValidAmberY(40));
        assertFalse(OreVeinDistributionLogic.isValidAmberY(-1));
        assertFalse(OreVeinDistributionLogic.isValidAmberY(81));
    }

    @Test
    public void testCalculateUniformCinnabarY() {
        assertEquals(-64, OreVeinDistributionLogic.calculateUniformCinnabarY(0));
        assertEquals(0, OreVeinDistributionLogic.calculateUniformCinnabarY(64));
        assertEquals(-32, OreVeinDistributionLogic.calculateUniformCinnabarY(32));

        // Test negative random rolls (simulating Random.nextInt() which could be negative if not properly bounded or just testing Math.abs logic)
        assertEquals(-64, OreVeinDistributionLogic.calculateUniformCinnabarY(-65));
    }

    @Test
    public void testCalculateUniformAmberY() {
        assertEquals(0, OreVeinDistributionLogic.calculateUniformAmberY(0));
        assertEquals(80, OreVeinDistributionLogic.calculateUniformAmberY(80));
        assertEquals(40, OreVeinDistributionLogic.calculateUniformAmberY(40));

        assertEquals(0, OreVeinDistributionLogic.calculateUniformAmberY(-81));
    }

    @Test
    public void testCalculateTriangleCinnabarY() {
        // CINNABAR_MIN_Y = -64, CINNABAR_MAX_Y = 0
        // halfRange = 32
        // Y = -64 + (rand1 % 33) + (rand2 % 33)

        // Min possible Y: rand1=0, rand2=0 -> -64
        assertEquals(-64, OreVeinDistributionLogic.calculateTriangleCinnabarY(0, 0));

        // Max possible Y: rand1=32, rand2=32 -> 0
        assertEquals(0, OreVeinDistributionLogic.calculateTriangleCinnabarY(32, 32));

        // Mid (Peak): rand1=16, rand2=16 -> -32
        assertEquals(-32, OreVeinDistributionLogic.calculateTriangleCinnabarY(16, 16));
    }

    @Test
    public void testCalculateTriangleAmberY() {
        // AMBER_MIN_Y = 0, AMBER_MAX_Y = 80
        // halfRange = 40
        // Y = 0 + (rand1 % 41) + (rand2 % 41)

        // Min possible Y: rand1=0, rand2=0 -> 0
        assertEquals(0, OreVeinDistributionLogic.calculateTriangleAmberY(0, 0));

        // Max possible Y: rand1=40, rand2=40 -> 80
        assertEquals(80, OreVeinDistributionLogic.calculateTriangleAmberY(40, 40));

        // Mid (Peak): rand1=20, rand2=20 -> 40
        assertEquals(40, OreVeinDistributionLogic.calculateTriangleAmberY(20, 20));
    }

    @Test
    public void testComputeVeinSize() {
        // base = 5, variance = 3, randomRoll
        // size = 5 + (randomRoll % 4)
        assertEquals(5, OreVeinDistributionLogic.computeVeinSize(5, 3, 0));
        assertEquals(6, OreVeinDistributionLogic.computeVeinSize(5, 3, 1));
        assertEquals(7, OreVeinDistributionLogic.computeVeinSize(5, 3, 2));
        assertEquals(8, OreVeinDistributionLogic.computeVeinSize(5, 3, 3));
        assertEquals(5, OreVeinDistributionLogic.computeVeinSize(5, 3, 4));

        // negative roll
        assertEquals(6, OreVeinDistributionLogic.computeVeinSize(5, 3, -1));

        // zero variance
        assertEquals(5, OreVeinDistributionLogic.computeVeinSize(5, 0, 100));

        // negative variance
        assertEquals(5, OreVeinDistributionLogic.computeVeinSize(5, -2, 100));
    }

    @Test
    public void testComputeVeinDensity() {
        // baseVeins = 2, chance = 50, randomRoll

        // < 50 => adds 1
        assertEquals(3, OreVeinDistributionLogic.computeVeinDensity(2, 50, 0));
        assertEquals(3, OreVeinDistributionLogic.computeVeinDensity(2, 50, 49));

        // >= 50 => adds 0
        assertEquals(2, OreVeinDistributionLogic.computeVeinDensity(2, 50, 50));
        assertEquals(2, OreVeinDistributionLogic.computeVeinDensity(2, 50, 99));

        // negative roll
        assertEquals(3, OreVeinDistributionLogic.computeVeinDensity(2, 50, -49));
        assertEquals(2, OreVeinDistributionLogic.computeVeinDensity(2, 50, -50));

        // 0 chance
        assertEquals(2, OreVeinDistributionLogic.computeVeinDensity(2, 0, 0));

        // 100 chance
        assertEquals(3, OreVeinDistributionLogic.computeVeinDensity(2, 100, 99));
    }
}
