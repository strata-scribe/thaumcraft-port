package thaumcraft.common.items.consumables.logic;

public final class AlumentumLogic {

    private AlumentumLogic() {}

    public static int getBurnTime() {
        return 6400; // 32 items
    }

    public static float getSmelterSpeedMultiplier() {
        return 0.8f;
    }

    public static float getExplosionRadius() {
        return 1.5f;
    }

    public static float calculateImpactDamage(float velocity) {
        return Math.max(0.0f, Math.min(20.0f, velocity * 4.0f));
    }

    public static boolean shouldIgniteTarget(float roll) {
        return roll < 0.75f;
    }
}
