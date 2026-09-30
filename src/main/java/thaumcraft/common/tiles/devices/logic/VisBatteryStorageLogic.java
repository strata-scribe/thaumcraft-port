package thaumcraft.common.tiles.devices.logic;

public class VisBatteryStorageLogic {

    public static final float ABSORB_THRESHOLD_RATIO = 0.95f;
    public static final float DISCHARGE_THRESHOLD_RATIO = 0.75f;
    public static final float RAPID_DISCHARGE_MULTIPLIER = 2.0f;

    private float storedVis;
    private final float maxCapacity;
    private final float siphonRate;
    private final float dischargeRate;

    private static float sanitize(float val) {
        if (Float.isNaN(val) || Float.isInfinite(val)) return 0.0f;
        return Math.max(0.0f, val);
    }

    public VisBatteryStorageLogic(float maxCapacity, float siphonRate, float dischargeRate) {
        this.maxCapacity = sanitize(maxCapacity);
        this.siphonRate = sanitize(siphonRate);
        this.dischargeRate = sanitize(dischargeRate);
        this.storedVis = 0.0f;
    }

    public float getStoredVis() {
        return storedVis;
    }

    public void setStoredVis(float storedVis) {
        if (Float.isNaN(storedVis) || Float.isInfinite(storedVis)) {
            return;
        }
        this.storedVis = Math.max(0.0f, Math.min(this.maxCapacity, storedVis));
    }

    public float getMaxCapacity() {
        return maxCapacity;
    }

    public float getSiphonRate() {
        return siphonRate;
    }

    public float getDischargeRate() {
        return dischargeRate;
    }

    /**
     * Siphons vis from the aura, returning the amount that was actually drawn.
     * @param availableAuraVis The amount of vis currently in the aura.
     * @return The amount of vis siphoned and stored.
     */
    public float siphonFromAura(float availableAuraVis) {
        if (Float.isNaN(availableAuraVis) || Float.isInfinite(availableAuraVis) || availableAuraVis <= 0.0f) {
            return 0.0f;
        }

        float spaceAvailable = maxCapacity - storedVis;
        if (spaceAvailable <= 0.0f) return 0.0f;

        float amountToSiphon = Math.min(siphonRate, Math.min(availableAuraVis, spaceAvailable));
        storedVis += amountToSiphon;
        return amountToSiphon;
    }

    /**
     * Discharges vis to a machine, returning the amount that was actually provided.
     * @param requestedVis The amount of vis the machine wants.
     * @return The amount of vis discharged from the battery.
     */
    public float dischargeToMachine(float requestedVis) {
        if (Float.isNaN(requestedVis) || Float.isInfinite(requestedVis) || requestedVis <= 0.0f) {
            return 0.0f;
        }

        float availableToDischarge = Math.min(storedVis, dischargeRate);
        float amountDischarged = Math.min(requestedVis, availableToDischarge);

        storedVis -= amountDischarged;
        return amountDischarged;
    }

    public boolean isFull() {
        return storedVis >= maxCapacity;
    }

    public boolean isEmpty() {
        return storedVis <= 0.0f;
    }

    public float getFillRatio() {
        return maxCapacity > 0.0f ? (storedVis / maxCapacity) : 0.0f;
    }

    public float getRemainingCapacity() {
        return Math.max(0.0f, maxCapacity - storedVis);
    }

    public static boolean shouldAbsorb(float currentVis, float baseAura, boolean powered) {
        if (powered || Float.isNaN(currentVis) || Float.isNaN(baseAura)) return false;
        return currentVis > (baseAura * ABSORB_THRESHOLD_RATIO);
    }

    public static boolean shouldDischarge(float currentVis, float baseAura, boolean powered, float storedVis) {
        if (storedVis <= 0.0f || Float.isNaN(storedVis)) return false;
        if (powered) return true;
        if (Float.isNaN(currentVis) || Float.isNaN(baseAura)) return false;
        return currentVis < (baseAura * DISCHARGE_THRESHOLD_RATIO);
    }

    public static float calculateAbsorbAmount(float currentVis, float baseAura, float remainingCapacity, float siphonRate) {
        if (remainingCapacity <= 0.0f || siphonRate <= 0.0f) return 0.0f;
        if (Float.isNaN(currentVis) || Float.isNaN(baseAura) || Float.isNaN(remainingCapacity) || Float.isNaN(siphonRate)) return 0.0f;
        float excess = currentVis - (baseAura * ABSORB_THRESHOLD_RATIO);
        if (excess <= 0.0f) return 0.0f;
        return Math.min(siphonRate, Math.min(excess, remainingCapacity));
    }

    public static float calculateDischargeAmount(float currentVis, float baseAura, float storedVis, float dischargeRate, boolean powered) {
        if (storedVis <= 0.0f || dischargeRate <= 0.0f) return 0.0f;
        if (Float.isNaN(currentVis) || Float.isNaN(baseAura) || Float.isNaN(storedVis) || Float.isNaN(dischargeRate)) return 0.0f;
        if (powered) {
            return Math.min(storedVis, dischargeRate * RAPID_DISCHARGE_MULTIPLIER);
        }
        float deficit = (baseAura * DISCHARGE_THRESHOLD_RATIO) - currentVis;
        if (deficit <= 0.0f) return 0.0f;
        return Math.min(dischargeRate, Math.min(deficit, storedVis));
    }
}
