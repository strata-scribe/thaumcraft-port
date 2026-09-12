package thaumcraft.common.aura;

import org.junit.jupiter.api.Test;
import thaumcraft.common.lib.CelestialCalendarLogic;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CelestialAuraModifierLogicTest {

    @Test
    public void testFullMoonMultiplier() {
        assertEquals(1.2, CelestialAuraModifierLogic.getVisRechargeMultiplier(CelestialCalendarLogic.MoonPhase.FULL_MOON), 0.001);
    }

    @Test
    public void testNewMoonMultiplier() {
        assertEquals(0.8, CelestialAuraModifierLogic.getVisRechargeMultiplier(CelestialCalendarLogic.MoonPhase.NEW_MOON), 0.001);
    }

    @Test
    public void testDefaultMultiplier() {
        assertEquals(1.0, CelestialAuraModifierLogic.getVisRechargeMultiplier(CelestialCalendarLogic.MoonPhase.WAXING_CRESCENT), 0.001);
        assertEquals(1.0, CelestialAuraModifierLogic.getVisRechargeMultiplier(CelestialCalendarLogic.MoonPhase.FIRST_QUARTER), 0.001);
        assertEquals(1.0, CelestialAuraModifierLogic.getVisRechargeMultiplier(CelestialCalendarLogic.MoonPhase.WAXING_GIBBOUS), 0.001);
        assertEquals(1.0, CelestialAuraModifierLogic.getVisRechargeMultiplier(CelestialCalendarLogic.MoonPhase.WANING_GIBBOUS), 0.001);
        assertEquals(1.0, CelestialAuraModifierLogic.getVisRechargeMultiplier(CelestialCalendarLogic.MoonPhase.THIRD_QUARTER), 0.001);
        assertEquals(1.0, CelestialAuraModifierLogic.getVisRechargeMultiplier(CelestialCalendarLogic.MoonPhase.WANING_CRESCENT), 0.001);
    }
}
