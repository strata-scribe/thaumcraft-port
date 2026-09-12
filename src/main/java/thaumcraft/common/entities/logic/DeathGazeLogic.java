package thaumcraft.common.entities.logic;

public class DeathGazeLogic {

    /**
     * Calculates the angle in degrees between a look vector and a direction vector to a target.
     *
     * @param lookX The X component of the look vector.
     * @param lookY The Y component of the look vector.
     * @param lookZ The Z component of the look vector.
     * @param dirX The X component of the direction vector to the target.
     * @param dirY The Y component of the direction vector to the target.
     * @param dirZ The Z component of the direction vector to the target.
     * @return The angle between the two vectors in degrees (0 to 180).
     */
    public static double calculateGazeAngle(double lookX, double lookY, double lookZ, double dirX, double dirY, double dirZ) {
        double dotProduct = lookX * dirX + lookY * dirY + lookZ * dirZ;
        double lookMag = Math.sqrt(lookX * lookX + lookY * lookY + lookZ * lookZ);
        double dirMag = Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);

        if (lookMag == 0 || dirMag == 0) {
            return 0; // Avoid division by zero
        }

        double cosTheta = dotProduct / (lookMag * dirMag);
        // Clamp to [-1.0, 1.0] to avoid NaN from floating point inaccuracies
        cosTheta = Math.max(-1.0, Math.min(1.0, cosTheta));

        return Math.toDegrees(Math.acos(cosTheta));
    }

    /**
     * Checks if a target is within a certain field of view (cone angle) based on the look vector.
     *
     * @param lookX The X component of the look vector.
     * @param lookY The Y component of the look vector.
     * @param lookZ The Z component of the look vector.
     * @param dirX The X component of the direction vector to the target.
     * @param dirY The Y component of the direction vector to the target.
     * @param dirZ The Z component of the direction vector to the target.
     * @param coneAngleDegrees The maximum angle in degrees (field of view / 2).
     * @return true if the target is within the cone, false otherwise.
     */
    public static boolean isTargetInGazeCone(double lookX, double lookY, double lookZ, double dirX, double dirY, double dirZ, double coneAngleDegrees) {
        double angle = calculateGazeAngle(lookX, lookY, lookZ, dirX, dirY, dirZ);
        return angle <= coneAngleDegrees;
    }

    /**
     * Calculates the progressive wither damage based on how long the gaze has been maintained.
     *
     * @param gazeTicks The number of ticks the gaze has been maintained continuously.
     * @return The calculated wither damage.
     */
    public static float calculateWitherDamage(int gazeTicks) {
        if (gazeTicks < 0) return 0.0f;

        // Base damage 1.0, increases by 1.0 every 40 ticks (2 seconds)
        return 1.0f + (gazeTicks / 40.0f);
    }

    /**
     * Calculates the duration of the wither effect to apply based on how long the gaze has been maintained.
     *
     * @param gazeTicks The number of ticks the gaze has been maintained continuously.
     * @return The duration of the wither effect in ticks.
     */
    public static int calculateWitherDuration(int gazeTicks) {
        if (gazeTicks < 0) return 0;

        // Base duration 100 ticks (5 seconds), plus 10 ticks for every tick the gaze is held
        return 100 + (gazeTicks * 10);
    }
}
