package thaumcraft.common.items.tools.logic;

/**
 * Pure, headless domain logic for the Essentia Resonator.
 * Zero Minecraft server or registry dependencies.
 */
public class EssentiaResonatorLogic {

    /**
     * Formats suction readout string based on suction strength and aspect filter.
     */
    public static String formatSuction(int suction, String aspectTag) {
        if (aspectTag == null || aspectTag.isEmpty()) {
            return suction > 0 ? "Suction: " + suction : "No suction";
        }
        return "Suction: " + suction + " (" + aspectTag + ")";
    }

    /**
     * Determines if a block ID corresponds to an inspectable essentia device.
     */
    public static boolean canInspectDevice(String blockId) {
        if (blockId == null) return false;
        String s = blockId.toLowerCase();
        return s.contains("tube") || s.contains("jar") || s.contains("smelter") || s.contains("alembic") || s.contains("centrifuge") || s.contains("bellows");
    }

    /**
     * Calculates the audio pitch for the resonator based on suction strength relative to max suction.
     */
    public static float calculateResonatorPitch(int suction, int maxSuction) {
        if (maxSuction <= 0 || suction <= 0) return 1.0f;
        float ratio = (float) suction / (float) maxSuction;
        return 0.5f + Math.min(1.5f, ratio * 1.5f);
    }
}
