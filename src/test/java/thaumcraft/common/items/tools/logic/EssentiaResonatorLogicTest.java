package thaumcraft.common.items.tools.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EssentiaResonatorLogic Domain Tests")
public class EssentiaResonatorLogicTest {

    @Test
    @DisplayName("Format suction string with and without aspect tags, zero and positive suction")
    public void testFormatSuction() {
        // Empty or null tag with positive suction
        assertEquals("Suction: 16", EssentiaResonatorLogic.formatSuction(16, null));
        assertEquals("Suction: 8", EssentiaResonatorLogic.formatSuction(8, ""));

        // Empty or null tag with zero or negative suction
        assertEquals("No suction", EssentiaResonatorLogic.formatSuction(0, null));
        assertEquals("No suction", EssentiaResonatorLogic.formatSuction(0, ""));
        assertEquals("No suction", EssentiaResonatorLogic.formatSuction(-5, null));
        assertEquals("No suction", EssentiaResonatorLogic.formatSuction(-1, ""));

        // Tagged suction
        assertEquals("Suction: 16 (ignis)", EssentiaResonatorLogic.formatSuction(16, "ignis"));
        assertEquals("Suction: 32 (aqua)", EssentiaResonatorLogic.formatSuction(32, "aqua"));
        assertEquals("Suction: 0 (aer)", EssentiaResonatorLogic.formatSuction(0, "aer"));
    }

    @Test
    @DisplayName("Device inspection capability: tube, jar, smelter, alembic, centrifuge, bellows")
    public void testCanInspectDevice() {
        // Valid essentia devices
        assertTrue(EssentiaResonatorLogic.canInspectDevice("thaumcraft:tube"));
        assertTrue(EssentiaResonatorLogic.canInspectDevice("thaumcraft:tube_valve"));
        assertTrue(EssentiaResonatorLogic.canInspectDevice("thaumcraft:jar_normal"));
        assertTrue(EssentiaResonatorLogic.canInspectDevice("thaumcraft:jar_void"));
        assertTrue(EssentiaResonatorLogic.canInspectDevice("thaumcraft:smelter_basic"));
        assertTrue(EssentiaResonatorLogic.canInspectDevice("thaumcraft:smelter_thaumium"));
        assertTrue(EssentiaResonatorLogic.canInspectDevice("thaumcraft:alembic"));
        assertTrue(EssentiaResonatorLogic.canInspectDevice("thaumcraft:centrifuge"));
        assertTrue(EssentiaResonatorLogic.canInspectDevice("thaumcraft:bellows"));
        assertTrue(EssentiaResonatorLogic.canInspectDevice("TUBE_FILTER"));

        // Invalid devices and edge cases
        assertFalse(EssentiaResonatorLogic.canInspectDevice("minecraft:dirt"));
        assertFalse(EssentiaResonatorLogic.canInspectDevice("minecraft:stone"));
        assertFalse(EssentiaResonatorLogic.canInspectDevice("minecraft:air"));
        assertFalse(EssentiaResonatorLogic.canInspectDevice("thaumcraft:pedestal_arcane"));
        assertFalse(EssentiaResonatorLogic.canInspectDevice(null));
        assertFalse(EssentiaResonatorLogic.canInspectDevice(""));
    }

    @Test
    @DisplayName("Calculate resonator pitch across min, max, boundary, and zero values")
    public void testCalculateResonatorPitch() {
        // Zero or negative suction returns 1.0f base pitch
        assertEquals(1.0f, EssentiaResonatorLogic.calculateResonatorPitch(0, 32), 1e-4f);
        assertEquals(1.0f, EssentiaResonatorLogic.calculateResonatorPitch(-5, 32), 1e-4f);

        // Zero or negative max suction returns 1.0f
        assertEquals(1.0f, EssentiaResonatorLogic.calculateResonatorPitch(16, 0), 1e-4f);
        assertEquals(1.0f, EssentiaResonatorLogic.calculateResonatorPitch(16, -10), 1e-4f);

        // Half suction: 0.5 + (0.5 * 1.5) = 1.25f
        assertEquals(1.25f, EssentiaResonatorLogic.calculateResonatorPitch(16, 32), 1e-4f);

        // Full suction: 0.5 + (1.0 * 1.5) = 2.0f
        assertEquals(2.0f, EssentiaResonatorLogic.calculateResonatorPitch(32, 32), 1e-4f);

        // Over-saturated suction clamped to max pitch: 0.5 + 1.5 = 2.0f
        assertEquals(2.0f, EssentiaResonatorLogic.calculateResonatorPitch(64, 32), 1e-4f);

        // Low suction ratio
        assertEquals(0.515f, EssentiaResonatorLogic.calculateResonatorPitch(1, 100), 1e-4f);
    }
}
