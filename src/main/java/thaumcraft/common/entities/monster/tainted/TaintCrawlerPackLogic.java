package thaumcraft.common.entities.monster.tainted;

public final class TaintCrawlerPackLogic {

    private TaintCrawlerPackLogic() {
    }

    /**
     * Calculates the movement speed multiplier based on the number of nearby companion crawlers.
     * Each companion adds a 10% speed boost, up to a maximum of 50% (5 companions).
     *
     * @param companionCount The number of nearby companion crawlers.
     * @return The speed multiplier (1.0 to 1.5).
     */
    public static float calculateSpeedMultiplier(int companionCount) {
        if (companionCount <= 0) return 1.0f;
        float bonus = 0.10f * companionCount;
        return 1.0f + Math.min(bonus, 0.50f);
    }

    /**
     * Calculates the damage multiplier based on the number of nearby companion crawlers.
     * Each companion adds a 20% damage boost, up to a maximum of 100% (5 companions).
     *
     * @param companionCount The number of nearby companion crawlers.
     * @return The damage multiplier (1.0 to 2.0).
     */
    public static float calculateDamageMultiplier(int companionCount) {
        if (companionCount <= 0) return 1.0f;
        float bonus = 0.20f * companionCount;
        return 1.0f + Math.min(bonus, 1.0f);
    }
}
