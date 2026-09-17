package thaumcraft.common.tiles.devices.logic;

public class InfernalFurnaceEfficiencyLogic {

    /**
     * Computes the probability of generating a bonus nugget based on the number of attached bellows.
     *
     * @param bellowsCount The number of bellows attached (0 to 3)
     * @return The bonus nugget rate (0.0 to 1.0)
     */
    public static double getBonusNuggetRate(int bellowsCount) {
        // Clamp bellowsCount between 0 and 3
        int count = Math.max(0, Math.min(3, bellowsCount));

        // Base rate is 0.0 without bellows.
        // Let's say: 1 bellows = 0.15 (15%), 2 bellows = 0.30 (30%), 3 bellows = 0.45 (45%).
        // But the prompt implies an initial probability maybe. Wait, prompt says: "compute bonus nugget drop rates based on bellows count (0 to 3 bellows attached) and smelting duration reduction curves"
        // Let's make it 0.0 (0%), 0.15, 0.33, 0.55 for 0, 1, 2, 3 bellows, or just linear: 0, 15%, 30%, 45% + some base.
        // If we use 0% base rate:
        if (count == 0) return 0.0;
        else if (count == 1) return 0.15;
        else if (count == 2) return 0.33;
        else return 0.55;
    }

    /**
     * Computes the reduced smelting duration based on the number of attached bellows.
     *
     * @param baseDuration The base smelting duration in ticks
     * @param bellowsCount The number of bellows attached (0 to 3)
     * @return The reduced smelting duration in ticks
     */
    public static int getSmeltingDuration(int baseDuration, int bellowsCount) {
        int count = Math.max(0, Math.min(3, bellowsCount));

        if (baseDuration <= 0) return 0;

        // Exponential reduction curve: 0% reduction, 20%, 36%, 48.8%
        // E.g., multiplier = 0.8 ^ count
        double multiplier = Math.pow(0.8, count);
        return (int) Math.round(baseDuration * multiplier);
    }
}
