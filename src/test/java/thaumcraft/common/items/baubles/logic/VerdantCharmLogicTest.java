package thaumcraft.common.items.baubles.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class VerdantCharmLogicTest {

    @Test
    public void testCalculateHealingPulseInterval_FullHealth() {
        assertEquals(VerdantCharmLogic.BASE_HEAL_INTERVAL, VerdantCharmLogic.calculateHealingPulseInterval(20.0f, 20.0f));
    }

    @Test
    public void testCalculateHealingPulseInterval_ZeroHealth() {
        assertEquals(VerdantCharmLogic.MIN_HEAL_INTERVAL, VerdantCharmLogic.calculateHealingPulseInterval(0.0f, 20.0f));
    }

    @Test
    public void testCalculateHealingPulseInterval_HalfHealth() {
        int expected = VerdantCharmLogic.MIN_HEAL_INTERVAL + (VerdantCharmLogic.BASE_HEAL_INTERVAL - VerdantCharmLogic.MIN_HEAL_INTERVAL) / 2;
        assertEquals(expected, VerdantCharmLogic.calculateHealingPulseInterval(10.0f, 20.0f));
    }

    @Test
    public void testCalculateSaturationConversionRatio_FullFood() {
        assertEquals(0.5f, VerdantCharmLogic.calculateSaturationConversionRatio(20, 20), 0.001f);
    }

    @Test
    public void testCalculateSaturationConversionRatio_ZeroFood() {
        assertEquals(1.5f, VerdantCharmLogic.calculateSaturationConversionRatio(0, 20), 0.001f);
    }

    @Test
    public void testCalculateSaturationConversionRatio_HalfFood() {
        assertEquals(1.0f, VerdantCharmLogic.calculateSaturationConversionRatio(10, 20), 0.001f);
    }
}
