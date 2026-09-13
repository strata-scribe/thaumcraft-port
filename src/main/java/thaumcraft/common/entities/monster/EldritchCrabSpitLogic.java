package thaumcraft.common.entities.monster;

public class EldritchCrabSpitLogic {

    public static class SpitVector {
        public final double x;
        public final double y;
        public final double z;

        public SpitVector(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public static class DamageResult {
        public final double standardDamage;
        public final double bypassDamage;

        public DamageResult(double standardDamage, double bypassDamage) {
            this.standardDamage = standardDamage;
            this.bypassDamage = bypassDamage;
        }
    }

    /**
     * Calculates the initial velocity vector needed to hit a target.
     * Includes a slight vertical arc to compensate for gravity based on horizontal distance.
     *
     * @param startX Starting X coordinate
     * @param startY Starting Y coordinate
     * @param startZ Starting Z coordinate
     * @param targetX Target X coordinate
     * @param targetY Target Y coordinate
     * @param targetZ Target Z coordinate
     * @param velocity The magnitude of the initial velocity
     * @return The calculated SpitVector for the trajectory
     */
    public static SpitVector calculateTrajectory(double startX, double startY, double startZ,
                                                 double targetX, double targetY, double targetZ,
                                                 double velocity) {
        double dx = targetX - startX;
        double dy = targetY - startY;
        double dz = targetZ - startZ;

        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);

        // Add a vertical arc compensation based on horizontal distance
        dy += horizontalDistance * 0.2;

        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (distance == 0) {
            return new SpitVector(0, velocity, 0);
        }

        return new SpitVector(
            (dx / distance) * velocity,
            (dy / distance) * velocity,
            (dz / distance) * velocity
        );
    }

    /**
     * Calculates the final velocity of the spit upon impact after being in the air for a given number of ticks.
     *
     * @param initialVx Initial X velocity
     * @param initialVy Initial Y velocity
     * @param initialVz Initial Z velocity
     * @param ticksInAir Number of ticks the projectile has been traveling
     * @param dragMultiplier The velocity multiplier per tick (e.g. 0.99)
     * @param gravity The vertical velocity subtracted per tick
     * @return The final SpitVector upon impact
     */
    public static SpitVector calculateImpactVelocity(double initialVx, double initialVy, double initialVz,
                                                     int ticksInAir, double dragMultiplier, double gravity) {
        double currentVx = initialVx;
        double currentVy = initialVy;
        double currentVz = initialVz;

        for (int i = 0; i < ticksInAir; i++) {
            currentVx *= dragMultiplier;
            currentVy *= dragMultiplier;
            currentVz *= dragMultiplier;
            currentVy -= gravity;
        }

        return new SpitVector(currentVx, currentVy, currentVz);
    }

    /**
     * Calculates how much damage is standard versus armor-bypassing venom damage.
     *
     * @param baseDamage The total raw damage
     * @param armorValue The target's armor value (unused in the raw split calculation but available for more complex logic)
     * @param bypassPercentage The percentage of damage that ignores armor (0.0 to 1.0)
     * @return A DamageResult containing the standard and bypass damage amounts
     */
    public static DamageResult calculateArmorBypassDamage(double baseDamage, double armorValue, double bypassPercentage) {
        double clampedPercentage = Math.max(0.0, Math.min(1.0, bypassPercentage));
        double bypassDamage = baseDamage * clampedPercentage;
        double standardDamage = baseDamage - bypassDamage;

        // armorValue can be used if standard damage gets reduced here, but for decoupled logic,
        // returning the raw split is usually sufficient unless we simulate armor reduction itself.
        // We will just return the split based on percentage.

        return new DamageResult(standardDamage, bypassDamage);
    }
}
