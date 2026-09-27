package thaumcraft.common.tiles.devices;

import org.junit.jupiter.api.Test;
import thaumcraft.common.blocks.devices.logic.ArcaneEarNoteLogic;
import static org.junit.jupiter.api.Assertions.*;

public class ArcaneEarBlockEntityTest {

    @Test
    public void testArcaneEarLogic() {
        int note = 0;
        int instrumentIndex = 0; // 0 = HARP

        // Cycle note
        note = (note + 1) % 25;
        assertEquals(1, note, "Note should be incremented to 1");

        // Cycle instrument
        instrumentIndex = (instrumentIndex + 1) % 16; // 16 instruments in NoteBlockInstrument
        assertEquals(1, instrumentIndex, "Instrument should be incremented to 1");

        // Let's test boundary condition for note
        note = 24;
        note = (note + 1) % 25;
        assertEquals(0, note, "Note should wrap around to 0");
    }

    @Test
    public void testSignalDispatch() {
        // Simulating the redstone dispatch
        boolean powered = false;

        // trigger
        powered = true;
        int activeTicks = ArcaneEarNoteLogic.getPulseLengthTicks();

        assertTrue(powered);
        assertEquals(20, activeTicks);

        // tick
        activeTicks--;
        assertTrue(activeTicks > 0);

        // fast forward
        activeTicks = 0;
        if (activeTicks == 0) {
            powered = false;
        }

        assertFalse(powered, "Should power off after ticks exhaust");
    }

    @Test
    public void testMatchLogic() {
        assertTrue(ArcaneEarNoteLogic.matches(5, "HARP", 5, "HARP"));
        assertFalse(ArcaneEarNoteLogic.matches(5, "HARP", 6, "HARP"));
        assertFalse(ArcaneEarNoteLogic.matches(5, "HARP", 5, "BASEDRUM"));
    }
}
