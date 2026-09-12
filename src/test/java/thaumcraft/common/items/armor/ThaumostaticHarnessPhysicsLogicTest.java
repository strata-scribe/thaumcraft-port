package thaumcraft.common.items.armor;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ThaumostaticHarnessPhysicsLogicTest {

    @Test
    public void testSimulateAerodynamicDrag() {
        assertEquals(0.95, ThaumostaticHarnessPhysicsLogic.simulateAerodynamicDrag(1.0), 0.001);
        assertEquals(-0.95, ThaumostaticHarnessPhysicsLogic.simulateAerodynamicDrag(-1.0), 0.001);
        assertEquals(0.0, ThaumostaticHarnessPhysicsLogic.simulateAerodynamicDrag(0.0), 0.001);
    }

    @Test
    public void testSimulateHoverStability() {
        // Hovering active
        assertEquals(0.85, ThaumostaticHarnessPhysicsLogic.simulateHoverStability(1.0, true), 0.001);
        assertEquals(-0.85, ThaumostaticHarnessPhysicsLogic.simulateHoverStability(-1.0, true), 0.001);

        // Snap to 0.0 when below threshold (0.01)
        assertEquals(0.0, ThaumostaticHarnessPhysicsLogic.simulateHoverStability(0.005, true), 0.001);
        assertEquals(0.0, ThaumostaticHarnessPhysicsLogic.simulateHoverStability(-0.005, true), 0.001);

        // Hovering inactive
        assertEquals(1.0, ThaumostaticHarnessPhysicsLogic.simulateHoverStability(1.0, false), 0.001);
        assertEquals(-1.0, ThaumostaticHarnessPhysicsLogic.simulateHoverStability(-1.0, false), 0.001);
        assertEquals(0.005, ThaumostaticHarnessPhysicsLogic.simulateHoverStability(0.005, false), 0.001);
    }

    @Test
    public void testSimulateSprintMomentumDampening() {
        // Stopped sprinting
        assertEquals(1.6, ThaumostaticHarnessPhysicsLogic.simulateSprintMomentumDampening(2.0, false, true), 0.001);
        assertEquals(-1.6, ThaumostaticHarnessPhysicsLogic.simulateSprintMomentumDampening(-2.0, false, true), 0.001);

        // Still sprinting
        assertEquals(2.0, ThaumostaticHarnessPhysicsLogic.simulateSprintMomentumDampening(2.0, true, true), 0.001);

        // Not sprinting, and wasn't sprinting
        assertEquals(2.0, ThaumostaticHarnessPhysicsLogic.simulateSprintMomentumDampening(2.0, false, false), 0.001);

        // Just started sprinting
        assertEquals(2.0, ThaumostaticHarnessPhysicsLogic.simulateSprintMomentumDampening(2.0, true, false), 0.001);
    }
}
