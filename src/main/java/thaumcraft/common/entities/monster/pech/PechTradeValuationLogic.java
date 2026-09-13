package thaumcraft.common.entities.monster.pech;

public class PechTradeValuationLogic {

    public enum PreferenceTier {
        COMMON(1),
        GOLD(3),
        GEM(5),
        ARTIFACT(10);

        private final int weight;

        PreferenceTier(int weight) {
            this.weight = weight;
        }

        public int getWeight() {
            return weight;
        }
    }

    public enum BarterYield {
        NONE,
        COMMON,
        UNCOMMON,
        RARE,
        ARTIFACT
    }

    /**
     * Calculates the score of offered items based on the tier weight and quantity.
     *
     * @param tier     The preference tier of the item.
     * @param quantity The amount of the item offered.
     * @return The calculated score.
     */
    public static int scoreItem(PreferenceTier tier, int quantity) {
        if (tier == null || quantity <= 0) {
            return 0;
        }
        return tier.getWeight() * quantity;
    }

    /**
     * Determines the yielded loot tier based on the item score and a random roll.
     *
     * @param score      The evaluated score of the offered items.
     * @param randomRoll A random value between 0.0 (inclusive) and 1.0 (exclusive).
     * @return The barter yield tier.
     */
    public static BarterYield determineBarterYield(int score, double randomRoll) {
        if (score <= 0) {
            return BarterYield.NONE;
        }

        // Adjust effective score based on the random roll to introduce variability
        double effectiveScore = score * (0.5 + randomRoll);

        if (effectiveScore < 2.0) {
            return BarterYield.NONE;
        } else if (effectiveScore < 5.0) {
            return BarterYield.COMMON;
        } else if (effectiveScore < 10.0) {
            return BarterYield.UNCOMMON;
        } else if (effectiveScore < 20.0) {
            return BarterYield.RARE;
        } else {
            return BarterYield.ARTIFACT;
        }
    }
}
