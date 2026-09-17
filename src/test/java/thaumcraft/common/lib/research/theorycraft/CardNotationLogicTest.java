package thaumcraft.common.lib.research.theorycraft;

import org.junit.jupiter.api.Test;
import thaumcraft.common.lib.research.theorycraft.logic.CardNotationLogic;
import static org.junit.jupiter.api.Assertions.*;

public class CardNotationLogicTest {

    @Test
    public void testValidateMaterials() {
        assertTrue(CardNotationLogic.validateMaterials(1, 1));
        assertTrue(CardNotationLogic.validateMaterials(5, 2));
        assertFalse(CardNotationLogic.validateMaterials(0, 1));
        assertFalse(CardNotationLogic.validateMaterials(1, 0));
        assertFalse(CardNotationLogic.validateMaterials(0, 0));
    }

    @Test
    public void testComputeKnowledgeGain() {
        // Test a few different seeds to ensure it stays within bounds
        for (long seed = 0; seed < 100; seed++) {
            int gain = CardNotationLogic.computeKnowledgeGain(seed);
            assertTrue(gain >= 10 && gain <= 25, "Knowledge gain should be between 10 and 25. Got: " + gain);
        }
    }

    @Test
    public void testCalculateInspirationPreservation() {
        // 5 cognition -> 50% chance
        assertTrue(CardNotationLogic.calculateInspirationPreservation(5, 0.49f));
        assertFalse(CardNotationLogic.calculateInspirationPreservation(5, 0.51f));

        // 10 cognition -> 100% chance
        assertTrue(CardNotationLogic.calculateInspirationPreservation(10, 0.99f));

        // 0 cognition -> 0% chance
        assertFalse(CardNotationLogic.calculateInspirationPreservation(0, 0.01f));
    }
}
