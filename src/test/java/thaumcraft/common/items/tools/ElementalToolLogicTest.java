package thaumcraft.common.items.tools;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ElementalToolLogic (Axe of the Stream, Pickaxe of the Core,
 * Sword of the Zephyr, Shovel of the Earthmover, Hoe of the Growth).
 */
public class ElementalToolLogicTest {

    // =========================================================================
    // 1. Axe of the Stream
    // =========================================================================

    @Test
    public void testMagnetVelocityWithinRadius() {
        // Item at (0, 0, 0), Player at (3, 4, 0) -> distance = 5
        ElementalToolLogic.Vector3D vel = ElementalToolLogic.calculateMagnetVelocity(
                0, 0, 0, 3, 4, 0, 8.0);

        assertTrue(vel.length() > 0.0);
        assertTrue(vel.length() <= ElementalToolLogic.MAX_MAGNET_SPEED);
        // Direction should point towards player (positive x and positive y)
        assertTrue(vel.x() > 0);
        assertTrue(vel.y() > 0);
        assertEquals(0.0, vel.z(), 1e-6);
    }

    @Test
    public void testMagnetVelocityOutsideRadiusZero() {
        // Distance 10 > maxRadius 8
        ElementalToolLogic.Vector3D vel = ElementalToolLogic.calculateMagnetVelocity(
                0, 0, 0, 10, 0, 0, 8.0);
        assertEquals(0.0, vel.length(), 1e-6);
    }

    @Test
    public void testFilterAndSortTreeLogs() {
        List<ElementalToolLogic.BlockCoordinate> logs = new ArrayList<>();
        logs.add(new ElementalToolLogic.BlockCoordinate(0, 64, 0));
        logs.add(new ElementalToolLogic.BlockCoordinate(0, 70, 0));
        logs.add(new ElementalToolLogic.BlockCoordinate(0, 66, 0));

        List<ElementalToolLogic.BlockCoordinate> sorted = ElementalToolLogic.filterAndSortTreeLogs(logs);

        assertEquals(3, sorted.size());
        assertEquals(70, sorted.get(0).y()); // highest first
        assertEquals(66, sorted.get(1).y());
        assertEquals(64, sorted.get(2).y()); // lowest last
    }

    @Test
    public void testFilterTreeLogsCappedAtLimit() {
        List<ElementalToolLogic.BlockCoordinate> logs = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            logs.add(new ElementalToolLogic.BlockCoordinate(0, i, 0));
        }

        List<ElementalToolLogic.BlockCoordinate> sorted = ElementalToolLogic.filterAndSortTreeLogs(logs);
        assertEquals(ElementalToolLogic.MAX_TREE_FELL_LOGS, sorted.size());
    }

    // =========================================================================
    // 2. Pickaxe of the Core
    // =========================================================================

    @Test
    public void testFindPrioritizedOreRarityOverDistance() {
        List<ElementalToolLogic.SoundedOre> ores = new ArrayList<>();
        // Common ore at distance 2
        ores.add(new ElementalToolLogic.SoundedOre(new ElementalToolLogic.BlockCoordinate(2, 0, 0), ElementalToolLogic.ORE_TIER_COMMON, 2.0));
        // Rare ore (Diamond) at distance 8
        ores.add(new ElementalToolLogic.SoundedOre(new ElementalToolLogic.BlockCoordinate(8, 0, 0), ElementalToolLogic.ORE_TIER_RARE, 8.0));

        ElementalToolLogic.SoundedOre prioritized = ElementalToolLogic.findPrioritizedOre(ores);
        assertNotNull(prioritized);
        assertEquals(ElementalToolLogic.ORE_TIER_RARE, prioritized.rarityTier());
    }

    @Test
    public void testFindPrioritizedOreCloserDistanceWhenTied() {
        List<ElementalToolLogic.SoundedOre> ores = new ArrayList<>();
        ores.add(new ElementalToolLogic.SoundedOre(new ElementalToolLogic.BlockCoordinate(5, 0, 0), ElementalToolLogic.ORE_TIER_THAUMIC, 5.0));
        ores.add(new ElementalToolLogic.SoundedOre(new ElementalToolLogic.BlockCoordinate(2, 0, 0), ElementalToolLogic.ORE_TIER_THAUMIC, 2.0));

        ElementalToolLogic.SoundedOre prioritized = ElementalToolLogic.findPrioritizedOre(ores);
        assertNotNull(prioritized);
        assertEquals(2.0, prioritized.distance(), 1e-6);
    }

    @Test
    public void testNativeClusterDrops() {
        // Level 0 fortune: chance = 25%
        assertTrue(ElementalToolLogic.shouldDropNativeCluster(0, 0.20));
        assertFalse(ElementalToolLogic.shouldDropNativeCluster(0, 0.30));

        // Level 3 fortune: chance = 25% + 30% = 55%
        assertTrue(ElementalToolLogic.shouldDropNativeCluster(3, 0.50));
        assertFalse(ElementalToolLogic.shouldDropNativeCluster(3, 0.60));
    }

    // =========================================================================
    // 3. Shovel of the Earthmover
    // =========================================================================

    @Test
    public void testCalculate3x3GridUpFaceXZPlane() {
        ElementalToolLogic.BlockCoordinate center = new ElementalToolLogic.BlockCoordinate(10, 64, 20);
        List<ElementalToolLogic.BlockCoordinate> grid = ElementalToolLogic.calculate3x3Grid(center, ElementalToolLogic.BlockFace.UP);

        assertEquals(9, grid.size());
        for (ElementalToolLogic.BlockCoordinate pos : grid) {
            assertEquals(64, pos.y(), "Y level must remain identical on UP face click");
            assertTrue(Math.abs(pos.x() - 10) <= 1);
            assertTrue(Math.abs(pos.z() - 20) <= 1);
        }
    }

    @Test
    public void testCalculate3x3GridNorthFaceXYPlane() {
        ElementalToolLogic.BlockCoordinate center = new ElementalToolLogic.BlockCoordinate(10, 64, 20);
        List<ElementalToolLogic.BlockCoordinate> grid = ElementalToolLogic.calculate3x3Grid(center, ElementalToolLogic.BlockFace.NORTH);

        assertEquals(9, grid.size());
        for (ElementalToolLogic.BlockCoordinate pos : grid) {
            assertEquals(20, pos.z(), "Z level must remain identical on NORTH face click");
            assertTrue(Math.abs(pos.x() - 10) <= 1);
            assertTrue(Math.abs(pos.y() - 64) <= 1);
        }
    }

    @Test
    public void testCalculate3x3GridEastFaceYZPlane() {
        ElementalToolLogic.BlockCoordinate center = new ElementalToolLogic.BlockCoordinate(10, 64, 20);
        List<ElementalToolLogic.BlockCoordinate> grid = ElementalToolLogic.calculate3x3Grid(center, ElementalToolLogic.BlockFace.EAST);

        assertEquals(9, grid.size());
        for (ElementalToolLogic.BlockCoordinate pos : grid) {
            assertEquals(10, pos.x(), "X level must remain identical on EAST face click");
            assertTrue(Math.abs(pos.y() - 64) <= 1);
            assertTrue(Math.abs(pos.z() - 20) <= 1);
        }
    }

    // =========================================================================
    // 4. Sword of the Zephyr
    // =========================================================================

    @Test
    public void testZephyrImpulseCalculations() {
        // Player at (0, 0, 0), Target at (2, 0, 0)
        ElementalToolLogic.Vector3D impulse = ElementalToolLogic.calculateZephyrImpulse(
                0, 0, 0, 2, 0, 0, 6.0);

        assertTrue(impulse.x() > 0, "Must push away from player in positive X");
        assertTrue(impulse.y() >= 0.35, "Must have upward loft");
    }

    @Test
    public void testZephyrImpulseOutsideRadiusZero() {
        ElementalToolLogic.Vector3D impulse = ElementalToolLogic.calculateZephyrImpulse(
                0, 0, 0, 10, 0, 0, 6.0);
        assertEquals(0.0, impulse.length(), 1e-6);
    }

    // =========================================================================
    // 5. Hoe of the Growth
    // =========================================================================

    @Test
    public void testCropGrowthAcceleration() {
        assertTrue(ElementalToolLogic.shouldAccelerateCropGrowth(0.99, true), "Direct target always accelerated");
        assertTrue(ElementalToolLogic.shouldAccelerateCropGrowth(0.35, false), "Surrounding crop with low roll accelerated");
        assertFalse(ElementalToolLogic.shouldAccelerateCropGrowth(0.45, false), "Surrounding crop with high roll not accelerated");
    }
}
