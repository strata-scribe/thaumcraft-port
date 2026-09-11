package thaumcraft.common.lib.crafting;

/**
 * Pure Java logic decoupled from Minecraft/Forge for calculating infusion
 * hazard item knockoff probabilities and trajectories.
 */
public class InfusionDisplacementLogic {

    /**
     * Determines whether an item should be knocked off a pedestal based on
     * the current instability.
     *
     * @param instability The instability rating (e.g., recipe instability).
     * @param randomValue A random double between 0.0 and 1.0.
     * @return true if the item should be knocked off, false otherwise.
     */
    public static boolean shouldKnockoff(int instability, double randomValue) {
        if (instability <= 0) return false;

        // Base probability scales with instability.
        // e.g. 5 instability -> 0.1 chance, 25 instability -> 0.5 chance
        double probability = Math.min(0.75, instability * 0.02);

        return randomValue < probability;
    }

    /**
     * Generates a 3D velocity vector (vx, vy, vz) for an item being knocked off,
     * pointing outward from the matrix center and slightly upward.
     *
     * @param dx The x-distance from the matrix to the pedestal.
     * @param dz The z-distance from the matrix to the pedestal.
     * @param randX A random value for X variance (typically -1 to 1).
     * @param randY A random value for Y variance (typically 0 to 1).
     * @param randZ A random value for Z variance (typically -1 to 1).
     * @return A double array [vx, vy, vz].
     */
    public static double[] generateLaunchTrajectory(double dx, double dz, double randX, double randY, double randZ) {
        // Normalize the vector pointing from the matrix to the pedestal
        double length = Math.sqrt(dx * dx + dz * dz);

        double dirX = 0;
        double dirZ = 0;

        if (length > 0.001) {
            dirX = dx / length;
            dirZ = dz / length;
        }

        // Base launch velocity outward
        double baseVel = 0.2;

        // Final velocity combining outward direction and some randomness
        double vx = dirX * baseVel + randX * 0.1;
        double vy = 0.2 + randY * 0.2; // Upward kick
        double vz = dirZ * baseVel + randZ * 0.1;

        return new double[]{vx, vy, vz};
    }
}
