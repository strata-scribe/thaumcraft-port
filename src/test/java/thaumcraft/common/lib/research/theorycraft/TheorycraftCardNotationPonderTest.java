package thaumcraft.common.lib.research.theorycraft;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import thaumcraft.api.research.theorycraft.CardNotation;
import thaumcraft.api.research.theorycraft.CardPonder;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.common.lib.research.theorycraft.logic.CardNotationLogic;
import thaumcraft.common.lib.research.theorycraft.logic.CardPonderLogic;
import net.minecraft.world.entity.player.Player;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

public class TheorycraftCardNotationPonderTest {

    @Test
    public void testCardNotationMaterialsAndProgress() {
        assertTrue(CardNotationLogic.validateMaterials(1, 1), "Should be valid with 1 ink and 1 paper");
        assertFalse(CardNotationLogic.validateMaterials(0, 1), "Should be invalid with 0 ink");
        assertFalse(CardNotationLogic.validateMaterials(1, 0), "Should be invalid with 0 paper");

        int progress = CardNotationLogic.computeKnowledgeGain(12345L);
        assertTrue(progress >= 10 && progress <= 25, "Progress should be between 10 and 25");

        assertTrue(CardNotationLogic.calculateInspirationPreservation(5, 0.4f), "Should preserve inspiration");
        assertFalse(CardNotationLogic.calculateInspirationPreservation(5, 0.6f), "Should not preserve inspiration");

        // Also test the card's general functions headlessly
        CardNotation card = new CardNotation();
        assertTrue(card.isAidOnly());
        assertEquals(1, card.getInspirationCost());
    }

    @Test
    public void testCardPonderFreeProgressWithoutMaterialCost() {
        Set<String> categories = new HashSet<>();
        categories.add("ALCHEMY");
        categories.add("ARTIFICE");

        Set<String> blocked = new HashSet<>();

        CardPonderLogic.AllocationResult result = CardPonderLogic.calculateProgressAllocation(categories, blocked);

        assertFalse(result.earlyExit);
        assertTrue(result.success);

        int totalAllocated = 0;
        for (Map.Entry<String, Integer> entry : result.allocations.entrySet()) {
            totalAllocated += entry.getValue();
        }

        assertEquals(30, totalAllocated, "Should allocate 25 points to categories plus 5 to BASICS");
        assertTrue(result.allocations.containsKey("BASICS"), "Should contain BASICS allocation");
        assertEquals(5, result.allocations.get("BASICS"), "BASICS should get exactly 5 points if it wasn't in categories originally, or more if it was");

        assertTrue(CardPonderLogic.verifyInspirationCost(2));
        assertFalse(CardPonderLogic.verifyInspirationCost(1));

        // Also test the card's general functions headlessly
        CardPonder card = new CardPonder();
        assertEquals(CardPonderLogic.INSPIRATION_COST, card.getInspirationCost());
        assertNull(card.getRequiredItems(), "CardPonder should have no material cost");
    }
}
