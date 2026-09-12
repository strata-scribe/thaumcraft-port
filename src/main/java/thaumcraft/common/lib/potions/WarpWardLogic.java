package thaumcraft.common.lib.potions;

public class WarpWardLogic {

    /**
     * Calculates the probability (0.0 to 1.0) that a warp event is mitigated
     * based on the amplifier of the Warp Ward potion effect.
     *
     * @param warpWardAmplifier The amplifier level of the potion effect (0 = level 1).
     * @return The mitigation chance as a decimal.
     */
    public static double calculateWarpEventMitigationPercentage(int warpWardAmplifier) {
        if (warpWardAmplifier < 0) {
            return 0.0;
        }

        // Base mitigation is 50% for amplifier 0
        // Increases by 25% per additional amplifier level
        double chance = 0.5 + (warpWardAmplifier * 0.25);

        return Math.min(chance, 1.0);
    }

    /**
     * Calculates the remaining duration of the warp ward effect after a given
     * number of ticks have elapsed. Ensures duration doesn't drop below 0.
     *
     * @param initialDuration The initial duration in ticks.
     * @param ticksElapsed The number of ticks that have elapsed.
     * @return The remaining duration in ticks.
     */
    public static int calculateWarpWardDecay(int initialDuration, int ticksElapsed) {
        if (initialDuration < 0) {
            return 0;
        }
        if (ticksElapsed < 0) {
            return initialDuration;
        }

        int remaining = initialDuration - ticksElapsed;
        return Math.max(remaining, 0);
    }
}
