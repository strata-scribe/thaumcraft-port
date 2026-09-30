package thaumcraft.common.items.tools.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GolemBellLogic Headless Unit Tests")
public class GolemBellLogicTest {

    @Test
    @DisplayName("Verify canInteractWithSeal respects seal presence")
    public void testCanInteractWithSeal() {
        assertTrue(GolemBellLogic.canInteractWithSeal(true), "Should interact when target is a seal");
        assertFalse(GolemBellLogic.canInteractWithSeal(false), "Should not interact when target is not a seal");
    }

    @Test
    @DisplayName("Verify canBindGolem requires both target golem and sneaking")
    public void testCanBindGolem() {
        assertTrue(GolemBellLogic.canBindGolem(true, true), "Should bind when target golem is present and sneaking");
        assertFalse(GolemBellLogic.canBindGolem(true, false), "Should not bind when not sneaking");
        assertFalse(GolemBellLogic.canBindGolem(false, true), "Should not bind when no target golem");
        assertFalse(GolemBellLogic.canBindGolem(false, false), "Should not bind when neither present");
    }

    @Test
    @DisplayName("Verify formatGolemStatus handles null, working, and idle states")
    public void testFormatGolemStatus() {
        assertEquals("Unknown Golem", GolemBellLogic.formatGolemStatus(null, true),
                "Null name should return 'Unknown Golem'");
        assertEquals("Unknown Golem", GolemBellLogic.formatGolemStatus(null, false),
                "Null name should return 'Unknown Golem'");

        assertEquals("Clay Golem (Working)", GolemBellLogic.formatGolemStatus("Clay Golem", true),
                "Working golem status format");
        assertEquals("Thaumium Golem (Idle)", GolemBellLogic.formatGolemStatus("Thaumium Golem", false),
                "Idle golem status format");
    }
}
