package thaumcraft.common.golems;

import org.junit.jupiter.api.Test;
import thaumcraft.api.golems.parts.GolemAddon;
import thaumcraft.api.golems.parts.GolemArm;
import thaumcraft.api.golems.parts.GolemHead;
import thaumcraft.api.golems.parts.GolemLeg;
import thaumcraft.api.golems.parts.GolemMaterial;
import thaumcraft.api.golems.parts.PartModel;

import static org.junit.jupiter.api.Assertions.*;

public class GolemBuilderLogicTest {

    private GolemMaterial createMaterial(String key) {
        return new GolemMaterial(key, new String[0], null, 0, 0, 0, 0, null, null, null);
    }

    private GolemHead createHead() {
        return new GolemHead("HEAD", new String[0], null, (PartModel) null, new Object[0], null);
    }

    private GolemArm createArm() {
        return new GolemArm("ARM", new String[0], null, (PartModel) null, new Object[0], null);
    }

    private GolemLeg createLeg() {
        return new GolemLeg("LEG", new String[0], null, (PartModel) null, new Object[0], null);
    }

    private GolemAddon createAddon() {
        return new GolemAddon("ADDON", new String[0], null, (PartModel) null, new Object[0], null);
    }

    @Test
    public void testValidateParts() {
        GolemMaterial mat = createMaterial("WOOD");
        GolemHead head = createHead();
        GolemArm arm = createArm();
        GolemLeg leg = createLeg();
        GolemAddon addon = createAddon();

        // Missing material
        assertFalse(GolemBuilderLogic.validateParts(null, head, arm, leg, null));
        // Missing head
        assertFalse(GolemBuilderLogic.validateParts(mat, null, arm, leg, null));
        // Missing arm
        assertFalse(GolemBuilderLogic.validateParts(mat, head, null, leg, null));
        // Missing leg
        assertFalse(GolemBuilderLogic.validateParts(mat, head, arm, null, null));

        // Valid (addon is optional)
        assertTrue(GolemBuilderLogic.validateParts(mat, head, arm, leg, null));
        assertTrue(GolemBuilderLogic.validateParts(mat, head, arm, leg, addon));
    }

    @Test
    public void testGetBuildTime() {
        assertEquals(100, GolemBuilderLogic.getBuildTime(createMaterial("WOOD")));
        assertEquals(100, GolemBuilderLogic.getBuildTime(createMaterial("CLAY")));
        assertEquals(200, GolemBuilderLogic.getBuildTime(createMaterial("IRON")));
        assertEquals(200, GolemBuilderLogic.getBuildTime(createMaterial("BRASS")));
        assertEquals(300, GolemBuilderLogic.getBuildTime(createMaterial("THAUMIUM")));
        assertEquals(400, GolemBuilderLogic.getBuildTime(createMaterial("VOID")));
        assertEquals(100, GolemBuilderLogic.getBuildTime(createMaterial("UNKNOWN")));
        assertEquals(0, GolemBuilderLogic.getBuildTime(null));
    }

    @Test
    public void testGetVisCost() {
        GolemMaterial wood = createMaterial("WOOD");
        GolemMaterial iron = createMaterial("IRON");
        GolemHead head = createHead();
        GolemArm arm = createArm();
        GolemLeg leg = createLeg();
        GolemAddon addon = createAddon();

        // Wood (10) + head (5) + arm (5) + leg (5) = 25
        assertEquals(25, GolemBuilderLogic.getVisCost(wood, head, arm, leg, null));
        // Wood + all parts including addon (10) = 35
        assertEquals(35, GolemBuilderLogic.getVisCost(wood, head, arm, leg, addon));

        // Iron (20) + all parts = 45
        assertEquals(45, GolemBuilderLogic.getVisCost(iron, head, arm, leg, addon));
    }

    @Test
    public void testGetClayCost() {
        GolemMaterial wood = createMaterial("WOOD");
        GolemHead head = createHead();
        GolemArm arm = createArm();
        GolemLeg leg = createLeg();
        GolemAddon addon = createAddon();

        // Base (1) + Material (1) = 2
        assertEquals(2, GolemBuilderLogic.getClayCost(wood, null, null, null, null));

        // Full without addon = 1(base) + 4 = 5
        assertEquals(5, GolemBuilderLogic.getClayCost(wood, head, arm, leg, null));

        // Full with addon = 1(base) + 5 = 6
        assertEquals(6, GolemBuilderLogic.getClayCost(wood, head, arm, leg, addon));
    }
}
