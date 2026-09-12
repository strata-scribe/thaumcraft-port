package thaumcraft.common.lib.capabilities;

public class RunicRingLogic {

    /**
     * Calculates the total bonus runic shielding points from two equipped rings.
     * Each ring provides its tier in shielding.
     * If both rings are worn (tier > 0), a synergy bonus of 1 is added.
     *
     * @param ring1Tier The tier of the first ring (0 if not equipped/no tier).
     * @param ring2Tier The tier of the second ring (0 if not equipped/no tier).
     * @return The total bonus runic shielding.
     */
    public static int calculateBonusShielding(int ring1Tier, int ring2Tier) {
        int bonus = 0;

        if (ring1Tier > 0) {
            bonus += ring1Tier;
        }

        if (ring2Tier > 0) {
            bonus += ring2Tier;
        }

        if (ring1Tier > 0 && ring2Tier > 0) {
            bonus += 1; // Synergy bonus
        }

        return bonus;
    }
}
