package thaumcraft.common.tiles.crafting.logic;

/**
 * Pure Java logic for crucible bubble physics simulation, decoupled from Minecraft/Forge APIs.
 * Handles the calculation of bubble burst coordinates on the liquid surface and the
 * resulting steam dispersion vectors.
 */
public class CrucibleBubblePhysicsLogic {

    /**
     * Calculates the 3D local coordinate for a bubble burst on the crucible's liquid surface.
     * The X and Z coordinates are constrained within the provided bounds (representing the inner bowl),
     * and the Y coordinate is set to the current fluid height.
     *
     * @param randomX     A random double, typically in the range [0.0, 1.0].
     * @param randomZ     A random double, typically in the range [0.0, 1.0].
     * @param fluidHeight The Y coordinate representing the surface level of the fluid.
     * @param minBound    The minimum coordinate bound (e.g., inner edge of the crucible wall).
     * @param maxBound    The maximum coordinate bound (e.g., opposite inner edge of the crucible wall).
     * @return An array containing [x, y, z] coordinates for the bubble burst.
     */
    public static double[] calculateBubbleBurstCoordinates(double randomX, double randomZ, double fluidHeight, double minBound, double maxBound) {
        double span = maxBound - minBound;
        double x = minBound + (randomX * span);
        double z = minBound + (randomZ * span);
        return new double[]{x, fluidHeight, z};
    }

    /**
     * Calculates the 3D velocity vector for rising steam resulting from a bubble burst.
     * The horizontal spread is determined by a random angle, while the upward velocity is constant.
     *
     * @param randomAngle      A random angle in radians [0.0, 2*PI] for the direction of steam dispersion.
     * @param horizontalSpread The magnitude of the horizontal dispersion velocity.
     * @param upwardVelocity   The magnitude of the vertical rising velocity.
     * @return An array containing [dx, dy, dz] velocity vectors.
     */
    public static double[] calculateSteamDispersionVector(double randomAngle, double horizontalSpread, double upwardVelocity) {
        double dx = Math.cos(randomAngle) * horizontalSpread;
        double dz = Math.sin(randomAngle) * horizontalSpread;
        return new double[]{dx, upwardVelocity, dz};
    }
}
