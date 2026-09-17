package thaumcraft.common.tiles.devices.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InfernalFurnaceEfficiencyLogicTest {

    @Test
    public void testBonusNuggetRate() {
        // Normal bounds
        assertEquals(0.0, InfernalFurnaceEfficiencyLogic.getBonusNuggetRate(0), 0.001);
        assertEquals(0.15, InfernalFurnaceEfficiencyLogic.getBonusNuggetRate(1), 0.001);
        assertEquals(0.33, InfernalFurnaceEfficiencyLogic.getBonusNuggetRate(2), 0.001);
        assertEquals(0.55, InfernalFurnaceEfficiencyLogic.getBonusNuggetRate(3), 0.001);

        // Out of bounds
        assertEquals(0.0, InfernalFurnaceEfficiencyLogic.getBonusNuggetRate(-1), 0.001);
        assertEquals(0.55, InfernalFurnaceEfficiencyLogic.getBonusNuggetRate(4), 0.001);
    }

    @Test
    public void testSmeltingDuration() {
        int baseDuration = 100;

        // Normal bounds
        // 100 * 1.0 = 100
        assertEquals(100, InfernalFurnaceEfficiencyLogic.getSmeltingDuration(baseDuration, 0));
        // 100 * 0.8 = 80
        assertEquals(80, InfernalFurnaceEfficiencyLogic.getSmeltingDuration(baseDuration, 1));
        // 100 * 0.64 = 64
        assertEquals(64, InfernalFurnaceEfficiencyLogic.getSmeltingDuration(baseDuration, 2));
        // 100 * 0.512 = 51
        assertEquals(51, InfernalFurnaceEfficiencyLogic.getSmeltingDuration(baseDuration, 3));

        // Out of bounds
        assertEquals(100, InfernalFurnaceEfficiencyLogic.getSmeltingDuration(baseDuration, -5));
        assertEquals(51, InfernalFurnaceEfficiencyLogic.getSmeltingDuration(baseDuration, 5));
    }

    @Test
    public void testSmeltingDurationZeroOrNegative() {
        assertEquals(0, InfernalFurnaceEfficiencyLogic.getSmeltingDuration(0, 1));
        assertEquals(0, InfernalFurnaceEfficiencyLogic.getSmeltingDuration(-10, 2));
    }
}
