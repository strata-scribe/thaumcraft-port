package thaumcraft.common.entities.monster.tainted;

public class TaintSeedGrowthFootprintLogic {

    /**
     * Calculates the maturity stage of a taint seed based on its age.
     * @param age The current age in ticks.
     * @param maxAge The maximum age for full maturity.
     * @return An integer representing the maturity stage, from 0 to 4.
     */
    public static int calculateMaturityStage(int age, int maxAge) {
        if (maxAge <= 0) return 4;
        if (age < 0) return 0;
        if (age >= maxAge) return 4;

        return (int) (((double) age / maxAge) * 4.0);
    }

    /**
     * Calculates the radius of the corruption footprint.
     * @param stage The current maturity stage (0 to 4).
     * @param maxRadius The maximum radius at full maturity.
     * @return The footprint radius.
     */
    public static double calculateFootprintRadius(int stage, double maxRadius) {
        if (stage < 0) return 0.0;
        if (stage > 4) stage = 4;

        return (stage / 4.0) * maxRadius;
    }

    /**
     * Checks if a specific block is within the taint seed's circular footprint.
     * @param seedX X coordinate of the seed.
     * @param seedZ Z coordinate of the seed.
     * @param blockX X coordinate of the block.
     * @param blockZ Z coordinate of the block.
     * @param radius The footprint radius.
     * @return true if the block is inside or exactly on the radius boundary.
     */
    public static boolean isBlockInFootprint(double seedX, double seedZ, double blockX, double blockZ, double radius) {
        if (radius < 0) return false;

        double dx = blockX - seedX;
        double dz = blockZ - seedZ;

        return (dx * dx + dz * dz) <= (radius * radius);
    }
}
