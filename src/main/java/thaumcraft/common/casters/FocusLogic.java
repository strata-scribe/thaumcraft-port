package thaumcraft.common.casters;

import java.util.List;
import java.util.Locale;

/**
 * Pure Java decoupled logic engine for Thaumcraft Spell Foci and Auromancy.
 * Contains mathematical formulas for vis costs, activation cooldowns,
 * medium complexities, effect calculations, modifier behaviors, trajectory dispersion,
 * and node chain validation.
 *
 * Guaranteed ZERO imports from net.minecraft or net.neoforged for pure JUnit 5 test isolation.
 */
public final class FocusLogic {

    private FocusLogic() {}

    // =========================================================================
    // 1. Packaging & Economics
    // =========================================================================

    /**
     * Calculates the Vis cost of a focus package.
     * Formula: (complexity <= 0) ? 0.0f : (complexity / 5.0f) * consumptionModifier
     */
    public static float calculateVisCost(int complexity, float consumptionModifier) {
        if (complexity <= 0) {
            return 0.0f;
        }
        return (complexity / 5.0f) * Math.max(0.0f, consumptionModifier);
    }

    /**
     * Overload for standard 1.0f consumption modifier.
     */
    public static float calculateVisCost(int complexity) {
        return calculateVisCost(complexity, 1.0f);
    }

    /**
     * Calculates activation cooldown ticks for a focus package.
     * Formula: Math.max(5, (complexity / 5) * (complexity / 4))
     */
    public static int calculateActivationTime(int complexity) {
        if (complexity <= 0) {
            return 5;
        }
        return Math.max(5, (complexity / 5) * (complexity / 4));
    }

    /**
     * Alias for calculateActivationTime to satisfy CasterGauntlet contract.
     */
    public static int calculateCooldownTicks(int complexity) {
        return calculateActivationTime(complexity);
    }

    /**
     * Aggregates total complexity of a root/parent node and its children/subpackages.
     */
    public static int calculateTotalComplexity(int baseNodeComplexity, List<Integer> childComplexities) {
        int total = Math.max(0, baseNodeComplexity);
        if (childComplexities != null) {
            for (Integer c : childComplexities) {
                if (c != null && c > 0) {
                    total += c;
                }
            }
        }
        return total;
    }

    /**
     * Blends RGB color components into an arithmetic average color.
     * Returns 0xFFFFFF (white) if list is null or empty.
     */
    public static int blendEffectColors(List<Integer> rgbColors) {
        if (rgbColors == null || rgbColors.isEmpty()) {
            return 0xFFFFFF;
        }
        long r = 0;
        long g = 0;
        long b = 0;
        int count = 0;
        for (Integer color : rgbColors) {
            if (color != null) {
                r += (color >> 16) & 0xFF;
                g += (color >> 8) & 0xFF;
                b += color & 0xFF;
                count++;
            }
        }
        if (count == 0) {
            return 0xFFFFFF;
        }
        int avgR = (int) (r / count);
        int avgG = (int) (g / count);
        int avgB = (int) (b / count);
        return (avgR << 16) | (avgG << 8) | avgB;
    }

    // =========================================================================
    // 2. Medium Complexities & Mechanics
    // =========================================================================

    public static int calculateTouchComplexity() {
        return 2;
    }

    public static int calculateBoltComplexity() {
        return 5;
    }

    /**
     * Projectile complexity formula:
     * Base: 4 + (speed - 1) / 2
     * Options: 0=None (+0), 1=Bouncy (+3), 2=Seeking Hostile (+5), 3=Seeking Friendly (+5)
     */
    public static int calculateProjectileComplexity(int speed, int option) {
        int clampedSpeed = Math.max(1, speed);
        int c = 4 + (clampedSpeed - 1) / 2;
        switch (option) {
            case 1 -> c += 3;
            case 2, 3 -> c += 5;
            default -> {}
        }
        return c;
    }

    /**
     * Projectile speed calculation: speedSetting / 3.0f
     */
    public static float calculateProjectileSpeed(int speedSetting) {
        return Math.max(1, speedSetting) / 3.0f;
    }

    public static int calculatePlanarComplexity() {
        return 4;
    }

    public static int calculateMineComplexity() {
        return 4;
    }

    /**
     * Scatter modifier complexity formula:
     * Math.max(2, (int)(2.0f * (forks - coneDegrees / 45.0f)))
     */
    public static int calculateScatterComplexity(int forks, int coneDegrees) {
        return (int) Math.max(2.0f, 2.0f * (forks - coneDegrees / 45.0f));
    }

    /**
     * Scatter power multiplier formula:
     * 1.0f / (forks / 2.0f) = 2.0f / forks
     */
    public static float calculateScatterPowerMultiplier(int forks) {
        if (forks <= 0) {
            return 1.0f;
        }
        return 1.0f / (forks / 2.0f);
    }

    public static int calculateSplitTargetComplexity() {
        return 4;
    }

    public static int calculateSplitTrajectoryComplexity() {
        return 5;
    }

    public static float calculateSplitPowerMultiplier() {
        return 0.75f;
    }

    // =========================================================================
    // 3. Effect Formulas
    // =========================================================================

    // --- Fire ---
    public static int calculateFireComplexity(int power, int duration) {
        return Math.max(0, duration) + Math.max(0, power) * 2;
    }

    public static float calculateFireDamage(int power, float finalPower) {
        return (3.0f + Math.max(0, power)) * Math.max(0.0f, finalPower);
    }

    public static float calculateFireBurnDuration(int duration, float finalPower) {
        int dur = Math.max(0, duration);
        return (1.0f + (float) (dur * dur)) * Math.max(0.0f, finalPower);
    }

    // --- Frost ---
    public static int calculateFrostComplexity(int power, int duration) {
        return Math.max(0, duration) + Math.max(0, power) * 2;
    }

    public static float calculateFrostDamage(int power, float finalPower) {
        return (3.0f + Math.max(0, power)) * Math.max(0.0f, finalPower);
    }

    public static int calculateFrostSlownessDuration(int duration) {
        return 20 * Math.max(0, duration);
    }

    public static int calculateFrostSlownessPotency(int power, float finalPower) {
        return (int) (1.0f + Math.max(0, power) * Math.max(0.0f, finalPower) / 3.0f);
    }

    public static float calculateFrostFreezeRadius(int power, float finalPower) {
        return Math.min(16.0f, 2.0f * Math.max(0, power) * Math.max(0.0f, finalPower));
    }

    public static float calculateFrostedIceRadius(int power, float finalPower) {
        return calculateFrostFreezeRadius(power, finalPower);
    }

    // --- Earth ---
    public static int calculateEarthComplexity(int power) {
        return Math.max(0, power) * 3;
    }

    public static float calculateEarthDamage(int power, float finalPower) {
        return 2.0f * Math.max(0, power) * Math.max(0.0f, finalPower);
    }

    public static float calculateEarthMaxBreakHardness(int power, float finalPower) {
        return calculateEarthDamage(power, finalPower) / 25.0f;
    }

    // --- Air ---
    public static int calculateAirComplexity(int power) {
        return Math.max(0, power) * 2;
    }

    public static float calculateAirDamage(int power, float finalPower) {
        return (1.0f + Math.max(0, power)) * Math.max(0.0f, finalPower);
    }

    public static float calculateAirKnockback(int power, float finalPower) {
        return calculateAirDamage(power, finalPower) * 0.25f;
    }

    // --- Curse ---
    public static int calculateCurseComplexity(int power, int duration) {
        return Math.max(0, duration) + Math.max(0, power) * 3;
    }

    public static float calculateCurseDamage(int power, float finalPower) {
        return (1.0f + Math.max(0, power)) * Math.max(0.0f, finalPower);
    }

    public static int calculateCurseDebuffDuration(int duration) {
        return 20 * Math.max(0, duration);
    }

    public static int calculateCurseDebuffPotency(int power, float finalPower) {
        return Math.max(0, (int) (Math.max(0, power) * Math.max(0.0f, finalPower) / 2.0f));
    }

    public static float calculateCurseSapRadius(int power, float finalPower) {
        return (float) Math.min(8.0, 1.5 * Math.max(0, power) * Math.max(0.0f, finalPower));
    }

    // --- Flux ---
    public static int calculateFluxComplexity(int power) {
        return Math.max(0, power) * 3;
    }

    public static float calculateFluxDamage(int power, float finalPower) {
        return (3.0f + Math.max(0, power)) * Math.max(0.0f, finalPower);
    }

    // --- Heal ---
    public static int calculateHealComplexity(int power) {
        return Math.max(0, power) * 4;
    }

    public static float calculateHealAmount(int power, float finalPower) {
        return Math.max(0, power) * Math.max(0.0f, finalPower);
    }

    public static float calculateHealUndeadDamage(int power, float finalPower) {
        return Math.max(0, power) * Math.max(0.0f, finalPower) * 1.5f;
    }

    // --- Break ---
    public static int calculateBreakComplexity(int power, int silk, int fortune) {
        int c = Math.max(0, power) * 3 + (silk > 0 ? 4 : 0);
        if (fortune > 0) {
            c += (fortune + 1) * 3;
        }
        return c;
    }

    public static float calculateBreakDurability(float blockHardness) {
        return (float) Math.sqrt(Math.max(0.0f, blockHardness) * 100.0);
    }

    public static int calculateBreakDelayTicks(float durability, float strength, int index) {
        if (strength <= 0.0f) {
            return 0;
        }
        return (int) (durability / strength / 3.0f * Math.max(0, index));
    }

    public static int calculateBreakDelay(float hardness, float strength, int num) {
        return calculateBreakDelayTicks(calculateBreakDurability(hardness), strength, num);
    }

    public static float calculateBreakVisFactor(int fortune, boolean silk) {
        return 0.25f + (silk ? 0.25f : 0.0f) + Math.max(0, fortune) * 0.1f;
    }

    // --- Generic Contract Effect Damage ---
    public static float calculateEffectDamage(String effectType, int power, float finalPower) {
        if (effectType == null) {
            return 0.0f;
        }
        return switch (effectType.toUpperCase(Locale.ROOT)) {
            case "FIRE" -> calculateFireDamage(power, finalPower);
            case "FROST" -> calculateFrostDamage(power, finalPower);
            case "EARTH" -> calculateEarthDamage(power, finalPower);
            case "AIR" -> calculateAirDamage(power, finalPower);
            case "CURSE" -> calculateCurseDamage(power, finalPower);
            case "FLUX" -> calculateFluxDamage(power, finalPower);
            case "HEAL" -> calculateHealAmount(power, finalPower);
            case "HEAL_UNDEAD" -> calculateHealUndeadDamage(power, finalPower);
            default -> 0.0f;
        };
    }

    // =========================================================================
    // 4. Trajectory Dispersion Math
    // =========================================================================

    /**
     * Calculates a jittered dispersion vector for scatter modifiers.
     * Normalized parent direction is perturbed by gaussian jitter proportional to coneDegrees.
     */
    public static double[] calculateScatterVector(double dirX, double dirY, double dirZ,
                                                  double randX, double randY, double randZ, int coneDegrees) {
        double len = Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
        if (len > 1.0E-6) {
            dirX /= len;
            dirY /= len;
            dirZ /= len;
        }
        double factor = 0.0075 * coneDegrees;
        double newX = dirX + randX * factor;
        double newY = dirY + randY * factor;
        double newZ = dirZ + randZ * factor;
        double newLen = Math.sqrt(newX * newX + newY * newY + newZ * newZ);
        if (newLen > 1.0E-6) {
            newX /= newLen;
            newY /= newLen;
            newZ /= newLen;
        }
        return new double[] { newX, newY, newZ };
    }

    // =========================================================================
    // 5. Node Hierarchy Validation
    // =========================================================================

    public record NodeInfo(String key, String supplyRequired, String supplyProduced) {}

    /**
     * Validates that each non-root node in the chain has its required supply satisfied
     * by the output of the preceding node in the chain.
     */
    public static boolean validateNodeChain(List<NodeInfo> chain) {
        if (chain == null || chain.isEmpty()) {
            return false;
        }
        String currentSupply = null;
        for (int i = 0; i < chain.size(); i++) {
            NodeInfo node = chain.get(i);
            if (node == null) {
                return false;
            }
            if (i == 0) {
                currentSupply = node.supplyProduced();
            } else {
                String req = node.supplyRequired();
                if (req != null && !req.isEmpty()) {
                    if (currentSupply == null || !supplyMatches(currentSupply, req)) {
                        return false;
                    }
                }
                if (node.supplyProduced() != null) {
                    currentSupply = node.supplyProduced();
                }
            }
        }
        return true;
    }

    private static boolean supplyMatches(String available, String required) {
        if (available.equalsIgnoreCase(required)) {
            return true;
        }
        String[] parts = available.split("[,; ]+");
        for (String p : parts) {
            if (p.equalsIgnoreCase(required)) {
                return true;
            }
        }
        return false;
    }
}
