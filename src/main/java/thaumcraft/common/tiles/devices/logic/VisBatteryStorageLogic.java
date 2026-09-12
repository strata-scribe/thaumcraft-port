package thaumcraft.common.tiles.devices.logic;

public class VisBatteryStorageLogic {

    private float storedVis;
    private final float maxCapacity;
    private final float siphonRate;
    private final float dischargeRate;

    public VisBatteryStorageLogic(float maxCapacity, float siphonRate, float dischargeRate) {
        this.maxCapacity = Math.max(0, maxCapacity);
        this.siphonRate = Math.max(0, siphonRate);
        this.dischargeRate = Math.max(0, dischargeRate);
        this.storedVis = 0.0f;
    }

    public float getStoredVis() {
        return storedVis;
    }

    public void setStoredVis(float storedVis) {
        this.storedVis = Math.max(0, Math.min(this.maxCapacity, storedVis));
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
        if (availableAuraVis <= 0) return 0;

        float spaceAvailable = maxCapacity - storedVis;
        if (spaceAvailable <= 0) return 0;

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
        if (requestedVis <= 0) return 0;

        float availableToDischarge = Math.min(storedVis, dischargeRate);
        float amountDischarged = Math.min(requestedVis, availableToDischarge);

        storedVis -= amountDischarged;
        return amountDischarged;
    }
}
