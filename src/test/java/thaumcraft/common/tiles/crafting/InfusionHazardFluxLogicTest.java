package thaumcraft.common.tiles.crafting;

import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

public class InfusionHazardFluxLogicTest {

    @Test
    public void testShouldTriggerHazard() {
        // Use a fixed seed for predictable results if needed, or mock Random
        // Since we are using java.util.Random, we can just test the bounds.

        // Zero instability should never trigger
        Random mockRandomZero = new Random() {
            @Override
            public float nextFloat() {
                return 0.5f;
            }
        };
        assertFalse(InfusionHazardFluxLogic.shouldTriggerHazard(mockRandomZero, 0.0f));

        // High instability should trigger if nextFloat is low
        Random mockRandomLow = new Random() {
            @Override
            public float nextFloat() {
                return 0.1f;
            }
        };
        assertTrue(InfusionHazardFluxLogic.shouldTriggerHazard(mockRandomLow, 50.0f)); // 50 * 0.005 = 0.25 > 0.1

        // High instability should not trigger if nextFloat is high
        Random mockRandomHigh = new Random() {
            @Override
            public float nextFloat() {
                return 0.9f;
            }
        };
        assertFalse(InfusionHazardFluxLogic.shouldTriggerHazard(mockRandomHigh, 50.0f)); // 50 * 0.005 = 0.25 < 0.9
    }

    @Test
    public void testShouldSpawnFluxGoo() {
        // Base chance is 0.1, maxes at 0.5
        Random mockRandomLow = new Random() {
            @Override
            public float nextFloat() {
                return 0.05f;
            }
        };
        // Should always spawn if random is very low
        assertTrue(InfusionHazardFluxLogic.shouldSpawnFluxGoo(mockRandomLow, 0.0f));

        Random mockRandomHigh = new Random() {
            @Override
            public float nextFloat() {
                return 0.6f;
            }
        };
        // Should never spawn if random > 0.5
        assertFalse(InfusionHazardFluxLogic.shouldSpawnFluxGoo(mockRandomHigh, 1000.0f));

        Random mockRandomMid = new Random() {
            @Override
            public float nextFloat() {
                return 0.2f;
            }
        };
        // At 0 instability, chance is 0.1. 0.2 is greater, so false
        assertFalse(InfusionHazardFluxLogic.shouldSpawnFluxGoo(mockRandomMid, 0.0f));
        // At 100 instability, chance is 0.1 + 0.2 = 0.3. 0.2 is less, so true
        assertTrue(InfusionHazardFluxLogic.shouldSpawnFluxGoo(mockRandomMid, 100.0f));
    }

    @Test
    public void testCalculateGasEmissionAmount() {
        assertEquals(1.0f, InfusionHazardFluxLogic.calculateGasEmissionAmount(0.0f), 0.001f);
        assertEquals(2.0f, InfusionHazardFluxLogic.calculateGasEmissionAmount(10.0f), 0.001f);
        assertEquals(6.0f, InfusionHazardFluxLogic.calculateGasEmissionAmount(50.0f), 0.001f);
        assertEquals(11.0f, InfusionHazardFluxLogic.calculateGasEmissionAmount(100.0f), 0.001f);
    }
}
