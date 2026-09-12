package thaumcraft.common.golems.logic;

/**
 * Pure Java logic helper for Golem Combat Combo scaling.
 * Strict mathematical decoupling: contains zero Minecraft or NeoForge classloader imports.
 */
public class GolemCombatComboLogic {

    /**
     * Computes consecutive strike damage scaling.
     * Damage scales by +20% per combo count.
     *
     * @param baseDamage The base damage of the strike.
     * @param comboCount The number of consecutive strikes.
     * @return The scaled damage amount.
     */
    public static double calculateComboDamage(double baseDamage, int comboCount) {
        if (comboCount < 0) {
            comboCount = 0;
        }
        return baseDamage * (1.0 + (comboCount * 0.2));
    }

    /**
     * Computes blunt force stun probabilities.
     * Base stun chance increases by +5% per combo count.
     * Blunt modifier adds +20% flat.
     *
     * @param baseStunChance The base chance to stun (0.0 to 1.0).
     * @param comboCount The number of consecutive strikes.
     * @param hasBluntModifier Whether the strike has a blunt modifier.
     * @return The probability to stun, clamped between 0.0 and 1.0.
     */
    public static double calculateStunProbability(double baseStunChance, int comboCount, boolean hasBluntModifier) {
        if (comboCount < 0) {
            comboCount = 0;
        }
        double probability = baseStunChance + (comboCount * 0.05);
        if (hasBluntModifier) {
            probability += 0.2;
        }
        return Math.max(0.0, Math.min(1.0, probability));
    }
}
