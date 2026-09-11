package thaumcraft.common.lib;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CelestialCalendarLogicTest {

    @Test
    public void testDayNightCycle() {
        assertEquals(CelestialCalendarLogic.DayPhase.DAY, CelestialCalendarLogic.getDayPhase(0));
        assertEquals(CelestialCalendarLogic.DayPhase.DAY, CelestialCalendarLogic.getDayPhase(12999));
        assertEquals(CelestialCalendarLogic.DayPhase.NIGHT, CelestialCalendarLogic.getDayPhase(13000));
        assertEquals(CelestialCalendarLogic.DayPhase.NIGHT, CelestialCalendarLogic.getDayPhase(22999));
        assertEquals(CelestialCalendarLogic.DayPhase.DAY, CelestialCalendarLogic.getDayPhase(23000));
    }

    @Test
    public void testMoonPhases() {
        assertEquals(CelestialCalendarLogic.MoonPhase.FULL_MOON, CelestialCalendarLogic.getMoonPhase(0));
        assertEquals(CelestialCalendarLogic.MoonPhase.WANING_GIBBOUS, CelestialCalendarLogic.getMoonPhase(24000));
        assertEquals(CelestialCalendarLogic.MoonPhase.THIRD_QUARTER, CelestialCalendarLogic.getMoonPhase(48000));
        assertEquals(CelestialCalendarLogic.MoonPhase.NEW_MOON, CelestialCalendarLogic.getMoonPhase(24000 * 4));

        // Negative time
        assertEquals(CelestialCalendarLogic.MoonPhase.WAXING_GIBBOUS, CelestialCalendarLogic.getMoonPhase(-24000));
    }

    @Test
    public void testAuromancyPower() {
        assertEquals(1.20, CelestialCalendarLogic.getAuromancyPowerMultiplier(0), 0.001);
        assertEquals(1.0, CelestialCalendarLogic.getAuromancyPowerMultiplier(24000), 0.001);
        assertEquals(1.20, CelestialCalendarLogic.getAuromancyPowerMultiplier(24000 * 8), 0.001);
    }

    @Test
    public void testAstrologicalSign() {
        assertEquals(CelestialCalendarLogic.AstrologicalSign.ARIES, CelestialCalendarLogic.getAstrologicalSign(0));
        assertEquals(CelestialCalendarLogic.AstrologicalSign.TAURUS, CelestialCalendarLogic.getAstrologicalSign(24000 * 8));
        assertEquals(CelestialCalendarLogic.AstrologicalSign.ARIES, CelestialCalendarLogic.getAstrologicalSign(24000 * 8 * 12));

        // Negative time
        assertEquals(CelestialCalendarLogic.AstrologicalSign.PISCES, CelestialCalendarLogic.getAstrologicalSign(-24000 * 8));
    }
}
