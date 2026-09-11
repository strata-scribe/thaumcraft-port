package thaumcraft.common.tiles.devices;

import org.junit.jupiter.api.Test;
import thaumcraft.common.lib.BellowsLogic;
import static org.junit.jupiter.api.Assertions.*;

public class BellowsLogicTest {
    @Test
    public void testFurnaceCookTimeReduction() {
        int currentProgress = 10;
        int bellowsCount = 2;
        int newProgress = BellowsLogic.getAcceleratedFurnaceProgress(currentProgress, bellowsCount);
        assertEquals(12, newProgress, "Progress should increase by the number of bellows");
    }

    @Test
    public void testCrucibleHeatIncrease() {
        short heat = 199;
        int bellowsCount = 2;
        short newHeat = BellowsLogic.getAcceleratedCrucibleHeat(heat, bellowsCount);
        assertEquals(201, newHeat, "Heat should increase beyond 200 with bellows");

        short maxHeat = BellowsLogic.getAcceleratedCrucibleHeat((short) 249, 2);
        assertEquals(250, maxHeat, "Max heat with 2 bellows should be capped at 250");
    }
}
