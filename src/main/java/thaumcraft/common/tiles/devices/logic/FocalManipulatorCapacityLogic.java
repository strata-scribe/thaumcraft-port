package thaumcraft.common.tiles.devices.logic;

public class FocalManipulatorCapacityLogic {

    /**
     * Calculates the maximum complexity budget for spell creation.
     *
     * @param manipulatorTier The tier of the focal manipulator (e.g., 1, 2, 3).
     * @param casterExperienceLevel The experience level of the caster.
     * @return The maximum complexity budget.
     */
    public static int calculateComplexityBudget(int manipulatorTier, int casterExperienceLevel) {
        if (manipulatorTier <= 0) {
            return 0;
        }

        int baseComplexity = 15 * manipulatorTier;
        int maxBonus = 10 * manipulatorTier;
        int expBonus = Math.min(casterExperienceLevel, maxBonus);

        return baseComplexity + expBonus;
    }
}
