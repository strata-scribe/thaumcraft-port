package thaumcraft.common.items.tools;

/**
 * Logic for calculating Thaumometer zoom mechanics including:
 * - Celestial scanning field-of-view (FOV) scaling
 * - Focal distance dampening
 * - Aspect detection radius
 */
public class ThaumometerZoomLogic {

    /**
     * Calculates the scaled field-of-view based on the current zoom level.
     * Zoom level typically ranges from 0.0 (no zoom) to 1.0 (max zoom).
     *
     * @param zoomLevel Current zoom level of the thaumometer (0.0 to 1.0).
     * @param baseFov The default field of view when not zoomed.
     * @return The scaled field of view.
     */
    public static double calculateFovScaling(double zoomLevel, double baseFov) {
        // Ensure zoomLevel is bounded between 0.0 and 1.0
        double clampedZoom = Math.max(0.0, Math.min(1.0, zoomLevel));

        // At 0.0 zoom, FOV is baseFov.
        // At 1.0 zoom, FOV could be reduced significantly, for example down to 1/3 of the baseFov.
        double minFov = baseFov / 3.0;
        return baseFov - (clampedZoom * (baseFov - minFov));
    }

    /**
     * Calculates the dampened focal distance based on the zoom level.
     * Higher zoom levels increase dampening to stabilize the view.
     *
     * @param zoomLevel Current zoom level of the thaumometer (0.0 to 1.0).
     * @param baseDistance The original focal distance.
     * @return The dampened focal distance.
     */
    public static double calculateFocalDampening(double zoomLevel, double baseDistance) {
        double clampedZoom = Math.max(0.0, Math.min(1.0, zoomLevel));

        // At 0.0 zoom, dampening is 1.0 (no change).
        // At 1.0 zoom, dampening factor could be 0.1, making movements 10x slower/smaller.
        double dampeningFactor = 1.0 - (0.9 * clampedZoom);
        return baseDistance * dampeningFactor;
    }

    /**
     * Calculates the effective aspect detection radius based on the zoom level.
     * Zooming in (higher zoom level) decreases the FOV but allows focusing more deeply,
     * which might reduce peripheral detection but could increase range, or vice versa depending on design.
     * Let's say zooming in makes the radius slightly smaller but more focused.
     *
     * @param zoomLevel Current zoom level of the thaumometer (0.0 to 1.0).
     * @param baseRadius The default detection radius.
     * @return The scaled aspect detection radius.
     */
    public static double calculateAspectDetectionRadius(double zoomLevel, double baseRadius) {
        double clampedZoom = Math.max(0.0, Math.min(1.0, zoomLevel));

        // Zooming in reduces the effective radius slightly because the view is narrowed.
        // At 1.0 zoom, radius could be reduced to 70% of base radius.
        double scaledRadius = baseRadius * (1.0 - (0.3 * clampedZoom));
        return Math.max(0.0, scaledRadius);
    }
}
