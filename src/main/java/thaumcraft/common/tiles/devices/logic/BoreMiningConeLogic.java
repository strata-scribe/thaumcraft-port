package thaumcraft.common.tiles.devices.logic;

import java.util.ArrayList;
import java.util.List;

public class BoreMiningConeLogic {

    /**
     * Calculates a list of block coordinates [x, y, z] that fall within the mining cone
     * projected from the origin along the specified yaw and pitch up to the given depth.
     * A block is within the cone if its perpendicular distance to the central ray is
     * <= maxRadius * (current_distance / depth).
     *
     * @param originX   The origin X coordinate.
     * @param originY   The origin Y coordinate.
     * @param originZ   The origin Z coordinate.
     * @param yaw       The yaw angle in degrees.
     * @param pitch     The pitch angle in degrees.
     * @param depth     The maximum depth of the cone.
     * @param maxRadius The maximum radius of the cone at its base (at distance = depth).
     * @return A list of int[] containing [x, y, z] coordinates of the blocks within the cone.
     */
    public static List<int[]> calculateMiningCone(int originX, int originY, int originZ, double yaw, double pitch, int depth, int maxRadius) {
        List<int[]> coneBlocks = new ArrayList<>();

        double f = Math.PI / 180.0;
        double dx = -Math.sin(yaw * f) * Math.cos(pitch * f);
        double dy = -Math.sin(pitch * f);
        double dz = Math.cos(yaw * f) * Math.cos(pitch * f);

        int boxRadius = Math.max(maxRadius, depth);

        for (int x = originX - boxRadius; x <= originX + boxRadius; x++) {
            for (int y = originY - boxRadius; y <= originY + boxRadius; y++) {
                for (int z = originZ - boxRadius; z <= originZ + boxRadius; z++) {
                    double vx = x - originX;
                    double vy = y - originY;
                    double vz = z - originZ;

                    double proj = vx * dx + vy * dy + vz * dz;

                    if (proj >= 0 && proj <= depth) {
                        double distSq = (vx * vx + vy * vy + vz * vz) - (proj * proj);
                        double currentRadius = maxRadius * (proj / depth);
                        if (distSq <= currentRadius * currentRadius) {
                            coneBlocks.add(new int[]{x, y, z});
                        }
                    }
                }
            }
        }

        return coneBlocks;
    }

    /**
     * Calculates the maximum excavation depth based on the base depth and a depth bonus.
     *
     * @param baseDepth  The base depth of the arcane bore.
     * @param depthBonus The depth bonus to apply.
     * @return The final excavation depth.
     */
    public static int calculateExcavationDepth(int baseDepth, int depthBonus) {
        return baseDepth + depthBonus;
    }

    /**
     * Calculates the energy consumption per block broken based on block hardness and an efficiency multiplier.
     *
     * @param blockHardness        The hardness of the block being broken.
     * @param efficiencyMultiplier The efficiency multiplier (e.g., from enchantments or upgrades).
     * @return The energy consumption per block (minimum 1).
     */
    public static int calculateEnergyConsumption(float blockHardness, double efficiencyMultiplier) {
        double multiplier = Math.max(0.001, efficiencyMultiplier);
        return Math.max(1, (int) (blockHardness * 10 / multiplier));
    }
}
