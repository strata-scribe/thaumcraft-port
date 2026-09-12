package thaumcraft.common.tiles.essentia.logic;

/**
 * Decoupled logic class for computing Essentia Tube flow particle physics.
 * This class has zero Minecraft/Forge imports, allowing for easy unit testing.
 */
public class TubeFlowParticleLogic {

    /**
     * Calculates particle trajectory positions traveling along tube center lines.
     * Interpolates between start and end coordinates based on progress.
     * Progress is clamped between 0.0 and 1.0.
     *
     * @param startX start x coordinate
     * @param startY start y coordinate
     * @param startZ start z coordinate
     * @param endX end x coordinate
     * @param endY end y coordinate
     * @param endZ end z coordinate
     * @param progress value between 0.0 and 1.0 indicating distance along the path
     * @return an array of [x, y, z] interpolated coordinates
     */
    public static double[] getWaypointInterpolation(double startX, double startY, double startZ,
                                                    double endX, double endY, double endZ,
                                                    double progress) {
        double clampedProgress = Math.max(0.0, Math.min(1.0, progress));
        double x = startX + (endX - startX) * clampedProgress;
        double y = startY + (endY - startY) * clampedProgress;
        double z = startZ + (endZ - startZ) * clampedProgress;
        return new double[]{x, y, z};
    }

    /**
     * Computes particle speed scaling with flow volume.
     * Higher flow volumes result in faster moving particles.
     * Base speed is 0.05, scaling up by 0.01 per volume unit.
     *
     * @param flowVolume the amount of essentia flowing
     * @return the computed speed scale
     */
    public static double computeSpeedScaling(int flowVolume) {
        if (flowVolume <= 0) {
            return 0.0;
        }
        return 0.05 + (flowVolume * 0.01);
    }

    /**
     * Calculates the flow velocity based on the suction gradient.
     * A larger difference between source and target suction results in higher velocity.
     *
     * @param sourceSuction suction at the source node
     * @param targetSuction suction at the target node
     * @return the computed velocity factor
     */
    public static double calculateFlowVelocity(int sourceSuction, int targetSuction) {
        int gradient = sourceSuction - targetSuction;
        if (gradient <= 0) {
            return 0.0;
        }
        // Base velocity plus a small increment per suction gradient point
        return 0.1 + (gradient * 0.02);
    }
}
