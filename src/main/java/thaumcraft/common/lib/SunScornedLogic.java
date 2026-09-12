package thaumcraft.common.lib;

public class SunScornedLogic {

    public static final int MAX_HEAT = 100;
    public static final int HEAT_RATE_SUN = 1;
    public static final int COOL_RATE_SHADE = 2;

    /**
     * Calculates the new heat value based on current heat and whether the subject is in direct sunlight.
     *
     * @param currentHeat The current heat buildup (0 to MAX_HEAT).
     * @param inSunlight True if in direct sunlight, false if in shade.
     * @return The updated heat buildup, clamped between 0 and MAX_HEAT.
     */
    public static int calculateHeat(int currentHeat, boolean inSunlight) {
        int newHeat = currentHeat;
        if (inSunlight) {
            newHeat += HEAT_RATE_SUN;
        } else {
            newHeat -= COOL_RATE_SHADE;
        }

        return Math.max(0, Math.min(MAX_HEAT, newHeat));
    }
}
