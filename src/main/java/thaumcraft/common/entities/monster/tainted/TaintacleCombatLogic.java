package thaumcraft.common.entities.monster.tainted;

public class TaintacleCombatLogic {

    /**
     * Checks if a target is within the Taintacle's melee reach (5 blocks).
     * @param distanceSq The squared distance to the target.
     * @return true if within reach, false otherwise.
     */
    public static boolean isWithinMeleeReach(double distanceSq) {
        return distanceSq <= 25.0;
    }

    /**
     * Calculates the knockback impulse vector applied to a target hit by a Taintacle.
     * @param dx The x-distance to the target.
     * @param dz The z-distance to the target.
     * @param strength The base strength of the knockback.
     * @return A double array [x, y, z] representing the velocity impulse to add to the target.
     */
    public static double[] calculateWhipKnockback(double dx, double dz, double strength) {
        double dist = Math.sqrt(dx * dx + dz * dz);
        if (dist < 1.0E-4) {
            return new double[]{0.0, strength * 0.5, 0.0};
        }
        return new double[]{
            (dx / dist) * strength,
            strength * 0.5,
            (dz / dist) * strength
        };
    }

    /**
     * Calculates the trajectory vector for a spit attack.
     * Adds an upward arc based on horizontal distance to account for gravity.
     * @param dx The x-distance to the target.
     * @param dy The y-distance to the target.
     * @param dz The z-distance to the target.
     * @param velocity The velocity of the projectile.
     * @return A double array [x, y, z] representing the trajectory vector.
     */
    public static double[] calculateSpitTrajectory(double dx, double dy, double dz, double velocity) {
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        // Add an upward arc for gravity factor based on horizontal distance
        double adjustedDy = dy + horizontalDistance * 0.2;

        double dist = Math.sqrt(dx * dx + adjustedDy * adjustedDy + dz * dz);
        if (dist < 1.0E-4) {
            return new double[]{0.0, velocity, 0.0};
        }

        return new double[]{
            (dx / dist) * velocity,
            (adjustedDy / dist) * velocity,
            (dz / dist) * velocity
        };
    }

    /**
     * Checks whether the attack should inflict Flux Taint.
     * @return true, as Taintacles always inflict Flux Taint on melee hits.
     */
    public static boolean shouldInflictFluxTaint() {
        return true;
    }
}
