package thaumcraft.common.entities.monster;

public class EldritchCrabLogic {

    public static class LeapVector {
        public final double x;
        public final double y;
        public final double z;

        public LeapVector(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public static class HostStats {
        public final double maxHealth;
        public final double speed;
        public final boolean hasCrabCrown;

        public HostStats(double maxHealth, double speed, boolean hasCrabCrown) {
            this.maxHealth = maxHealth;
            this.speed = speed;
            this.hasCrabCrown = hasCrabCrown;
        }
    }

    /**
     * Calculates the stats of a host that has been inhabited by an Eldritch Crab.
     * The host gets 2x max health, 1.5x speed, and wears the Crab crown.
     *
     * @param originalHealth The host's original maximum health
     * @param originalSpeed The host's original speed
     * @return The updated HostStats
     */
    public static HostStats getInhabitedHostStats(double originalHealth, double originalSpeed) {
        return new HostStats(originalHealth * 2.0, originalSpeed * 1.5, true);
    }

    /**
     * Checks if the crab is within a valid distance to latch onto the target's head.
     *
     * @param crabX Crab's X coordinate
     * @param crabY Crab's Y coordinate
     * @param crabZ Crab's Z coordinate
     * @param targetX Target's X coordinate
     * @param targetY Target's Y coordinate (feet)
     * @param targetZ Target's Z coordinate
     * @param targetEyeHeight The height of the target's eyes/head
     * @param latchDistance The maximum distance threshold for latching
     * @return True if within latch distance, false otherwise
     */
    public static boolean isWithinLatchDistance(double crabX, double crabY, double crabZ,
                                                double targetX, double targetY, double targetZ, double targetEyeHeight, double latchDistance) {
        double dx = targetX - crabX;
        double dy = (targetY + targetEyeHeight) - crabY;
        double dz = targetZ - crabZ;

        double distanceSqr = dx * dx + dy * dy + dz * dz;
        return distanceSqr <= latchDistance * latchDistance;
    }

    /**
     * Calculates the leap trajectory for the Eldritch Crab targeting the head of a humanoid.
     * The crab tries to jump slightly upwards and towards the target's head.
     *
     * @param crabX Crab's X coordinate
     * @param crabY Crab's Y coordinate
     * @param crabZ Crab's Z coordinate
     * @param targetX Target's X coordinate
     * @param targetY Target's Y coordinate (feet)
     * @param targetZ Target's Z coordinate
     * @param targetEyeHeight The height of the target's eyes/head
     * @return The calculated LeapVector
     */
    public static LeapVector calculateLeapingTrajectory(double crabX, double crabY, double crabZ,
                                                        double targetX, double targetY, double targetZ, double targetEyeHeight) {
        double dx = targetX - crabX;
        double dy = (targetY + targetEyeHeight) - crabY;
        double dz = targetZ - crabZ;

        double distance = Math.sqrt(dx * dx + dz * dz);

        if (distance == 0) {
            return new LeapVector(0, 0.5, 0); // Jump straight up if exactly below/above
        }

        // Normalize direction and scale by leap power (e.g. 0.8)
        double scale = 0.8 / distance;

        double leapX = dx * scale;
        // Target vertical velocity based on height difference, bounded to reasonable limits
        double leapY = Math.min(1.0, Math.max(0.4, (dy / distance) + 0.4));
        double leapZ = dz * scale;

        return new LeapVector(leapX, leapY, leapZ);
    }
}
