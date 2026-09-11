package thaumcraft.common.lib;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WarpProgressionLogic {

    public enum WarpType {
        TEMPORARY,
        NORMAL,
        PERMANENT
    }

    public static class PlayerWarpState {
        private int temporaryWarp = 0;
        private int normalWarp = 0;
        private int permanentWarp = 0;
        private int temporalTimer = 0;

        public void addWarp(int amount, WarpType type) {
            switch (type) {
                case TEMPORARY:
                    temporaryWarp = Math.max(0, temporaryWarp + amount);
                    break;
                case NORMAL:
                    normalWarp = Math.max(0, normalWarp + amount);
                    break;
                case PERMANENT:
                    permanentWarp = Math.max(0, permanentWarp + amount);
                    break;
            }
        }

        public int getTotalWarp() {
            return temporaryWarp + normalWarp + permanentWarp;
        }

        public int getWarp(WarpType type) {
            switch (type) {
                case TEMPORARY: return temporaryWarp;
                case NORMAL: return normalWarp;
                case PERMANENT: return permanentWarp;
            }
            return 0;
        }

        public void incrementTimer(int ticks) {
            temporalTimer += ticks;
        }

        public int getTemporalTimer() {
            return temporalTimer;
        }

        public void resetTimer() {
            temporalTimer = 0;
        }
    }

    private static final Map<UUID, PlayerWarpState> warpStates = new HashMap<>();

    public static PlayerWarpState getState(UUID playerId) {
        return warpStates.computeIfAbsent(playerId, k -> new PlayerWarpState());
    }

    public static void addWarp(UUID playerId, int amount, WarpType type) {
        getState(playerId).addWarp(amount, type);
    }

    public static int getTotalWarp(UUID playerId) {
        return getState(playerId).getTotalWarp();
    }

    public static int getWarp(UUID playerId, WarpType type) {
        return getState(playerId).getWarp(type);
    }

    public static void incrementTimer(UUID playerId, int ticks) {
        getState(playerId).incrementTimer(ticks);
    }

    public static void clearState(UUID playerId) {
        warpStates.remove(playerId);
    }

    /**
     * Evaluates hallucination trigger probability based on total warp and temporal timer.
     * If triggered, resets the temporal timer and reduces temporary warp.
     *
     * @param playerId    The UUID of the player.
     * @param randomValue A random value, typically between 0.0 and 1.0.
     * @return true if hallucination triggers, false otherwise.
     */
    public static boolean rollHallucination(UUID playerId, double randomValue) {
        PlayerWarpState state = getState(playerId);
        int totalWarp = state.getTotalWarp();

        if (totalWarp <= 0) {
            return false;
        }

        // Probability formula incorporating total warp and temporal timer
        double probability = (totalWarp * 0.01) + (state.getTemporalTimer() * 0.0001);

        if (randomValue < probability) {
            state.resetTimer();
            if (state.getWarp(WarpType.TEMPORARY) > 0) {
                state.addWarp(-1, WarpType.TEMPORARY);
            }
            return true;
        }

        return false;
    }
}
