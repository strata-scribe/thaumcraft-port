package thaumcraft.common.items.armor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BootsLiquidLogic Comprehensive Unit Tests")
class BootsLiquidLogicTest {

    private static final double EPSILON = 1e-6;

    @Nested
    @DisplayName("canWalkOnLiquid Tests")
    class CanWalkOnLiquidTests {

        @Test
        @DisplayName("Returns true when charged, moving forward, sprinting, and not sneaking")
        void testValidLiquidWalkingState() {
            assertTrue(BootsLiquidLogic.canWalkOnLiquid(true, true, false, true));
        }

        @Test
        @DisplayName("Returns false when uncharged")
        void testUncharged() {
            assertFalse(BootsLiquidLogic.canWalkOnLiquid(false, true, false, true));
        }

        @Test
        @DisplayName("Returns false when not moving forward")
        void testNotMovingForward() {
            assertFalse(BootsLiquidLogic.canWalkOnLiquid(true, false, false, true));
        }

        @Test
        @DisplayName("Returns false when not sprinting")
        void testNotSprinting() {
            assertFalse(BootsLiquidLogic.canWalkOnLiquid(true, true, false, false));
        }

        @Test
        @DisplayName("Returns false when sneaking")
        void testSneaking() {
            assertFalse(BootsLiquidLogic.canWalkOnLiquid(true, true, true, true));
        }
    }

    @Nested
    @DisplayName("isAtOrAboveLiquidSurface Tests")
    class IsAtOrAboveLiquidSurfaceTests {

        @Test
        @DisplayName("Returns true when player Y is well above the liquid surface")
        void testPlayerAboveSurface() {
            assertTrue(BootsLiquidLogic.isAtOrAboveLiquidSurface(10.0, 9.0));
        }

        @Test
        @DisplayName("Returns true when player Y is exactly at the liquid surface")
        void testPlayerAtSurface() {
            assertTrue(BootsLiquidLogic.isAtOrAboveLiquidSurface(10.0, 10.0));
        }

        @Test
        @DisplayName("Returns true when player Y is slightly below the liquid surface but within tolerance")
        void testPlayerSlightlyBelowWithinTolerance() {
            assertTrue(BootsLiquidLogic.isAtOrAboveLiquidSurface(9.95, 10.0));
            assertTrue(BootsLiquidLogic.isAtOrAboveLiquidSurface(9.9, 10.0));
        }

        @Test
        @DisplayName("Returns false when player Y is below the tolerance margin")
        void testPlayerBelowTolerance() {
            assertFalse(BootsLiquidLogic.isAtOrAboveLiquidSurface(9.8, 10.0));
            assertFalse(BootsLiquidLogic.isAtOrAboveLiquidSurface(8.0, 10.0));
        }
    }

    @Nested
    @DisplayName("calculateVerticalVelocityOverride Tests")
    class CalculateVerticalVelocityOverrideTests {

        @Test
        @DisplayName("Returns 0.0 when falling (negative Y motion)")
        void testFallingReturnsZero() {
            assertEquals(0.0, BootsLiquidLogic.calculateVerticalVelocityOverride(-0.5), EPSILON);
            assertEquals(0.0, BootsLiquidLogic.calculateVerticalVelocityOverride(-0.1), EPSILON);
        }

        @Test
        @DisplayName("Returns current motion when jumping/rising (positive Y motion)")
        void testRisingReturnsCurrent() {
            assertEquals(0.5, BootsLiquidLogic.calculateVerticalVelocityOverride(0.5), EPSILON);
            assertEquals(0.1, BootsLiquidLogic.calculateVerticalVelocityOverride(0.1), EPSILON);
        }

        @Test
        @DisplayName("Returns 0.0 when motion is exactly 0.0")
        void testZeroReturnsZero() {
            assertEquals(0.0, BootsLiquidLogic.calculateVerticalVelocityOverride(0.0), EPSILON);
        }
    }
}
