package thaumcraft.common.lib.research.theorycraft.logic;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CardPonderLogic {

    public static final int INSPIRATION_COST = 2;

    public static class AllocationResult {
        public final Map<String, Integer> allocations;
        public final boolean earlyExit;
        public final boolean success;

        public AllocationResult(Map<String, Integer> allocations, boolean earlyExit, boolean success) {
            this.allocations = allocations;
            this.earlyExit = earlyExit;
            this.success = success;
        }
    }

    /**
     * Calculates the free progress allocation across categories that are not blocked.
     * @param categories Set of all category strings in the data.
     * @param blockedCategories Set of blocked categories.
     * @return AllocationResult containing points allocated, whether to exit early, and the final return value.
     */
    public static AllocationResult calculateProgressAllocation(Set<String> categories, Set<String> blockedCategories) {
        Map<String, Integer> allocations = new HashMap<>();

        int a = 25;
        int tries = 0;
        boolean earlyExit = false;

        while (a > 0 && tries < 1000) {
            tries++;
            for (String category : categories) {
                if (blockedCategories.contains(category)) {
                    if (categories.size() <= 1) {
                        earlyExit = true;
                        return new AllocationResult(allocations, earlyExit, false);
                    }
                    continue;
                }
                allocations.put(category, allocations.getOrDefault(category, 0) + 1);
                a--;
                if (a <= 0) break;
            }
        }

        allocations.put("BASICS", allocations.getOrDefault("BASICS", 0) + 5);
        return new AllocationResult(allocations, earlyExit, a != 20);
    }

    /**
     * Calculates the weighting of categories based on recent player interactions.
     * @param recentInteractions Map of category strings to number of interactions.
     * @return Map of category strings to proportional weights (0.0 to 1.0).
     */
    public static Map<String, Double> calculateCategoryWeighting(Map<String, Integer> recentInteractions) {
        Map<String, Double> weights = new HashMap<>();
        if (recentInteractions == null || recentInteractions.isEmpty()) {
            return weights;
        }

        int totalInteractions = 0;
        for (Integer count : recentInteractions.values()) {
            if (count > 0) {
                totalInteractions += count;
            }
        }

        if (totalInteractions == 0) {
            for (String category : recentInteractions.keySet()) {
                weights.put(category, 0.0);
            }
            return weights;
        }

        for (Map.Entry<String, Integer> entry : recentInteractions.entrySet()) {
            int count = entry.getValue();
            if (count > 0) {
                weights.put(entry.getKey(), (double) count / totalInteractions);
            } else {
                weights.put(entry.getKey(), 0.0);
            }
        }

        return weights;
    }

    /**
     * Verifies if the current inspiration is enough to cover the cost.
     * @param currentInspiration The player's current inspiration.
     * @return true if there is enough inspiration.
     */
    public static boolean verifyInspirationCost(int currentInspiration) {
        return currentInspiration >= INSPIRATION_COST;
    }
}
