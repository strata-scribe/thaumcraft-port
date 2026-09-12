package thaumcraft.common.entities.monster.boss;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import thaumcraft.common.entities.monster.boss.FluxRiftGrowthLogic;

public class FluxRiftGrowthLogicTest {

    @Test
    public void testCalculateRiftExpansionWithPureEssentia() {
        float currentSize = 10.0f;
        float ventedEssentiaAmount = 20.0f;
        float dirtyEssentiaRatio = 0.0f;

        // baseExpansion = 20.0f * 0.05f = 1.0f
        // multiplier = 1.0f + 0 = 1.0f
        // expectedSize = 10.0f + (1.0f * 1.0f) = 11.0f

        float newSize = FluxRiftGrowthLogic.calculateRiftExpansion(currentSize, ventedEssentiaAmount, dirtyEssentiaRatio);
        assertEquals(11.0f, newSize, 0.001f, "Size should increase by base amount for pure essentia.");
    }

    @Test
    public void testCalculateRiftExpansionWithDirtyEssentia() {
        float currentSize = 10.0f;
        float ventedEssentiaAmount = 20.0f;
        float dirtyEssentiaRatio = 0.5f;

        // baseExpansion = 20.0f * 0.05f = 1.0f
        // multiplier = 1.0f + (0.5f * 2.0f) = 2.0f
        // expectedSize = 10.0f + (1.0f * 2.0f) = 12.0f

        float newSize = FluxRiftGrowthLogic.calculateRiftExpansion(currentSize, ventedEssentiaAmount, dirtyEssentiaRatio);
        assertEquals(12.0f, newSize, 0.001f, "Size should increase more with dirty essentia.");
    }

    @Test
    public void testCalculateRiftExpansionWithFullDirtyEssentia() {
        float currentSize = 10.0f;
        float ventedEssentiaAmount = 20.0f;
        float dirtyEssentiaRatio = 1.0f;

        // baseExpansion = 20.0f * 0.05f = 1.0f
        // multiplier = 1.0f + (1.0f * 2.0f) = 3.0f
        // expectedSize = 10.0f + (1.0f * 3.0f) = 13.0f

        float newSize = FluxRiftGrowthLogic.calculateRiftExpansion(currentSize, ventedEssentiaAmount, dirtyEssentiaRatio);
        assertEquals(13.0f, newSize, 0.001f, "Size should increase at max multiplier with full dirty essentia.");
    }

    @Test
    public void testCalculateRiftExpansionWithZeroAmount() {
        float currentSize = 10.0f;
        float ventedEssentiaAmount = 0.0f;
        float dirtyEssentiaRatio = 1.0f;

        float newSize = FluxRiftGrowthLogic.calculateRiftExpansion(currentSize, ventedEssentiaAmount, dirtyEssentiaRatio);
        assertEquals(10.0f, newSize, 0.001f, "Size should remain the same when vented amount is 0.");
    }

    @Test
    public void testCalculateStabilityDegradationWithPureEssentia() {
        float currentStability = 100.0f;
        float ventedEssentiaAmount = 10.0f;
        float dirtyEssentiaRatio = 0.0f;

        // baseDegradation = 10.0f * 0.1f = 1.0f
        // multiplier = 1.0f + 0 = 1.0f
        // expectedStability = 100.0f - (1.0f * 1.0f) = 99.0f

        float newStability = FluxRiftGrowthLogic.calculateStabilityDegradation(currentStability, ventedEssentiaAmount, dirtyEssentiaRatio);
        assertEquals(99.0f, newStability, 0.001f, "Stability should decrease by base amount for pure essentia.");
    }

    @Test
    public void testCalculateStabilityDegradationWithDirtyEssentia() {
        float currentStability = 100.0f;
        float ventedEssentiaAmount = 10.0f;
        float dirtyEssentiaRatio = 0.5f;

        // baseDegradation = 10.0f * 0.1f = 1.0f
        // multiplier = 1.0f + (0.5f * 4.0f) = 3.0f
        // expectedStability = 100.0f - (1.0f * 3.0f) = 97.0f

        float newStability = FluxRiftGrowthLogic.calculateStabilityDegradation(currentStability, ventedEssentiaAmount, dirtyEssentiaRatio);
        assertEquals(97.0f, newStability, 0.001f, "Stability should decrease more with dirty essentia.");
    }

    @Test
    public void testCalculateStabilityDegradationWithFullDirtyEssentia() {
        float currentStability = 100.0f;
        float ventedEssentiaAmount = 10.0f;
        float dirtyEssentiaRatio = 1.0f;

        // baseDegradation = 10.0f * 0.1f = 1.0f
        // multiplier = 1.0f + (1.0f * 4.0f) = 5.0f
        // expectedStability = 100.0f - (1.0f * 5.0f) = 95.0f

        float newStability = FluxRiftGrowthLogic.calculateStabilityDegradation(currentStability, ventedEssentiaAmount, dirtyEssentiaRatio);
        assertEquals(95.0f, newStability, 0.001f, "Stability should decrease at max multiplier with full dirty essentia.");
    }

    @Test
    public void testCalculateStabilityDegradationClampedAtZero() {
        float currentStability = 5.0f;
        float ventedEssentiaAmount = 100.0f;
        float dirtyEssentiaRatio = 1.0f;

        // Degradation will be much larger than 5.0f

        float newStability = FluxRiftGrowthLogic.calculateStabilityDegradation(currentStability, ventedEssentiaAmount, dirtyEssentiaRatio);
        assertEquals(0.0f, newStability, 0.001f, "Stability should not go below 0.");
    }

    @Test
    public void testCalculateStabilityDegradationWithZeroAmount() {
        float currentStability = 100.0f;
        float ventedEssentiaAmount = 0.0f;
        float dirtyEssentiaRatio = 1.0f;

        float newStability = FluxRiftGrowthLogic.calculateStabilityDegradation(currentStability, ventedEssentiaAmount, dirtyEssentiaRatio);
        assertEquals(100.0f, newStability, 0.001f, "Stability should remain the same when vented amount is 0.");
    }
}
