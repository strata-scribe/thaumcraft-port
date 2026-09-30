package thaumcraft.common.items.consumables.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AlumentumLogic Unit Tests")
public class AlumentumLogicTest {

    @Test
    @DisplayName("Verify burn time, smelter speed, and explosion radius constants")
    void testBasicConstants() {
        assertEquals(6400, AlumentumLogic.getBurnTime());
        assertEquals(0.8f, AlumentumLogic.getSmelterSpeedMultiplier(), 1e-6f);
        assertEquals(1.5f, AlumentumLogic.getExplosionRadius(), 1e-6f);
    }

    @Test
    @DisplayName("Verify calculateImpactDamage clamping between 0 and 20")
    void testCalculateImpactDamage() {
        // Negative velocity clamped to 0
        assertEquals(0.0f, AlumentumLogic.calculateImpactDamage(-2.0f), 1e-6f);
        assertEquals(0.0f, AlumentumLogic.calculateImpactDamage(0.0f), 1e-6f);

        // Linear scaling: velocity * 4
        assertEquals(4.0f, AlumentumLogic.calculateImpactDamage(1.0f), 1e-6f);
        assertEquals(8.0f, AlumentumLogic.calculateImpactDamage(2.0f), 1e-6f);
        assertEquals(16.0f, AlumentumLogic.calculateImpactDamage(4.0f), 1e-6f);

        // Clamped at max 20
        assertEquals(20.0f, AlumentumLogic.calculateImpactDamage(5.0f), 1e-6f);
        assertEquals(20.0f, AlumentumLogic.calculateImpactDamage(10.0f), 1e-6f);
    }

    @Test
    @DisplayName("Verify shouldIgniteTarget roll threshold")
    void testShouldIgniteTarget() {
        assertTrue(AlumentumLogic.shouldIgniteTarget(0.0f));
        assertTrue(AlumentumLogic.shouldIgniteTarget(0.5f));
        assertTrue(AlumentumLogic.shouldIgniteTarget(0.749f));

        assertFalse(AlumentumLogic.shouldIgniteTarget(0.75f));
        assertFalse(AlumentumLogic.shouldIgniteTarget(0.8f));
        assertFalse(AlumentumLogic.shouldIgniteTarget(1.0f));
    }
}
