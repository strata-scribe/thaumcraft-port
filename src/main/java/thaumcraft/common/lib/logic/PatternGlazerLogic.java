package thaumcraft.common.lib.logic;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.Arrays;

public class PatternGlazerLogic {

    /**
     * Encodes an array of aspect tags into a single string pattern, separated by commas.
     * Null or empty arrays return an empty string. Null elements are treated as empty strings.
     */
    public static String encodeAspectPattern(String[] aspects) {
        if (aspects == null || aspects.length == 0) {
            return "";
        }
        return Arrays.stream(aspects)
                .map(s -> s == null ? "" : s)
                .collect(Collectors.joining(","));
    }

    /**
     * Decodes a string pattern into an array of aspect tags.
     * Empty string returns an empty array.
     */
    public static String[] decodeAspectPattern(String encoded) {
        if (encoded == null || encoded.trim().isEmpty()) {
            return new String[0];
        }
        return encoded.split(",", -1);
    }

    /**
     * Validates a blueprint pattern to ensure the number of elements matches width * height.
     * Additionally checks for negative dimensions.
     */
    public static boolean validateBlueprint(int width, int height, String[] pattern) {
        if (width <= 0 || height <= 0 || pattern == null) {
            return false;
        }
        return pattern.length == width * height;
    }

    /**
     * Calculates the vis requirement for a pattern based on base cost and individual aspect costs.
     * Ignores empty or null aspect tags. Unknown aspect tags are assumed to cost 0.
     */
    public static int calculateVisRequirement(String[] pattern, Map<String, Integer> aspectVisCosts, int baseCost) {
        if (pattern == null || pattern.length == 0) {
            return baseCost;
        }

        int totalCost = baseCost;
        for (String aspect : pattern) {
            if (aspect != null && !aspect.trim().isEmpty()) {
                totalCost += aspectVisCosts.getOrDefault(aspect, 0);
            }
        }
        return Math.max(0, totalCost);
    }
}
