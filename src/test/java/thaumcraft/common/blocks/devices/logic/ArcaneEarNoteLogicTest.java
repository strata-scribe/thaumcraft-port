package thaumcraft.common.blocks.devices.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ArcaneEarNoteLogicTest {

    @Test
    public void testMatches() {
        // Test exactly matching note and instrument
        assertTrue(ArcaneEarNoteLogic.matches(10, "HARP", 10, "HARP"), "Expected match for identical note and instrument.");

        // Test matching note, but different instrument
        assertFalse(ArcaneEarNoteLogic.matches(10, "HARP", 10, "BASS"), "Expected mismatch for different instruments.");

        // Test different note, matching instrument
        assertFalse(ArcaneEarNoteLogic.matches(10, "HARP", 12, "HARP"), "Expected mismatch for different notes.");

        // Test different note and different instrument
        assertFalse(ArcaneEarNoteLogic.matches(10, "HARP", 12, "BASS"), "Expected mismatch for different notes and instruments.");

        // Test null ambient instrument
        assertFalse(ArcaneEarNoteLogic.matches(10, null, 10, "HARP"), "Expected mismatch for null ambient instrument.");

        // Test null tuned instrument
        assertFalse(ArcaneEarNoteLogic.matches(10, "HARP", 10, null), "Expected mismatch for null tuned instrument.");
    }

    @Test
    public void testGetPulseLengthTicks() {
        // Test pulse length ticks
        assertEquals(20, ArcaneEarNoteLogic.getPulseLengthTicks(), "Expected pulse length ticks to be 20.");
    }
}
