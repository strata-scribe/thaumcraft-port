package thaumcraft.common.golems.logic;

/**
 * Pure Java logic class for evaluating Golem Biome modifiers based on terrain and temperature.
 * Completely decoupled from Minecraft/Forge classes for isolated JUnit testing.
 */
public class GolemBiomeModifierLogic {

    /**
     * Calculates the movement speed multiplier based on terrain type and temperature.
     *
     * @param terrainType The type of terrain (e.g., "ICE", "SAND", "SNOW").
     * @param temperature The biome temperature.
     * @return the combined movement speed multiplier.
     */
    public static float getMovementMultiplier(String terrainType, float temperature) {
        float multiplier = 1.0f;

        // Terrain modifiers
        if (terrainType != null) {
            String upperTerrain = terrainType.toUpperCase();
            if (upperTerrain.equals("ICE")) {
                multiplier *= 1.2f; // Slip and slide, go faster
            } else if (upperTerrain.equals("SAND") || upperTerrain.equals("SNOW")) {
                multiplier *= 0.8f; // Bogged down
            }
        }

        // Temperature modifiers
        if (temperature >= 1.5f) {
            multiplier *= 0.9f; // Overheating
        } else if (temperature <= 0.2f) {
            multiplier *= 0.9f; // Freezing joints
        }

        return multiplier;
    }
}
