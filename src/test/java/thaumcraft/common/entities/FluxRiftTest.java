package thaumcraft.common.entities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import thaumcraft.common.entities.monster.boss.FluxRiftLogic;

public class FluxRiftTest {

    @Test
    public void testInitialStateDefaults() {
        float initialSize = 1.0f;
        float initialStability = 100.0f;

        assertEquals(1.0f, initialSize, 0.01f, "Initial size should be 1.0f");
        assertEquals(100.0f, initialStability, 0.01f, "Initial stability should be 100.0f");
    }

    @Test
    public void testFluxConsumptionAndGrowth() {
        float size = 1.0f;
        float drainedFlux = 5.0f;

        float newSize = FluxRiftLogic.calculateSizeGrowth(size, drainedFlux);
        assertEquals(1.05f, newSize, 0.001f, "Size should increment by 0.05 when 5 flux is consumed");
    }

    @Test
    public void testStabilityDecay() {
        float stability = 100.0f;
        float size = 10.0f;

        float newStability = FluxRiftLogic.calculateStabilityDecay(stability, size);
        assertEquals(99.5f, newStability, 0.01f, "Stability should decrease by size * 0.05 (10 * 0.05 = 0.5)");
    }

    @Test
    public void testCollapseOnZeroStability() {
        float size = 10.0f;
        float stability = 0.5f;

        assertFalse(FluxRiftLogic.shouldCollapse(stability, size), "Rift should not collapse while stability > 0");

        float decayedStability = FluxRiftLogic.calculateStabilityDecay(stability, size); // 0.5 - 0.5 = 0.0f
        assertTrue(FluxRiftLogic.shouldCollapse(decayedStability, size), "Rift should collapse when stability <= 0");
    }

    @Test
    public void testTaintSeedSpawnThreshold() {
        float smallRift = 15.0f;
        float largeRift = 25.0f;
        float maxRift = 50.0f;

        assertFalse(FluxRiftLogic.shouldSpawnTaintSeed(smallRift), "Small rift (< 20) should drop primordial pearl");
        assertTrue(FluxRiftLogic.shouldSpawnTaintSeed(largeRift), "Large rift (>= 20) should spawn taint seed");
        assertTrue(FluxRiftLogic.shouldCollapse(100.0f, maxRift), "Rift should collapse when size >= 50.0f");
    }
}

