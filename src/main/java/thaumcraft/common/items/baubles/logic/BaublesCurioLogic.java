package thaumcraft.common.items.baubles.logic;

public class BaublesCurioLogic {

    public static final int VIS_AMULET_MAX_CHARGE = 250;
    public static final int VOIDSEER_WARP_BONUS = 1;
    public static final int UNDYING_REGEN_TICKS = 900;
    public static final int UNDYING_ABSORPTION_TICKS = 200;

    /**
     * Calculates the maximum charge for the Vis Amulet.
     *
     * @return 250
     */
    public static int calculateVisAmuletMaxCharge() {
        return VIS_AMULET_MAX_CHARGE;
    }

    /**
     * Calculates the amount of charge transferred from an amulet to a target item.
     *
     * @param currentAmuletCharge Current charge in the amulet.
     * @param itemNeededCharge    Amount of charge the target item needs.
     * @param transferRate        Maximum transfer rate per tick/cycle.
     * @return Amount of charge transferred.
     */
    public static int calculateRechargeTransfer(int currentAmuletCharge, int itemNeededCharge, int transferRate) {
        if (currentAmuletCharge <= 0 || itemNeededCharge <= 0 || transferRate <= 0) {
            return 0;
        }
        return Math.min(transferRate, Math.min(currentAmuletCharge, itemNeededCharge));
    }

    /**
     * Calculates the bonus experience granted by the Band of Curiosity.
     *
     * @param xp          Original base experience amount.
     * @param bonusFactor Fractional bonus factor (e.g. 0.25 for +25%).
     * @return Augmented experience amount.
     */
    public static int calculateCuriosityBonusExp(int xp, float bonusFactor) {
        if (xp <= 0) {
            return 0;
        }
        return Math.max(xp, Math.round(xp * (1.0f + Math.max(0.0f, bonusFactor))));
    }

    /**
     * Determines whether research/knowledge should be granted by the Band of Curiosity.
     *
     * @param roll   A random float value in [0.0, 1.0).
     * @param chance Probability threshold.
     * @return true if roll is strictly less than chance.
     */
    public static boolean shouldGrantCuriosityKnowledge(float roll, float chance) {
        return roll < chance;
    }

    /**
     * Determines whether incoming damage would be fatal and should be prevented by the Charm of Undying.
     *
     * @param currentHealth  Entity current health.
     * @param incomingDamage Incoming damage amount.
     * @return true if remaining health would be <= 0.
     */
    public static boolean shouldPreventFatalDamage(float currentHealth, float incomingDamage) {
        return (currentHealth - incomingDamage) <= 0.0f;
    }

    /**
     * Calculates revive health for the Charm of Undying (10% of max health, minimum 1.0f).
     *
     * @param maxHealth Entity max health.
     * @return Health upon revive.
     */
    public static float calculateUndyingReviveHealth(float maxHealth) {
        if (maxHealth <= 0.0f) {
            return 1.0f;
        }
        return Math.max(1.0f, maxHealth * 0.1f);
    }

    /**
     * Returns the warp bonus granted by the Voidseer Charm.
     *
     * @return 1
     */
    public static int getVoidseerWarpBonus() {
        return VOIDSEER_WARP_BONUS;
    }

    /**
     * Returns the duration in ticks for Regeneration effect upon undying revive.
     *
     * @return 900
     */
    public static int getUndyingRegenDurationTicks() {
        return UNDYING_REGEN_TICKS;
    }

    /**
     * Returns the duration in ticks for Absorption effect upon undying revive.
     *
     * @return 200
     */
    public static int getUndyingAbsorptionDurationTicks() {
        return UNDYING_ABSORPTION_TICKS;
    }
}
