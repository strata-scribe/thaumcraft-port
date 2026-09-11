package thaumcraft.common.tiles.essentia.logic;

import java.util.Random;

/**
 * Helper logic class for Essentia Smelter auxiliary attachments.
 * Contains purely mathematical and simulation logic decoupled from Minecraft APIs,
 * making it suitable for standard JUnit testing.
 */
public class SmelterAuxLogic {

    /**
     * Calculates the reduced smelt time based on the number of attached auxiliary pumps.
     * Each pump reduces the time by 20%, up to a maximum of 2 pumps (40% reduction).
     *
     * @param baseSmeltTime the initial smelt time (e.g., after bellows are applied)
     * @param auxPumps      the number of adjacent auxiliary pumps
     * @return the final smelt time
     */
    public static int calculateSmeltTime(int baseSmeltTime, int auxPumps) {
        int effectivePumps = Math.min(auxPumps, 2);
        float multiplier = 1.0f - (effectivePumps * 0.20f);
        return Math.max(1, (int) (baseSmeltTime * multiplier));
    }

    /**
     * Calculates the amount of flux that is successfully absorbed by attached vents.
     * Each vent provides an independent 33.3% chance to absorb a point of flux.
     *
     * @param totalFlux the total number of flux points generated
     * @param ventCount the number of adjacent vents
     * @param random    the random instance to use for probability rolls
     * @return the number of flux points that were absorbed
     */
    public static int getVentedFlux(int totalFlux, int ventCount, Random random) {
        if (ventCount <= 0 || totalFlux <= 0) {
            return 0;
        }

        int absorbed = 0;
        for (int i = 0; i < totalFlux; i++) {
            for (int v = 0; v < ventCount; v++) {
                if (random.nextFloat() < 0.333f) {
                    absorbed++;
                    break; // Move to the next flux point once one vent absorbs it
                }
            }
        }
        return absorbed;
    }
}
