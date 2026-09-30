package thaumcraft.common.blocks.essentia.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TubeConnectionLogic Domain Tests")
public class TubeConnectionLogicTest {

    @Test
    @DisplayName("toggleFace: inverts boolean open state")
    void testToggleFace() {
        assertFalse(TubeConnectionLogic.toggleFace(true), "true -> false");
        assertTrue(TubeConnectionLogic.toggleFace(false), "false -> true");
    }

    @Test
    @DisplayName("canConnect: requires both face to be open and neighbor to be connectable")
    void testCanConnect() {
        assertTrue(TubeConnectionLogic.canConnect(true, true), "open + connectable -> true");
        assertFalse(TubeConnectionLogic.canConnect(true, false), "open + non-connectable -> false");
        assertFalse(TubeConnectionLogic.canConnect(false, true), "closed + connectable -> false");
        assertFalse(TubeConnectionLogic.canConnect(false, false), "closed + non-connectable -> false");
    }

    @Test
    @DisplayName("encodeDirectionMask: correctly packs boolean flags into 6-bit mask")
    void testEncodeDirectionMask() {
        // All false -> 0
        assertEquals(0, TubeConnectionLogic.encodeDirectionMask(false, false, false, false, false, false));

        // Individual bits
        assertEquals(1, TubeConnectionLogic.encodeDirectionMask(true, false, false, false, false, false), "North bit 0 = 1");
        assertEquals(2, TubeConnectionLogic.encodeDirectionMask(false, true, false, false, false, false), "South bit 1 = 2");
        assertEquals(4, TubeConnectionLogic.encodeDirectionMask(false, false, true, false, false, false), "East bit 2 = 4");
        assertEquals(8, TubeConnectionLogic.encodeDirectionMask(false, false, false, true, false, false), "West bit 3 = 8");
        assertEquals(16, TubeConnectionLogic.encodeDirectionMask(false, false, false, false, true, false), "Up bit 4 = 16");
        assertEquals(32, TubeConnectionLogic.encodeDirectionMask(false, false, false, false, false, true), "Down bit 5 = 32");

        // Combined masks
        assertEquals(3, TubeConnectionLogic.encodeDirectionMask(true, true, false, false, false, false), "North + South = 3");
        assertEquals(12, TubeConnectionLogic.encodeDirectionMask(false, false, true, true, false, false), "East + West = 12");
        assertEquals(48, TubeConnectionLogic.encodeDirectionMask(false, false, false, false, true, true), "Up + Down = 48");

        // All true -> 63 (0b111111)
        assertEquals(63, TubeConnectionLogic.encodeDirectionMask(true, true, true, true, true, true));
    }

    @Test
    @DisplayName("isDirectionInMask: matches set bits and rejects unset bits")
    void testIsDirectionInMask() {
        int mask = TubeConnectionLogic.encodeDirectionMask(true, false, true, false, true, false); // North(0), East(2), Up(4)

        assertTrue(TubeConnectionLogic.isDirectionInMask(mask, 0), "North (0) should be in mask");
        assertFalse(TubeConnectionLogic.isDirectionInMask(mask, 1), "South (1) should not be in mask");
        assertTrue(TubeConnectionLogic.isDirectionInMask(mask, 2), "East (2) should be in mask");
        assertFalse(TubeConnectionLogic.isDirectionInMask(mask, 3), "West (3) should not be in mask");
        assertTrue(TubeConnectionLogic.isDirectionInMask(mask, 4), "Up (4) should be in mask");
        assertFalse(TubeConnectionLogic.isDirectionInMask(mask, 5), "Down (5) should not be in mask");

        // Test with full mask (63)
        for (int i = 0; i < 6; i++) {
            assertTrue(TubeConnectionLogic.isDirectionInMask(63, i), "Ordinal " + i + " should be in full mask");
        }

        // Test with zero mask (0)
        for (int i = 0; i < 6; i++) {
            assertFalse(TubeConnectionLogic.isDirectionInMask(0, i), "Ordinal " + i + " should not be in zero mask");
        }
    }

    @Test
    @DisplayName("isDirectionInMask: rejects out-of-bounds direction ordinals")
    void testIsDirectionInMaskOutOfBounds() {
        int[] outOfBounds = {-10, -1, 6, 7, 32, 100};
        for (int invalidOrdinal : outOfBounds) {
            assertFalse(TubeConnectionLogic.isDirectionInMask(63, invalidOrdinal),
                    "Invalid ordinal " + invalidOrdinal + " must be rejected");
        }
    }

    @Test
    @DisplayName("countConnectedFaces: counts active connections accurately from 0 to 6")
    void testCountConnectedFaces() {
        assertEquals(0, TubeConnectionLogic.countConnectedFaces(false, false, false, false, false, false));
        assertEquals(1, TubeConnectionLogic.countConnectedFaces(true, false, false, false, false, false));
        assertEquals(2, TubeConnectionLogic.countConnectedFaces(true, true, false, false, false, false));
        assertEquals(3, TubeConnectionLogic.countConnectedFaces(true, true, true, false, false, false));
        assertEquals(4, TubeConnectionLogic.countConnectedFaces(true, true, true, true, false, false));
        assertEquals(5, TubeConnectionLogic.countConnectedFaces(true, true, true, true, true, false));
        assertEquals(6, TubeConnectionLogic.countConnectedFaces(true, true, true, true, true, true));
    }

    @Test
    @DisplayName("isStraightThrough: true only for unbranched single-axis alignments")
    void testIsStraightThrough() {
        // Valid straight axes
        assertTrue(TubeConnectionLogic.isStraightThrough(true, true, false, false, false, false), "North-South straight");
        assertTrue(TubeConnectionLogic.isStraightThrough(false, false, true, true, false, false), "East-West straight");
        assertTrue(TubeConnectionLogic.isStraightThrough(false, false, false, false, true, true), "Up-Down straight");

        // Bends / corners (2 connections, non-collinear)
        assertFalse(TubeConnectionLogic.isStraightThrough(true, false, true, false, false, false), "North-East corner");
        assertFalse(TubeConnectionLogic.isStraightThrough(true, false, false, false, true, false), "North-Up corner");
        assertFalse(TubeConnectionLogic.isStraightThrough(false, false, true, false, false, true), "East-Down corner");

        // T-junctions (3 connections)
        assertFalse(TubeConnectionLogic.isStraightThrough(true, true, true, false, false, false), "T-junction NS+E");
        assertFalse(TubeConnectionLogic.isStraightThrough(false, false, true, true, true, false), "T-junction EW+U");

        // 4-way, 5-way, 6-way junctions
        assertFalse(TubeConnectionLogic.isStraightThrough(true, true, true, true, false, false), "4-way cross NS+EW");
        assertFalse(TubeConnectionLogic.isStraightThrough(true, true, true, true, true, false), "5-way junction");
        assertFalse(TubeConnectionLogic.isStraightThrough(true, true, true, true, true, true), "6-way hub");

        // Single connection / dead ends
        assertFalse(TubeConnectionLogic.isStraightThrough(true, false, false, false, false, false), "Single connection North");
        assertFalse(TubeConnectionLogic.isStraightThrough(false, false, false, false, true, false), "Single connection Up");

        // Isolated (0 connections)
        assertFalse(TubeConnectionLogic.isStraightThrough(false, false, false, false, false, false), "Zero connections");
    }

    @Test
    @DisplayName("canClearFilter: crouching + empty hand + hasFilter")
    void testCanClearFilter() {
        assertTrue(TubeConnectionLogic.canClearFilter(true, true, true));
        assertFalse(TubeConnectionLogic.canClearFilter(true, true, false));
        assertFalse(TubeConnectionLogic.canClearFilter(true, false, true));
        assertFalse(TubeConnectionLogic.canClearFilter(false, true, true));
        assertFalse(TubeConnectionLogic.canClearFilter(false, false, false));
    }

    @Test
    @DisplayName("canApplyFilter: no existing filter + valid aspect item")
    void testCanApplyFilter() {
        assertTrue(TubeConnectionLogic.canApplyFilter(false, true));
        assertFalse(TubeConnectionLogic.canApplyFilter(false, false));
        assertFalse(TubeConnectionLogic.canApplyFilter(true, true));
        assertFalse(TubeConnectionLogic.canApplyFilter(true, false));
    }
}
