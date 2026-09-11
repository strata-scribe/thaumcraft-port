package thaumcraft.common.world.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class VisCrystalWorldgenLogicTest {

    private VisCrystalWorldgenLogic logic;

    @BeforeEach
    public void setUp() {
        logic = new VisCrystalWorldgenLogic();
    }

    @Test
    public void testGetAspectForBiome_Ignis() {
        assertEquals("ignis", logic.getAspectForBiome("minecraft:nether_wastes"));
        assertEquals("ignis", logic.getAspectForBiome("minecraft:desert"));
        assertEquals("ignis", logic.getAspectForBiome("DESERT_HILLS"));
    }

    @Test
    public void testGetAspectForBiome_Aqua() {
        assertEquals("aqua", logic.getAspectForBiome("minecraft:ocean"));
        assertEquals("aqua", logic.getAspectForBiome("minecraft:river"));
        assertEquals("aqua", logic.getAspectForBiome("DEEP_WATER"));
    }

    @Test
    public void testGetAspectForBiome_Aer() {
        assertEquals("aer", logic.getAspectForBiome("minecraft:windswept_hills"));
        assertEquals("aer", logic.getAspectForBiome("mountain"));
        assertEquals("aer", logic.getAspectForBiome("EXTREME_HILLS"));
    }

    @Test
    public void testGetAspectForBiome_Terra() {
        assertEquals("terra", logic.getAspectForBiome("minecraft:forest"));
        assertEquals("terra", logic.getAspectForBiome("jungle"));
        assertEquals("terra", logic.getAspectForBiome("swamp"));
    }

    @Test
    public void testGetAspectForBiome_Ordo() {
        assertEquals("ordo", logic.getAspectForBiome("minecraft:plains"));
        assertEquals("ordo", logic.getAspectForBiome("SUNFLOWER_PLAINS"));
    }

    @Test
    public void testGetAspectForBiome_Perditio() {
        assertEquals("perditio", logic.getAspectForBiome("minecraft:taiga"));
        assertEquals("perditio", logic.getAspectForBiome("ice_spikes"));
    }

    @Test
    public void testGetAspectForBiome_Default() {
        assertEquals("terra", logic.getAspectForBiome("minecraft:mushroom_fields"));
        assertEquals("terra", logic.getAspectForBiome("unknown"));
        assertEquals("terra", logic.getAspectForBiome(null));
    }

    @Test
    public void testCalculateClusterGeneration_SizeZero() {
        List<VisCrystalWorldgenLogic.CrystalPlacement> placements = logic.calculateClusterGeneration(10, 20, 30, 0, 12345L);
        assertTrue(placements.isEmpty());
    }

    @Test
    public void testCalculateClusterGeneration_SizeOne() {
        List<VisCrystalWorldgenLogic.CrystalPlacement> placements = logic.calculateClusterGeneration(10, 20, 30, 1, 12345L);
        assertEquals(1, placements.size());

        VisCrystalWorldgenLogic.CrystalPlacement p = placements.get(0);
        assertEquals(10, p.x);
        assertEquals(20, p.y);
        assertEquals(30, p.z);
        assertEquals(3, p.stage);
    }

    @Test
    public void testCalculateClusterGeneration_Multiple() {
        int startX = 10;
        int startY = 20;
        int startZ = 30;
        int size = 5;
        long seed = 12345L;

        List<VisCrystalWorldgenLogic.CrystalPlacement> placements = logic.calculateClusterGeneration(startX, startY, startZ, size, seed);

        assertEquals(size, placements.size());

        // Check center
        VisCrystalWorldgenLogic.CrystalPlacement center = placements.get(0);
        assertEquals(startX, center.x);
        assertEquals(startY, center.y);
        assertEquals(startZ, center.z);
        assertEquals(3, center.stage);

        // Check others
        for (int i = 1; i < size; i++) {
            VisCrystalWorldgenLogic.CrystalPlacement p = placements.get(i);

            // Check bounding box
            assertTrue(p.x >= startX - 2 && p.x <= startX + 2);
            assertTrue(p.y >= startY - 2 && p.y <= startY + 2);
            assertTrue(p.z >= startZ - 2 && p.z <= startZ + 2);

            // Stage should be 0 to 2
            assertTrue(p.stage >= 0 && p.stage <= 2);

            // Check it's not the center
            assertFalse(p.x == startX && p.y == startY && p.z == startZ);
        }

        // Ensure all locations are unique
        long uniqueCount = placements.stream()
            .map(p -> p.x + "," + p.y + "," + p.z)
            .distinct()
            .count();
        assertEquals(size, uniqueCount);
    }

    @Test
    public void testCalculateClusterGeneration_MaxAttemptsExceeded() {
        // Test that the method doesn't infinite loop if size is very large compared to the bounding box
        int size = 200; // 5x5x5 = 125 max unique spots, so it can't place 200
        List<VisCrystalWorldgenLogic.CrystalPlacement> placements = logic.calculateClusterGeneration(0, 0, 0, size, 42L);

        // It should have placed as many as it could (or stopped at maxAttempts)
        assertTrue(placements.size() > 1 && placements.size() <= 125);
    }
}
