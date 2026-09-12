package thaumcraft.common.lib.sound.logic;

import org.junit.jupiter.api.Test;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WarpWhispersLogicTest {

    @Test
    public void testCalculatePlaybackFrequency() {
        assertEquals(300, WarpWhispersLogic.calculatePlaybackFrequency("ELDRITCH"));
        assertEquals(300, WarpWhispersLogic.calculatePlaybackFrequency("eldritch"));

        assertEquals(600, WarpWhispersLogic.calculatePlaybackFrequency("ALCHEMY"));

        assertEquals(800, WarpWhispersLogic.calculatePlaybackFrequency("AUROMANCY"));
        assertEquals(800, WarpWhispersLogic.calculatePlaybackFrequency("ARTIFICE"));
        assertEquals(800, WarpWhispersLogic.calculatePlaybackFrequency("GOLEMANCY"));

        assertEquals(1000, WarpWhispersLogic.calculatePlaybackFrequency("FUNDAMENTALS"));
        assertEquals(1000, WarpWhispersLogic.calculatePlaybackFrequency("UNKNOWN_CAT"));

        assertEquals(1200, WarpWhispersLogic.calculatePlaybackFrequency(""));
        assertEquals(1200, WarpWhispersLogic.calculatePlaybackFrequency(null));
    }

    @Test
    public void testSelectWhisperLine() {
        Random random = new Random(42); // fixed seed for predictability or just check bounds

        // check typical categories
        String lineEldritch = WarpWhispersLogic.selectWhisperLine("ELDRITCH", random);
        assertTrue(lineEldritch.startsWith("whisper.eldritch."));
        int idxEldritch = Integer.parseInt(lineEldritch.substring(lineEldritch.lastIndexOf('.') + 1));
        assertTrue(idxEldritch >= 1 && idxEldritch <= 3);

        String lineAlchemy = WarpWhispersLogic.selectWhisperLine("ALCHEMY", random);
        assertTrue(lineAlchemy.startsWith("whisper.alchemy."));
        int idxAlchemy = Integer.parseInt(lineAlchemy.substring(lineAlchemy.lastIndexOf('.') + 1));
        assertTrue(idxAlchemy >= 1 && idxAlchemy <= 3);

        // check null / empty
        String lineNull = WarpWhispersLogic.selectWhisperLine(null, random);
        assertTrue(lineNull.startsWith("whisper.generic."));
        int idxNull = Integer.parseInt(lineNull.substring(lineNull.lastIndexOf('.') + 1));
        assertTrue(idxNull >= 1 && idxNull <= 3);

        String lineEmpty = WarpWhispersLogic.selectWhisperLine("", random);
        assertTrue(lineEmpty.startsWith("whisper.generic."));
        int idxEmpty = Integer.parseInt(lineEmpty.substring(lineEmpty.lastIndexOf('.') + 1));
        assertTrue(idxEmpty >= 1 && idxEmpty <= 3);
    }
}
