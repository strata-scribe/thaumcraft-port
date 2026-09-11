package thaumcraft.common.world;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import thaumcraft.common.world.features.WorldgenTreeLogic;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive JUnit 5 test suite verifying WorldgenTreeLogic procedural geometry,
 * canopy mathematical models, trunk cross-sections, and obelisk structure metrics.
 */
public class WorldgenTreeLogicTest {

    @Test
    @DisplayName("Greatwood trunk height: 11 + rand11 (clamped in [11, 21])")
    public void testGreatwoodHeight() {
        int[][] testCases = {
                {0, 11},
                {1, 12},
                {5, 16},
                {9, 20},
                {10, 21}
        };
        for (int[] tc : testCases) {
            int rand11 = tc[0];
            int expectedHeight = tc[1];
            assertEquals(expectedHeight, WorldgenTreeLogic.calculateGreatwoodHeight(rand11),
                    "Greatwood height mismatch for rand11=" + rand11);
            assertEquals(expectedHeight, WorldgenTreeLogic.calculateGreatwoodHeightRaw(rand11),
                    "Greatwood raw height mismatch for rand11=" + rand11);
        }
    }

    @Test
    @DisplayName("Greatwood spider dungeon roll: 1-in-8 chance (roll8 == 0)")
    public void testGreatwoodSpiderDungeonRoll() {
        assertTrue(WorldgenTreeLogic.shouldSpawnSpiderDungeon(0), "Roll 0 should trigger spider dungeon");
        assertTrue(WorldgenTreeLogic.shouldSpawnGreatwoodSpiderNest(0), "Alias should trigger spider dungeon");
        assertTrue(WorldgenTreeLogic.shouldSpawnSpiderDungeon(8), "Roll 8 (modulo 8 = 0) should trigger dungeon");

        for (int roll = 1; roll < 8; roll++) {
            assertFalse(WorldgenTreeLogic.shouldSpawnSpiderDungeon(roll),
                    "Roll " + roll + " should not trigger spider dungeon");
            assertFalse(WorldgenTreeLogic.shouldSpawnGreatwoodSpiderNest(roll),
                    "Alias should not trigger spider dungeon for roll " + roll);
        }
    }

    @Test
    @DisplayName("Greatwood canopy layer sizing along vertical axis")
    public void testGreatwoodLayerSize() {
        int height = 20;

        // Below 30% height (y < 6)
        assertEquals(-1.618f, WorldgenTreeLogic.calculateGreatwoodLayerSize(height, 0), 0.001f,
                "Layer size below canopy base must return -1.618f");
        assertEquals(-1.618f, WorldgenTreeLogic.calculateGreatwoodLayerSize(height, 5), 0.001f,
                "Layer size below canopy base must return -1.618f");

        // At midpoint y = height / 2 = 10 (dy = 0 => h2 * 0.5f = 10 * 0.5 = 5.0f)
        assertEquals(5.0f, WorldgenTreeLogic.calculateGreatwoodLayerSize(height, 10), 0.001f,
                "Layer size at midpoint should be 5.0f");

        // Outside upper limit
        assertEquals(0.0f, WorldgenTreeLogic.calculateGreatwoodLayerSize(height, 25), 0.001f,
                "Layer size above tree height should be 0.0f");
    }

    @Test
    @DisplayName("Greatwood dual canopy radius scaling (1.2 base, 1.66 upper)")
    public void testGreatwoodCanopyGeometry() {
        float trunkRadius = 2.0f;
        float baseRadius = WorldgenTreeLogic.calculateBaseCanopyRadius(trunkRadius); // 2.4f
        float upperRadius = WorldgenTreeLogic.calculateUpperCanopyRadius(trunkRadius); // 3.32f

        assertEquals(2.4f, baseRadius, 0.001f, "Base canopy radius scaleWidth 1.2 mismatch");
        assertEquals(3.32f, upperRadius, 0.001f, "Upper canopy radius scaleWidth 1.66 mismatch");

        // Center (0, 0)
        assertTrue(WorldgenTreeLogic.isInsideBaseCanopy(0, 0, baseRadius), "Center should be inside base canopy");
        assertTrue(WorldgenTreeLogic.isInsideUpperCanopy(0, 0, upperRadius), "Center should be inside upper canopy");
        assertTrue(WorldgenTreeLogic.isInsideGreatwoodCanopy(0, 0, baseRadius), "Center should be inside canopy");

        // Point (2, 1): distSq = 4 + 1 = 5. baseRadiusSq = 5.76 => inside
        assertTrue(WorldgenTreeLogic.isInsideBaseCanopy(2, 1, baseRadius), "(2,1) distSq 5 <= 5.76");
        assertTrue(WorldgenTreeLogic.isInsideDualCanopy(2, 1, baseRadius, upperRadius, false), "Dual canopy base pass");

        // Point (2, 2): distSq = 8. baseRadiusSq = 5.76 => outside base, inside upper (upperRadiusSq = 11.02)
        assertFalse(WorldgenTreeLogic.isInsideBaseCanopy(2, 2, baseRadius), "(2,2) distSq 8 > 5.76");
        assertTrue(WorldgenTreeLogic.isInsideUpperCanopy(2, 2, upperRadius), "(2,2) distSq 8 <= 11.02");
        assertFalse(WorldgenTreeLogic.isInsideDualCanopy(2, 2, baseRadius, upperRadius, false), "Dual canopy base pass");
        assertTrue(WorldgenTreeLogic.isInsideDualCanopy(2, 2, baseRadius, upperRadius, true), "Dual canopy upper pass");

        // Far point (4, 4): distSq = 32 => outside both
        assertFalse(WorldgenTreeLogic.isInsideUpperCanopy(4, 4, upperRadius), "(4,4) distSq 32 > 11.02");
    }

    @Test
    @DisplayName("Silverwood trunk cross-section: core (0,0) and 4 cardinal arms ((+-1,0), (0,+-1))")
    public void testSilverwoodTrunkCrossShape() {
        // Core
        assertTrue(WorldgenTreeLogic.isSilverwoodTrunkBlock(0, 0), "Core (0,0) must be trunk");

        // Cardinal arms
        assertTrue(WorldgenTreeLogic.isSilverwoodTrunkBlock(1, 0), "Cardinal East (1,0) must be trunk");
        assertTrue(WorldgenTreeLogic.isSilverwoodTrunkBlock(-1, 0), "Cardinal West (-1,0) must be trunk");
        assertTrue(WorldgenTreeLogic.isSilverwoodTrunkBlock(0, 1), "Cardinal South (0,1) must be trunk");
        assertTrue(WorldgenTreeLogic.isSilverwoodTrunkBlock(0, -1), "Cardinal North (0,-1) must be trunk");

        // Diagonal corners: not trunk (these are buttresses)
        assertFalse(WorldgenTreeLogic.isSilverwoodTrunkBlock(1, 1), "Diagonal (1,1) is not trunk");
        assertFalse(WorldgenTreeLogic.isSilverwoodTrunkBlock(-1, 1), "Diagonal (-1,1) is not trunk");
        assertFalse(WorldgenTreeLogic.isSilverwoodTrunkBlock(1, -1), "Diagonal (1,-1) is not trunk");
        assertFalse(WorldgenTreeLogic.isSilverwoodTrunkBlock(-1, -1), "Diagonal (-1,-1) is not trunk");

        // Distant blocks
        assertFalse(WorldgenTreeLogic.isSilverwoodTrunkBlock(2, 0), "Distant (2,0) is not trunk");
        assertFalse(WorldgenTreeLogic.isSilverwoodTrunkBlock(0, 2), "Distant (0,2) is not trunk");
    }

    @Test
    @DisplayName("Silverwood root buttresses at ground level: 4 diagonal positions ((+-1, +-1))")
    public void testSilverwoodRootButtresses() {
        // Diagonals are buttresses
        assertTrue(WorldgenTreeLogic.isSilverwoodButtressBlock(1, 1), "(1,1) is buttress");
        assertTrue(WorldgenTreeLogic.isSilverwoodButtressBlock(-1, 1), "(-1,1) is buttress");
        assertTrue(WorldgenTreeLogic.isSilverwoodButtressBlock(1, -1), "(1,-1) is buttress");
        assertTrue(WorldgenTreeLogic.isSilverwoodButtressBlock(-1, -1), "(-1,-1) is buttress");

        // Core and cardinals are not buttresses
        assertFalse(WorldgenTreeLogic.isSilverwoodButtressBlock(0, 0), "Core (0,0) is not buttress");
        assertFalse(WorldgenTreeLogic.isSilverwoodButtressBlock(1, 0), "Cardinal (1,0) is not buttress");
        assertFalse(WorldgenTreeLogic.isSilverwoodButtressBlock(0, 1), "Cardinal (0,1) is not buttress");
    }

    @Test
    @DisplayName("Silverwood spherical foliage: (dx*dx + dy*dy + dz*dz) <= radiusSq")
    public void testSilverwoodSphericalFoliage() {
        int radiusSq = 10;

        // Center
        assertTrue(WorldgenTreeLogic.isInsideSilverwoodCanopy(0, 0, 0, radiusSq), "Center (0,0,0) is inside canopy");

        // Close offsets: (1, 1, 1) distSq = 3 <= 10
        assertTrue(WorldgenTreeLogic.isInsideSilverwoodCanopy(1, 1, 1, radiusSq), "(1,1,1) distSq 3 <= 10");

        // Axis boundary: (3, 0, 0) distSq = 9 <= 10
        assertTrue(WorldgenTreeLogic.isInsideSilverwoodCanopy(3, 0, 0, radiusSq), "(3,0,0) distSq 9 <= 10");

        // Outside sphere: (3, 1, 1) distSq = 9 + 1 + 1 = 11 > 10
        assertFalse(WorldgenTreeLogic.isInsideSilverwoodCanopy(3, 1, 1, radiusSq), "(3,1,1) distSq 11 > 10");

        // With bonus radius
        assertTrue(WorldgenTreeLogic.isSilverwoodCanopyBlock(3, 1, 1, 2), "distSq 11 <= 10 + 2");
        assertFalse(WorldgenTreeLogic.isSilverwoodCanopyBlock(3, 2, 2, 2), "distSq 9+4+4=17 > 12");
    }

    @Test
    @DisplayName("Silverwood trunk height: 7 + rand4 (range [7, 10])")
    public void testSilverwoodHeight() {
        int[][] testCases = {
                {0, 7},
                {1, 8},
                {2, 9},
                {3, 10}
        };
        for (int[] tc : testCases) {
            int rand4 = tc[0];
            int expectedHeight = tc[1];
            assertEquals(expectedHeight, WorldgenTreeLogic.calculateSilverwoodHeight(rand4),
                    "Silverwood height mismatch for rand4=" + rand4);
            assertEquals(expectedHeight, WorldgenTreeLogic.calculateSilverwoodHeight(7, 4, rand4),
                    "Silverwood parameterized height mismatch for roll=" + rand4);
        }
    }

    @Test
    @DisplayName("Eldritch Obelisk platform bounds (7x7 dais: |dx| <= 3 && |dz| <= 3)")
    public void testEldritchObeliskPlatform() {
        int radius = 3;

        // Inside platform
        assertTrue(WorldgenTreeLogic.isObeliskPlatformBlock(0, 0, radius), "Center is inside platform");
        assertTrue(WorldgenTreeLogic.isObeliskPlatformBlock(3, 3, radius), "Corner (3,3) is inside platform");
        assertTrue(WorldgenTreeLogic.isObeliskPlatformBlock(-3, 2, radius), "Edge (-3,2) is inside platform");

        // Outside platform
        assertFalse(WorldgenTreeLogic.isObeliskPlatformBlock(4, 0, radius), "(4,0) is outside platform");
        assertFalse(WorldgenTreeLogic.isObeliskPlatformBlock(0, -4, radius), "(0,-4) is outside platform");
        assertFalse(WorldgenTreeLogic.isObeliskPlatformBlock(4, 4, radius), "(4,4) is outside platform");
    }

    @Test
    @DisplayName("Eldritch Obelisk pedestals at 4 corners ((+-3, +-3))")
    public void testEldritchObeliskPedestals() {
        int radius = 3;

        // 4 Corners are pedestals
        assertTrue(WorldgenTreeLogic.isObeliskPedestal(3, 3, radius), "(3,3) is pedestal");
        assertTrue(WorldgenTreeLogic.isObeliskPedestal(-3, 3, radius), "(-3,3) is pedestal");
        assertTrue(WorldgenTreeLogic.isObeliskPedestal(3, -3, radius), "(3,-3) is pedestal");
        assertTrue(WorldgenTreeLogic.isObeliskPedestal(-3, -3, radius), "(-3,-3) is pedestal");

        // Non-corners are not pedestals
        assertFalse(WorldgenTreeLogic.isObeliskPedestal(3, 0, radius), "Edge (3,0) is not pedestal");
        assertFalse(WorldgenTreeLogic.isObeliskPedestal(0, 3, radius), "Edge (0,3) is not pedestal");
        assertFalse(WorldgenTreeLogic.isObeliskPedestal(0, 0, radius), "Center (0,0) is not pedestal");
    }

    @Test
    @DisplayName("Eldritch Obelisk spire column, height range [5, 7], and capstone")
    public void testEldritchObeliskSpireAndCapstone() {
        // Spire column is at (0, 0)
        assertTrue(WorldgenTreeLogic.isObeliskSpireBlock(0, 0), "Spire is centered at (0,0)");
        assertFalse(WorldgenTreeLogic.isObeliskSpireBlock(1, 0), "(1,0) is not spire");
        assertFalse(WorldgenTreeLogic.isObeliskSpireBlock(0, -1), "(0,-1) is not spire");

        // Spire height in [5, 7]
        assertEquals(5, WorldgenTreeLogic.calculateObeliskSpireHeight(0), "rand 0 yields spire height 5");
        assertEquals(6, WorldgenTreeLogic.calculateObeliskSpireHeight(1), "rand 1 yields spire height 6");
        assertEquals(7, WorldgenTreeLogic.calculateObeliskSpireHeight(2), "rand 2 yields spire height 7");
        assertEquals(5, WorldgenTreeLogic.calculateObeliskSpireHeight(3), "rand 3 wraps to spire height 5");

        // Capstone at top of spire
        int spireHeight = 6;
        assertTrue(WorldgenTreeLogic.isObeliskCapstone(0, spireHeight, 0, spireHeight),
                "Capstone at (0, spireHeight, 0)");
        assertFalse(WorldgenTreeLogic.isObeliskCapstone(0, spireHeight - 1, 0, spireHeight),
                "Wrong Y is not capstone");
        assertFalse(WorldgenTreeLogic.isObeliskCapstone(1, spireHeight, 0, spireHeight),
                "Off-center is not capstone");
    }
}
