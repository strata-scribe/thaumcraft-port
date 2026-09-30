package thaumcraft.common.items.curios.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CurioLogic Unit Tests")
public class CurioLogicTest {

    @Test
    @DisplayName("Verify variant count and validity check")
    void testVariantCountAndValidity() {
        assertEquals(6, CurioLogic.VARIANT_COUNT);

        assertTrue(CurioLogic.isValidVariant(0));
        assertTrue(CurioLogic.isValidVariant(1));
        assertTrue(CurioLogic.isValidVariant(2));
        assertTrue(CurioLogic.isValidVariant(3));
        assertTrue(CurioLogic.isValidVariant(4));
        assertTrue(CurioLogic.isValidVariant(5));

        assertFalse(CurioLogic.isValidVariant(-1));
        assertFalse(CurioLogic.isValidVariant(6));
        assertFalse(CurioLogic.isValidVariant(7));
        assertFalse(CurioLogic.isValidVariant(100));
    }

    @Test
    @DisplayName("Verify getVariantName mapping")
    void testGetVariantName() {
        assertEquals("arcane", CurioLogic.getVariantName(0));
        assertEquals("preserved", CurioLogic.getVariantName(1));
        assertEquals("ancient", CurioLogic.getVariantName(2));
        assertEquals("eldritch", CurioLogic.getVariantName(3));
        assertEquals("illuminated", CurioLogic.getVariantName(4));
        assertEquals("twisted", CurioLogic.getVariantName(5));

        assertEquals("unknown", CurioLogic.getVariantName(-1));
        assertEquals("unknown", CurioLogic.getVariantName(6));
        assertEquals("unknown", CurioLogic.getVariantName(99));
    }

    @Test
    @DisplayName("Verify getResearchCategory mapping")
    void testGetResearchCategory() {
        assertEquals("BASICS", CurioLogic.getResearchCategory(0));
        assertEquals("ALCHEMY", CurioLogic.getResearchCategory(1));
        assertEquals("INFUSION", CurioLogic.getResearchCategory(2));
        assertEquals("ELDRITCH", CurioLogic.getResearchCategory(3));
        assertEquals("AUROMANCY", CurioLogic.getResearchCategory(4));
        assertEquals("GOLEMANCY", CurioLogic.getResearchCategory(5));

        assertEquals("BASICS", CurioLogic.getResearchCategory(-1));
        assertEquals("BASICS", CurioLogic.getResearchCategory(6));
        assertEquals("BASICS", CurioLogic.getResearchCategory(99));
    }

    @Test
    @DisplayName("Verify calculateKnowledgeGrant based on roll")
    void testCalculateKnowledgeGrant() {
        assertEquals(1, CurioLogic.calculateKnowledgeGrant(0, 0.0f));
        assertEquals(1, CurioLogic.calculateKnowledgeGrant(1, 0.49f));
        assertEquals(2, CurioLogic.calculateKnowledgeGrant(2, 0.5f));
        assertEquals(2, CurioLogic.calculateKnowledgeGrant(3, 0.75f));
        assertEquals(2, CurioLogic.calculateKnowledgeGrant(4, 1.0f));
    }

    @Test
    @DisplayName("Verify calculateTheoryBonus returns 25")
    void testCalculateTheoryBonus() {
        assertEquals(25, CurioLogic.calculateTheoryBonus(0));
        assertEquals(25, CurioLogic.calculateTheoryBonus(1));
        assertEquals(25, CurioLogic.calculateTheoryBonus(5));
        assertEquals(25, CurioLogic.calculateTheoryBonus(-1));
    }
}
