package thaumcraft.api.casters.focus;

public class SpectralArrowLogic {
    // Decouple armor penetration percentage and damage formula here

    public static float calculateDamage(int powerSetting, float finalPower) {
        return (powerSetting * 3) * finalPower;
    }

    public static float calculateArmorPenetration(int piercingSetting) {
        // e.g. 10% per piercing setting point
        return piercingSetting * 0.1f;
    }
}
