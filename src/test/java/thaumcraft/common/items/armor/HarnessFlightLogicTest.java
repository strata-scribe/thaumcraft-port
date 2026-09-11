package thaumcraft.common.items.armor;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HarnessFlightLogicTest {

    @Test
    public void testCalculateHarnessVisDrain() {
        assertEquals(0.0f, HarnessFlightLogic.calculateHarnessVisDrain(false, false, false), 0.001f);
        assertEquals(2.5f, HarnessFlightLogic.calculateHarnessVisDrain(true, false, false), 0.001f);
        assertEquals(1.0f, HarnessFlightLogic.calculateHarnessVisDrain(false, true, false), 0.001f);

        // Sprinting tests
        assertEquals(7.5f, HarnessFlightLogic.calculateHarnessVisDrain(true, false, true), 0.001f);
        assertEquals(3.0f, HarnessFlightLogic.calculateHarnessVisDrain(false, true, true), 0.001f);
        assertEquals(0.0f, HarnessFlightLogic.calculateHarnessVisDrain(false, false, true), 0.001f);
    }

    @Test
    public void testCalculateHarnessDrainPerTick() {
        assertEquals(0.0f, HarnessFlightLogic.calculateHarnessDrainPerTick(false, false, false), 0.001f);
        assertEquals(2.5f / 20.0f, HarnessFlightLogic.calculateHarnessDrainPerTick(true, false, false), 0.001f);
        assertEquals(1.0f / 20.0f, HarnessFlightLogic.calculateHarnessDrainPerTick(false, true, false), 0.001f);

        // Sprinting tests
        assertEquals(7.5f / 20.0f, HarnessFlightLogic.calculateHarnessDrainPerTick(true, false, true), 0.001f);
        assertEquals(3.0f / 20.0f, HarnessFlightLogic.calculateHarnessDrainPerTick(false, true, true), 0.001f);
    }

    @Test
    public void testCalculateHarnessSpeed() {
        assertEquals(1.0, HarnessFlightLogic.calculateHarnessSpeed(1.0, 1.0), 0.001);
        assertEquals(0.15, HarnessFlightLogic.calculateHarnessSpeed(0.0, 1.0), 0.001);
        assertEquals(0.85, HarnessFlightLogic.calculateHarnessSpeed(1.0, 0.0), 0.001);
    }

    @Test
    public void testCalculateHarnessVerticalMotion() {
        // Jump + Sneak (hover active)
        assertEquals(0.0, HarnessFlightLogic.calculateHarnessVerticalMotion(0.0, true, true, true), 0.001);
        assertEquals(0.0, HarnessFlightLogic.calculateHarnessVerticalMotion(0.005, true, true, true), 0.001);
        assertEquals(0.85, HarnessFlightLogic.calculateHarnessVerticalMotion(1.0, true, true, true), 0.001);

        // Jump + Sneak (hover inactive)
        assertEquals(1.0, HarnessFlightLogic.calculateHarnessVerticalMotion(1.0, true, true, false), 0.001);

        // Jump only
        assertEquals(0.15, HarnessFlightLogic.calculateHarnessVerticalMotion(0.0, true, false, false), 0.001);
        assertEquals(0.60, HarnessFlightLogic.calculateHarnessVerticalMotion(0.5, true, false, false), 0.001);
        assertEquals(0.60, HarnessFlightLogic.calculateHarnessVerticalMotion(0.8, true, false, false), 0.001);

        // Sneak only
        assertEquals(-0.15, HarnessFlightLogic.calculateHarnessVerticalMotion(0.0, false, true, false), 0.001);
        assertEquals(-0.40, HarnessFlightLogic.calculateHarnessVerticalMotion(-0.3, false, true, false), 0.001);
        assertEquals(-0.40, HarnessFlightLogic.calculateHarnessVerticalMotion(-0.6, false, true, false), 0.001);

        // Hover only
        assertEquals(0.0, HarnessFlightLogic.calculateHarnessVerticalMotion(0.005, false, false, true), 0.001);
        assertEquals(0.85, HarnessFlightLogic.calculateHarnessVerticalMotion(1.0, false, false, true), 0.001);

        // None
        assertEquals(1.0, HarnessFlightLogic.calculateHarnessVerticalMotion(1.0, false, false, false), 0.001);
    }

    @Test
    public void testCalculateHarnessDescentDamping() {
        assertEquals(-0.3, HarnessFlightLogic.calculateHarnessDescentDamping(-0.5), 0.001);
        assertEquals(-0.2, HarnessFlightLogic.calculateHarnessDescentDamping(-0.2), 0.001);
        assertEquals(0.0, HarnessFlightLogic.calculateHarnessDescentDamping(0.0), 0.001);
        assertEquals(0.5, HarnessFlightLogic.calculateHarnessDescentDamping(0.5), 0.001);
    }

    @Test
    public void testCalculateForwardSpeedBoost() {
        assertEquals(1.0, HarnessFlightLogic.calculateForwardSpeedBoost(1.0, false), 0.001);
        assertEquals(2.0, HarnessFlightLogic.calculateForwardSpeedBoost(1.0, true), 0.001);
        assertEquals(5.0, HarnessFlightLogic.calculateForwardSpeedBoost(2.5, true), 0.001);
    }
}
