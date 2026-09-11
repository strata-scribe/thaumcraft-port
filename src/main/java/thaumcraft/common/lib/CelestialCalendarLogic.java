package thaumcraft.common.lib;

public class CelestialCalendarLogic {

    public enum DayPhase {
        DAY, NIGHT
    }

    public enum MoonPhase {
        FULL_MOON,
        WANING_GIBBOUS,
        THIRD_QUARTER,
        WANING_CRESCENT,
        NEW_MOON,
        WAXING_CRESCENT,
        FIRST_QUARTER,
        WAXING_GIBBOUS
    }

    public enum AstrologicalSign {
        ARIES, TAURUS, GEMINI, CANCER, LEO, VIRGO, LIBRA, SCORPIO, SAGITTARIUS, CAPRICORN, AQUARIUS, PISCES
    }

    public static DayPhase getDayPhase(long totalTime) {
        long timeOfDay = totalTime % 24000L;
        if (timeOfDay < 0) {
            timeOfDay += 24000L;
        }

        if (timeOfDay >= 13000 && timeOfDay < 23000) {
            return DayPhase.NIGHT;
        }
        return DayPhase.DAY;
    }

    public static MoonPhase getMoonPhase(long totalTime) {
        long day = Math.floorDiv(totalTime, 24000L);
        int phase = (int) (day % 8L);
        if (phase < 0) {
            phase += 8;
        }
        return MoonPhase.values()[phase];
    }

    public static AstrologicalSign getAstrologicalSign(long totalTime) {
        long day = Math.floorDiv(totalTime, 24000L);
        int signIndex = (int) (Math.floorDiv(day, 8L) % 12L);
        if (signIndex < 0) {
            signIndex += 12;
        }
        return AstrologicalSign.values()[signIndex];
    }

    public static double getAuromancyPowerMultiplier(long totalTime) {
        return getMoonPhase(totalTime) == MoonPhase.FULL_MOON ? 1.20 : 1.0;
    }
}
