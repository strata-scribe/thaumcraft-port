package thaumcraft.common.golems.seals;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SealBreakerLogicTest {

    @Test
    public void testUnbreakableBlock() {
        assertEquals(-1, SealBreakerLogic.calculateBreakDuration(-1.0f, 0, 0), "Unbreakable blocks should return -1");
    }

    @Test
    public void testInstantBreakBlock() {
        assertEquals(1, SealBreakerLogic.calculateBreakDuration(0.0f, 0, 0), "0 hardness blocks should take 1 tick");
    }

    @Test
    public void testBaseHardness() {
        // hardness 1, str 0, tool 0 -> 1 * 30 * 1 * 1 = 30
        assertEquals(30, SealBreakerLogic.calculateBreakDuration(1.0f, 0, 0));

        // hardness 2, str 0, tool 0 -> 2 * 30 * 1 * 1 = 60
        assertEquals(60, SealBreakerLogic.calculateBreakDuration(2.0f, 0, 0));
    }

    @Test
    public void testGolemStrength() {
        // hardness 1, str 1, tool 0 -> 30 * 0.9 = 27
        assertEquals(27, SealBreakerLogic.calculateBreakDuration(1.0f, 1, 0));

        // hardness 1, str 5, tool 0 -> 30 * 0.5 = 15
        assertEquals(15, SealBreakerLogic.calculateBreakDuration(1.0f, 5, 0));

        // hardness 1, str 10, tool 0 -> 30 * 0.5 (max reduction) = 15
        assertEquals(15, SealBreakerLogic.calculateBreakDuration(1.0f, 10, 0));
    }

    @Test
    public void testToolHarvestLevel() {
        // hardness 1, str 0, tool 1 -> 30 * 0.5 = 15
        assertEquals(15, SealBreakerLogic.calculateBreakDuration(1.0f, 0, 1));

        // hardness 1, str 0, tool 2 -> 30 * 0.25 = 8
        assertEquals(8, SealBreakerLogic.calculateBreakDuration(1.0f, 0, 2));

        // hardness 1, str 0, tool 3 -> 30 * 0.125 = 4
        assertEquals(4, SealBreakerLogic.calculateBreakDuration(1.0f, 0, 3));
    }

    @Test
    public void testCombinedModifiers() {
        // hardness 5 (150 base), str 5 (0.5x = 75), tool 3 (0.125x = 9.375 -> 9)
        assertEquals(9, SealBreakerLogic.calculateBreakDuration(5.0f, 5, 3));

        // hardness 0.5 (15 base), str 2 (0.8x = 12), tool 2 (0.25x = 3)
        assertEquals(3, SealBreakerLogic.calculateBreakDuration(0.5f, 2, 2));
    }

    @Test
    public void testMinimumDuration() {
        // very low hardness, high str, high tool -> should not be less than 1 tick
        assertEquals(1, SealBreakerLogic.calculateBreakDuration(0.1f, 10, 10));
    }
}
