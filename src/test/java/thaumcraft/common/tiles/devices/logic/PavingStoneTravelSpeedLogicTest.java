package thaumcraft.common.tiles.devices.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PavingStoneTravelSpeedLogicTest {

    @Test
    public void testCalculateSpeedBoostMultiplier() {
        assertEquals(1.0, PavingStoneTravelSpeedLogic.calculateSpeedBoostMultiplier(-5), 0.001);
        assertEquals(1.0, PavingStoneTravelSpeedLogic.calculateSpeedBoostMultiplier(0), 0.001);
        assertEquals(1.25, PavingStoneTravelSpeedLogic.calculateSpeedBoostMultiplier(10), 0.001);
        assertEquals(1.5, PavingStoneTravelSpeedLogic.calculateSpeedBoostMultiplier(20), 0.001);
        assertEquals(1.5, PavingStoneTravelSpeedLogic.calculateSpeedBoostMultiplier(100), 0.001);
    }

    @Test
    public void testCalculateJumpBoostMultiplier() {
        assertEquals(1.0, PavingStoneTravelSpeedLogic.calculateJumpBoostMultiplier(-2), 0.001);
        assertEquals(1.0, PavingStoneTravelSpeedLogic.calculateJumpBoostMultiplier(0), 0.001);
        assertEquals(1.2, PavingStoneTravelSpeedLogic.calculateJumpBoostMultiplier(10), 0.001);
        assertEquals(1.3, PavingStoneTravelSpeedLogic.calculateJumpBoostMultiplier(15), 0.001);
        assertEquals(1.3, PavingStoneTravelSpeedLogic.calculateJumpBoostMultiplier(50), 0.001);
    }

    @Test
    public void testCalculateContainmentDeceleration() {
        double radius = 10.0;

        // Inner region
        assertEquals(1.0, PavingStoneTravelSpeedLogic.calculateContainmentDeceleration(0.0, radius), 0.001);
        assertEquals(1.0, PavingStoneTravelSpeedLogic.calculateContainmentDeceleration(5.0, radius), 0.001);
        assertEquals(1.0, PavingStoneTravelSpeedLogic.calculateContainmentDeceleration(8.0, radius), 0.001);

        // Deceleration region (8.0 to 10.0)
        // distance = 9.0 -> penetration = 1.0 / 2.0 = 0.5 -> 1 - 0.25 = 0.75
        assertEquals(0.75, PavingStoneTravelSpeedLogic.calculateContainmentDeceleration(9.0, radius), 0.001);

        // Edge and beyond
        assertEquals(0.0, PavingStoneTravelSpeedLogic.calculateContainmentDeceleration(10.0, radius), 0.001);
        assertEquals(0.0, PavingStoneTravelSpeedLogic.calculateContainmentDeceleration(12.0, radius), 0.001);

        // Edge cases
        assertEquals(1.0, PavingStoneTravelSpeedLogic.calculateContainmentDeceleration(-1.0, radius), 0.001);
        assertEquals(1.0, PavingStoneTravelSpeedLogic.calculateContainmentDeceleration(5.0, 0.0), 0.001);
    }
}
