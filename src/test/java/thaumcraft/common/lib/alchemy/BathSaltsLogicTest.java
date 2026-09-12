package thaumcraft.common.lib.alchemy;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BathSaltsLogicTest {

    @Test
    public void testCalculateWaterInfusionDuration() {
        assertEquals(0, BathSaltsLogic.calculateWaterInfusionDuration(0));
        assertEquals(12000, BathSaltsLogic.calculateWaterInfusionDuration(1));
        assertEquals(24000, BathSaltsLogic.calculateWaterInfusionDuration(2));
        assertEquals(72000, BathSaltsLogic.calculateWaterInfusionDuration(6));
        assertEquals(72000, BathSaltsLogic.calculateWaterInfusionDuration(10));
    }

    @Test
    public void testCalculateSoothingPotionDuration() {
        assertEquals(24000, BathSaltsLogic.calculateSoothingPotionDuration(0));
        assertEquals(23000, BathSaltsLogic.calculateSoothingPotionDuration(10));
        assertEquals(14000, BathSaltsLogic.calculateSoothingPotionDuration(100));
        assertEquals(1200, BathSaltsLogic.calculateSoothingPotionDuration(300)); // Capped at minimum
    }

    @Test
    public void testCalculateWarpEventInhibitionChance() {
        assertEquals(1.0, BathSaltsLogic.calculateWarpEventInhibitionChance(0), 0.001);
        assertEquals(1.0, BathSaltsLogic.calculateWarpEventInhibitionChance(10), 0.001);
        assertEquals(0.95, BathSaltsLogic.calculateWarpEventInhibitionChance(20), 0.001);
        assertEquals(0.5, BathSaltsLogic.calculateWarpEventInhibitionChance(120), 0.001);
        assertEquals(0.5, BathSaltsLogic.calculateWarpEventInhibitionChance(200), 0.001); // Capped at minimum
    }
}
