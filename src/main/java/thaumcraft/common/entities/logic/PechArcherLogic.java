package thaumcraft.common.entities.logic;

public class PechArcherLogic {

    public enum ArrowType {
        REGULAR,
        POISON,
        SPECTRAL
    }

    /**
     * Computes the ranged attack draw interval in ticks based on target distance and difficulty.
     *
     * @param distance The distance to the target.
     * @param difficulty The difficulty level (0=Peaceful, 1=Easy, 2=Normal, 3=Hard).
     * @return The interval in ticks.
     */
    public static int computeDrawInterval(double distance, int difficulty) {
        int baseInterval = 60;

        if (difficulty == 1) {
            baseInterval = 50;
        } else if (difficulty == 2) {
            baseInterval = 40;
        } else if (difficulty >= 3) {
            baseInterval = 30;
        }

        if (distance <= 5.0) {
            baseInterval = Math.max(10, baseInterval - 15);
        } else if (distance >= 20.0) {
            baseInterval += 20;
        }

        return baseInterval;
    }

    /**
     * Computes the probability of applying a poison arrow.
     *
     * @param difficulty The difficulty level (0=Peaceful, 1=Easy, 2=Normal, 3=Hard).
     * @return The probability (0.0 to 1.0).
     */
    public static double getPoisonArrowChance(int difficulty) {
        if (difficulty <= 0) return 0.0;
        if (difficulty == 1) return 0.10;
        if (difficulty == 2) return 0.20;
        return 0.35;
    }

    /**
     * Computes the probability of applying a spectral arrow.
     *
     * @param difficulty The difficulty level (0=Peaceful, 1=Easy, 2=Normal, 3=Hard).
     * @return The probability (0.0 to 1.0).
     */
    public static double getSpectralArrowChance(int difficulty) {
        if (difficulty <= 0) return 0.0;
        if (difficulty == 1) return 0.05;
        if (difficulty == 2) return 0.10;
        return 0.20;
    }

    /**
     * Determines the arrow type to fire based on a random roll and difficulty.
     *
     * @param randomRoll A random value between 0.0 (inclusive) and 1.0 (exclusive).
     * @param difficulty The difficulty level.
     * @return The ArrowType to use.
     */
    public static ArrowType determineArrowType(double randomRoll, int difficulty) {
        double spectralChance = getSpectralArrowChance(difficulty);
        double poisonChance = getPoisonArrowChance(difficulty);

        if (randomRoll < spectralChance) {
            return ArrowType.SPECTRAL;
        } else if (randomRoll < spectralChance + poisonChance) {
            return ArrowType.POISON;
        }
        return ArrowType.REGULAR;
    }
}
