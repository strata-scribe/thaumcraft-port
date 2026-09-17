package thaumcraft.common.lib.research.theorycraft.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CardAnalyzeLogic {

    public static int getRequiredObservations() {
        return 1;
    }

    public static boolean hasRequiredObservations(int playerObservations) {
        return playerObservations >= 1;
    }

    public static List<String> filterCategories(List<String> categories) {
        List<String> filtered = new ArrayList<>();
        if (categories != null) {
            for (String category : categories) {
                if (!"BASICS".equals(category)) {
                    filtered.add(category);
                }
            }
        }
        return filtered;
    }

    public static String selectRandomCategory(long seed, List<String> categories) {
        if (categories == null || categories.isEmpty()) {
            return null;
        }
        Random random = new Random(seed);
        int index = random.nextInt(categories.size());
        return categories.get(index);
    }

    public static class ProgressionReward {
        public final int mainCategoryPoints;
        public final int basicsBonus;

        public ProgressionReward(int mainCategoryPoints, int basicsBonus) {
            this.mainCategoryPoints = mainCategoryPoints;
            this.basicsBonus = basicsBonus;
        }
    }

    public static ProgressionReward calculateProgressionRewards(long seed) {
        Random random = new Random(seed);
        int mainCategoryPoints = 25 + random.nextInt(26); // 25 to 50 inclusive
        int basicsBonus = 5;
        return new ProgressionReward(mainCategoryPoints, basicsBonus);
    }
}
