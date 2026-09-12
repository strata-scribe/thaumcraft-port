package thaumcraft.common.aura;

import thaumcraft.common.lib.CelestialCalendarLogic;

public class CelestialAuraModifierLogic {

    public static double getVisRechargeMultiplier(CelestialCalendarLogic.MoonPhase moonPhase) {
        if (moonPhase == CelestialCalendarLogic.MoonPhase.FULL_MOON) {
            return 1.2;
        } else if (moonPhase == CelestialCalendarLogic.MoonPhase.NEW_MOON) {
            return 0.8;
        }
        return 1.0;
    }
}
