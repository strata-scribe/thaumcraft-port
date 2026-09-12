package thaumcraft.common.lib.sound;

import org.junit.jupiter.api.Test;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SoundAttenuationLogicTest {

    @Test
    public void testCalculateVolume_ZeroDistance() {
        assertEquals(1.0f, SoundAttenuationLogic.calculateVolume(0.0f, 100.0f), 0.001f);
    }

    @Test
    public void testCalculateVolume_NegativeDistance() {
        assertEquals(1.0f, SoundAttenuationLogic.calculateVolume(-5.0f, 100.0f), 0.001f);
    }

    @Test
    public void testCalculateVolume_MaxDistance() {
        assertEquals(0.0f, SoundAttenuationLogic.calculateVolume(100.0f, 100.0f), 0.001f);
    }

    @Test
    public void testCalculateVolume_BeyondMaxDistance() {
        assertEquals(0.0f, SoundAttenuationLogic.calculateVolume(150.0f, 100.0f), 0.001f);
    }

    @Test
    public void testCalculateVolume_MidDistance() {
        assertEquals(0.5f, SoundAttenuationLogic.calculateVolume(50.0f, 100.0f), 0.001f);
    }

    @Test
    public void testCalculatePitch_ZeroVariance() {
        Random random = new Random(42);
        assertEquals(1.0f, SoundAttenuationLogic.calculatePitch(1.0f, 0.0f, random), 0.001f);
    }

    @Test
    public void testCalculatePitch_NegativeVariance() {
        Random random = new Random(42);
        assertEquals(1.0f, SoundAttenuationLogic.calculatePitch(1.0f, -0.5f, random), 0.001f);
    }

    @Test
    public void testCalculatePitch_WithinVarianceBounds() {
        Random random = new Random();
        float basePitch = 1.0f;
        float variance = 0.2f;

        for (int i = 0; i < 100; i++) {
            float pitch = SoundAttenuationLogic.calculatePitch(basePitch, variance, random);
            assertTrue(pitch >= basePitch - variance && pitch <= basePitch + variance,
                    "Pitch " + pitch + " should be within " + (basePitch - variance) + " and " + (basePitch + variance));
        }
    }

    @Test
    public void testCalculatePitch_PredictableRandom() {
        Random random = new Random(42); // fixed seed
        float basePitch = 1.0f;
        float variance = 0.5f;

        // Random(42).nextFloat() returns ~0.73
        // Formula: (0.73 * 2 * 0.5) - 0.5 = 0.73 - 0.5 = 0.23
        // Result: 1.0 + 0.23 = 1.23
        float expectedPitch = basePitch + (random.nextFloat() * 2.0f * variance) - variance;

        // Reset random
        random = new Random(42);
        float actualPitch = SoundAttenuationLogic.calculatePitch(basePitch, variance, random);

        assertEquals(expectedPitch, actualPitch, 0.001f);
    }
}
