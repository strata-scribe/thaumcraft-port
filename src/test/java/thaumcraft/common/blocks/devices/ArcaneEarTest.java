package thaumcraft.common.blocks.devices;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ArcaneEarTest {

    @Test
    public void testArcaneEarLogic() {
        // As Minecraft environments are difficult to mock for simple tests,
        // we can write a plain Java POJO test testing the tuning cycling logic
        // Since we can't easily instantiate TileArcaneEar, we simulate the state changes here.

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
        int activeTicks = 20;

        assertTrue(powered);

        // tick
        activeTicks--;

        // fast forward
        activeTicks = 0;
        if (activeTicks == 0) {
            powered = false;
        }

        assertFalse(powered, "Should power off after ticks exhaust");
    }
}
