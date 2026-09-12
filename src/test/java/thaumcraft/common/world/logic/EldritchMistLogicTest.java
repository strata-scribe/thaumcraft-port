package thaumcraft.common.world.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EldritchMistLogicTest {

    @Test
    void testCalculateMistRadius_ExpandsNormally() {
        int ticksActive = 100;
        double expansionRate = 0.1; // 0.1 units per tick
        double maxRadius = 20.0;

        double radius = EldritchMistLogic.calculateMistRadius(ticksActive, expansionRate, maxRadius);
        assertEquals(10.0, radius, 0.001, "Mist radius should expand to 10.0 after 100 ticks at 0.1 rate.");
    }

    @Test
    void testCalculateMistRadius_CappedAtMax() {
        int ticksActive = 1000;
        double expansionRate = 0.1;
        double maxRadius = 20.0;

        double radius = EldritchMistLogic.calculateMistRadius(ticksActive, expansionRate, maxRadius);
        assertEquals(20.0, radius, 0.001, "Mist radius should cap at maxRadius (20.0).");
    }

    @Test
    void testCalculateMistRadius_NegativeTicks() {
        int ticksActive = -50;
        double expansionRate = 0.1;
        double maxRadius = 20.0;

        double radius = EldritchMistLogic.calculateMistRadius(ticksActive, expansionRate, maxRadius);
        assertEquals(0.0, radius, 0.001, "Mist radius should be 0 for negative ticks.");
    }

    @Test
    void testCalculateSanityLoss_AtCenter() {
        double distanceToCenter = 0.0;
        double currentRadius = 10.0;
        double maxSanityLoss = 5.0;

        double loss = EldritchMistLogic.calculateSanityLoss(distanceToCenter, currentRadius, maxSanityLoss);
        assertEquals(5.0, loss, 0.001, "Sanity loss at center should be maxSanityLoss.");
    }

    @Test
    void testCalculateSanityLoss_AtEdge() {
        double distanceToCenter = 10.0;
        double currentRadius = 10.0;
        double maxSanityLoss = 5.0;

        double loss = EldritchMistLogic.calculateSanityLoss(distanceToCenter, currentRadius, maxSanityLoss);
        assertEquals(0.0, loss, 0.001, "Sanity loss at edge should be 0.");
    }

    @Test
    void testCalculateSanityLoss_OutsideMist() {
        double distanceToCenter = 15.0;
        double currentRadius = 10.0;
        double maxSanityLoss = 5.0;

        double loss = EldritchMistLogic.calculateSanityLoss(distanceToCenter, currentRadius, maxSanityLoss);
        assertEquals(0.0, loss, 0.001, "Sanity loss outside mist should be 0.");
    }

    @Test
    void testCalculateSanityLoss_Halfway() {
        double distanceToCenter = 5.0;
        double currentRadius = 10.0;
        double maxSanityLoss = 5.0;

        double loss = EldritchMistLogic.calculateSanityLoss(distanceToCenter, currentRadius, maxSanityLoss);
        assertEquals(2.5, loss, 0.001, "Sanity loss halfway to edge should be half of maxSanityLoss.");
    }

    @Test
    void testCalculateSanityLoss_ZeroRadius() {
        double distanceToCenter = 0.0;
        double currentRadius = 0.0;
        double maxSanityLoss = 5.0;

        double loss = EldritchMistLogic.calculateSanityLoss(distanceToCenter, currentRadius, maxSanityLoss);
        assertEquals(0.0, loss, 0.001, "Sanity loss with 0 radius should be 0.");
    }

    @Test
    void testIsInsideMist() {
        assertTrue(EldritchMistLogic.isInsideMist(5.0, 10.0), "Should be inside mist.");
        assertFalse(EldritchMistLogic.isInsideMist(10.0, 10.0), "Edge is not inside mist.");
        assertFalse(EldritchMistLogic.isInsideMist(15.0, 10.0), "Should be outside mist.");
        assertFalse(EldritchMistLogic.isInsideMist(0.0, 0.0), "Zero radius mist means not inside.");
    }

    @Test
    void testShouldApplySanityLoss() {
        assertFalse(EldritchMistLogic.shouldApplySanityLoss(0), "Tick 0 should not apply loss.");
        assertFalse(EldritchMistLogic.shouldApplySanityLoss(10), "Tick 10 should not apply loss.");
        assertTrue(EldritchMistLogic.shouldApplySanityLoss(20), "Tick 20 should apply loss (1 second).");
        assertTrue(EldritchMistLogic.shouldApplySanityLoss(40), "Tick 40 should apply loss (2 seconds).");
        assertFalse(EldritchMistLogic.shouldApplySanityLoss(21), "Tick 21 should not apply loss.");
    }
}
