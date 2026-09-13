package thaumcraft.common.world.logic;

public class TaintedGeodeGrowthLogic {

    /**
     * Calculates the number of ticks required for a tainted crystal cluster to grow,
     * based on the ambient vitium flux. Higher flux means faster growth.
     *
     * @param ambientVitiumFlux The amount of vitium flux in the ambient environment.
     * @return The number of ticks required for growth. Returns -1 if growth is stalled (flux <= 0).
     */
    public static int calculateGrowthTicks(double ambientVitiumFlux) {
        if (ambientVitiumFlux <= 0.0) {
            return -1;
        }

        double baseTicks = 6000.0;
        double standardFlux = 50.0;

        double calculatedTicks = baseTicks * (standardFlux / ambientVitiumFlux);

        int minTicks = 1200; // Cap at fast growth
        int maxTicks = 24000; // Cap at slow growth

        int finalTicks = (int) Math.round(calculatedTicks);

        if (finalTicks < minTicks) {
            return minTicks;
        } else if (finalTicks > maxTicks) {
            return maxTicks;
        }

        return finalTicks;
    }

    /**
     * Determines whether a new crystal cluster can spawn in a geode,
     * depending on the available flux and current cluster density.
     *
     * @param ambientVitiumFlux The amount of vitium flux available.
     * @param nearbyClusters    The current number of nearby clusters.
     * @param maxClusters       The maximum allowed number of nearby clusters.
     * @return True if a new cluster can spawn, false otherwise.
     */
    public static boolean canSpawnNewCluster(double ambientVitiumFlux, int nearbyClusters, int maxClusters) {
        if (ambientVitiumFlux < 10.0) {
            return false; // Not enough flux to spark a new cluster
        }

        return nearbyClusters < maxClusters;
    }
}
