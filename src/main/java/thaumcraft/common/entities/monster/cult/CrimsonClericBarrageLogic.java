package thaumcraft.common.entities.monster.cult;

public class CrimsonClericBarrageLogic {

    /**
     * Computes the staggered [x, y, z] offsets for a firebolt during a barrage.
     * The shots are spread horizontally based on their index.
     *
     * @param barrageIndex The index of the current shot (0 to barrageSize - 1)
     * @param barrageSize The total number of shots in the barrage
     * @param spread The maximum spread distance from the center
     * @return A double array [x, y, z] representing the trajectory offset
     */
    public static double[] calculateTrajectoryOffset(int barrageIndex, int barrageSize, double spread) {
        if (barrageSize <= 1) {
            return new double[]{0.0, 0.0, 0.0};
        }

        // Distribute offsets evenly from -spread to +spread horizontally
        double offsetProgress = (double) barrageIndex / (barrageSize - 1);
        double xOffset = -spread + (offsetProgress * spread * 2.0);

        // Minor variation in y and z could be added, but for now we keep it horizontal
        double yOffset = 0.0;
        double zOffset = 0.0;

        return new double[]{xOffset, yOffset, zOffset};
    }

    /**
     * Computes the cooldown interval after the barrage completes.
     *
     * @param barrageSize The total number of shots in the barrage
     * @param difficultyModifier A modifier based on difficulty (e.g., higher difficulty = lower cooldown)
     * @return The cooldown interval in ticks
     */
    public static int calculateCooldownInterval(int barrageSize, double difficultyModifier) {
        // Base cooldown of 40 ticks, plus 10 ticks per shot
        int baseCooldown = 40 + (barrageSize * 10);

        // Apply difficulty modifier (minimum 0.1 to prevent division by zero or negative)
        double modifier = Math.max(0.1, difficultyModifier);

        return (int) Math.max(10, baseCooldown / modifier);
    }

    /**
     * Computes the interval between shots during a barrage.
     *
     * @param difficultyModifier A modifier based on difficulty (e.g., higher difficulty = faster stagger)
     * @return The stagger interval in ticks
     */
    public static int calculateStaggerInterval(double difficultyModifier) {
        // Base stagger interval of 5 ticks
        double baseInterval = 5.0;

        // Apply difficulty modifier
        double modifier = Math.max(0.1, difficultyModifier);

        // Return at least 1 tick interval
        return (int) Math.max(1, Math.round(baseInterval / modifier));
    }
}
