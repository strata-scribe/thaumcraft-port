package thaumcraft.common.items.tools;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SanitySoapLogic {

    private static final Map<UUID, WashData> washDataMap = new HashMap<>();

    // Time window for diminishing returns (e.g., 24000 ticks = 1 in-game day)
    private static final long RESET_TIME_TICKS = 24000;

    private static class WashData {
        int washCount;
        long lastWashTime;

        WashData(int washCount, long lastWashTime) {
            this.washCount = washCount;
            this.lastWashTime = lastWashTime;
        }
    }

    /**
     * Calculates the amount of temporary warp to remove based on current warp and consecutive washes.
     * @param currentTemporaryWarp The current temporary warp.
     * @param consecutiveWashes The number of recent consecutive washes.
     * @return The amount of temporary warp to reduce.
     */
    public static int calculateWarpReduction(int currentTemporaryWarp, int consecutiveWashes) {
        if (currentTemporaryWarp <= 0) {
            return 0;
        }

        // Base reduction is 50% of current temporary warp
        // Efficiency halves with each consecutive wash
        double percentage = 0.5 / (consecutiveWashes + 1);
        int reduction = (int) (currentTemporaryWarp * percentage);

        // Ensure at least 1 point is removed if there's any warp and it's not overly diminished
        if (reduction == 0 && currentTemporaryWarp > 0 && percentage > 0.05) {
            reduction = 1;
        }

        return Math.min(reduction, currentTemporaryWarp);
    }

    /**
     * Retrieves the consecutive wash count for a player, resetting it if enough time has passed.
     * @param playerId The UUID of the player.
     * @param currentTime The current time (e.g., world ticks).
     * @return The number of consecutive washes.
     */
    public static int getConsecutiveWashes(UUID playerId, long currentTime) {
        WashData data = washDataMap.get(playerId);
        if (data == null) {
            return 0;
        }

        // If enough time has passed since the last wash, reset the count
        if (currentTime - data.lastWashTime >= RESET_TIME_TICKS || currentTime < data.lastWashTime) {
            return 0;
        }

        return data.washCount;
    }

    /**
     * Records a wash for a player, updating their consecutive wash count.
     * @param playerId The UUID of the player.
     * @param currentTime The current time (e.g., world ticks).
     */
    public static void recordWash(UUID playerId, long currentTime) {
        WashData data = washDataMap.get(playerId);

        if (data == null || currentTime - data.lastWashTime >= RESET_TIME_TICKS || currentTime < data.lastWashTime) {
            washDataMap.put(playerId, new WashData(1, currentTime));
        } else {
            washDataMap.put(playerId, new WashData(data.washCount + 1, currentTime));
        }
    }

    /**
     * Clears all wash data (useful for testing or server restarts).
     */
    public static void clearWashData() {
        washDataMap.clear();
    }
}
