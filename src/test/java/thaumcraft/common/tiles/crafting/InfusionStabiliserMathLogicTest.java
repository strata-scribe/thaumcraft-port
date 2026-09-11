package thaumcraft.common.tiles.crafting;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InfusionStabiliserMathLogicTest {

    @Test
    public void testCalcDiminishingReturns() {
        assertEquals(10.0f, InfusionStabiliserMathLogic.calcDiminishingReturns(10.0f, 0), 0.001f);
        assertEquals(7.5f, InfusionStabiliserMathLogic.calcDiminishingReturns(10.0f, 1), 0.001f);
        assertEquals(5.625f, InfusionStabiliserMathLogic.calcDiminishingReturns(10.0f, 2), 0.001f);
        assertEquals(4.21875f, InfusionStabiliserMathLogic.calcDiminishingReturns(10.0f, 3), 0.001f);
    }

    @Test
    public void testPillarModifiers() {
        assertEquals(-1, InfusionStabiliserMathLogic.getAncientPillarCycleTimeModifier());
        assertEquals(-0.1f, InfusionStabiliserMathLogic.getAncientPillarCostModifier(), 0.001f);
        assertEquals(-0.1f, InfusionStabiliserMathLogic.getAncientPillarStabilityModifier(), 0.001f);

        assertEquals(-3, InfusionStabiliserMathLogic.getEldritchPillarCycleTimeModifier());
        assertEquals(0.05f, InfusionStabiliserMathLogic.getEldritchPillarCostModifier(), 0.001f);
        assertEquals(0.2f, InfusionStabiliserMathLogic.getEldritchPillarStabilityModifier(), 0.001f);
    }

    @Test
    public void testPedestalModifiers() {
        assertEquals(0.0025f, InfusionStabiliserMathLogic.getPedestalEldritchCostModifier(), 0.0001f);
        assertEquals(-0.01f, InfusionStabiliserMathLogic.getPedestalAncientCostModifier(), 0.0001f);
    }
}
