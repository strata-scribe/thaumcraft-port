package thaumcraft.common.world.features;

/**
 * Pure Java logic helper for ore generation distributions.
 * Strict mathematical decoupling: contains zero Minecraft or NeoForge classloader imports.
 */
public class OreVeinDistributionLogic {

    public static final int CINNABAR_MIN_Y = -64;
    public static final int CINNABAR_MAX_Y = 0;

    public static final int AMBER_MIN_Y = 0;
    public static final int AMBER_MAX_Y = 80;

    /**
     * Validates if a given Y level is within Cinnabar's natural distribution bounds (-64 to 0).
     */
    public static boolean isValidCinnabarY(int y) {
        return y >= CINNABAR_MIN_Y && y <= CINNABAR_MAX_Y;
    }

    /**
     * Validates if a given Y level is within Amber's natural distribution bounds (0 to 80).
     */
    public static boolean isValidAmberY(int y) {
        return y >= AMBER_MIN_Y && y <= AMBER_MAX_Y;
    }

    /**
     * Calculates a uniformly distributed Y level for Cinnabar.
     */
    public static int calculateUniformCinnabarY(int randomVal) {
        int range = CINNABAR_MAX_Y - CINNABAR_MIN_Y + 1;
        return CINNABAR_MIN_Y + Math.abs(randomVal % range);
    }

    /**
     * Calculates a uniformly distributed Y level for Amber.
     */
    public static int calculateUniformAmberY(int randomVal) {
        int range = AMBER_MAX_Y - AMBER_MIN_Y + 1;
        return AMBER_MIN_Y + Math.abs(randomVal % range);
    }

    /**
     * Calculates a triangular distributed Y level for Cinnabar (peak at Y = -32).
     */
    public static int calculateTriangleCinnabarY(int randomVal1, int randomVal2) {
        int halfRange = (CINNABAR_MAX_Y - CINNABAR_MIN_Y) / 2;
        return CINNABAR_MIN_Y + Math.abs(randomVal1 % (halfRange + 1)) + Math.abs(randomVal2 % (halfRange + 1));
    }

    /**
     * Calculates a triangular distributed Y level for Amber (peak at Y = 40).
     */
    public static int calculateTriangleAmberY(int randomVal1, int randomVal2) {
        int halfRange = (AMBER_MAX_Y - AMBER_MIN_Y) / 2;
        return AMBER_MIN_Y + Math.abs(randomVal1 % (halfRange + 1)) + Math.abs(randomVal2 % (halfRange + 1));
    }

    /**
     * Computes the base vein size with a randomized variance.
     *
     * @param base       Base minimum size of the vein
     * @param variance   The range of additional blocks
     * @param randomRoll The random roll value used to compute the extra size
     * @return Final computed vein size
     */
    public static int computeVeinSize(int base, int variance, int randomRoll) {
        if (variance <= 0) return base;
        return base + Math.abs(randomRoll % (variance + 1));
    }

    /**
     * Computes the number of veins per chunk based on base density and an extra fractional chance.
     *
     * @param baseVeins  Guaranteed number of veins per chunk
     * @param chance     Probability (0-99) of generating one additional vein
     * @param randomRoll The random roll (0-99 typically) to evaluate the chance
     * @return Final number of veins to attempt generating in the chunk
     */
    public static int computeVeinDensity(int baseVeins, int chance, int randomRoll) {
        int veins = baseVeins;
        if (Math.abs(randomRoll % 100) < chance) {
            veins++;
        }
        return veins;
    }
}
