package thaumcraft.common.lib.research.theorycraft.logic;

import java.util.LinkedHashMap;
import java.util.Map;

public class CardRethinkLogic {
    public static class RethinkResult {
        public Map<String, Integer> updatedTotals;
        public int bonusDraws;
        public int basicsAdded;
        public int refundedInspiration;

        public RethinkResult(Map<String, Integer> updatedTotals, int bonusDraws, int basicsAdded, int refundedInspiration) {
            this.updatedTotals = updatedTotals;
            this.bonusDraws = bonusDraws;
            this.basicsAdded = basicsAdded;
            this.refundedInspiration = refundedInspiration;
        }
    }

    public static boolean checkInitialization(Map<String, Integer> categoryTotals) {
        int a = 0;
        for (Integer total : categoryTotals.values()) {
            a += total;
        }
        return a >= 10;
    }

    public static RethinkResult calculateRethink(Map<String, Integer> currentTotals, int currentBonusDraws, int randomBasicsValue, int currentInspirationStart, int currentInspiration) {
        if (!checkInitialization(currentTotals)) {
            return null;
        }

        int a = 0;
        for (Integer total : currentTotals.values()) {
            a += total;
        }
        a = Math.min(a, 10);

        Map<String, Integer> newTotals = new LinkedHashMap<>(currentTotals);

        int tries = 0;
        while (a > 0 && tries < 1000) {
            tries++;

            boolean anyPositive = false;
            for (String category : newTotals.keySet()) {
                int currentVal = newTotals.get(category);
                if (currentVal > 0) {
                    newTotals.put(category, currentVal - 1);
                    a--;
                    anyPositive = true;
                }
                if (a <= 0) {
                    break;
                }
            }
            if (!anyPositive) {
                break;
            }
        }

        // Remove empty keys to mimic old logic
        newTotals.entrySet().removeIf(entry -> entry.getValue() <= 0);

        int newBonusDraws = currentBonusDraws + 1;

        int basicsAdded = randomBasicsValue;
        newTotals.put("BASICS", newTotals.getOrDefault("BASICS", 0) + basicsAdded);

        // Refund 50% inspiration
        int usedInspiration = currentInspirationStart - currentInspiration;
        int refund = usedInspiration / 2;

        return new RethinkResult(newTotals, newBonusDraws, basicsAdded, refund);
    }
}
