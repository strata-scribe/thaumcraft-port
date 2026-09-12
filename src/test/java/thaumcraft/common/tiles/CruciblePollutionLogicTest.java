package thaumcraft.common.tiles;

import org.junit.jupiter.api.Test;
import thaumcraft.common.tiles.crafting.CruciblePollutionLogic;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CruciblePollutionLogicTest {

    @Test
    public void testCalculateSpilloverPollution() {
        assertEquals(0.0f, CruciblePollutionLogic.calculateSpilloverPollution(0), 0.001f);
        assertEquals(0.1f, CruciblePollutionLogic.calculateSpilloverPollution(1), 0.001f);
        assertEquals(1.0f, CruciblePollutionLogic.calculateSpilloverPollution(10), 0.001f);
        assertEquals(2.5f, CruciblePollutionLogic.calculateSpilloverPollution(25), 0.001f);
        assertEquals(10.0f, CruciblePollutionLogic.calculateSpilloverPollution(100), 0.001f);
    }
}
