package thaumcraft.common.entities.monster.tainted;

public class TaintacleSweepLogic {

    /**
     * Checks if a target is within a 360-degree cylindrical bounding area.
     * @param dx The x-distance to the target.
     * @param dy The y-distance to the target.
     * @param dz The z-distance to the target.
     * @param maxRadius The maximum horizontal radius of the sweep.
     * @param maxYReach The maximum vertical reach (up or down).
     * @return true if within reach, false otherwise.
     */
    public static boolean isWithinSweepReach(double dx, double dy, double dz, double maxRadius, double maxYReach) {
        double horizontalDistanceSq = dx * dx + dz * dz;
        return horizontalDistanceSq <= maxRadius * maxRadius && Math.abs(dy) <= maxYReach;
    }

    /**
     * Determines if the target falls within a sweeping arc segment at a given facing angle.
     * @param dx The x-distance to the target.
     * @param dz The z-distance to the target.
     * @param sweepFacingAngle The central angle of the sweep arc in degrees.
     * @param arcWidth The total width of the arc in degrees.
     * @return true if the target is within the collision arc, false otherwise.
     */
    public static boolean isWithinHorizontalCollisionArc(double dx, double dz, double sweepFacingAngle, double arcWidth) {
        if (Math.abs(dx) < 1.0E-4 && Math.abs(dz) < 1.0E-4) {
            return true; // Target is exactly at the origin, effectively within any arc.
        }

        double targetAngle = Math.toDegrees(Math.atan2(dz, dx));

        // Normalize angles to [0, 360)
        targetAngle = (targetAngle % 360 + 360) % 360;
        double centerAngle = (sweepFacingAngle % 360 + 360) % 360;

        double difference = Math.abs(targetAngle - centerAngle);
        if (difference > 180) {
            difference = 360 - difference;
        }

        return difference <= (arcWidth / 2.0);
    }

    /**
     * Computes an outward radial knockback vector [x, y, z] for targets hit by the sweep.
     * @param dx The x-distance to the target.
     * @param dz The z-distance to the target.
     * @param knockbackStrength The strength of the knockback.
     * @return A double array [x, y, z] representing the knockback vector.
     */
    public static double[] calculateSweepKnockback(double dx, double dz, double knockbackStrength) {
        double dist = Math.sqrt(dx * dx + dz * dz);
        if (dist < 1.0E-4) {
            return new double[]{0.0, knockbackStrength * 0.5, 0.0};
        }
        return new double[]{
            (dx / dist) * knockbackStrength,
            knockbackStrength * 0.5,
            (dz / dist) * knockbackStrength
        };
    }
}
