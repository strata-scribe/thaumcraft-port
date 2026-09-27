package thaumcraft.common.lib.research.theorycraft;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import thaumcraft.common.lib.research.theorycraft.logic.CardChannelLogic;
import thaumcraft.common.lib.research.theorycraft.logic.CardCurioLogic;
import thaumcraft.common.lib.research.theorycraft.logic.CardCurioLogic.ProgressionReward;

public class TheorycraftCardChannelsCurioTest {

    @Test
    public void testCardChannelAffinityBoost() {
        int boost = CardChannelLogic.calculateAffinityBoost();
        assertEquals(25, boost, "CardChannels should give an affinity boost of 25");
    }

    @Test
    public void testCardCurioConsumptionRewards() {
        // Test "arcane" curio
        ProgressionReward arcaneReward = CardCurioLogic.calculateProgressionRewards(12345L, "arcane");
        assertEquals("AUROMANCY", arcaneReward.category);
        assertTrue(arcaneReward.categoryPoints >= 25 && arcaneReward.categoryPoints <= 35);
        assertEquals(5, arcaneReward.basicsBonus);
        assertNull(arcaneReward.secondaryCategory);

        // Test "preserved" curio
        ProgressionReward preservedReward = CardCurioLogic.calculateProgressionRewards(12345L, "preserved");
        assertEquals("ALCHEMY", preservedReward.category);
        assertTrue(preservedReward.categoryPoints >= 25 && preservedReward.categoryPoints <= 35);
        assertEquals(5, preservedReward.basicsBonus);
        assertNull(preservedReward.secondaryCategory);

        // Test "ancient" curio
        ProgressionReward ancientReward = CardCurioLogic.calculateProgressionRewards(12345L, "ancient");
        assertEquals("GOLEMANCY", ancientReward.category);
        assertTrue(ancientReward.categoryPoints >= 25 && ancientReward.categoryPoints <= 35);
        assertNull(ancientReward.secondaryCategory);

        // Test "eldritch" curio
        ProgressionReward eldritchReward = CardCurioLogic.calculateProgressionRewards(12345L, "eldritch");
        assertEquals("ELDRITCH", eldritchReward.category);
        assertTrue(eldritchReward.categoryPoints >= 25 && eldritchReward.categoryPoints <= 35);
        assertNull(eldritchReward.secondaryCategory);

        // Test "knowledge" curio
        ProgressionReward knowledgeReward = CardCurioLogic.calculateProgressionRewards(12345L, "knowledge");
        assertEquals("INFUSION", knowledgeReward.category);
        assertTrue(knowledgeReward.categoryPoints >= 25 && knowledgeReward.categoryPoints <= 35);
        assertNull(knowledgeReward.secondaryCategory);

        // Test "twisted" curio
        ProgressionReward twistedReward = CardCurioLogic.calculateProgressionRewards(12345L, "twisted");
        assertEquals("ARTIFICE", twistedReward.category);
        assertTrue(twistedReward.categoryPoints >= 25 && twistedReward.categoryPoints <= 35);
        assertNull(twistedReward.secondaryCategory);

        // Test "rites" curio
        ProgressionReward ritesReward = CardCurioLogic.calculateProgressionRewards(12345L, "rites");
        assertEquals("ELDRITCH", ritesReward.category);
        assertTrue(ritesReward.categoryPoints >= 15 && ritesReward.categoryPoints <= 20);
        assertEquals("AUROMANCY", ritesReward.secondaryCategory);
        assertTrue(ritesReward.secondaryPoints >= 10 && ritesReward.secondaryPoints <= 15);

        // Test default
        ProgressionReward defaultReward = CardCurioLogic.calculateProgressionRewards(12345L, "unknown");
        assertEquals("BASICS", defaultReward.category);
        assertTrue(defaultReward.categoryPoints >= 25 && defaultReward.categoryPoints <= 35);
        assertNull(defaultReward.secondaryCategory);
    }
}
