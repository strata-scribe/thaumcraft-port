package thaumcraft.common.tiles.devices.logic;

public class ArcaneBoreTrajectoryLogic {

    /**
     * Calculates the normalized trajectory vector (step increments) based on yaw and pitch.
     * Yaw 0 is +Z, Yaw 90 is -X, Yaw 180 is -Z, Yaw 270 is +X.
     * Pitch -90 is +Y, Pitch 90 is -Y.
     *
     * @param yaw   The yaw angle in degrees.
     * @param pitch The pitch angle in degrees.
     * @return A double array containing [x, y, z] normalized step increments.
     */
    public static double[] calculateStepIncrements(double yaw, double pitch) {
        double f = Math.PI / 180.0;
        double x = -Math.sin(yaw * f) * Math.cos(pitch * f);
        double y = -Math.sin(pitch * f);
        double z = Math.cos(yaw * f) * Math.cos(pitch * f);
        return new double[]{x, y, z};
    }

    /**
     * Calculates the rotational sweep offset that the Arcane Bore uses.
     *
     * @param yaw         The base yaw angle.
     * @param pitch       The base pitch angle.
     * @param sweepOffset The sweep offset to apply.
     * @return A double array containing [newYaw, newPitch].
     */
    public static double[] calculateDigAngle(double yaw, double pitch, double sweepOffset) {
        return new double[]{ yaw + sweepOffset, pitch + sweepOffset };
    }

    /**
     * Calculates the coordinates of the block intersected by the raycast at a certain distance.
     *
     * @param startX   The starting X coordinate.
     * @param startY   The starting Y coordinate.
     * @param startZ   The starting Z coordinate.
     * @param yaw      The yaw angle in degrees.
     * @param pitch    The pitch angle in degrees.
     * @param distance The distance of the raycast.
     * @return An int array containing [x, y, z] coordinates of the target block.
     */
    public static int[] calculateTargetBlock(double startX, double startY, double startZ, double yaw, double pitch, double distance) {
        double[] increments = calculateStepIncrements(yaw, pitch);
        int x = (int) Math.floor(startX + increments[0] * distance);
        int y = (int) Math.floor(startY + increments[1] * distance);
        int z = (int) Math.floor(startZ + increments[2] * distance);
        return new int[]{x, y, z};
    }

    /**
     * Calculates the 3D bounding box surrounding the target block.
     *
     * @param x      The center block X.
     * @param y      The center block Y.
     * @param z      The center block Z.
     * @param radius The radius of the bounding box.
     * @return An int array containing [minX, minY, minZ, maxX, maxY, maxZ].
     */
    public static int[] calculateTargetBlockBounds(int x, int y, int z, int radius) {
        return new int[]{
                x - radius, y - radius, z - radius,
                x + radius, y + radius, z + radius
        };
    }
}
