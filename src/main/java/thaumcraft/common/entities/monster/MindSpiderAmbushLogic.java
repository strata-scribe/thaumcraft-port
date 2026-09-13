package thaumcraft.common.entities.monster;

/**
 * Pure Java logic helper for Mind Spider ambush mechanics.
 * Strict mathematical decoupling: contains zero Minecraft or NeoForge classloader imports.
 */
public class MindSpiderAmbushLogic {

    /**
     * Calculates the stealth detection radius based on player's stealth state.
     *
     * @param baseRadius The default detection radius.
     * @param isSneaking True if the target is sneaking.
     * @param isInvisible True if the target is invisible.
     * @return The calculated detection radius.
     */
    public static double calculateStealthDetectionRadius(double baseRadius, boolean isSneaking, boolean isInvisible) {
        double radius = baseRadius;
        if (isSneaking) {
            radius *= 0.8;
        }
        if (isInvisible) {
            radius *= 0.5;
        }
        return radius;
    }

    /**
     * Calculates the leap launch trajectory towards a target.
     *
     * @param startX Starting X coordinate of the spider.
     * @param startY Starting Y coordinate of the spider.
     * @param startZ Starting Z coordinate of the spider.
     * @param targetX Target X coordinate.
     * @param targetY Target Y coordinate.
     * @param targetZ Target Z coordinate.
     * @param leapSpeed The leap speed multiplier.
     * @return An array containing the [vx, vy, vz] trajectory components.
     */
    public static double[] calculateAmbushLeapTrajectory(double startX, double startY, double startZ,
                                                         double targetX, double targetY, double targetZ,
                                                         double leapSpeed) {
        double dx = targetX - startX;
        double dy = targetY - startY;
        double dz = targetZ - startZ;

        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);

        if (horizontalDistance < 1e-4) {
            return new double[]{0.0, leapSpeed * 0.5, 0.0};
        }

        double vx = (dx / horizontalDistance) * leapSpeed;
        double vz = (dz / horizontalDistance) * leapSpeed;

        // Add a vertical arc component based on leap speed and target height difference
        double vy = leapSpeed * 0.5 + (dy > 0 ? dy * 0.1 : 0.0);

        return new double[]{vx, vy, vz};
    }
}
