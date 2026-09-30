package thaumcraft.common.items.tools.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TurretPlacerLogic Unit Tests")
public class TurretPlacerLogicTest {

    @Test
    @DisplayName("Verify canPlaceOnFace only allows UP face (case-insensitive)")
    void testCanPlaceOnFace() {
        assertTrue(TurretPlacerLogic.canPlaceOnFace("UP"));
        assertTrue(TurretPlacerLogic.canPlaceOnFace("up"));
        assertTrue(TurretPlacerLogic.canPlaceOnFace("Up"));

        assertFalse(TurretPlacerLogic.canPlaceOnFace("DOWN"));
        assertFalse(TurretPlacerLogic.canPlaceOnFace("down"));
        assertFalse(TurretPlacerLogic.canPlaceOnFace("NORTH"));
        assertFalse(TurretPlacerLogic.canPlaceOnFace("SOUTH"));
        assertFalse(TurretPlacerLogic.canPlaceOnFace("EAST"));
        assertFalse(TurretPlacerLogic.canPlaceOnFace("WEST"));
        assertFalse(TurretPlacerLogic.canPlaceOnFace(null));
        assertFalse(TurretPlacerLogic.canPlaceOnFace(""));
    }

    @Test
    @DisplayName("Verify canSurviveOn checks solid surface requirement")
    void testCanSurviveOn() {
        assertTrue(TurretPlacerLogic.canSurviveOn(true));
        assertFalse(TurretPlacerLogic.canSurviveOn(false));
    }

    @Test
    @DisplayName("Verify placement cooldown is 10 ticks")
    void testGetPlacementCooldown() {
        assertEquals(10, TurretPlacerLogic.getPlacementCooldown());
    }
}
