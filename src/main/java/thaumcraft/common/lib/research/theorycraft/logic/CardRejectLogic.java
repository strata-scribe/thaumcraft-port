package thaumcraft.common.lib.research.theorycraft.logic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardRejectLogic {

    /**
     * Computes the inspiration cost (penalty) or refund for rejecting a card.
     * @param baseCost The base cost of rejection.
     * @param fatigue The current fatigue level.
     * @param randomFactor A random float between 0.0 and 1.0.
     * @return Positive values indicate a penalty, negative values indicate a refund.
     */
    public static int computeRejectionCost(int baseCost, int fatigue, float randomFactor) {
        int penalty = baseCost + fatigue;
        // 10% chance to get an inspiration refund, diminished by fatigue
        float refundChance = Math.max(0.01f, 0.1f - (fatigue * 0.01f));

        if (randomFactor < refundChance) {
            return -1; // Refund 1 inspiration
        }
        return penalty;
    }

    /**
     * Calculates the probability distribution for drawing new cards after a rejection,
     * taking fatigue into account which flattens or skews the distribution.
     * @param availableCards List of available card identifiers.
     * @param fatigue The current fatigue level.
     * @return A map of card identifiers to their normalized probability (0.0 to 1.0).
     */
    public static Map<String, Double> computeRedrawProbabilityDistribution(List<String> availableCards, int fatigue) {
        Map<String, Double> distribution = new HashMap<>();
        if (availableCards == null || availableCards.isEmpty()) {
            return distribution;
        }

        double[] weights = new double[availableCards.size()];
        double totalWeight = 0;

        for (int i = 0; i < availableCards.size(); i++) {
            // Base weight is 1.0.
            // Earlier cards get a bonus, but fatigue flattens this bonus.
            double bonus = Math.max(0.0, (availableCards.size() - i) - (fatigue * 0.5));
            weights[i] = 1.0 + bonus;
            totalWeight += weights[i];
        }

        for (int i = 0; i < availableCards.size(); i++) {
            distribution.put(availableCards.get(i), weights[i] / totalWeight);
        }

        return distribution;
    }

    /**
     * Computes how much fatigue increases after a rejection.
     * @param currentFatigue The current fatigue level.
     * @param rejectionCount The number of times cards have been rejected.
     * @return The new fatigue level.
     */
    public static int computeFatigueEscalation(int currentFatigue, int rejectionCount) {
        // Base escalation is 1, plus additional escalation for consecutive rejections
        return currentFatigue + 1 + Math.max(0, rejectionCount / 2);
    }
}
