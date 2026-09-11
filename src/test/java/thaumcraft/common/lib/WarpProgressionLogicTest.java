package thaumcraft.common.lib;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class WarpProgressionLogicTest {

    private UUID playerId;

    @BeforeEach
    public void setUp() {
        playerId = UUID.randomUUID();
        WarpProgressionLogic.clearState(playerId);
    }

    @Test
    public void testAddWarpAndGetTotal() {
        WarpProgressionLogic.addWarp(playerId, 10, WarpProgressionLogic.WarpType.TEMPORARY);
        WarpProgressionLogic.addWarp(playerId, 5, WarpProgressionLogic.WarpType.NORMAL);
        WarpProgressionLogic.addWarp(playerId, 2, WarpProgressionLogic.WarpType.PERMANENT);

        assertEquals(10, WarpProgressionLogic.getWarp(playerId, WarpProgressionLogic.WarpType.TEMPORARY));
        assertEquals(5, WarpProgressionLogic.getWarp(playerId, WarpProgressionLogic.WarpType.NORMAL));
        assertEquals(2, WarpProgressionLogic.getWarp(playerId, WarpProgressionLogic.WarpType.PERMANENT));
        assertEquals(17, WarpProgressionLogic.getTotalWarp(playerId));
    }

    @Test
    public void testNegativeWarpDoesNotDropBelowZero() {
        WarpProgressionLogic.addWarp(playerId, 5, WarpProgressionLogic.WarpType.TEMPORARY);
        WarpProgressionLogic.addWarp(playerId, -10, WarpProgressionLogic.WarpType.TEMPORARY);

        assertEquals(0, WarpProgressionLogic.getWarp(playerId, WarpProgressionLogic.WarpType.TEMPORARY));
    }

    @Test
    public void testTemporalTimerIncrements() {
        WarpProgressionLogic.incrementTimer(playerId, 20);
        WarpProgressionLogic.incrementTimer(playerId, 15);

        WarpProgressionLogic.PlayerWarpState state = WarpProgressionLogic.getState(playerId);
        assertEquals(35, state.getTemporalTimer());
    }

    @Test
    public void testRollHallucinationTriggersAndUpdatesState() {
        WarpProgressionLogic.addWarp(playerId, 50, WarpProgressionLogic.WarpType.TEMPORARY);
        WarpProgressionLogic.incrementTimer(playerId, 1000);

        // Calculate expected probability: 50 * 0.01 + 1000 * 0.0001 = 0.5 + 0.1 = 0.6
        // Passing a random value less than 0.6 should trigger the hallucination
        assertTrue(WarpProgressionLogic.rollHallucination(playerId, 0.5));

        WarpProgressionLogic.PlayerWarpState state = WarpProgressionLogic.getState(playerId);
        assertEquals(0, state.getTemporalTimer(), "Timer should reset after trigger");
        assertEquals(49, WarpProgressionLogic.getWarp(playerId, WarpProgressionLogic.WarpType.TEMPORARY), "Temporary warp should reduce by 1");
    }

    @Test
    public void testRollHallucinationDoesNotTrigger() {
        WarpProgressionLogic.addWarp(playerId, 10, WarpProgressionLogic.WarpType.TEMPORARY);
        WarpProgressionLogic.incrementTimer(playerId, 100);

        // Calculate expected probability: 10 * 0.01 + 100 * 0.0001 = 0.1 + 0.01 = 0.11
        // Passing a random value greater than or equal to 0.11 should not trigger the hallucination
        assertFalse(WarpProgressionLogic.rollHallucination(playerId, 0.2));

        WarpProgressionLogic.PlayerWarpState state = WarpProgressionLogic.getState(playerId);
        assertEquals(100, state.getTemporalTimer(), "Timer should not reset if not triggered");
        assertEquals(10, WarpProgressionLogic.getWarp(playerId, WarpProgressionLogic.WarpType.TEMPORARY), "Temporary warp should not reduce if not triggered");
    }

    @Test
    public void testRollHallucinationZeroWarp() {
        assertFalse(WarpProgressionLogic.rollHallucination(playerId, 0.0));
    }

    @Test
    public void testRollHallucinationNoTemporaryWarpToReduce() {
        WarpProgressionLogic.addWarp(playerId, 50, WarpProgressionLogic.WarpType.NORMAL);

        // Probability: 50 * 0.01 = 0.5
        assertTrue(WarpProgressionLogic.rollHallucination(playerId, 0.4));

        WarpProgressionLogic.PlayerWarpState state = WarpProgressionLogic.getState(playerId);
        assertEquals(0, state.getTemporalTimer(), "Timer should reset after trigger");
        assertEquals(0, WarpProgressionLogic.getWarp(playerId, WarpProgressionLogic.WarpType.TEMPORARY), "Temporary warp should remain 0");
        assertEquals(50, WarpProgressionLogic.getWarp(playerId, WarpProgressionLogic.WarpType.NORMAL), "Normal warp should remain unaffected");
    }
}
