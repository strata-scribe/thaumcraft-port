package thaumcraft.api.casters.focus;

public class FocusExchangeLogic {

    public static int calculateExchangeComplexity(int power) {
        return Math.max(0, power) * 3;
    }

    public static boolean hasExchangePermission(float targetHardness, float sourceHardness) {
        // Blocks with hardness < 0 cannot be destroyed or moved (e.g., Bedrock).
        return targetHardness >= 0 && sourceHardness >= 0;
    }

    public static boolean isHardnessCompatible(float targetHardness, float sourceHardness) {
        if (!hasExchangePermission(targetHardness, sourceHardness)) return false;
        return sourceHardness <= targetHardness * 2.0f + 1.0f;
    }

    public static float calculateVisCost(float targetHardness, float sourceHardness, double distance) {
        float hardnessDiff = Math.abs(targetHardness - sourceHardness);
        float distanceFactor = (float) (Math.max(0, distance - 1) * 0.1f);
        return 0.5f + (hardnessDiff * 0.5f) + distanceFactor;
    }
}
