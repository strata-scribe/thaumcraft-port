package thaumcraft.common.casters.logic;

public class FocusSplitLogic {

    /**
     * Calculates a divergent directional vector for a trajectory fork with uniform angular spread.
     * Uses zero Minecraft imports.
     *
     * @param dirX         Original direction X
     * @param dirY         Original direction Y
     * @param dirZ         Original direction Z
     * @param angleSetting The total angular spread in degrees.
     * @param index        The branch index (0 to totalForks - 1).
     * @param totalForks   The total number of forks/branches.
     * @return A double[] containing the new (normalized) directional vector {x, y, z}.
     */
    public static double[] calculateSplitVector(double dirX, double dirY, double dirZ, int angleSetting, int index, int totalForks) {
        // Normalize input direction
        double len = Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
        if (len > 1.0E-6) {
            dirX /= len;
            dirY /= len;
            dirZ /= len;
        } else {
            return new double[]{0, 0, 0};
        }

        if (totalForks <= 1) {
            return new double[]{dirX, dirY, dirZ};
        }

        // Calculate rotation angle for this index
        double rotationAngleDegrees = (angleSetting / (totalForks - 1.0)) * index - (angleSetting / 2.0);
        double theta = Math.toRadians(rotationAngleDegrees);

        if (Math.abs(theta) < 1.0E-6) {
            return new double[]{dirX, dirY, dirZ};
        }

        // Up vector
        double upX = 0;
        double upY = 1;
        double upZ = 0;

        // Cross product of Dir and Up to find the axis of rotation (horizontal spread)
        double axisX = 0;
        double axisY = 1;
        double axisZ = 0;

        double axisLen = Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);


        if (Math.abs(dirY) > 0.999) {
            axisX = 1;
            axisY = 0;
            axisZ = 0;
            axisLen = 1;
        }

        // Normalize axis
        axisX /= axisLen;
        axisY /= axisLen;
        axisZ /= axisLen;

        // Rodrigues' rotation formula
        // v_rot = v * cos(theta) + (axis x v) * sin(theta) + axis * (axis . v) * (1 - cos(theta))
        double cosT = Math.cos(theta);
        double sinT = Math.sin(theta);

        // Cross product of (axis x dir)
        double crossX = axisY * dirZ - axisZ * dirY;
        double crossY = axisZ * dirX - axisX * dirZ;
        double crossZ = axisX * dirY - axisY * dirX;

        // Dot product of (axis . dir)
        double dot = axisX * dirX + axisY * dirY + axisZ * dirZ;

        double newX = dirX * cosT + crossX * sinT + axisX * dot * (1 - cosT);
        double newY = dirY * cosT + crossY * sinT + axisY * dot * (1 - cosT);
        double newZ = dirZ * cosT + crossZ * sinT + axisZ * dot * (1 - cosT);

        // Re-normalize just to be safe
        double newLen = Math.sqrt(newX * newX + newY * newY + newZ * newZ);
        if (newLen > 1.0E-6) {
            newX /= newLen;
            newY /= newLen;
            newZ /= newLen;
        }

        return new double[]{newX, newY, newZ};
    }
}
