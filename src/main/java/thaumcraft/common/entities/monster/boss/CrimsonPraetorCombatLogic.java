package thaumcraft.common.entities.monster.boss;

public class CrimsonPraetorCombatLogic {

    /**
     * Calculates the current boss phase based on health.
     * Phase 1: > 60% health
     * Phase 2: > 30% health, <= 60% health
     * Phase 3: <= 30% health
     */
    public static int calculatePhase(float currentHealth, float maxHealth) {
        if (maxHealth <= 0) return 1;
        float ratio = currentHealth / maxHealth;
        if (ratio > 0.6f) return 1;
        if (ratio > 0.3f) return 2;
        return 3;
    }

    /**
     * Calculates the radius of the whirlwind attack based on the current phase.
     */
    public static float getWhirlwindRadius(int phase) {
        if (phase >= 3) return 6.0f;
        if (phase == 2) return 4.5f;
        return 3.0f;
    }

    /**
     * Calculates the damage of the whirlwind attack.
     */
    public static float calculateWhirlwindDamage(int phase, float baseDamage) {
        if (phase >= 3) return baseDamage * 1.5f;
        if (phase == 2) return baseDamage * 1.25f;
        return baseDamage;
    }

    /**
     * Calculates the knockback strength of the whirlwind attack.
     */
    public static float getWhirlwindKnockback(int phase) {
        if (phase >= 3) return 2.0f;
        if (phase == 2) return 1.5f;
        return 1.0f;
    }

    /**
     * Checks if a target is within the whirlwind attack range.
     */
    public static boolean isTargetInWhirlwindRange(double dx, double dy, double dz, float radius) {
        double distSq = dx * dx + dy * dy + dz * dz;
        return distSq <= radius * radius;
    }

    /**
     * Checks if a cultist is within the 24-block range for the battle roar.
     */
    public static boolean isCultistInRangeForRoar(double dx, double dy, double dz) {
        double distSq = dx * dx + dy * dy + dz * dz;
        return distSq <= 24.0 * 24.0;
    }

    /**
     * Gets the amplifier for the Strength buff granted by battle roar.
     * 1 means Strength II.
     */
    public static int getRoarStrengthAmplifier() {
        return 1;
    }

    /**
     * Gets the amplifier for the Speed buff granted by battle roar.
     * 0 means Speed I.
     */
    public static int getRoarSpeedAmplifier() {
        return 0;
    }
}
