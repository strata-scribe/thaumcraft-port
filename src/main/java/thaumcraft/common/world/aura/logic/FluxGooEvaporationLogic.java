package thaumcraft.common.world.aura.logic;

public class FluxGooEvaporationLogic {

    private static final float BASE_EVAPORATION_RATE_PER_TICK = 0.05f;
    private static final float FLUX_PER_UNIT_EVAPORATED = 2.0f; // 2 flux points for every 1 unit of goo evaporated

    /**
     * Calculates the amount of flux goo that evaporates over a given number of ticks.
     * The amount cannot exceed the current volume.
     *
     * @param currentVolume The current volume of the flux goo.
     * @param ticksElapsed  The number of ticks elapsed since the last evaporation calculation.
     * @return The volume of flux goo that evaporated.
     */
    public static float calculateEvaporationAmount(float currentVolume, int ticksElapsed) {
        if (currentVolume <= 0 || ticksElapsed <= 0) {
            return 0.0f;
        }

        float expectedEvaporation = BASE_EVAPORATION_RATE_PER_TICK * ticksElapsed;
        return Math.min(expectedEvaporation, currentVolume);
    }

    /**
     * Calculates the amount of vitium flux points released into the chunk aura based on the evaporated volume.
     *
     * @param evaporatedVolume The volume of flux goo that evaporated.
     * @return The amount of vitium flux points released.
     */
    public static float calculateFluxReleased(float evaporatedVolume) {
        if (evaporatedVolume <= 0) {
            return 0.0f;
        }

        return evaporatedVolume * FLUX_PER_UNIT_EVAPORATED;
    }
}
