package thaumcraft.common.items.armor;

import java.util.Map;
import java.util.stream.Collectors;

public class GogglesAuraLogic {

    public enum AuraDangerLevel {
        SAFE,
        ELEVATED,
        DANGEROUS
    }

    public static String formatVisPercentage(float vis, short base) {
        if (base <= 0) return "0%";
        int percentage = Math.round((vis / base) * 100f);
        return percentage + "%";
    }

    public static String formatFluxPercentage(float flux, short base) {
        if (base <= 0) return "0%";
        int percentage = Math.round((flux / base) * 100f);
        return percentage + "%";
    }

    public static AuraDangerLevel getFluxWarningThreshold(float flux, short base) {
        if (base <= 0) {
            return flux > 0 ? AuraDangerLevel.DANGEROUS : AuraDangerLevel.SAFE;
        }
        float ratio = flux / base;
        if (ratio >= 0.75f) {
            return AuraDangerLevel.DANGEROUS;
        } else if (ratio >= 0.50f) {
            return AuraDangerLevel.ELEVATED;
        } else {
            return AuraDangerLevel.SAFE;
        }
    }

    public static String formatAspectTags(Map<String, Integer> aspects) {
        if (aspects == null || aspects.isEmpty()) {
            return "No Aspects";
        }
        return aspects.entrySet().stream()
            .map(entry -> entry.getKey() + " (" + entry.getValue() + ")")
            .collect(Collectors.joining(", "));
    }
}
