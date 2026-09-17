package thaumcraft.common.lib.research.theorycraft.logic;

import java.util.Map;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

public class CardBalanceLogic {

    public static boolean canInitialize(Map<String, Integer> categoryTotals, Collection<String> categoriesBlocked) {
        int total = 0;
        int size = 0;
        for (String c : categoryTotals.keySet()) {
            if (categoriesBlocked.contains(c)) continue;
            total += categoryTotals.get(c);
            size++;
        }
        return categoriesBlocked.size() < categoryTotals.size() - 1 && total >= size;
    }

    public static boolean calculateBalancedTotals(Map<String, Integer> categoryTotals, Collection<String> categoriesBlocked) {
        if (!canInitialize(categoryTotals, categoriesBlocked)) {
            return false;
        }

        List<String> unblockedCategories = new ArrayList<>();
        for (String c : categoryTotals.keySet()) {
            if (!categoriesBlocked.contains(c)) {
                unblockedCategories.add(c);
            }
        }

        if (unblockedCategories.isEmpty()) return false;

        boolean changed = true;
        while (changed) {
            changed = false;
            String minCat = null;
            String maxCat = null;
            int minVal = Integer.MAX_VALUE;
            int maxVal = Integer.MIN_VALUE;

            for (String cat : unblockedCategories) {
                int val = categoryTotals.get(cat);
                if (val < minVal) {
                    minVal = val;
                    minCat = cat;
                }
                if (val > maxVal) {
                    maxVal = val;
                    maxCat = cat;
                }
            }

            if (maxCat != null && minCat != null && (maxVal - minVal) > 1) {
                categoryTotals.put(maxCat, maxVal - 1);
                categoryTotals.put(minCat, minVal + 1);
                changed = true;
            }
        }

        return true;
    }
}
