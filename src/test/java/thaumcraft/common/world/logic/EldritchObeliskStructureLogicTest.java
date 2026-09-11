package thaumcraft.common.world.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class EldritchObeliskStructureLogicTest {

    @Test
    @DisplayName("Eldritch Obelisk platform bounds (7x7 dais: |dx| <= 3 && |dz| <= 3)")
    public void testEldritchObeliskPlatform() {
        int radius = 3;

        // Inside platform
        assertTrue(EldritchObeliskStructureLogic.isObeliskPlatformBlock(0, 0, radius), "Center is inside platform");
        assertTrue(EldritchObeliskStructureLogic.isObeliskPlatformBlock(3, 3, radius), "Corner (3,3) is inside platform");
        assertTrue(EldritchObeliskStructureLogic.isObeliskPlatformBlock(-3, 2, radius), "Edge (-3,2) is inside platform");

        // Outside platform
        assertFalse(EldritchObeliskStructureLogic.isObeliskPlatformBlock(4, 0, radius), "(4,0) is outside platform");
        assertFalse(EldritchObeliskStructureLogic.isObeliskPlatformBlock(0, -4, radius), "(0,-4) is outside platform");
        assertFalse(EldritchObeliskStructureLogic.isObeliskPlatformBlock(4, 4, radius), "(4,4) is outside platform");
    }

    @Test
    @DisplayName("Eldritch Obelisk pedestals at 4 corners ((+-3, +-3))")
    public void testEldritchObeliskPedestals() {
        int radius = 3;

        // 4 Corners are pedestals
        assertTrue(EldritchObeliskStructureLogic.isObeliskPedestal(3, 3, radius), "(3,3) is pedestal");
        assertTrue(EldritchObeliskStructureLogic.isObeliskPedestal(-3, 3, radius), "(-3,3) is pedestal");
        assertTrue(EldritchObeliskStructureLogic.isObeliskPedestal(3, -3, radius), "(3,-3) is pedestal");
        assertTrue(EldritchObeliskStructureLogic.isObeliskPedestal(-3, -3, radius), "(-3,-3) is pedestal");

        // Non-corners are not pedestals
        assertFalse(EldritchObeliskStructureLogic.isObeliskPedestal(3, 0, radius), "Edge (3,0) is not pedestal");
        assertFalse(EldritchObeliskStructureLogic.isObeliskPedestal(0, 3, radius), "Edge (0,3) is not pedestal");
        assertFalse(EldritchObeliskStructureLogic.isObeliskPedestal(0, 0, radius), "Center (0,0) is not pedestal");
    }

    @Test
    @DisplayName("Eldritch Obelisk spire block")
    public void testEldritchObeliskSpireBlock() {
        assertTrue(EldritchObeliskStructureLogic.isObeliskSpireBlock(0, 0), "Spire is centered at (0,0)");
        assertFalse(EldritchObeliskStructureLogic.isObeliskSpireBlock(1, 0), "(1,0) is not spire");
        assertFalse(EldritchObeliskStructureLogic.isObeliskSpireBlock(0, -1), "(0,-1) is not spire");
    }

    @Test
    @DisplayName("Eldritch Obelisk spire height is exactly 12")
    public void testEldritchObeliskSpireHeight() {
        assertEquals(12, EldritchObeliskStructureLogic.getSpireHeight(), "Spire height should be exactly 12");
    }

    @Test
    @DisplayName("Eldritch Obelisk capstone")
    public void testEldritchObeliskCapstone() {
        int spireHeight = EldritchObeliskStructureLogic.getSpireHeight();
        assertTrue(EldritchObeliskStructureLogic.isObeliskCapstone(0, spireHeight + 1, 0), "Capstone at (0, spireHeight + 1, 0)");
        assertFalse(EldritchObeliskStructureLogic.isObeliskCapstone(0, spireHeight, 0), "Wrong Y is not capstone");
        assertFalse(EldritchObeliskStructureLogic.isObeliskCapstone(1, spireHeight + 1, 0), "Off-center is not capstone");
    }

    @Test
    @DisplayName("Eldritch Obelisk glyphed stone ring")
    public void testEldritchObeliskGlyphedStone() {
        assertTrue(EldritchObeliskStructureLogic.isGlyphCarvedStone(2, 0), "(2, 0) is glyphed");
        assertTrue(EldritchObeliskStructureLogic.isGlyphCarvedStone(2, 2), "(2, 2) is glyphed");
        assertTrue(EldritchObeliskStructureLogic.isGlyphCarvedStone(-2, -2), "(-2, -2) is glyphed");
        assertTrue(EldritchObeliskStructureLogic.isGlyphCarvedStone(0, -2), "(0, -2) is glyphed");

        assertFalse(EldritchObeliskStructureLogic.isGlyphCarvedStone(3, 0), "(3, 0) is not glyphed");
        assertFalse(EldritchObeliskStructureLogic.isGlyphCarvedStone(1, 1), "(1, 1) is not glyphed");
        assertFalse(EldritchObeliskStructureLogic.isGlyphCarvedStone(3, 3), "(3, 3) is not glyphed");
    }

    @Test
    @DisplayName("Eldritch Obelisk calculate bounding box")
    public void testEldritchObeliskBoundingBox() {
        int radius = 3;
        int[] bb = EldritchObeliskStructureLogic.calculateBoundingBox(radius);
        assertArrayEquals(new int[]{-3, 0, -3, 3, 13, 3}, bb, "Bounding box should be [-3, 0, -3, 3, 13, 3]");
    }

    @Test
    @DisplayName("Eldritch Obelisk piece offsets count")
    public void testEldritchObeliskPieceOffsets() {
        int radius = 3;
        List<int[]> offsets = EldritchObeliskStructureLogic.getPieceOffsets(radius);

        // Count total pieces:
        // Platform: 7x7 = 49
        // Pedestals: 4
        // Spire: 12
        // Capstone: 1
        // Total = 66
        assertEquals(66, offsets.size(), "Total pieces should be 66");

        // Verify some specific pieces
        boolean foundCapstone = false;
        boolean foundCenterSpire = false;
        for (int[] offset : offsets) {
            if (offset[0] == 0 && offset[1] == 13 && offset[2] == 0) foundCapstone = true;
            if (offset[0] == 0 && offset[1] == 5 && offset[2] == 0) foundCenterSpire = true;
        }

        assertTrue(foundCapstone, "Capstone should be in offsets");
        assertTrue(foundCenterSpire, "Center spire should be in offsets");
    }
}
