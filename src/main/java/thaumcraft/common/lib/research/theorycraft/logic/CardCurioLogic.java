package thaumcraft.common.lib.research.theorycraft.logic;

import java.util.Random;

public class CardCurioLogic {
    public static class ProgressionReward {
        public final int categoryPoints;
        public final String category;
        public final int secondaryPoints;
        public final String secondaryCategory;
        public final int basicsBonus;

        public ProgressionReward(int categoryPoints, String category, int secondaryPoints, String secondaryCategory, int basicsBonus) {
            this.categoryPoints = categoryPoints;
            this.category = category;
            this.secondaryPoints = secondaryPoints;
            this.secondaryCategory = secondaryCategory;
            this.basicsBonus = basicsBonus;
        }
    }

    public static ProgressionReward calculateProgressionRewards(long seed, String curioType) {
        Random random = new Random(seed);
        int points = 0;
        String category = "";
        int secondaryPoints = 0;
        String secondaryCategory = null;

        switch (curioType != null ? curioType : "") {
            case "arcane":
                category = "AUROMANCY";
                points = 25 + random.nextInt(11); // 25-35
                break;
            case "preserved":
                category = "ALCHEMY";
                points = 25 + random.nextInt(11); // 25-35
                break;
            case "ancient":
                category = "GOLEMANCY";
                points = 25 + random.nextInt(11); // 25-35
                break;
            case "eldritch":
                category = "ELDRITCH";
                points = 25 + random.nextInt(11); // 25-35
                break;
            case "knowledge":
                category = "INFUSION";
                points = 25 + random.nextInt(11); // 25-35
                break;
            case "twisted":
                category = "ARTIFICE";
                points = 25 + random.nextInt(11); // 25-35
                break;
            case "rites":
                category = "ELDRITCH";
                points = 15 + random.nextInt(6); // 15-20
                secondaryCategory = "AUROMANCY";
                secondaryPoints = 10 + random.nextInt(6); // 10-15
                break;
            default:
                category = "BASICS";
                points = 25 + random.nextInt(11); // 25-35
                break;
        }

        return new ProgressionReward(points, category, secondaryPoints, secondaryCategory, 5);
    }
}
