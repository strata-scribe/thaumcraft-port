package thaumcraft.common.entities;

/**
 * Pure Java logic helper for Eldritch mob mechanics, such as the Eldritch Crab
 * and Inhabited Zombie.
 * Strict mathematical decoupling: contains zero Minecraft or NeoForge classloader imports.
 */
public class EldritchMobLogic {

    /**
     * Calculates the normalized leap trajectory vector for the Eldritch Crab.
     *
     * @param dx The delta X to the target.
     * @param dy The delta Y to the target.
     * @param dz The delta Z to the target.
     * @param distance The 2D or 3D horizontal distance to the target.
     * @param leapVelocity The base velocity multiplier for the leap.
     * @return A double array containing the {x, y, z} motion vector.
     */
    public static double[] calculateCrabLeapTrajectory(double dx, double dy, double dz, double distance, double leapVelocity) {
        if (distance <= 0) {
            return new double[]{0, 0, 0};
        }
        double motionX = (dx / distance) * leapVelocity;
        double motionY = leapVelocity * 0.5; // Upward arc
        double motionZ = (dz / distance) * leapVelocity;
        return new double[]{motionX, motionY, motionZ};
    }

    /**
     * Determines whether the Eldritch Crab can successfully possess a target.
     *
     * @param isHumanoid Whether the target is a valid humanoid (e.g. Zombie).
     * @param isAlreadyPossessed Whether the target is already possessed by another crab.
     * @param distanceToTarget The calculated distance between crab and target.
     * @param possessionRange The maximum range at which possession can occur (e.g., 2.0).
     * @return true if the crab can possess the target, false otherwise.
     */
    public static boolean canPossess(boolean isHumanoid, boolean isAlreadyPossessed, double distanceToTarget, double possessionRange) {
        return isHumanoid && !isAlreadyPossessed && distanceToTarget <= possessionRange;
    }

    /**
     * Calculates the boosted armor value for an Inhabited Zombie.
     *
     * @param baseArmor The original base armor of the target.
     * @return The augmented armor value.
     */
    public static double calculateAugmentedArmor(double baseArmor) {
        // Boost armor by an additional amount, e.g., +6.0
        return baseArmor + 6.0;
    }

    /**
     * Calculates the boosted movement speed for an Inhabited Zombie.
     *
     * @param baseSpeed The original base speed of the target.
     * @return The augmented speed value.
     */
    public static double calculateAugmentedSpeed(double baseSpeed) {
        // Boost speed by a multiplier, e.g., 1.5x
        return baseSpeed * 1.5;
    }
}
