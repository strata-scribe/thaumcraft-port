package thaumcraft.common.tiles.devices;

/**
 * Pure Java logic helper for Barrier Stone mechanics, such as creature pathing
 * repulsion vectors and warding field exclusion radiuses.
 * Strict mathematical decoupling: contains zero Minecraft or NeoForge classloader imports.
 */
public class BarrierStoneLogic {

    /**
     * Checks if an entity is inside the warding field exclusion zone.
     *
     * @param stoneX The X coordinate of the barrier stone.
     * @param stoneY The Y coordinate of the barrier stone.
     * @param stoneZ The Z coordinate of the barrier stone.
     * @param entityX The X coordinate of the entity.
     * @param entityY The Y coordinate of the entity.
     * @param entityZ The Z coordinate of the entity.
     * @param radius The radius of the warding field.
     * @return true if the entity is inside the exclusion zone, false otherwise.
     */
    public static boolean isInsideExclusionZone(double stoneX, double stoneY, double stoneZ,
                                                double entityX, double entityY, double entityZ,
                                                double radius) {
        double dx = entityX - stoneX;
        double dy = entityY - stoneY;
        double dz = entityZ - stoneZ;
        return (dx * dx + dy * dy + dz * dz) <= (radius * radius);
    }

    /**
     * Calculates the repulsion vector for an entity inside the warding field.
     *
     * @param stoneX The X coordinate of the barrier stone.
     * @param stoneY The Y coordinate of the barrier stone.
     * @param stoneZ The Z coordinate of the barrier stone.
     * @param entityX The X coordinate of the entity.
     * @param entityY The Y coordinate of the entity.
     * @param entityZ The Z coordinate of the entity.
     * @param radius The radius of the warding field.
     * @param repulsionStrength The base strength of the repulsion.
     * @return A double array containing the {x, y, z} repulsion vector.
     */
    public static double[] calculateRepulsionVector(double stoneX, double stoneY, double stoneZ,
                                                    double entityX, double entityY, double entityZ,
                                                    double radius, double repulsionStrength) {
        double dx = entityX - stoneX;
        double dy = entityY - stoneY;
        double dz = entityZ - stoneZ;
        double distSq = dx * dx + dy * dy + dz * dz;

        // If outside the radius, no repulsion
        if (distSq > radius * radius) {
            return new double[]{0.0, 0.0, 0.0};
        }

        // If exactly at the center, push out in an arbitrary direction (e.g., +X) to prevent getting stuck
        if (distSq == 0) {
            return new double[]{repulsionStrength, 0.0, 0.0};
        }

        double distance = Math.sqrt(distSq);

        // Normalize the vector and apply repulsion strength
        double normalizedX = dx / distance;
        double normalizedY = dy / distance;
        double normalizedZ = dz / distance;

        return new double[]{
            normalizedX * repulsionStrength,
            normalizedY * repulsionStrength,
            normalizedZ * repulsionStrength
        };
    }
}
