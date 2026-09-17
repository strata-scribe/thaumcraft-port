package thaumcraft.common.lib.logic;

public class MirrorTransportStabilityLogic {

    /**
     * Calculates the vis cost for transporting items or essentia across a magic mirror link.
     *
     * @param startDim The dimension ID of the starting mirror.
     * @param startX   The X coordinate of the starting mirror.
     * @param startY   The Y coordinate of the starting mirror.
     * @param startZ   The Z coordinate of the starting mirror.
     * @param endDim   The dimension ID of the ending mirror.
     * @param endX     The X coordinate of the ending mirror.
     * @param endY     The Y coordinate of the ending mirror.
     * @param endZ     The Z coordinate of the ending mirror.
     * @param amount   The amount of items or essentia being transported.
     * @return The vis cost required for the transport.
     */
    public static double calculateVisCost(String startDim, double startX, double startY, double startZ,
                                          String endDim, double endX, double endY, double endZ, int amount) {
        if (startDim == null || endDim == null) {
            return 0.0;
        }

        if (!startDim.equals(endDim)) {
            // Interdimensional transfer
            return 5.0 + (amount * 0.5);
        } else {
            // Same dimension transfer
            double dx = endX - startX;
            double dy = endY - startY;
            double dz = endZ - startZ;
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

            double cost = 0.1 + (distance * 0.01);
            return cost;
        }
    }

    /**
     * Calculates the instability risk (chance of generating flux/failing) for a mirror transport.
     *
     * @param startDim The dimension ID of the starting mirror.
     * @param startX   The X coordinate of the starting mirror.
     * @param startY   The Y coordinate of the starting mirror.
     * @param startZ   The Z coordinate of the starting mirror.
     * @param endDim   The dimension ID of the ending mirror.
     * @param endX     The X coordinate of the ending mirror.
     * @param endY     The Y coordinate of the ending mirror.
     * @param endZ     The Z coordinate of the ending mirror.
     * @param amount   The amount of items or essentia being transported.
     * @return A float between 0.0 and 1.0 representing the risk of instability.
     */
    public static float calculateInstabilityRisk(String startDim, double startX, double startY, double startZ,
                                                 String endDim, double endX, double endY, double endZ, int amount) {
        if (startDim == null || endDim == null) {
            return 0.0f;
        }

        float risk = 0.0f;
        if (!startDim.equals(endDim)) {
            // Interdimensional transfer risk
            risk = 0.05f + (amount * 0.01f);
        } else {
            // Same dimension transfer risk
            double dx = endX - startX;
            double dy = endY - startY;
            double dz = endZ - startZ;
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

            risk = (float) (distance * 0.0001 * amount);
        }

        return Math.min(1.0f, Math.max(0.0f, risk));
    }
}
