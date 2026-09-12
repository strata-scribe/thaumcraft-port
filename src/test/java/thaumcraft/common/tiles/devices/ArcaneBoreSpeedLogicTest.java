package thaumcraft.common.tiles.devices;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArcaneBoreSpeedLogicTest {

    @Test
    public void testCalculateMiningTime() {
        // Instant breaking
        assertEquals(1, ArcaneBoreSpeedLogic.calculateMiningTime(0.0f, 1));
        assertEquals(1, ArcaneBoreSpeedLogic.calculateMiningTime(0.0f, 5));

        // Stone-like (1.5 hardness)
        assertEquals(1, ArcaneBoreSpeedLogic.calculateMiningTime(1.5f, 1));
        assertEquals(1, ArcaneBoreSpeedLogic.calculateMiningTime(1.5f, 2));

        // Obsidian-like (50.0 hardness)
        assertEquals(50, ArcaneBoreSpeedLogic.calculateMiningTime(50.0f, 1));
        assertEquals(25, ArcaneBoreSpeedLogic.calculateMiningTime(50.0f, 2));
        assertEquals(10, ArcaneBoreSpeedLogic.calculateMiningTime(50.0f, 5));

        // Unbreakable (-1.0 hardness)
        assertEquals(-1, ArcaneBoreSpeedLogic.calculateMiningTime(-1.0f, 1));
    }

    @Test
    public void testCalculateVisConsumption() {
        // Vis consumption logic

        // Instant breaking
        assertEquals(0.0f, ArcaneBoreSpeedLogic.calculateVisConsumption(0.0f, 1.0f));
        assertEquals(0.0f, ArcaneBoreSpeedLogic.calculateVisConsumption(0.0f, 5.0f));

        // Stone-like (1.5 hardness)
        assertEquals(1.5f, ArcaneBoreSpeedLogic.calculateVisConsumption(1.5f, 1.0f));
        assertEquals(7.5f, ArcaneBoreSpeedLogic.calculateVisConsumption(1.5f, 5.0f));

        // Obsidian-like (50.0 hardness)
        assertEquals(50.0f, ArcaneBoreSpeedLogic.calculateVisConsumption(50.0f, 1.0f));
        assertEquals(250.0f, ArcaneBoreSpeedLogic.calculateVisConsumption(50.0f, 5.0f));

        // Unbreakable (-1.0 hardness)
        assertEquals(0.0f, ArcaneBoreSpeedLogic.calculateVisConsumption(-1.0f, 1.0f));
    }
}
