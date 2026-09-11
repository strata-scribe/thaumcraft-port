package thaumcraft.common.casters;

import java.util.List;

/**
 * Pure Java decoupled logic engine for Caster Gauntlet cooldowns and vis discounts.
 *
 * Guaranteed ZERO imports from net.minecraft or net.neoforged for pure JUnit 5 test isolation.
 */
public final class CasterGauntletLogic {

    private CasterGauntletLogic() {}

    /**
     * Calculates casting cooldown ticks based on focus complexity.
     * Formula: Math.max(5, (complexity / 5) * (complexity / 4))
     */
    public static int calculateCooldownTicks(int complexity) {
        if (complexity <= 0) {
            return 5;
        }
        return Math.max(5, (complexity / 5) * (complexity / 4));
    }

    /**
     * Evaluates total vis discount provided by worn robes, goggles, and baubles.
     * Capped at 50% discount.
     *
     * @param pieceDiscounts List of vis discounts provided by individual worn gear pieces.
     * @return Total aggregated vis discount bounded between 0% and 50%.
     */
    public static int calculateTotalVisDiscount(List<Integer> pieceDiscounts) {
        if (pieceDiscounts == null || pieceDiscounts.isEmpty()) {
            return 0;
        }
        int total = 0;
        for (Integer discount : pieceDiscounts) {
            if (discount != null && discount > 0) {
                total += discount;
            }
        }
        return Math.min(50, total);
    }

    /**
     * Converts a discount percentage (0 to 100) into a consumption modifier (1.0 to 0.0).
     * For example, a 15% discount yields a 0.85f consumption modifier.
     */
    public static float calculateConsumptionModifier(int discountPercentage) {
        int boundedDiscount = Math.max(0, Math.min(50, discountPercentage));
        return 1.0f - (boundedDiscount / 100.0f);
    }
}
