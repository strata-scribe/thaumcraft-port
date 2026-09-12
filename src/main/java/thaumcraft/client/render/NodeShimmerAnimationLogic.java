package thaumcraft.client.render;

public class NodeShimmerAnimationLogic {

    /**
     * Computes the pulsing scale factor for an aura node based on time.
     *
     * @param time       The current time (e.g., ticks or milliseconds).
     * @param baseScale  The base scale of the aura node.
     * @param pulseSpeed The speed at which the node pulses.
     * @param magnitude  The magnitude of the pulse (amplitude).
     * @return The pulsing scale factor.
     */
    public static float computeScaleFactor(long time, float baseScale, float pulseSpeed, float magnitude) {
        return baseScale + (float) Math.sin(time * pulseSpeed) * magnitude;
    }

    /**
     * Computes the luminescent brightness for an aura node based on time.
     *
     * @param time           The current time (e.g., ticks or milliseconds).
     * @param baseBrightness The baseline brightness of the aura node.
     * @param waveSpeed      The speed of the brightness wave.
     * @param magnitude      The magnitude of the brightness variation (amplitude).
     * @return The luminescent brightness value.
     */
    public static float computeBrightness(long time, float baseBrightness, float waveSpeed, float magnitude) {
        return baseBrightness + (float) Math.cos(time * waveSpeed) * magnitude;
    }
}
