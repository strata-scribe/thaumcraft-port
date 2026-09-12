package thaumcraft.common.lib;

public class WarpHallucinationTriggerLogic {

    public enum WarpGate {
        NONE,
        MINOR,
        NORMAL,
        MAJOR
    }

    /**
     * Evaluates the warp gate level based on the total amount of warp.
     *
     * @param totalWarp The total warp the player has.
     * @return The corresponding WarpGate.
     */
    public static WarpGate evaluateGate(int totalWarp) {
        if (totalWarp >= 50) {
            return WarpGate.MAJOR;
        } else if (totalWarp >= 30) {
            return WarpGate.NORMAL;
        } else if (totalWarp >= 10) {
            return WarpGate.MINOR;
        } else {
            return WarpGate.NONE;
        }
    }

    /**
     * Determines the timer (in ticks) until the next hallucination event spawns,
     * based on the current warp gate and a random factor.
     *
     * @param gate The current WarpGate.
     * @param randomFactor A random double between 0.0 (inclusive) and 1.0 (exclusive).
     * @return The timer in ticks. Returns -1 if no event should spawn (NONE gate).
     */
    public static int determineEventSpawnTimer(WarpGate gate, double randomFactor) {
        if (randomFactor < 0.0 || randomFactor >= 1.0) {
            throw new IllegalArgumentException("randomFactor must be >= 0.0 and < 1.0");
        }

        switch (gate) {
            case MAJOR:
                // 500 to 1000 ticks
                return 500 + (int) (randomFactor * 500);
            case NORMAL:
                // 1000 to 2000 ticks
                return 1000 + (int) (randomFactor * 1000);
            case MINOR:
                // 2000 to 4000 ticks
                return 2000 + (int) (randomFactor * 2000);
            case NONE:
            default:
                return -1;
        }
    }
}
