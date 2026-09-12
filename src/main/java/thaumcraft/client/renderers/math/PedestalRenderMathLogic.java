package thaumcraft.client.renderers.math;

public class PedestalRenderMathLogic {

    /**
     * Calculates the item hover elevation (bobbing) above the pedestal surface.
     * The item bobbs up and down using a sine wave based on time.
     *
     * @param renderTime the current render tick time
     * @return the y-offset for the item's hover elevation
     */
    public static float calculateHoverElevation(float renderTime) {
        return (float) Math.sin(renderTime / 10.0f) * 0.1f + 0.1f;
    }

    /**
     * Calculates the continuous 360-degree rotation angle of the item.
     *
     * @param renderTime the current render tick time
     * @return the rotation angle in degrees
     */
    public static float calculateRotationAngle(float renderTime) {
        return (renderTime * 4.0f) % 360.0f;
    }

}
