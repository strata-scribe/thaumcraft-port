package thaumcraft.common.tiles.crafting;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InfusionLightningLogicTest {

    @Test
    public void testShouldStrike() {
        // High instability, high cycle time -> high threshold
        // threshold = 0.05 + (10.0 * 0.01) + (1000 * 0.0001) = 0.05 + 0.1 + 0.1 = 0.25
        assertTrue(InfusionLightningLogic.shouldStrike(10.0f, 1000, 0.24f));
        assertFalse(InfusionLightningLogic.shouldStrike(10.0f, 1000, 0.26f));

        // Zero instability, zero cycle time -> base threshold
        // threshold = 0.05 + 0 + 0 = 0.05
        assertTrue(InfusionLightningLogic.shouldStrike(0.0f, 0, 0.04f));
        assertFalse(InfusionLightningLogic.shouldStrike(0.0f, 0, 0.06f));
    }

    @Test
    public void testDetermineTarget() {
        // Player target: randomFloat < 0.2f
        assertEquals(InfusionLightningLogic.TargetType.PLAYER, InfusionLightningLogic.determineTarget(0.1f));
        assertEquals(InfusionLightningLogic.TargetType.PLAYER, InfusionLightningLogic.determineTarget(0.199f));

        // Pedestal target: 0.2f <= randomFloat < 0.5f
        assertEquals(InfusionLightningLogic.TargetType.PEDESTAL, InfusionLightningLogic.determineTarget(0.2f));
        assertEquals(InfusionLightningLogic.TargetType.PEDESTAL, InfusionLightningLogic.determineTarget(0.499f));

        // Block target: randomFloat >= 0.5f
        assertEquals(InfusionLightningLogic.TargetType.BLOCK, InfusionLightningLogic.determineTarget(0.5f));
        assertEquals(InfusionLightningLogic.TargetType.BLOCK, InfusionLightningLogic.determineTarget(0.9f));
    }

    @Test
    public void testCalculateDamage() {
        // Base case: 0 instability, 0 randomFloat -> 4.0 damage
        assertEquals(4.0f, InfusionLightningLogic.calculateDamage(0.0f, 0.0f), 0.001f);

        // High instability: 10 instability, 0 randomFloat -> 4.0 + 5.0 + 0 = 9.0 damage
        assertEquals(9.0f, InfusionLightningLogic.calculateDamage(10.0f, 0.0f), 0.001f);

        // High randomFloat: 0 instability, 1.0 randomFloat -> 4.0 + 0 + 4.0 = 8.0 damage
        assertEquals(8.0f, InfusionLightningLogic.calculateDamage(0.0f, 1.0f), 0.001f);

        // Max case: 10 instability, 1.0 randomFloat -> 4.0 + 5.0 + 4.0 = 13.0 damage
        assertEquals(13.0f, InfusionLightningLogic.calculateDamage(10.0f, 1.0f), 0.001f);

        // Negative instability (should still be at least 1.0)
        assertEquals(1.0f, InfusionLightningLogic.calculateDamage(-10.0f, 0.0f), 0.001f);
    }
}
