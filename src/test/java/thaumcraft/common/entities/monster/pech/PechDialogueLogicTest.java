package thaumcraft.common.entities.monster.pech;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import thaumcraft.common.entities.monster.pech.PechDialogueLogic.Mood;

public class PechDialogueLogicTest {

    @Test
    public void testMoodTransitions() {
        // Angry
        assertEquals(Mood.NEUTRAL, PechDialogueLogic.evaluateMoodTransition(Mood.ANGRY, 12));
        assertEquals(Mood.ANGRY, PechDialogueLogic.evaluateMoodTransition(Mood.ANGRY, 5));
        assertEquals(Mood.ANGRY, PechDialogueLogic.evaluateMoodTransition(Mood.ANGRY, 0));

        // Neutral
        assertEquals(Mood.FRIENDLY, PechDialogueLogic.evaluateMoodTransition(Mood.NEUTRAL, 15));
        assertEquals(Mood.NEUTRAL, PechDialogueLogic.evaluateMoodTransition(Mood.NEUTRAL, 10));
        assertEquals(Mood.ANGRY, PechDialogueLogic.evaluateMoodTransition(Mood.NEUTRAL, 0));

        // Friendly
        assertEquals(Mood.FRIENDLY, PechDialogueLogic.evaluateMoodTransition(Mood.FRIENDLY, 20));
        assertEquals(Mood.FRIENDLY, PechDialogueLogic.evaluateMoodTransition(Mood.FRIENDLY, 10));
        assertEquals(Mood.NEUTRAL, PechDialogueLogic.evaluateMoodTransition(Mood.FRIENDLY, 3));
        assertEquals(Mood.ANGRY, PechDialogueLogic.evaluateMoodTransition(Mood.FRIENDLY, 0));
    }

    @Test
    public void testDialogueResponses() {
        // Angry responses
        assertEquals(-1, PechDialogueLogic.getDialogueResponse(Mood.ANGRY, 0));
        assertEquals(-1, PechDialogueLogic.getDialogueResponse(Mood.ANGRY, 5));
        assertEquals(1, PechDialogueLogic.getDialogueResponse(Mood.ANGRY, 15));

        // Neutral responses
        assertEquals(0, PechDialogueLogic.getDialogueResponse(Mood.NEUTRAL, 0));
        assertEquals(0, PechDialogueLogic.getDialogueResponse(Mood.NEUTRAL, 3));
        assertEquals(1, PechDialogueLogic.getDialogueResponse(Mood.NEUTRAL, 10));
        assertEquals(2, PechDialogueLogic.getDialogueResponse(Mood.NEUTRAL, 20));

        // Friendly responses
        assertEquals(0, PechDialogueLogic.getDialogueResponse(Mood.FRIENDLY, 0));
        assertEquals(1, PechDialogueLogic.getDialogueResponse(Mood.FRIENDLY, 3));
        assertEquals(2, PechDialogueLogic.getDialogueResponse(Mood.FRIENDLY, 10));
        assertEquals(3, PechDialogueLogic.getDialogueResponse(Mood.FRIENDLY, 20));
    }
}
