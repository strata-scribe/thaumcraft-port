package thaumcraft.common.items.tools.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SanityCheckerLogic Domain Tests")
public class SanityCheckerLogicTest {

    @Test
    @DisplayName("Calculate total warp with positive, zero, and negative components")
    public void testCalculateTotalWarp() {
        // Positive inputs
        assertEquals(30, SanityCheckerLogic.calculateTotalWarp(5, 10, 15));
        assertEquals(6, SanityCheckerLogic.calculateTotalWarp(1, 2, 3));

        // Zero inputs
        assertEquals(0, SanityCheckerLogic.calculateTotalWarp(0, 0, 0));

        // Negative inputs clamped to 0
        assertEquals(0, SanityCheckerLogic.calculateTotalWarp(-5, -10, -2));

        // Mixed inputs
        assertEquals(30, SanityCheckerLogic.calculateTotalWarp(-5, 10, 20));
        assertEquals(7, SanityCheckerLogic.calculateTotalWarp(5, -10, 2));
        assertEquals(15, SanityCheckerLogic.calculateTotalWarp(5, 10, -20));
    }

    @Test
    @DisplayName("Evaluate warp danger levels: NONE, LOW, MODERATE, HIGH, CRITICAL")
    public void testGetWarpDangerLevel() {
        // NONE (<= 0)
        assertEquals("NONE", SanityCheckerLogic.getWarpDangerLevel(0));
        assertEquals("NONE", SanityCheckerLogic.getWarpDangerLevel(-10));

        // LOW (1 - 10)
        assertEquals("LOW", SanityCheckerLogic.getWarpDangerLevel(1));
        assertEquals("LOW", SanityCheckerLogic.getWarpDangerLevel(5));
        assertEquals("LOW", SanityCheckerLogic.getWarpDangerLevel(10));

        // MODERATE (11 - 25)
        assertEquals("MODERATE", SanityCheckerLogic.getWarpDangerLevel(11));
        assertEquals("MODERATE", SanityCheckerLogic.getWarpDangerLevel(18));
        assertEquals("MODERATE", SanityCheckerLogic.getWarpDangerLevel(25));

        // HIGH (26 - 50)
        assertEquals("HIGH", SanityCheckerLogic.getWarpDangerLevel(26));
        assertEquals("HIGH", SanityCheckerLogic.getWarpDangerLevel(35));
        assertEquals("HIGH", SanityCheckerLogic.getWarpDangerLevel(50));

        // CRITICAL (> 50)
        assertEquals("CRITICAL", SanityCheckerLogic.getWarpDangerLevel(51));
        assertEquals("CRITICAL", SanityCheckerLogic.getWarpDangerLevel(100));
        assertEquals("CRITICAL", SanityCheckerLogic.getWarpDangerLevel(500));
    }

    @Test
    @DisplayName("Calculate sanity percentage with bounds, threshold, and edge cases")
    public void testCalculateSanityPercentage() {
        // 100% sanity at 0 warp
        assertEquals(100.0f, SanityCheckerLogic.calculateSanityPercentage(0, 100), 1e-4f);

        // 50% sanity at half threshold
        assertEquals(50.0f, SanityCheckerLogic.calculateSanityPercentage(50, 100), 1e-4f);
        assertEquals(75.0f, SanityCheckerLogic.calculateSanityPercentage(25, 100), 1e-4f);

        // 0% sanity when reaching or exceeding threshold
        assertEquals(0.0f, SanityCheckerLogic.calculateSanityPercentage(100, 100), 1e-4f);
        assertEquals(0.0f, SanityCheckerLogic.calculateSanityPercentage(150, 100), 1e-4f);

        // Negative total warp clamped to 100%
        assertEquals(100.0f, SanityCheckerLogic.calculateSanityPercentage(-20, 100), 1e-4f);

        // Negative or zero threshold guard returns 0.0f
        assertEquals(0.0f, SanityCheckerLogic.calculateSanityPercentage(50, 0), 1e-4f);
        assertEquals(0.0f, SanityCheckerLogic.calculateSanityPercentage(50, -10), 1e-4f);
        assertEquals(0.0f, SanityCheckerLogic.calculateSanityPercentage(0, 0), 1e-4f);
    }

    @Test
    @DisplayName("Heartbeat trigger threshold (totalWarp >= 25)")
    public void testShouldPlayHeartbeatSound() {
        assertTrue(SanityCheckerLogic.shouldPlayHeartbeatSound(25));
        assertTrue(SanityCheckerLogic.shouldPlayHeartbeatSound(26));
        assertTrue(SanityCheckerLogic.shouldPlayHeartbeatSound(100));

        assertFalse(SanityCheckerLogic.shouldPlayHeartbeatSound(24));
        assertFalse(SanityCheckerLogic.shouldPlayHeartbeatSound(10));
        assertFalse(SanityCheckerLogic.shouldPlayHeartbeatSound(0));
        assertFalse(SanityCheckerLogic.shouldPlayHeartbeatSound(-5));
    }
}
