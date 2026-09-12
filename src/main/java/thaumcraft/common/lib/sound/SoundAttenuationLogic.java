package thaumcraft.common.lib.sound;

import java.util.Random;

public class SoundAttenuationLogic {

    /**
     * Calculates the attenuated volume based on the distance.
     * The volume decreases linearly from 1.0 at distance 0 to 0.0 at maxDistance.
     * If distance is greater than or equal to maxDistance, volume is 0.0.
     * If distance is less than or equal to 0, volume is 1.0.
     *
     * @param distance    The current distance from the sound source.
     * @param maxDistance The maximum distance at which the sound can be heard.
     * @return The calculated volume between 0.0 and 1.0.
     */
    public static float calculateVolume(float distance, float maxDistance) {
        if (distance <= 0) return 1.0f;
        if (maxDistance <= 0 || distance >= maxDistance) return 0.0f;

        return 1.0f - (distance / maxDistance);
    }

    /**
     * Calculates a randomized pitch based on a base pitch and a variance.
     * The pitch will be in the range [basePitch - variance, basePitch + variance].
     *
     * @param basePitch The base pitch of the sound.
     * @param variance  The maximum amount the pitch can vary from the base pitch.
     * @param random    A Random instance used to generate the variance.
     * @return The randomized pitch.
     */
    public static float calculatePitch(float basePitch, float variance, Random random) {
        if (variance <= 0) return basePitch;

        float randomVariance = (random.nextFloat() * 2.0f * variance) - variance;
        return basePitch + randomVariance;
    }
}
