package thaumcraft.common.tiles.crafting;

public class InfusionStabiliserMathLogic {

    public static float calcDiminishingReturns(float base, int count) {
        if (count > 0) {
            return base * (float) Math.pow(0.75, count);
        }
        return base;
    }

    public static int getAncientPillarCycleTimeModifier() {
        return -1;
    }

    public static float getAncientPillarCostModifier() {
        return -0.1f;
    }

    public static float getAncientPillarStabilityModifier() {
        return -0.1f;
    }

    public static int getEldritchPillarCycleTimeModifier() {
        return -3;
    }

    public static float getEldritchPillarCostModifier() {
        return 0.05f;
    }

    public static float getEldritchPillarStabilityModifier() {
        return 0.2f;
    }

    public static float getPedestalEldritchCostModifier() {
        return 0.0025f;
    }

    public static float getPedestalAncientCostModifier() {
        return -0.01f;
    }
}
