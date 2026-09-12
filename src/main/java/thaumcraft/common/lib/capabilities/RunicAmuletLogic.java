package thaumcraft.common.lib.capabilities;

public class RunicAmuletLogic {

    public static class BurstResult {
        public final float radius;
        public final float knockback;

        public BurstResult(float radius, float knockback) {
            this.radius = radius;
            this.knockback = knockback;
        }
    }

    public static BurstResult calculateOverloadBurst(int maxShield) {
        float radius = 2.0f + (maxShield * 0.5f);
        float knockback = 0.5f + (maxShield * 0.1f);
        return new BurstResult(radius, knockback);
    }
}
