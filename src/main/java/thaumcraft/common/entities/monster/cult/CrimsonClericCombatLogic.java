package thaumcraft.common.entities.monster.cult;

public class CrimsonClericCombatLogic {

    public static double[] calculateFireOrbTrajectory(double shooterX, double shooterY, double shooterZ,
                                                      double targetX, double targetY, double targetZ,
                                                      double velocity) {
        double dX = targetX - shooterX;
        double dY = targetY - shooterY;
        double dZ = targetZ - shooterZ;

        double distance = Math.sqrt(dX * dX + dY * dY + dZ * dZ);
        if (distance == 0) {
            return new double[]{0.0, 0.0, 0.0};
        }

        return new double[]{
            (dX / distance) * velocity,
            (dY / distance) * velocity,
            (dZ / distance) * velocity
        };
    }

    public static float calculateExplosionRadius(float baseRadius, float powerMultiplier) {
        return baseRadius * powerMultiplier;
    }

    public static boolean shouldBloodPrayerPulse(int ritualTicksActive) {
        return ritualTicksActive > 0 && ritualTicksActive % 100 == 0;
    }

    public static float calculateBloodPrayerHealing(float maxHealth) {
        return maxHealth * 0.25f;
    }
}
