package thaumcraft.common.entities.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VitiumGasLogicTest {

    @Test
    void testSimulateDiffusion() {
        // Initial state: volume 100, concentration 1.0, mass = 100
        // Diffusion rate: 0.1 per unit time, time = 2.0
        // newVolume = 100 + (100 * 0.1 * 2.0) = 120
        // newConcentration = 100 / 120 = 0.8333...

        VitiumGasLogic.GasState newState = VitiumGasLogic.simulateDiffusion(100.0, 1.0, 0.1, 2.0);

        assertEquals(120.0, newState.volume(), 0.001);
        assertEquals(100.0 / 120.0, newState.concentration(), 0.001);
    }

    @Test
    void testSimulateDiffusionZeroVolume() {
        VitiumGasLogic.GasState newState = VitiumGasLogic.simulateDiffusion(0.0, 1.0, 0.1, 2.0);

        assertEquals(0.0, newState.volume());
        assertEquals(0.0, newState.concentration());
    }

    @Test
    void testSimulateWindDrift() {
        // Initial pos: (10, 20, 30)
        // Wind: (1, 0, -1)
        // Drift coefficient: 0.5
        // Delta time: 2.0
        // newX = 10 + (1 * 0.5 * 2.0) = 11
        // newY = 20 + (0 * 0.5 * 2.0) = 20
        // newZ = 30 + (-1 * 0.5 * 2.0) = 29

        VitiumGasLogic.Position newPos = VitiumGasLogic.simulateWindDrift(10.0, 20.0, 30.0, 1.0, 0.0, -1.0, 0.5, 2.0);

        assertEquals(11.0, newPos.x(), 0.001);
        assertEquals(20.0, newPos.y(), 0.001);
        assertEquals(29.0, newPos.z(), 0.001);
    }

    @Test
    void testCalculateToxicDamageAboveThreshold() {
        // concentration: 2.0
        // threshold: 1.0
        // baseDamageRate: 5.0
        // exposureTime: 2.0
        // damage = (2.0 - 1.0) * 5.0 * 2.0 = 10.0

        double damage = VitiumGasLogic.calculateToxicDamage(2.0, 5.0, 1.0, 2.0);
        assertEquals(10.0, damage, 0.001);
    }

    @Test
    void testCalculateToxicDamageBelowThreshold() {
        // concentration: 0.5
        // threshold: 1.0

        double damage = VitiumGasLogic.calculateToxicDamage(0.5, 5.0, 1.0, 2.0);
        assertEquals(0.0, damage, 0.001);
    }

    @Test
    void testCalculateToxicDamageAtThreshold() {
        // concentration: 1.0
        // threshold: 1.0

        double damage = VitiumGasLogic.calculateToxicDamage(1.0, 5.0, 1.0, 2.0);
        assertEquals(0.0, damage, 0.001);
    }
}
