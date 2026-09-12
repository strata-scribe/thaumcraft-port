package thaumcraft.client.gui.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuraHudDialLogicTest {

    @Test
    void testCalculateNeedleAngle() {
        // Zero or negative max amount should return min angle
        assertEquals(0.0f, AuraHudDialLogic.calculateNeedleAngle(50f, 0f, 0.0f, 180.0f), 0.001f);

        // Exact minimum
        assertEquals(0.0f, AuraHudDialLogic.calculateNeedleAngle(0f, 100f, 0.0f, 180.0f), 0.001f);

        // Exact middle
        assertEquals(90.0f, AuraHudDialLogic.calculateNeedleAngle(50f, 100f, 0.0f, 180.0f), 0.001f);

        // Exact maximum
        assertEquals(180.0f, AuraHudDialLogic.calculateNeedleAngle(100f, 100f, 0.0f, 180.0f), 0.001f);

        // Over maximum is clamped
        assertEquals(180.0f, AuraHudDialLogic.calculateNeedleAngle(150f, 100f, 0.0f, 180.0f), 0.001f);

        // Under minimum is clamped
        assertEquals(0.0f, AuraHudDialLogic.calculateNeedleAngle(-50f, 100f, 0.0f, 180.0f), 0.001f);

        // Custom angle ranges (-90 to 90)
        assertEquals(0.0f, AuraHudDialLogic.calculateNeedleAngle(50f, 100f, -90.0f, 90.0f), 0.001f);
        assertEquals(90.0f, AuraHudDialLogic.calculateNeedleAngle(100f, 100f, -90.0f, 90.0f), 0.001f);
        assertEquals(-90.0f, AuraHudDialLogic.calculateNeedleAngle(0f, 100f, -90.0f, 90.0f), 0.001f);
    }

    @Test
    void testApplySpringPhysics() {
        float currentAngle = 0.0f;
        float targetAngle = 90.0f;
        float velocity = 0.0f;
        float springConstant = 0.1f;
        float dampening = 0.8f;

        // Step 1: Force pulls towards target
        float[] step1 = AuraHudDialLogic.applySpringPhysics(currentAngle, targetAngle, velocity, springConstant, dampening);
        float newAngle = step1[0];
        float newVelocity = step1[1];

        // F = -0.1 * (0 - 90) = 9
        // v = (0 + 9) * 0.8 = 7.2
        // pos = 0 + 7.2 = 7.2
        assertEquals(7.2f, newVelocity, 0.001f);
        assertEquals(7.2f, newAngle, 0.001f);

        // Step 2: Continuous applying should converge to target
        currentAngle = newAngle;
        velocity = newVelocity;

        for (int i = 0; i < 100; i++) {
            float[] result = AuraHudDialLogic.applySpringPhysics(currentAngle, targetAngle, velocity, springConstant, dampening);
            currentAngle = result[0];
            velocity = result[1];
        }

        // After many steps, should be very close to target and velocity near zero
        assertEquals(90.0f, currentAngle, 0.5f); // Use loose delta to account for slight oscillation
        assertEquals(0.0f, velocity, 0.1f);
    }

    @Test
    void testFormatPercentage() {
        assertEquals("0%", AuraHudDialLogic.formatPercentage(0f, 100f));
        assertEquals("50%", AuraHudDialLogic.formatPercentage(50f, 100f));
        assertEquals("100%", AuraHudDialLogic.formatPercentage(100f, 100f));

        // Over 100% clamps to 100%
        assertEquals("100%", AuraHudDialLogic.formatPercentage(150f, 100f));

        // Under 0% clamps to 0%
        assertEquals("0%", AuraHudDialLogic.formatPercentage(-10f, 100f));

        // Zero or negative max amount returns 0%
        assertEquals("0%", AuraHudDialLogic.formatPercentage(50f, 0f));
        assertEquals("0%", AuraHudDialLogic.formatPercentage(50f, -10f));

        // Fractional rounding
        assertEquals("33%", AuraHudDialLogic.formatPercentage(1f, 3f));
        assertEquals("67%", AuraHudDialLogic.formatPercentage(2f, 3f));
    }
}
