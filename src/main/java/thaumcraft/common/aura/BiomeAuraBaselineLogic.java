package thaumcraft.common.aura;

import java.util.Collection;

public class BiomeAuraBaselineLogic {

    /**
     * Determines the natural chunk vis capacity and baseline aura levels based on biome magic tags.
     * Magical biomes have 250 vis, Wastelands have 50 vis.
     *
     * @param biomeTags   The collection of magic tags associated with the biome.
     * @param defaultBase The standard safe default base to return if no matching tags are found.
     * @return The determined aura base value.
     */
    public static short getBaselineAura(Collection<String> biomeTags, short defaultBase) {
        if (biomeTags != null) {
            for (String tag : biomeTags) {
                if ("magical".equalsIgnoreCase(tag)) {
                    return 250;
                }
                if ("wasteland".equalsIgnoreCase(tag)) {
                    return 50;
                }
            }
        }
        return defaultBase;
    }
}
