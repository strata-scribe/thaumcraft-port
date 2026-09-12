package thaumcraft.common.aura;

public class AuraBaselineLogic {

    public record AuraCapacities(int baseVisCapacity, int fluxCeiling) {}

    /**
     * Calculates the baseline aura capacities (base vis capacity and flux ceiling)
     * based on biome temperature, rainfall, and magic affinity.
     *
     * @param temperature   Biome temperature (e.g., standard is ~0.5 to 1.0)
     * @param rainfall      Biome rainfall (e.g., standard is ~0.5)
     * @param magicAffinity Magic affinity multiplier (e.g., negative for mundane, positive for magical)
     * @return AuraCapacities containing calculated values.
     */
    public static AuraCapacities calculateCapacities(float temperature, float rainfall, float magicAffinity) {
        // Base Vis is positively affected by rainfall and magic affinity, slightly affected by temperature.
        int baseVis = (int) (100 + (temperature * 20) + (rainfall * 30) + (magicAffinity * 100));

        // Flux Ceiling is increased by temperature and magic affinity, decreased by rainfall.
        int fluxCeiling = (int) (50 + (temperature * 10) - (rainfall * 20) + (magicAffinity * 50));

        return new AuraCapacities(Math.max(10, baseVis), Math.max(5, fluxCeiling));
    }
}
