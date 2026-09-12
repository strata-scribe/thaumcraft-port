package thaumcraft.client.renderers.math;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PedestalRenderMathLogicTest {

    @Test
    public void testCalculateHoverElevation() {
        // Base sine values: sin(0) = 0, sin(PI/2) = 1, sin(3PI/2) = -1
        // Hover equation: sin(t/10) * 0.1 + 0.1

        // At t = 0, sin(0) = 0 => 0.1
        assertEquals(0.1f, PedestalRenderMathLogic.calculateHoverElevation(0.0f), 0.001f);

        // At t = 10 * PI/2 ~= 15.707963, sin(PI/2) = 1 => 1 * 0.1 + 0.1 = 0.2
        assertEquals(0.2f, PedestalRenderMathLogic.calculateHoverElevation((float)(10.0 * Math.PI / 2.0)), 0.001f);

        // At t = 10 * 3PI/2 ~= 47.12389, sin(3PI/2) = -1 => -1 * 0.1 + 0.1 = 0.0
        assertEquals(0.0f, PedestalRenderMathLogic.calculateHoverElevation((float)(10.0 * 3.0 * Math.PI / 2.0)), 0.001f);
    }

    @Test
    public void testCalculateRotationAngle() {
        // Rotation equation: (t * 4) % 360

        assertEquals(0.0f, PedestalRenderMathLogic.calculateRotationAngle(0.0f), 0.001f);

        // t = 10 => 10 * 4 = 40
        assertEquals(40.0f, PedestalRenderMathLogic.calculateRotationAngle(10.0f), 0.001f);

        // t = 90 => 90 * 4 = 360 % 360 = 0
        assertEquals(0.0f, PedestalRenderMathLogic.calculateRotationAngle(90.0f), 0.001f);

        // t = 100 => 100 * 4 = 400 % 360 = 40
        assertEquals(40.0f, PedestalRenderMathLogic.calculateRotationAngle(100.0f), 0.001f);
    }
}
