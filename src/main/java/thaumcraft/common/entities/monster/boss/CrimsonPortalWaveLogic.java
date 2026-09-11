package thaumcraft.common.entities.monster.boss;

public class CrimsonPortalWaveLogic {

    public enum CultistType {
        KNIGHT,
        CLERIC
    }

    public static class WaveComposition {
        private final int knights;
        private final int clerics;
        private final int spawnIntervalTicks;

        public WaveComposition(int knights, int clerics, int spawnIntervalTicks) {
            this.knights = knights;
            this.clerics = clerics;
            this.spawnIntervalTicks = spawnIntervalTicks;
        }

        public int getKnights() {
            return knights;
        }

        public int getClerics() {
            return clerics;
        }

        public int getSpawnIntervalTicks() {
            return spawnIntervalTicks;
        }
    }

    /**
     * Calculates the wave composition based on the current wave number.
     * Higher wave numbers result in more cultists and shorter intervals.
     * @param waveNumber The current wave number (starting from 1).
     * @return The WaveComposition for the given wave.
     */
    public static WaveComposition getWaveComposition(int waveNumber) {
        if (waveNumber <= 0) {
            waveNumber = 1;
        }

        // Base spawn values: starts at 2 knights, increases every 2 waves
        int knights = 2 + ((waveNumber - 1) / 2);
        // Clerics start spawning at wave 3, increases every 2 waves
        int clerics = waveNumber >= 3 ? 1 + ((waveNumber - 3) / 2) : 0;

        // Interval decreases as wave increases. Base 400 ticks (20s), decreases by 20 ticks per wave, min 100 ticks.
        int spawnIntervalTicks = Math.max(100, 400 - (waveNumber * 20));

        return new WaveComposition(knights, clerics, spawnIntervalTicks);
    }

    /**
     * Calculates the amount of warp flux released into the chunk upon portal destruction.
     * Scales with the number of waves the portal spawned.
     * @param wavesSurvived The number of waves spawned/survived.
     * @return The amount of flux released.
     */
    public static float getDestructionFluxRelease(int wavesSurvived) {
        // Base 10 flux, plus 5 for every wave survived
        return 10.0f + (Math.max(0, wavesSurvived) * 5.0f);
    }
}
