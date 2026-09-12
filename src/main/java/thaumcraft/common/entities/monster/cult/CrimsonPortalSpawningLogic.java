package thaumcraft.common.entities.monster.cult;

/**
 * Pure Java logic helper for determining Crimson Portal spawn waves.
 * Strict mathematical decoupling: contains zero Minecraft or NeoForge classloader imports.
 */
public class CrimsonPortalSpawningLogic {

    public static class CultistWaveMix {
        public final int knights;
        public final int clerics;
        public final int praetors;

        public CultistWaveMix(int knights, int clerics, int praetors) {
            this.knights = knights;
            this.clerics = clerics;
            this.praetors = praetors;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CultistWaveMix that = (CultistWaveMix) o;
            return knights == that.knights && clerics == that.clerics && praetors == that.praetors;
        }
    }

    /**
     * Determines the mix of cultists to spawn from a portal wave based on player warp and world age.
     *
     * @param playerWarp the warp level of the player near the portal.
     * @param worldAgeTicks the age of the world in ticks.
     * @return A CultistWaveMix object indicating how many of each type to spawn.
     */
    public static CultistWaveMix calculateWaveMix(int playerWarp, long worldAgeTicks) {
        int knights = 2; // Baseline
        int clerics = 1; // Baseline
        int praetors = 0; // Baseline

        long ONE_DAY_TICKS = 24000L;

        // Increase cleric count if warp is high
        if (playerWarp >= 50) {
            clerics += 1;
        }
        if (playerWarp >= 100) {
            clerics += 1;
            knights += 1;
        }

        // Spawn praetors if world is old enough AND warp is decent, or if warp is exceptionally high
        if (worldAgeTicks > 50 * ONE_DAY_TICKS && playerWarp > 20) {
            praetors += 1;
        } else if (playerWarp >= 80) {
            praetors += 1;
        }

        if (worldAgeTicks > 100 * ONE_DAY_TICKS && playerWarp > 60) {
            praetors += 1;
        }

        return new CultistWaveMix(knights, clerics, praetors);
    }
}
