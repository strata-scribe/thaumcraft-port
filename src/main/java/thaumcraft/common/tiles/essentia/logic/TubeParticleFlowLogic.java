package thaumcraft.common.tiles.essentia.logic;

import java.util.ArrayList;
import java.util.List;

/**
 * Decoupled logic class for computing Essentia Tube flow particle trajectories
 * through interconnected tube manifolds.
 * This class has zero Minecraft/Forge imports, allowing for easy unit testing.
 */
public class TubeParticleFlowLogic {

    /**
     * Interpolates the 3D position of a particle across multiple connected tube nodes
     * based on an overall progress value [0.0, 1.0].
     *
     * @param manifoldNodes a list of double[] arrays, each containing {x, y, z} coordinates
     * @param progress      value between 0.0 and 1.0 indicating overall traversal progress
     * @return a double[] array containing the {x, y, z} interpolated position, or null if the list is empty/null
     */
    public static double[] getPositionAtProgress(List<double[]> manifoldNodes, double progress) {
        if (manifoldNodes == null || manifoldNodes.isEmpty()) {
            return null;
        }

        if (manifoldNodes.size() == 1) {
            return new double[]{manifoldNodes.get(0)[0], manifoldNodes.get(0)[1], manifoldNodes.get(0)[2]};
        }

        double clampedProgress = Math.max(0.0, Math.min(1.0, progress));
        int numSegments = manifoldNodes.size() - 1;
        double progressPerSegment = 1.0 / numSegments;

        int segmentIndex = (int) (clampedProgress / progressPerSegment);
        if (segmentIndex >= numSegments) {
            segmentIndex = numSegments - 1;
        }

        double segmentStartProgress = segmentIndex * progressPerSegment;
        double segmentProgress = (clampedProgress - segmentStartProgress) / progressPerSegment;

        double[] startNode = manifoldNodes.get(segmentIndex);
        double[] endNode = manifoldNodes.get(segmentIndex + 1);

        double x = startNode[0] + (endNode[0] - startNode[0]) * segmentProgress;
        double y = startNode[1] + (endNode[1] - startNode[1]) * segmentProgress;
        double z = startNode[2] + (endNode[2] - startNode[2]) * segmentProgress;

        return new double[]{x, y, z};
    }

    /**
     * Generates a discrete list of coordinate points representing the full traversal path
     * across the tube manifold nodes.
     *
     * @param manifoldNodes   a list of double[] arrays, each containing {x, y, z} coordinates
     * @param stepsPerSegment the number of discrete steps to compute per segment connecting two nodes
     * @return a list of double[] arrays containing the {x, y, z} positions along the trajectory
     */
    public static List<double[]> computeTrajectory(List<double[]> manifoldNodes, int stepsPerSegment) {
        List<double[]> trajectory = new ArrayList<>();

        if (manifoldNodes == null || manifoldNodes.isEmpty()) {
            return trajectory;
        }

        if (manifoldNodes.size() == 1 || stepsPerSegment <= 0) {
            trajectory.add(new double[]{manifoldNodes.get(0)[0], manifoldNodes.get(0)[1], manifoldNodes.get(0)[2]});
            return trajectory;
        }

        for (int i = 0; i < manifoldNodes.size() - 1; i++) {
            double[] startNode = manifoldNodes.get(i);
            double[] endNode = manifoldNodes.get(i + 1);

            for (int step = 0; step < stepsPerSegment; step++) {
                double segmentProgress = (double) step / stepsPerSegment;
                double x = startNode[0] + (endNode[0] - startNode[0]) * segmentProgress;
                double y = startNode[1] + (endNode[1] - startNode[1]) * segmentProgress;
                double z = startNode[2] + (endNode[2] - startNode[2]) * segmentProgress;
                trajectory.add(new double[]{x, y, z});
            }
        }

        // Add the final node
        double[] lastNode = manifoldNodes.get(manifoldNodes.size() - 1);
        trajectory.add(new double[]{lastNode[0], lastNode[1], lastNode[2]});

        return trajectory;
    }
}
