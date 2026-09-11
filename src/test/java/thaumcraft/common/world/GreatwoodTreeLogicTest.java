package thaumcraft.common.world;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import thaumcraft.common.world.features.GreatwoodTreeGeneratorLogic;
import thaumcraft.common.world.features.GreatwoodTreeLogic;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GreatwoodTreeLogicTest {

    @Test
    @DisplayName("Branch blocks are calculated via 3D line drawing and connect correctly")
    public void testCalculateBranchBlocks() {
        // Simple straight branch along X axis
        List<int[]> lineX = GreatwoodTreeLogic.calculateBranchBlocks(0, 0, 0, 5, 0, 0);
        assertEquals(6, lineX.size(), "Line along X axis should have 6 blocks");
        assertArrayEquals(new int[]{0, 0, 0}, lineX.get(0));
        assertArrayEquals(new int[]{5, 0, 0}, lineX.get(lineX.size() - 1));

        // Diagonal branch
        List<int[]> diagonal = GreatwoodTreeLogic.calculateBranchBlocks(0, 0, 0, 3, 3, 3);
        assertEquals(4, diagonal.size(), "Diagonal should have 4 blocks");
        assertArrayEquals(new int[]{0, 0, 0}, diagonal.get(0));
        assertArrayEquals(new int[]{3, 3, 3}, diagonal.get(diagonal.size() - 1));

        // Start and end are same
        List<int[]> same = GreatwoodTreeLogic.calculateBranchBlocks(2, 2, 2, 2, 2, 2);
        assertEquals(1, same.size(), "Same start and end should yield 1 block");
        assertArrayEquals(new int[]{2, 2, 2}, same.get(0));
    }

    @Test
    @DisplayName("Trunk pillar is 2x2: coordinates (0,0), (1,0), (0,1), (1,1)")
    public void testTrunkPillarBounds() {
        assertTrue(GreatwoodTreeGeneratorLogic.isTrunkPillar(0, 0));
        assertTrue(GreatwoodTreeGeneratorLogic.isTrunkPillar(1, 0));
        assertTrue(GreatwoodTreeGeneratorLogic.isTrunkPillar(0, 1));
        assertTrue(GreatwoodTreeGeneratorLogic.isTrunkPillar(1, 1));

        assertFalse(GreatwoodTreeGeneratorLogic.isTrunkPillar(-1, 0));
        assertFalse(GreatwoodTreeGeneratorLogic.isTrunkPillar(2, 0));
        assertFalse(GreatwoodTreeGeneratorLogic.isTrunkPillar(0, -1));
        assertFalse(GreatwoodTreeGeneratorLogic.isTrunkPillar(1, 2));
    }

    @Test
    @DisplayName("Leaf cluster evaluates as a sphere within a specified radius")
    public void testLeafClusterSpheres() {
        // Radius 3
        float radius = 3.0f;

        // Center
        assertTrue(GreatwoodTreeGeneratorLogic.isInsideLeafCluster(0, 0, 0, radius));

        // Inside edge
        assertTrue(GreatwoodTreeGeneratorLogic.isInsideLeafCluster(2, 2, 1, radius)); // 4+4+1=9 <= 9

        // Outside
        assertFalse(GreatwoodTreeGeneratorLogic.isInsideLeafCluster(3, 1, 1, radius)); // 9+1+1=11 > 9
    }

    @Test
    @DisplayName("Hollow chamber probability evaluates correctly based on random roll")
    public void testHollowChamberChance() {
        int bound = 8;
        assertTrue(GreatwoodTreeGeneratorLogic.shouldGenerateHollowChamber(0, bound));
        assertFalse(GreatwoodTreeGeneratorLogic.shouldGenerateHollowChamber(1, bound));
        assertTrue(GreatwoodTreeGeneratorLogic.shouldGenerateHollowChamber(8, bound));
    }

    @Test
    @DisplayName("Hollow chamber evaluates correct content for Spawner, Web, Air, and Trunk")
    public void testHollowChamberContent() {
        int chamberHeight = 5;

        // Base layer is solid trunk
        assertEquals(GreatwoodTreeGeneratorLogic.ChamberContent.TRUNK, GreatwoodTreeGeneratorLogic.getChamberContent(0, 0, 0, chamberHeight));

        // Top layer is solid trunk
        assertEquals(GreatwoodTreeGeneratorLogic.ChamberContent.TRUNK, GreatwoodTreeGeneratorLogic.getChamberContent(0, chamberHeight - 1, 0, chamberHeight));

        // Y = 1 has spawner at center (0,0) and web elsewhere
        assertEquals(GreatwoodTreeGeneratorLogic.ChamberContent.SPAWNER, GreatwoodTreeGeneratorLogic.getChamberContent(0, 1, 0, chamberHeight));
        assertEquals(GreatwoodTreeGeneratorLogic.ChamberContent.WEB, GreatwoodTreeGeneratorLogic.getChamberContent(1, 1, 0, chamberHeight));
        assertEquals(GreatwoodTreeGeneratorLogic.ChamberContent.WEB, GreatwoodTreeGeneratorLogic.getChamberContent(0, 1, 1, chamberHeight));

        // Y = 2 has webs
        assertEquals(GreatwoodTreeGeneratorLogic.ChamberContent.WEB, GreatwoodTreeGeneratorLogic.getChamberContent(0, 2, 0, chamberHeight));

        // Y = 3 is air (since chamberHeight is 5, layer 3 is the top hollow layer)
        assertEquals(GreatwoodTreeGeneratorLogic.ChamberContent.AIR, GreatwoodTreeGeneratorLogic.getChamberContent(0, 3, 0, chamberHeight));
    }
}
