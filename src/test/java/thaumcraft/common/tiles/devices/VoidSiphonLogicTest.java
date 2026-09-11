package thaumcraft.common.tiles.devices;

import org.junit.jupiter.api.Test;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class VoidSiphonLogicTest {

    @Test
    public void testCycleTime() {
        VoidSiphonLogic logic = new VoidSiphonLogic(20);

        for (int i = 0; i < 19; i++) {
            logic.tick();
            assertFalse(logic.shouldSiphon());
        }

        logic.tick();
        assertTrue(logic.shouldSiphon());

        // Should reset after siphoning
        assertFalse(logic.shouldSiphon());
    }

    @Test
    public void testCalculateDecayRate() {
        VoidSiphonLogic logic = new VoidSiphonLogic();

        // At small size, minimum decay of 0.01f should apply
        assertEquals(0.01f, logic.calculateDecayRate(0.1f), 0.0001f);

        // At size 10, decay should be 10 * 0.05 = 0.5f
        assertEquals(0.5f, logic.calculateDecayRate(10.0f), 0.0001f);
    }

    @Test
    public void testShouldGenerateSeed() {
        VoidSiphonLogic logic = new VoidSiphonLogic();

        // Test with a mock random that returns 0.0 (always triggers if chance > 0)
        Random alwaysRandom = new Random() {
            @Override
            public float nextFloat() {
                return 0.0f; // Always trigger
            }
        };
        assertTrue(logic.shouldGenerateSeed(alwaysRandom, 1.0f));

        // Test with a mock random that returns 0.99 (never triggers unless chance >= 1.0)
        Random neverRandom = new Random() {
            @Override
            public float nextFloat() {
                return 0.99f;
            }
        };

        // Base chance is 0.1, max bonus is 0.4. Total max chance = 0.5
        // So with nextFloat() = 0.99, it should never generate a seed
        assertFalse(logic.shouldGenerateSeed(neverRandom, 1000.0f));

        // Let's test the math explicitly by controlling nextFloat
        // Chance at size 50 = 0.1 + (50/100 * 0.4) = 0.1 + 0.2 = 0.3? Wait, min(0.4, 50/100) = 0.4 or 0.5?
        // Math.min(0.40f, 50 / 100.0f) = Math.min(0.4, 0.5) = 0.4. Wait, 50/100 = 0.5.
        // chance = 0.10f + Math.min(0.40f, 50/100.0f) = 0.1 + 0.4 = 0.5.

        // Let's test size 20. Math.min(0.4, 20/100.0f) = Math.min(0.4, 0.2) = 0.2. chance = 0.1 + 0.2 = 0.3.
        Random thresholdRandom = new Random() {
            @Override
            public float nextFloat() {
                return 0.25f;
            }
        };

        // 0.25 < 0.30, so this should be true
        assertTrue(logic.shouldGenerateSeed(thresholdRandom, 20.0f));

        // Size 10: chance = 0.1 + 0.1 = 0.2. 0.25 < 0.20 is false.
        assertFalse(logic.shouldGenerateSeed(thresholdRandom, 10.0f));
    }
}
