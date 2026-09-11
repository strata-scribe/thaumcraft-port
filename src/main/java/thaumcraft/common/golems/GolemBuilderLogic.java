package thaumcraft.common.golems;

import thaumcraft.api.golems.parts.GolemAddon;
import thaumcraft.api.golems.parts.GolemArm;
import thaumcraft.api.golems.parts.GolemHead;
import thaumcraft.api.golems.parts.GolemLeg;
import thaumcraft.api.golems.parts.GolemMaterial;
import thaumcraft.api.golems.IGolemProperties;

public class GolemBuilderLogic {

    /**
     * Validates if a golem can be built.
     * Material, Head, Arms, and Legs are required. Addon is optional.
     */
    public static boolean validateParts(GolemMaterial material, GolemHead head, GolemArm arm, GolemLeg leg, GolemAddon addon) {
        return material != null && head != null && arm != null && leg != null;
    }

    /**
     * Calculates construction time based on material tier.
     * Lower tier (WOOD, CLAY) = 100 ticks
     * Mid tier (IRON, BRASS) = 200 ticks
     * High tier (THAUMIUM) = 300 ticks
     * Top tier (VOID) = 400 ticks
     */
    public static int getBuildTime(GolemMaterial material) {
        if (material == null) return 0;

        String key = material.key.toUpperCase();
        switch (key) {
            case "WOOD":
            case "CLAY":
                return 100;
            case "IRON":
            case "BRASS":
                return 200;
            case "THAUMIUM":
                return 300;
            case "VOID":
                return 400;
            default:
                return 100;
        }
    }

    /**
     * Vis cost calculation (arbitrary example scaling based on material and parts)
     */
    public static int getVisCost(GolemMaterial material, GolemHead head, GolemArm arm, GolemLeg leg, GolemAddon addon) {
        int cost = 0;
        if (material != null) {
            switch (material.key.toUpperCase()) {
                case "WOOD": case "CLAY": cost += 10; break;
                case "IRON": case "BRASS": cost += 20; break;
                case "THAUMIUM": cost += 30; break;
                case "VOID": cost += 40; break;
                default: cost += 10; break;
            }
        }
        if (head != null) cost += 5;
        if (arm != null) cost += 5;
        if (leg != null) cost += 5;
        if (addon != null) cost += 10;
        return cost;
    }

    /**
     * Clay cost or component cost
     */
    public static int getClayCost(GolemMaterial material, GolemHead head, GolemArm arm, GolemLeg leg, GolemAddon addon) {
        int cost = 1; // Base core cost
        if (material != null) cost += 1;
        if (head != null) cost += 1;
        if (arm != null) cost += 1;
        if (leg != null) cost += 1;
        if (addon != null) cost += 1;
        return cost;
    }
}
