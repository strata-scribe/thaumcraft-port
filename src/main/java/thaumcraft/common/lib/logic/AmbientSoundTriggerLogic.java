package thaumcraft.common.lib.logic;

import java.util.Random;

public class AmbientSoundTriggerLogic {

    /**
     * Calculates the next ambient sound interval for an active Infusion Altar.
     * The requirement is a deep matrix hum every 80-120 ticks.
     *
     * @param random The Random instance to use.
     * @return The number of ticks until the next ambient sound.
     */
    public static int getInfusionAltarInterval(Random random) {
        return 80 + random.nextInt(41);
    }

    /**
     * Calculates the next ambient sound interval for an Eldritch Portal.
     * Generates a sinister whispers interval.
     *
     * @param random The Random instance to use.
     * @return The number of ticks until the next ambient sound.
     */
    public static int getEldritchPortalInterval(Random random) {
        // We use 100-300 ticks interval for sinister whispers
        return 100 + random.nextInt(201);
    }
}
