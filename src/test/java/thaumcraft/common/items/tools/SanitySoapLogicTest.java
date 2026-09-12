package thaumcraft.common.items.tools;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SanitySoapLogicTest {

    @BeforeEach
    public void setup() {
        SanitySoapLogic.clearWashData();
    }

    @Test
    public void testCalculateWarpReduction() {
        // 0 consecutive washes: 50% reduction
        assertEquals(50, SanitySoapLogic.calculateWarpReduction(100, 0));
        assertEquals(5, SanitySoapLogic.calculateWarpReduction(10, 0));

        // 1 consecutive wash: 25% reduction
        assertEquals(25, SanitySoapLogic.calculateWarpReduction(100, 1));

        // 2 consecutive washes: 16% reduction
        assertEquals(16, SanitySoapLogic.calculateWarpReduction(100, 2));

        // 3 consecutive washes: 12% reduction
        assertEquals(12, SanitySoapLogic.calculateWarpReduction(100, 3));

        // Ensure at least 1 point is removed if there's any warp and percentage is > 0.05
        assertEquals(1, SanitySoapLogic.calculateWarpReduction(1, 0));
        assertEquals(1, SanitySoapLogic.calculateWarpReduction(2, 1));

        // Ensure 0 reduction when there is 0 warp
        assertEquals(0, SanitySoapLogic.calculateWarpReduction(0, 0));

        // High consecutive washes should eventually yield 0
        assertEquals(0, SanitySoapLogic.calculateWarpReduction(10, 10)); // 0.5 / 11 = 0.045
    }

    @Test
    public void testWashTracking() {
        UUID player1 = UUID.randomUUID();
        UUID player2 = UUID.randomUUID();

        // Initial state
        assertEquals(0, SanitySoapLogic.getConsecutiveWashes(player1, 0));
        assertEquals(0, SanitySoapLogic.getConsecutiveWashes(player2, 0));

        // First wash
        SanitySoapLogic.recordWash(player1, 100);
        assertEquals(1, SanitySoapLogic.getConsecutiveWashes(player1, 100));
        assertEquals(1, SanitySoapLogic.getConsecutiveWashes(player1, 200));
        assertEquals(0, SanitySoapLogic.getConsecutiveWashes(player2, 100));

        // Second wash
        SanitySoapLogic.recordWash(player1, 500);
        assertEquals(2, SanitySoapLogic.getConsecutiveWashes(player1, 500));

        // Wash for player 2
        SanitySoapLogic.recordWash(player2, 500);
        assertEquals(1, SanitySoapLogic.getConsecutiveWashes(player2, 500));

        // Wash tracking reset after 24000 ticks
        assertEquals(0, SanitySoapLogic.getConsecutiveWashes(player1, 500 + 24000));

        // Recording a wash after 24000 ticks resets the count
        SanitySoapLogic.recordWash(player1, 500 + 24000);
        assertEquals(1, SanitySoapLogic.getConsecutiveWashes(player1, 500 + 24000));
    }
}
