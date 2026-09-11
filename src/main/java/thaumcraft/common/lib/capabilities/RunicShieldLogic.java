package thaumcraft.common.lib.capabilities;

public class RunicShieldLogic {

    public static final int RECHARGE_DELAY = 40;
    public static final float VIS_COST = 1.0f;

    public static class DamageResult {
        public final int newShield;
        public final float remainingDamage;

        public DamageResult(int newShield, float remainingDamage) {
            this.newShield = newShield;
            this.remainingDamage = remainingDamage;
        }
    }

    public static DamageResult calculateDamageRemaining(int currentShield, float damage) {
        if (currentShield >= damage) {
            return new DamageResult(currentShield - (int) Math.ceil(damage), 0.0f);
        } else {
            return new DamageResult(0, damage - currentShield);
        }
    }

    public static class TickResult {
        public final int newRechargeDelay;
        public final boolean wantsToRecharge;

        public TickResult(int newRechargeDelay, boolean wantsToRecharge) {
            this.newRechargeDelay = newRechargeDelay;
            this.wantsToRecharge = wantsToRecharge;
        }
    }

    public static TickResult processTick(int currentShield, int maxShield, int rechargeDelay, long tickCount) {
        if (rechargeDelay > 0) {
            return new TickResult(rechargeDelay - 1, false);
        } else if (currentShield < maxShield) {
            if (tickCount % 20 == 0) {
                return new TickResult(0, true);
            }
        }
        return new TickResult(rechargeDelay, false);
    }
}
