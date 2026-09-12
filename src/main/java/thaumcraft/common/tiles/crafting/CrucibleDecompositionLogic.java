package thaumcraft.common.tiles.crafting;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Pure Java logic class for calculating aspect decomposition when items are dissolved in a Crucible.
 * Decouples decomposition mechanics from Minecraft/Forge dependencies.
 */
public class CrucibleDecompositionLogic {

    /**
     * Calculates the aspects yielded when an item decomposes in the crucible.
     * The input is a map of aspect string tags to integer amounts.
     *
     * @param objectAspects A map of aspects derived from the item (e.g., via AspectHelper). Null is permitted.
     * @return A map of the aspects that should be added to the crucible pool.
     */
    public static Map<String, Integer> calculateDecomposition(Map<String, Integer> objectAspects) {
        Map<String, Integer> result = new LinkedHashMap<>();
        if (objectAspects == null || objectAspects.isEmpty()) {
            return result;
        }

        // Standard 1:1 conversion for decomposed items in the crucible.
        for (Map.Entry<String, Integer> entry : objectAspects.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}
