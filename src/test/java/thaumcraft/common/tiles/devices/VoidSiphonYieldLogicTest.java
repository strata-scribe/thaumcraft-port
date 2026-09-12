package thaumcraft.common.tiles.devices;

import org.junit.jupiter.api.Test;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class VoidSiphonYieldLogicTest {

    @Test
    public void testCalculateDropChance() {
        // Base chance is 0.05 when stability is >= 50 and cycles = 0
        assertEquals(0.05f, VoidSiphonYieldLogic.calculateDropChance(50.0f, 0), 0.0001f);
        assertEquals(0.05f, VoidSiphonYieldLogic.calculateDropChance(100.0f, 0), 0.0001f);

        // Lower stability increases chance: 50 - 40 = 10, 10 * 0.005 = 0.05, total = 0.1
        assertEquals(0.10f, VoidSiphonYieldLogic.calculateDropChance(40.0f, 0), 0.0001f);

        // Negative stability is clamped to 0: 50 - 0 = 50, 50 * 0.005 = 0.25, total = 0.3
        assertEquals(0.30f, VoidSiphonYieldLogic.calculateDropChance(-10.0f, 0), 0.0001f);

        // Cycles increase chance: 10 cycles = 0.1, total = 0.15
        assertEquals(0.15f, VoidSiphonYieldLogic.calculateDropChance(50.0f, 10), 0.0001f);

        // Max chance is 1.0f
        assertEquals(1.0f, VoidSiphonYieldLogic.calculateDropChance(50.0f, 100), 0.0001f);
    }

    @Test
    public void testShouldGenerateSeed() {
        Random alwaysRandom = new Random() {
            @Override
            public float nextFloat() {
                return 0.0f; // Always trigger if chance > 0
            }
        };

        assertTrue(VoidSiphonYieldLogic.shouldGenerateSeed(50.0f, 0, alwaysRandom));

        Random neverRandom = new Random() {
            @Override
            public float nextFloat() {
                return 0.99f;
            }
        };

        // Chance = 0.05, 0.99 < 0.05 is false
        assertFalse(VoidSiphonYieldLogic.shouldGenerateSeed(50.0f, 0, neverRandom));

        // Chance = 1.0, 0.99 < 1.0 is true
        assertTrue(VoidSiphonYieldLogic.shouldGenerateSeed(50.0f, 100, neverRandom));
    }
}
