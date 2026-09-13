package thaumcraft.common.entities.logic;

/**
 * Pure Java logic for computing oscillating wave angles for shadow tendrils
 * surrounding eldritch entities.
 * Decoupled from Minecraft and NeoForge to facilitate JUnit testing.
 */
public class EldritchTendrilWaveLogic {

    /**
     * Calculates the oscillating angle for a tendril on the X axis.
     *
     * @param time The current tick time or animation time.
     * @param tendrilIndex The index of the tendril, used to offset the phase.
     * @param speed The speed of the oscillation.
     * @param amplitude The amplitude of the wave.
     * @return The calculated X angle.
     */
    public static float calculateTendrilAngleX(float time, int tendrilIndex, float speed, float amplitude) {
        float phaseOffset = tendrilIndex * 0.5f;
        return (float) (Math.sin(time * speed + phaseOffset) * amplitude);
    }

    /**
     * Calculates the oscillating angle for a tendril on the Y axis.
     *
     * @param time The current tick time or animation time.
     * @param tendrilIndex The index of the tendril, used to offset the phase.
     * @param speed The speed of the oscillation.
     * @param amplitude The amplitude of the wave.
     * @return The calculated Y angle.
     */
    public static float calculateTendrilAngleY(float time, int tendrilIndex, float speed, float amplitude) {
        float phaseOffset = tendrilIndex * 0.7f;
        return (float) (Math.cos(time * speed + phaseOffset) * amplitude);
    }

    /**
     * Calculates the oscillating angle for a tendril on the Z axis.
     *
     * @param time The current tick time or animation time.
     * @param tendrilIndex The index of the tendril, used to offset the phase.
     * @param speed The speed of the oscillation.
     * @param amplitude The amplitude of the wave.
     * @return The calculated Z angle.
     */
    public static float calculateTendrilAngleZ(float time, int tendrilIndex, float speed, float amplitude) {
        float phaseOffset = tendrilIndex * 0.9f;
        return (float) (Math.sin(time * speed * 0.8f + phaseOffset) * amplitude * 0.5f);
    }
}
