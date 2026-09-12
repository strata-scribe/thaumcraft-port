package thaumcraft.common.lib.sound.logic;

import java.util.Random;

public class WarpWhispersLogic {

    /**
     * Calculates the playback frequency (in ticks) for ambient whispers based on the current research category.
     * Eldritch research should result in more frequent whispers.
     *
     * @param researchCategory The active research category.
     * @return The tick interval for the whispers.
     */
    public static int calculatePlaybackFrequency(String researchCategory) {
        if (researchCategory == null || researchCategory.isEmpty()) {
            return 1200; // default slow tick interval for no category
        }

        String cat = researchCategory.toUpperCase();
        if (cat.equals("ELDRITCH")) {
            return 300; // fast tick interval
        } else if (cat.equals("ALCHEMY")) {
            return 600;
        } else if (cat.equals("AUROMANCY") || cat.equals("ARTIFICE") || cat.equals("GOLEMANCY")) {
            return 800;
        } else {
            return 1000; // generic default
        }
    }

    /**
     * Selects a whisper line string identifier based on the research category and randomness.
     *
     * @param researchCategory The active research category.
     * @param random The Random instance to use.
     * @return The string identifier for the ambient whisper line.
     */
    public static String selectWhisperLine(String researchCategory, Random random) {
        int index = random.nextInt(3) + 1; // 1, 2, or 3

        if (researchCategory == null || researchCategory.isEmpty()) {
            return "whisper.generic." + index;
        }

        return "whisper." + researchCategory.toLowerCase() + "." + index;
    }
}
