package thaumcraft.common.sounds.logic;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ThaumcraftSoundJsonLogicTest {

    @Test
    public void testIsValidSoundEventName() {
        assertTrue(ThaumcraftSoundJsonLogic.isValidSoundEventName("pech_idle"));
        assertTrue(ThaumcraftSoundJsonLogic.isValidSoundEventName("entity.pech.idle"));
        assertTrue(ThaumcraftSoundJsonLogic.isValidSoundEventName("pech/idle"));
        assertTrue(ThaumcraftSoundJsonLogic.isValidSoundEventName("pech-idle"));
        assertTrue(ThaumcraftSoundJsonLogic.isValidSoundEventName("pech_idle_1"));

        assertFalse(ThaumcraftSoundJsonLogic.isValidSoundEventName("Pech_Idle"));
        assertFalse(ThaumcraftSoundJsonLogic.isValidSoundEventName("pech idle"));
        assertFalse(ThaumcraftSoundJsonLogic.isValidSoundEventName("pech_idle!"));
        assertFalse(ThaumcraftSoundJsonLogic.isValidSoundEventName(""));
        assertFalse(ThaumcraftSoundJsonLogic.isValidSoundEventName(null));
    }

    @Test
    public void testIsValidSubtitleKey() {
        assertTrue(ThaumcraftSoundJsonLogic.isValidSubtitleKey("subtitles.thaumcraft.pech"));
        assertTrue(ThaumcraftSoundJsonLogic.isValidSubtitleKey("subtitles.entity.pech.idle"));
        assertTrue(ThaumcraftSoundJsonLogic.isValidSubtitleKey("thaumcraft.pech-idle"));
        assertTrue(ThaumcraftSoundJsonLogic.isValidSubtitleKey("thaumcraft.pech_idle_1"));

        assertFalse(ThaumcraftSoundJsonLogic.isValidSubtitleKey("subtitles/thaumcraft/pech"));
        assertFalse(ThaumcraftSoundJsonLogic.isValidSubtitleKey("Subtitles.Thaumcraft.Pech"));
        assertFalse(ThaumcraftSoundJsonLogic.isValidSubtitleKey("subtitles thaumcraft pech"));
        assertFalse(ThaumcraftSoundJsonLogic.isValidSubtitleKey(""));
        assertFalse(ThaumcraftSoundJsonLogic.isValidSubtitleKey(null));
    }

    @Test
    public void testIsValidFilePath() {
        assertTrue(ThaumcraftSoundJsonLogic.isValidFilePath("pech_idle"));
        assertTrue(ThaumcraftSoundJsonLogic.isValidFilePath("mob/pech/idle"));
        assertTrue(ThaumcraftSoundJsonLogic.isValidFilePath("pech_idle_1"));
        assertTrue(ThaumcraftSoundJsonLogic.isValidFilePath("thaumcraft:pech_idle"));

        assertFalse(ThaumcraftSoundJsonLogic.isValidFilePath("Pech_Idle"));
        assertFalse(ThaumcraftSoundJsonLogic.isValidFilePath("pech idle"));
    }

    @Test
    public void testValidateSoundDefinition() {
        List<String> validPaths = Arrays.asList("pech_idle", "thaumcraft:mob/pech_idle");

        List<String> errors1 = ThaumcraftSoundJsonLogic.validateSoundDefinition("pech_idle", "subtitles.thaumcraft.pech", validPaths);
        assertTrue(errors1.isEmpty());

        List<String> errors2 = ThaumcraftSoundJsonLogic.validateSoundDefinition("Pech_Idle", "subtitles thaumcraft pech", Collections.singletonList("pech idle"));
        assertEquals(3, errors2.size());
        assertTrue(errors2.stream().anyMatch(e -> e.contains("Invalid sound event name")));
        assertTrue(errors2.stream().anyMatch(e -> e.contains("Invalid subtitle key")));
        assertTrue(errors2.stream().anyMatch(e -> e.contains("Invalid file path")));

        List<String> errors3 = ThaumcraftSoundJsonLogic.validateSoundDefinition("pech_idle", null, Collections.emptyList());
        assertEquals(1, errors3.size());
        assertTrue(errors3.get(0).contains("must contain at least one file path"));
    }
}