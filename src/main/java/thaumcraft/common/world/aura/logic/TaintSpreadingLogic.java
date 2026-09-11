package thaumcraft.common.world.aura.logic;

public class TaintSpreadingLogic {

    public static String getTaintConversion(String blockName) {
        if (blockName == null) {
            return null;
        }

        blockName = blockName.toLowerCase();

        if (blockName.contains("grass") || blockName.contains("dirt")) {
            return "thaumcraft:tainted_soil";
        } else if (blockName.contains("stone")) {
            return "thaumcraft:crusted_taint";
        } else if (blockName.contains("log") || blockName.contains("wood")) {
            return "thaumcraft:tainted_wood";
        }

        return blockName; // No conversion
    }

    public static float calculateSpreadProbability(double distanceToSeed, double maxSpreadRadius) {
        if (distanceToSeed > maxSpreadRadius) {
            return 0.0f;
        }

        // Closer to seed = higher probability.
        // Let's say at 0 distance, prob is 1.0. At maxSpreadRadius, prob is 0.1
        // Probability = 1.0 - (distance / maxSpreadRadius) * 0.9
        float probability = (float) (1.0 - (distanceToSeed / maxSpreadRadius) * 0.9);
        return Math.max(0.0f, Math.min(1.0f, probability));
    }
}
