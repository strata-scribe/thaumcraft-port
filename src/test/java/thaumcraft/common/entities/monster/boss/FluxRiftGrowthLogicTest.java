package thaumcraft.common.entities.monster.boss;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import thaumcraft.common.entities.monster.boss.FluxRiftGrowthLogic;
import thaumcraft.common.entities.monster.boss.FluxRiftLogic;

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

    // --- FluxRiftLogic Tests ---

    @Test
    public void testCalculateSizeGrowthWithPositiveFlux() {
        float currentSize = 10.0f;
        float drainedFlux = 20.0f;

        // expected size = 10.0 + (20.0 * 0.01) = 10.2
        float newSize = FluxRiftLogic.calculateSizeGrowth(currentSize, drainedFlux);
        assertEquals(10.2f, newSize, 0.001f, "Size should increase proportionally to drained flux.");
    }

    @Test
    public void testCalculateSizeGrowthWithZeroFlux() {
        float currentSize = 10.0f;
        float drainedFlux = 0.0f;

        float newSize = FluxRiftLogic.calculateSizeGrowth(currentSize, drainedFlux);
        assertEquals(10.0f, newSize, 0.001f, "Size should not increase when drained flux is zero.");
    }

    @Test
    public void testCalculateSizeGrowthWithNegativeFlux() {
        float currentSize = 10.0f;
        float drainedFlux = -5.0f;

        float newSize = FluxRiftLogic.calculateSizeGrowth(currentSize, drainedFlux);
        assertEquals(10.0f, newSize, 0.001f, "Size should not increase when drained flux is negative.");
    }

    @Test
    public void testCalculateStabilityDecay() {
        float currentStability = 100.0f;
        float riftSize = 20.0f;

        // expected stability = 100.0 - (20.0 * 0.05) = 99.0
        float newStability = FluxRiftLogic.calculateStabilityDecay(currentStability, riftSize);
        assertEquals(99.0f, newStability, 0.001f, "Stability should decay proportionally to rift size.");
    }

    @Test
    public void testShouldCollapseDueToLowStability() {
        assertTrue(FluxRiftLogic.shouldCollapse(0.0f, 10.0f), "Should collapse if stability is 0.");
        assertTrue(FluxRiftLogic.shouldCollapse(-5.0f, 10.0f), "Should collapse if stability is negative.");
    }

    @Test
    public void testShouldCollapseDueToLargeSize() {
        assertTrue(FluxRiftLogic.shouldCollapse(100.0f, 50.0f), "Should collapse if rift size is >= 50.");
        assertTrue(FluxRiftLogic.shouldCollapse(100.0f, 60.0f), "Should collapse if rift size is >= 50.");
    }

    @Test
    public void testShouldNotCollapse() {
        assertFalse(FluxRiftLogic.shouldCollapse(10.0f, 40.0f), "Should not collapse if stability > 0 and size < 50.");
    }

    @Test
    public void testShouldSpawnTaintSeed() {
        assertTrue(FluxRiftLogic.shouldSpawnTaintSeed(20.0f), "Should spawn taint seed if size >= 20.");
        assertTrue(FluxRiftLogic.shouldSpawnTaintSeed(30.0f), "Should spawn taint seed if size >= 20.");
    }

    @Test
    public void testShouldNotSpawnTaintSeed() {
        assertFalse(FluxRiftLogic.shouldSpawnTaintSeed(19.9f), "Should not spawn taint seed if size < 20.");
    }
}
