package thaumcraft.common.golems.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

public class GolemLegLogicTest {

    @Test
    @DisplayName("Stilt trait sets step height to 1.25 blocks")
    void testGetStepHeight() {
        float baseHeight = 0.6f;

        // With stilt trait
        assertEquals(1.25f, GolemLegLogic.getStepHeight(baseHeight, Set.of("stilt", "climber")));
        assertEquals(1.25f, GolemLegLogic.getStepHeight(baseHeight, Set.of("STILT")));

        // Without stilt trait
        assertEquals(baseHeight, GolemLegLogic.getStepHeight(baseHeight, Set.of("wheeled", "scout")));
        assertEquals(baseHeight, GolemLegLogic.getStepHeight(baseHeight, Set.of()));
        assertEquals(baseHeight, GolemLegLogic.getStepHeight(baseHeight, null));
    }

    @Test
    @DisplayName("Wheels trait multiplies speed by 1.5x on smooth surfaces")
    void testGetMovementSpeedMultiplier() {
        // With wheels on smooth surface
        assertEquals(1.5f, GolemLegLogic.getMovementSpeedMultiplier(true, Set.of("wheeled", "heavy")));
        assertEquals(1.5f, GolemLegLogic.getMovementSpeedMultiplier(true, Set.of("wheels")));
        assertEquals(1.5f, GolemLegLogic.getMovementSpeedMultiplier(true, Set.of("WHEELED")));

        // With wheels on non-smooth surface
        assertEquals(1.0f, GolemLegLogic.getMovementSpeedMultiplier(false, Set.of("wheeled", "heavy")));

        // Without wheels trait
        assertEquals(1.0f, GolemLegLogic.getMovementSpeedMultiplier(true, Set.of("stilt")));
        assertEquals(1.0f, GolemLegLogic.getMovementSpeedMultiplier(true, Set.of()));
        assertEquals(1.0f, GolemLegLogic.getMovementSpeedMultiplier(true, null));
    }

    @Test
    @DisplayName("Climber trait allows ladder climbing")
    void testCanClimb() {
        assertTrue(GolemLegLogic.canClimb(Set.of("climber", "heavy")));
        assertTrue(GolemLegLogic.canClimb(Set.of("CLIMBER")));
        assertFalse(GolemLegLogic.canClimb(Set.of("wheeled", "stilt")));
        assertFalse(GolemLegLogic.canClimb(Set.of()));
        assertFalse(GolemLegLogic.canClimb(null));
    }

    @Test
    @DisplayName("Flier trait allows propeller flight over obstacles")
    void testCanFly() {
        assertTrue(GolemLegLogic.canFly(Set.of("flyer", "fragile")));
        assertTrue(GolemLegLogic.canFly(Set.of("flier")));
        assertTrue(GolemLegLogic.canFly(Set.of("FLYER")));
        assertFalse(GolemLegLogic.canFly(Set.of("climber", "wheeled")));
        assertFalse(GolemLegLogic.canFly(Set.of()));
        assertFalse(GolemLegLogic.canFly(null));
    }
}
