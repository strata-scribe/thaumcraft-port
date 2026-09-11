package thaumcraft.common.world.features;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

public class CrimsonCampStructureLogicTest {

    @Test
    @DisplayName("Camp Radius Constant")
    public void testCampRadius() {
        assertEquals(5, CrimsonCampStructureLogic.CAMP_RADIUS, "Camp radius should be 5");
    }

    @Test
    @DisplayName("Altar Stand is at (0, 0)")
    public void testIsAltarStand() {
        assertTrue(CrimsonCampStructureLogic.isAltarStand(0, 0), "(0, 0) should be altar stand");
        assertFalse(CrimsonCampStructureLogic.isAltarStand(1, 0), "(1, 0) should not be altar stand");
        assertFalse(CrimsonCampStructureLogic.isAltarStand(0, -1), "(0, -1) should not be altar stand");
        assertFalse(CrimsonCampStructureLogic.isAltarStand(1, 1), "(1, 1) should not be altar stand");
    }

    @Test
    @DisplayName("Ritual Circle radius bounds (distSq between 9 and 17)")
    public void testIsRitualCircle() {
        // Inner boundary
        assertTrue(CrimsonCampStructureLogic.isRitualCircle(3, 0), "(3, 0) sq=9 should be circle");
        assertTrue(CrimsonCampStructureLogic.isRitualCircle(0, -3), "(0, -3) sq=9 should be circle");

        // Inside ring
        assertTrue(CrimsonCampStructureLogic.isRitualCircle(3, 2), "(3, 2) sq=13 should be circle");

        // Outer boundary
        assertTrue(CrimsonCampStructureLogic.isRitualCircle(4, 1), "(4, 1) sq=17 should be circle");
        assertTrue(CrimsonCampStructureLogic.isRitualCircle(-4, 0), "(-4, 0) sq=16 should be circle");

        // Too close
        assertFalse(CrimsonCampStructureLogic.isRitualCircle(2, 2), "(2, 2) sq=8 should not be circle");
        assertFalse(CrimsonCampStructureLogic.isRitualCircle(2, 0), "(2, 0) sq=4 should not be circle");

        // Too far
        assertFalse(CrimsonCampStructureLogic.isRitualCircle(4, 2), "(4, 2) sq=20 should not be circle");
        assertFalse(CrimsonCampStructureLogic.isRitualCircle(0, 5), "(0, 5) sq=25 should not be circle");
    }

    @Test
    @DisplayName("Banner positions at corners (+-4, +-4)")
    public void testIsBannerPosition() {
        assertTrue(CrimsonCampStructureLogic.isBannerPosition(4, 4), "(4, 4) should be banner");
        assertTrue(CrimsonCampStructureLogic.isBannerPosition(-4, 4), "(-4, 4) should be banner");
        assertTrue(CrimsonCampStructureLogic.isBannerPosition(4, -4), "(4, -4) should be banner");
        assertTrue(CrimsonCampStructureLogic.isBannerPosition(-4, -4), "(-4, -4) should be banner");

        assertFalse(CrimsonCampStructureLogic.isBannerPosition(3, 4), "(3, 4) should not be banner");
        assertFalse(CrimsonCampStructureLogic.isBannerPosition(4, 0), "(4, 0) should not be banner");
        assertFalse(CrimsonCampStructureLogic.isBannerPosition(0, 0), "(0, 0) should not be banner");
    }

    @Test
    @DisplayName("Portal Frame standing vertically on Z=0, dy from 1 to 3, dx in [-1, 1]")
    public void testIsPortalFrame() {
        // Top and bottom frame
        assertTrue(CrimsonCampStructureLogic.isPortalFrame(0, 1, 0), "(0, 1, 0) bottom center is frame");
        assertTrue(CrimsonCampStructureLogic.isPortalFrame(-1, 1, 0), "(-1, 1, 0) bottom left is frame");
        assertTrue(CrimsonCampStructureLogic.isPortalFrame(1, 1, 0), "(1, 1, 0) bottom right is frame");

        assertTrue(CrimsonCampStructureLogic.isPortalFrame(0, 3, 0), "(0, 3, 0) top center is frame");
        assertTrue(CrimsonCampStructureLogic.isPortalFrame(-1, 3, 0), "(-1, 3, 0) top left is frame");
        assertTrue(CrimsonCampStructureLogic.isPortalFrame(1, 3, 0), "(1, 3, 0) top right is frame");

        // Side frame (dy=2)
        assertTrue(CrimsonCampStructureLogic.isPortalFrame(-1, 2, 0), "(-1, 2, 0) left side is frame");
        assertTrue(CrimsonCampStructureLogic.isPortalFrame(1, 2, 0), "(1, 2, 0) right side is frame");
        assertFalse(CrimsonCampStructureLogic.isPortalFrame(0, 2, 0), "(0, 2, 0) center is not frame (it is the portal itself)");

        // Out of bounds dy
        assertFalse(CrimsonCampStructureLogic.isPortalFrame(0, 0, 0), "dy=0 is not frame");
        assertFalse(CrimsonCampStructureLogic.isPortalFrame(0, 4, 0), "dy=4 is not frame");

        // Out of bounds dz
        assertFalse(CrimsonCampStructureLogic.isPortalFrame(0, 2, 1), "dz=1 is not frame");

        // Out of bounds dx
        assertFalse(CrimsonCampStructureLogic.isPortalFrame(2, 2, 0), "dx=2 is not frame");
    }

    @Test
    @DisplayName("Valid Surface Biome checks")
    public void testIsValidSurfaceBiome() {
        assertTrue(CrimsonCampStructureLogic.isValidSurfaceBiome(false, false), "No ceiling, not ocean -> Valid");
        assertFalse(CrimsonCampStructureLogic.isValidSurfaceBiome(true, false), "Has ceiling -> Invalid");
        assertFalse(CrimsonCampStructureLogic.isValidSurfaceBiome(false, true), "Is ocean -> Invalid");
        assertFalse(CrimsonCampStructureLogic.isValidSurfaceBiome(true, true), "Has ceiling and is ocean -> Invalid");
    }
}
