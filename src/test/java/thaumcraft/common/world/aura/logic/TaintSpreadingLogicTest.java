package thaumcraft.common.world.aura.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TaintSpreadingLogicTest {

    @Test
    public void testGetTaintConversion() {
        // Grass and Dirt to Tainted Soil
        assertEquals("thaumcraft:tainted_soil", TaintSpreadingLogic.getTaintConversion("minecraft:grass_block"));
        assertEquals("thaumcraft:tainted_soil", TaintSpreadingLogic.getTaintConversion("minecraft:dirt"));
        assertEquals("thaumcraft:tainted_soil", TaintSpreadingLogic.getTaintConversion("minecraft:coarse_dirt"));

        // Stone to Crusted Taint
        assertEquals("thaumcraft:crusted_taint", TaintSpreadingLogic.getTaintConversion("minecraft:stone"));
        assertEquals("thaumcraft:crusted_taint", TaintSpreadingLogic.getTaintConversion("minecraft:cobblestone"));

        // Logs and Wood to Tainted Wood
        assertEquals("thaumcraft:tainted_wood", TaintSpreadingLogic.getTaintConversion("minecraft:oak_log"));
        assertEquals("thaumcraft:tainted_wood", TaintSpreadingLogic.getTaintConversion("minecraft:spruce_wood"));

        // No conversion
        assertEquals("minecraft:glass", TaintSpreadingLogic.getTaintConversion("minecraft:glass"));
        assertEquals("minecraft:diamond_block", TaintSpreadingLogic.getTaintConversion("minecraft:diamond_block"));
        assertNull(TaintSpreadingLogic.getTaintConversion(null));
    }

    @Test
    public void testCalculateSpreadProbability() {
        // Distance 0 should be max probability
        assertEquals(1.0f, TaintSpreadingLogic.calculateSpreadProbability(0, 10), 0.01f);

        // Distance greater than max radius should be 0
        assertEquals(0.0f, TaintSpreadingLogic.calculateSpreadProbability(15, 10), 0.01f);

        // Midpoint should have a reasonable scaled probability
        float midProb = TaintSpreadingLogic.calculateSpreadProbability(5, 10);
        assertTrue(midProb > 0.1f && midProb < 1.0f);
        assertEquals(0.55f, midProb, 0.01f);
    }
}
