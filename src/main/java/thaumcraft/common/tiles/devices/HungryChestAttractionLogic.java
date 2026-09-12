package thaumcraft.common.tiles.devices;

public class HungryChestAttractionLogic {
    public static class AttractionResult {
        public final double vx;
        public final double vy;
        public final double vz;
        public final boolean shouldAbsorb;

        public AttractionResult(double vx, double vy, double vz, boolean shouldAbsorb) {
            this.vx = vx;
            this.vy = vy;
            this.vz = vz;
            this.shouldAbsorb = shouldAbsorb;
        }
    }

    public static double calculateDistanceDecay(double distance, double maxRadius) {
        if (distance >= maxRadius) return 0.0;
        return 1.0 - (distance / maxRadius);
    }

    public static double calculatePullSpeed(double distanceDecay, double baseSpeed) {
        return distanceDecay * baseSpeed;
    }

    public static AttractionResult calculateAttractionVector(
            double itemX, double itemY, double itemZ,
            double chestX, double chestY, double chestZ,
            double maxRadius, double absorbDistance, double baseSpeed) {

        double dx = chestX - itemX;
        double dy = chestY - itemY;
        double dz = chestZ - itemZ;

        double distanceSq = dx * dx + dy * dy + dz * dz;
        double distance = Math.sqrt(distanceSq);

        if (distance > maxRadius) {
            return new AttractionResult(0, 0, 0, false);
        }

        boolean shouldAbsorb = distance <= absorbDistance;

        if (distance > 0) {
            double decay = calculateDistanceDecay(distance, maxRadius);
            double speed = calculatePullSpeed(decay, baseSpeed);

            double vx = (dx / distance) * speed;
            double vy = (dy / distance) * speed;
            double vz = (dz / distance) * speed;

            return new AttractionResult(vx, vy, vz, shouldAbsorb);
        } else {
            return new AttractionResult(0, 0, 0, true);
        }
    }
}
