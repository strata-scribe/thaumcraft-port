package thaumcraft.client.render;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class NodeShimmerAnimationLogicTest {

    @Test
    public void testComputeScaleFactor() {
        float pulseSpeed = (float) (Math.PI / 20.0); // 10 units of time = PI / 2

        // Base case, sin(0) = 0 -> 1.0 + 0 * 0.5 = 1.0
        assertEquals(1.0f, NodeShimmerAnimationLogic.computeScaleFactor(0, 1.0f, pulseSpeed, 0.5f), 0.001f);

        // sin(PI/2) = 1.0 -> 1.0 + 1.0 * 0.5 = 1.5
        long timeQuarterWave = 10;
        assertEquals(1.5f, NodeShimmerAnimationLogic.computeScaleFactor(timeQuarterWave, 1.0f, pulseSpeed, 0.5f), 0.001f);

        // sin(PI) = 0 -> 1.0 + 0 * 0.5 = 1.0
        long timeHalfWave = 20;
        assertEquals(1.0f, NodeShimmerAnimationLogic.computeScaleFactor(timeHalfWave, 1.0f, pulseSpeed, 0.5f), 0.001f);

        // sin(3*PI/2) = -1.0 -> 1.0 - 1.0 * 0.5 = 0.5
        long timeThreeQuarterWave = 30;
        assertEquals(0.5f, NodeShimmerAnimationLogic.computeScaleFactor(timeThreeQuarterWave, 1.0f, pulseSpeed, 0.5f), 0.001f);
    }

    @Test
    public void testComputeBrightness() {
        float waveSpeed = (float) (Math.PI / 20.0); // 10 units of time = PI / 2

        // Base case, cos(0) = 1.0 -> 1.0 + 1.0 * 0.5 = 1.5
        assertEquals(1.5f, NodeShimmerAnimationLogic.computeBrightness(0, 1.0f, waveSpeed, 0.5f), 0.001f);

        // cos(PI/2) = 0 -> 1.0 + 0 * 0.5 = 1.0
        long timeQuarterWave = 10;
        assertEquals(1.0f, NodeShimmerAnimationLogic.computeBrightness(timeQuarterWave, 1.0f, waveSpeed, 0.5f), 0.001f);

        // cos(PI) = -1.0 -> 1.0 - 1.0 * 0.5 = 0.5
        long timeHalfWave = 20;
        assertEquals(0.5f, NodeShimmerAnimationLogic.computeBrightness(timeHalfWave, 1.0f, waveSpeed, 0.5f), 0.001f);

        // cos(3*PI/2) = 0 -> 1.0 + 0 * 0.5 = 1.0
        long timeThreeQuarterWave = 30;
        assertEquals(1.0f, NodeShimmerAnimationLogic.computeBrightness(timeThreeQuarterWave, 1.0f, waveSpeed, 0.5f), 0.001f);
    }
}
