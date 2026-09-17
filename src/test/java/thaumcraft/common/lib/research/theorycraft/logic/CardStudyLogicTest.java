package thaumcraft.common.lib.research.theorycraft.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CardStudyLogicTest {

    @Test
    public void testCalculateStudyYield_BaseYield() {
        // baseYield=10, rarityBonus=0*5=0, densityBonus=0*2=0, observationBonus=0/10=0 => 10
        assertEquals(10, CardStudyLogic.calculateStudyYield(0, 0, 0));
    }

    @Test
    public void testCalculateStudyYield_WithRarity() {
        // baseYield=10, rarityBonus=2*5=10, densityBonus=0*2=0, observationBonus=0/10=0 => 20
        assertEquals(20, CardStudyLogic.calculateStudyYield(2, 0, 0));
    }

    @Test
    public void testCalculateStudyYield_WithAspectDensity() {
        // baseYield=10, rarityBonus=0*5=0, densityBonus=5*2=10, observationBonus=0/10=0 => 20
        assertEquals(20, CardStudyLogic.calculateStudyYield(0, 5, 0));
    }

    @Test
    public void testCalculateStudyYield_WithObservationLevel() {
        // baseYield=10, rarityBonus=0*5=0, densityBonus=0*2=0, observationBonus=25/10=2 => 12
        assertEquals(12, CardStudyLogic.calculateStudyYield(0, 0, 25));
    }

    @Test
    public void testCalculateStudyYield_Combined() {
        // baseYield=10, rarityBonus=3*5=15, densityBonus=10*2=20, observationBonus=50/10=5 => 50
        assertEquals(50, CardStudyLogic.calculateStudyYield(3, 10, 50));
    }
}
