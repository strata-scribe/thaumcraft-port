package thaumcraft.common.lib.crafting;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class VoidMetalInfusionRequirementLogicTest {

    @Test
    public void testNonVoidItemPermitted() {
        // Any warp level should allow non-void items
        assertTrue(VoidMetalInfusionRequirementLogic.isRecipePermitted(0, "thaumium_ingot"));
        assertTrue(VoidMetalInfusionRequirementLogic.isRecipePermitted(10, "golem_bell"));
        assertTrue(VoidMetalInfusionRequirementLogic.isRecipePermitted(20, "arcane_stone"));
    }

    @Test
    public void testVoidItemDeniedBelowThreshold() {
        // Warp level below 15 should deny void items
        assertFalse(VoidMetalInfusionRequirementLogic.isRecipePermitted(0, "void_metal_ingot"));
        assertFalse(VoidMetalInfusionRequirementLogic.isRecipePermitted(14, "void_seed"));
        assertFalse(VoidMetalInfusionRequirementLogic.isRecipePermitted(5, "void_robe"));
    }

    @Test
    public void testVoidItemPermittedAtThreshold() {
        // Warp level at 15 should permit void items
        assertTrue(VoidMetalInfusionRequirementLogic.isRecipePermitted(15, "void_metal_ingot"));
        assertTrue(VoidMetalInfusionRequirementLogic.isRecipePermitted(15, "void_seed"));
    }

    @Test
    public void testVoidItemPermittedAboveThreshold() {
        // Warp level above 15 should permit void items
        assertTrue(VoidMetalInfusionRequirementLogic.isRecipePermitted(16, "void_metal_ingot"));
        assertTrue(VoidMetalInfusionRequirementLogic.isRecipePermitted(50, "void_seed"));
        assertTrue(VoidMetalInfusionRequirementLogic.isRecipePermitted(100, "void_robe"));
    }

    @Test
    public void testNullOrEmptyItemPermitted() {
        assertTrue(VoidMetalInfusionRequirementLogic.isRecipePermitted(0, null));
        assertTrue(VoidMetalInfusionRequirementLogic.isRecipePermitted(0, ""));
    }
}
