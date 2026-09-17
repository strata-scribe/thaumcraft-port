package thaumcraft.common.items.tools;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ThaumometerZoomLogicTest {

    private static final double DELTA = 0.0001;

    @Test
    void testCalculateFovScaling() {
        double baseFov = 90.0;

        // No zoom
        assertEquals(90.0, ThaumometerZoomLogic.calculateFovScaling(0.0, baseFov), DELTA);

        // Max zoom (reduces to 1/3)
        assertEquals(30.0, ThaumometerZoomLogic.calculateFovScaling(1.0, baseFov), DELTA);

        // Half zoom
        assertEquals(60.0, ThaumometerZoomLogic.calculateFovScaling(0.5, baseFov), DELTA);

        // Out of bounds (negative)
        assertEquals(90.0, ThaumometerZoomLogic.calculateFovScaling(-0.5, baseFov), DELTA);

        // Out of bounds (above 1)
        assertEquals(30.0, ThaumometerZoomLogic.calculateFovScaling(1.5, baseFov), DELTA);
    }

    @Test
    void testCalculateFocalDampening() {
        double baseDistance = 100.0;

        // No zoom
        assertEquals(100.0, ThaumometerZoomLogic.calculateFocalDampening(0.0, baseDistance), DELTA);

        // Max zoom (reduces to 10%)
        assertEquals(10.0, ThaumometerZoomLogic.calculateFocalDampening(1.0, baseDistance), DELTA);

        // Half zoom (reduces to 55%)
        assertEquals(55.0, ThaumometerZoomLogic.calculateFocalDampening(0.5, baseDistance), DELTA);

        // Out of bounds (negative)
        assertEquals(100.0, ThaumometerZoomLogic.calculateFocalDampening(-0.2, baseDistance), DELTA);

        // Out of bounds (above 1)
        assertEquals(10.0, ThaumometerZoomLogic.calculateFocalDampening(1.2, baseDistance), DELTA);
    }

    @Test
    void testCalculateAspectDetectionRadius() {
        double baseRadius = 20.0;

        // No zoom
        assertEquals(20.0, ThaumometerZoomLogic.calculateAspectDetectionRadius(0.0, baseRadius), DELTA);

        // Max zoom (reduces to 70%)
        assertEquals(14.0, ThaumometerZoomLogic.calculateAspectDetectionRadius(1.0, baseRadius), DELTA);

        // Half zoom (reduces to 85%)
        assertEquals(17.0, ThaumometerZoomLogic.calculateAspectDetectionRadius(0.5, baseRadius), DELTA);

        // Out of bounds (negative)
        assertEquals(20.0, ThaumometerZoomLogic.calculateAspectDetectionRadius(-0.5, baseRadius), DELTA);

        // Out of bounds (above 1)
        assertEquals(14.0, ThaumometerZoomLogic.calculateAspectDetectionRadius(2.0, baseRadius), DELTA);

        // Zero radius
        assertEquals(0.0, ThaumometerZoomLogic.calculateAspectDetectionRadius(0.5, 0.0), DELTA);
    }
}
