package thaumcraft.common.entities.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TaintSporeCloudLogicTest {

    @Test
    void testComputeExpansionNormal() {
        TaintSporeCloudLogic.CloudState state = TaintSporeCloudLogic.computeExpansion(100.0, 1.0, 0.1, 2.0);
        // newVolume = 100 + (100 * 0.1 * 2) = 120
        // mass = 100 * 1 = 100
        // newDensity = 100 / 120 = 0.8333...
        assertEquals(120.0, state.volume(), 0.001);
        assertEquals(100.0 / 120.0, state.density(), 0.001);
    }

    @Test
    void testComputeExpansionZeroVolume() {
        TaintSporeCloudLogic.CloudState state = TaintSporeCloudLogic.computeExpansion(0.0, 1.0, 0.1, 2.0);
        assertEquals(0.0, state.volume(), 0.001);
        assertEquals(0.0, state.density(), 0.001);
    }

    @Test
    void testComputeExpansionNegativeVolumeResult() {
        TaintSporeCloudLogic.CloudState state = TaintSporeCloudLogic.computeExpansion(100.0, 1.0, -1.0, 2.0);
        // newVolume = 100 + (100 * -1 * 2) = -100 (which is <= 0)
        assertEquals(0.0, state.volume(), 0.001);
        assertEquals(0.0, state.density(), 0.001);
    }

    @Test
    void testComputeWindDisplacement() {
        TaintSporeCloudLogic.Position pos = TaintSporeCloudLogic.computeWindDisplacement(10.0, 20.0, 30.0, 1.0, 0.0, -1.0, 0.5, 2.0);
        // x = 10 + (1.0 * 0.5 * 2) = 11
        // y = 20 + (0.0 * 0.5 * 2) = 20
        // z = 30 + (-1.0 * 0.5 * 2) = 29
        assertEquals(11.0, pos.x(), 0.001);
        assertEquals(20.0, pos.y(), 0.001);
        assertEquals(29.0, pos.z(), 0.001);
    }

    @Test
    void testComputePoisonDebuffDurationAboveThreshold() {
        double duration = TaintSporeCloudLogic.computePoisonDebuffDuration(2.0, 1.0, 5.0, 2.0);
        // (2.0 - 1.0) * 5.0 * 2.0 = 1.0 * 10.0 = 10.0
        assertEquals(10.0, duration, 0.001);
    }

    @Test
    void testComputePoisonDebuffDurationBelowThreshold() {
        double duration = TaintSporeCloudLogic.computePoisonDebuffDuration(0.5, 1.0, 5.0, 2.0);
        assertEquals(0.0, duration, 0.001);
    }

    @Test
    void testComputePoisonDebuffDurationAtThreshold() {
        double duration = TaintSporeCloudLogic.computePoisonDebuffDuration(1.0, 1.0, 5.0, 2.0);
        assertEquals(0.0, duration, 0.001);
    }
}