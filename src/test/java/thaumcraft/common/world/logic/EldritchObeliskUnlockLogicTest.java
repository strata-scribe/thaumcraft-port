package thaumcraft.common.world.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import thaumcraft.common.world.logic.EldritchObeliskUnlockLogic.UnlockItem;

public class EldritchObeliskUnlockLogicTest {

    @Test
    @DisplayName("Test valid standard portal opening sequence (4 Eldritch Eyes)")
    public void testPortalOpeningStandard() {
        List<UnlockItem> items = Arrays.asList(
            UnlockItem.ELDRITCH_EYE, UnlockItem.ELDRITCH_EYE,
            UnlockItem.ELDRITCH_EYE, UnlockItem.ELDRITCH_EYE
        );
        assertTrue(EldritchObeliskUnlockLogic.isPortalOpeningTriggered(items), "4 Eldritch Eyes should open the portal");
    }

    @Test
    @DisplayName("Test valid crimson portal opening sequence (3 Eldritch Eyes, 1 Crimson Rites)")
    public void testPortalOpeningCrimson() {
        List<UnlockItem> items = Arrays.asList(
            UnlockItem.ELDRITCH_EYE, UnlockItem.CRIMSON_RITES,
            UnlockItem.ELDRITCH_EYE, UnlockItem.ELDRITCH_EYE
        );
        assertTrue(EldritchObeliskUnlockLogic.isPortalOpeningTriggered(items), "3 Eyes and 1 Rites should open the portal");
    }

    @Test
    @DisplayName("Test incomplete sequence for portal opening (e.g. 3 items)")
    public void testPortalOpeningIncomplete() {
        List<UnlockItem> items = Arrays.asList(
            UnlockItem.ELDRITCH_EYE, UnlockItem.ELDRITCH_EYE,
            UnlockItem.ELDRITCH_EYE
        );
        assertFalse(EldritchObeliskUnlockLogic.isPortalOpeningTriggered(items), "Incomplete sequence should not open the portal");
    }

    @Test
    @DisplayName("Test invalid sequence for portal opening (too many rites)")
    public void testPortalOpeningInvalidTooManyRites() {
        List<UnlockItem> items = Arrays.asList(
            UnlockItem.ELDRITCH_EYE, UnlockItem.CRIMSON_RITES,
            UnlockItem.ELDRITCH_EYE, UnlockItem.CRIMSON_RITES
        );
        assertFalse(EldritchObeliskUnlockLogic.isPortalOpeningTriggered(items), "2 Rites should not open the portal");
    }

    @Test
    @DisplayName("Test portal opening with nulls in list")
    public void testPortalOpeningWithNulls() {
        List<UnlockItem> items = Arrays.asList(
            UnlockItem.ELDRITCH_EYE, null,
            UnlockItem.ELDRITCH_EYE, UnlockItem.ELDRITCH_EYE
        );
        assertFalse(EldritchObeliskUnlockLogic.isPortalOpeningTriggered(items), "Nulls in sequence should not trigger portal opening");
    }

    @Test
    @DisplayName("Test valid partial insertion sequence")
    public void testValidPartialInsertionSequence() {
        List<UnlockItem> items = Arrays.asList(
            UnlockItem.ELDRITCH_EYE, UnlockItem.CRIMSON_RITES
        );
        assertTrue(EldritchObeliskUnlockLogic.isValidInsertionSequence(items), "Valid partial sequence");

        List<UnlockItem> emptyItems = Collections.emptyList();
        assertTrue(EldritchObeliskUnlockLogic.isValidInsertionSequence(emptyItems), "Empty sequence is valid");
    }

    @Test
    @DisplayName("Test invalid insertion sequence (too many items)")
    public void testInvalidInsertionSequenceTooLong() {
        List<UnlockItem> items = Arrays.asList(
            UnlockItem.ELDRITCH_EYE, UnlockItem.ELDRITCH_EYE,
            UnlockItem.ELDRITCH_EYE, UnlockItem.ELDRITCH_EYE,
            UnlockItem.ELDRITCH_EYE
        );
        assertFalse(EldritchObeliskUnlockLogic.isValidInsertionSequence(items), "Sequence with >4 items is invalid");
    }

    @Test
    @DisplayName("Test invalid insertion sequence (multiple rites)")
    public void testInvalidInsertionSequenceMultipleRites() {
        List<UnlockItem> items = Arrays.asList(
            UnlockItem.CRIMSON_RITES, UnlockItem.CRIMSON_RITES
        );
        assertFalse(EldritchObeliskUnlockLogic.isValidInsertionSequence(items), "Sequence with >1 Crimson Rites is invalid");
    }
}
