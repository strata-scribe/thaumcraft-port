package thaumcraft.common.tiles.devices;

public class FluxVentingLogic {

    /**
     * Calculates the venting rate of a flux condenser based on its lattice state and local aura.
     * @param cleanLattices The number of clean lattices.
     * @param cloggedLattices The number of clogged lattices.
     * @param baseAura The base aura level in the chunk.
     * @return The calculated venting rate.
     */
    public static float calculateVentingRate(int cleanLattices, int cloggedLattices, float baseAura) {
        if (cleanLattices <= 0) {
            return 0.0f;
        }

        float rate = cleanLattices * 5.0f;
        if (cloggedLattices > 0) {
            rate -= cloggedLattices * 1.5f;
        }

        if (rate <= 0.0f) {
            return 0.0f;
        }

        if (baseAura > 0.0f) {
            rate *= Math.max(0.5f, Math.min(2.0f, baseAura / 100.0f));
        }

        return rate;
    }

    /**
     * Calculates the wear/degradation per unit of vitium condensed on the active lattices.
     * @param vitiumCondensed The amount of vitium condensed.
     * @param activeLattices The total active lattices processing the vitium.
     * @return The wear amount.
     */
    public static float calculateLatticeWear(float vitiumCondensed, int activeLattices) {
        if (vitiumCondensed <= 0.0f || activeLattices <= 0) {
            return 0.0f;
        }
        return (vitiumCondensed * 0.2f) / activeLattices;
    }

    /**
     * Calculates the environmental aura cleansing footprint radius.
     * @param activeLattices The number of active clean lattices.
     * @param ventingRate The current venting rate of the condenser.
     * @return The cleansing footprint radius.
     */
    public static int calculateCleansingFootprint(int activeLattices, float ventingRate) {
        if (activeLattices <= 0 || ventingRate <= 0.0f) {
            return 0;
        }
        int radius = (int) Math.sqrt(activeLattices * ventingRate);
        return Math.max(1, Math.min(radius, 64));
    }
}
