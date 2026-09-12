package thaumcraft.common.items.armor;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ThaumostaticHarnessFuelLogicTest {

    @Test
    public void testBaseConsumption() {
        assertEquals(1.0f, ThaumostaticHarnessFuelLogic.calculatePotentiaConsumption(0.0, 0.0), 0.001f);
    }

    @Test
    public void testHorizontalSpeedConsumption() {
        assertEquals(5.0f, ThaumostaticHarnessFuelLogic.calculatePotentiaConsumption(2.0, 0.0), 0.001f);
    }

    @Test
    public void testPositiveAscentConsumption() {
        assertEquals(6.0f, ThaumostaticHarnessFuelLogic.calculatePotentiaConsumption(0.0, 1.0), 0.001f);
    }

    @Test
    public void testNegativeAscentConsumption() {
        assertEquals(1.0f, ThaumostaticHarnessFuelLogic.calculatePotentiaConsumption(0.0, -1.0), 0.001f);
    }

    @Test
    public void testCombinedConsumption() {
        assertEquals(10.0f, ThaumostaticHarnessFuelLogic.calculatePotentiaConsumption(2.0, 1.0), 0.001f);
    }
}
