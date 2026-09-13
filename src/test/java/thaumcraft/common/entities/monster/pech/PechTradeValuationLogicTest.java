package thaumcraft.common.entities.monster.pech;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PechTradeValuationLogicTest {

    @Test
    public void testScoreItem_CommonTier() {
        assertEquals(1, PechTradeValuationLogic.scoreItem(PechTradeValuationLogic.PreferenceTier.COMMON, 1));
        assertEquals(5, PechTradeValuationLogic.scoreItem(PechTradeValuationLogic.PreferenceTier.COMMON, 5));
    }

    @Test
    public void testScoreItem_GoldTier() {
        assertEquals(3, PechTradeValuationLogic.scoreItem(PechTradeValuationLogic.PreferenceTier.GOLD, 1));
        assertEquals(15, PechTradeValuationLogic.scoreItem(PechTradeValuationLogic.PreferenceTier.GOLD, 5));
    }

    @Test
    public void testScoreItem_GemTier() {
        assertEquals(5, PechTradeValuationLogic.scoreItem(PechTradeValuationLogic.PreferenceTier.GEM, 1));
        assertEquals(25, PechTradeValuationLogic.scoreItem(PechTradeValuationLogic.PreferenceTier.GEM, 5));
    }

    @Test
    public void testScoreItem_ArtifactTier() {
        assertEquals(10, PechTradeValuationLogic.scoreItem(PechTradeValuationLogic.PreferenceTier.ARTIFACT, 1));
        assertEquals(50, PechTradeValuationLogic.scoreItem(PechTradeValuationLogic.PreferenceTier.ARTIFACT, 5));
    }

    @Test
    public void testScoreItem_InvalidInputs() {
        assertEquals(0, PechTradeValuationLogic.scoreItem(null, 5));
        assertEquals(0, PechTradeValuationLogic.scoreItem(PechTradeValuationLogic.PreferenceTier.GOLD, 0));
        assertEquals(0, PechTradeValuationLogic.scoreItem(PechTradeValuationLogic.PreferenceTier.GOLD, -2));
    }

    @Test
    public void testDetermineBarterYield_ZeroScore() {
        assertEquals(PechTradeValuationLogic.BarterYield.NONE, PechTradeValuationLogic.determineBarterYield(0, 0.99));
        assertEquals(PechTradeValuationLogic.BarterYield.NONE, PechTradeValuationLogic.determineBarterYield(-5, 0.99));
    }

    @Test
    public void testDetermineBarterYield_LowScore() {
        // Score = 1, Random = 0.5 -> Effective = 1 * (0.5 + 0.5) = 1.0 (< 2.0)
        assertEquals(PechTradeValuationLogic.BarterYield.NONE, PechTradeValuationLogic.determineBarterYield(1, 0.5));

        // Score = 1, Random = 0.9 -> Effective = 1 * (0.5 + 0.9) = 1.4 (< 2.0)
        assertEquals(PechTradeValuationLogic.BarterYield.NONE, PechTradeValuationLogic.determineBarterYield(1, 0.9));
    }

    @Test
    public void testDetermineBarterYield_CommonYield() {
        // Score = 3, Random = 0.0 -> Effective = 3 * 0.5 = 1.5 (< 2.0)
        assertEquals(PechTradeValuationLogic.BarterYield.NONE, PechTradeValuationLogic.determineBarterYield(3, 0.0));

        // Score = 3, Random = 0.2 -> Effective = 3 * 0.7 = 2.1 (>= 2.0 and < 5.0)
        assertEquals(PechTradeValuationLogic.BarterYield.COMMON, PechTradeValuationLogic.determineBarterYield(3, 0.2));

        // Score = 3, Random = 0.9 -> Effective = 3 * 1.4 = 4.2 (>= 2.0 and < 5.0)
        assertEquals(PechTradeValuationLogic.BarterYield.COMMON, PechTradeValuationLogic.determineBarterYield(3, 0.9));
    }

    @Test
    public void testDetermineBarterYield_UncommonYield() {
        // Score = 5, Random = 0.5 -> Effective = 5 * 1.0 = 5.0 (>= 5.0 and < 10.0)
        assertEquals(PechTradeValuationLogic.BarterYield.UNCOMMON, PechTradeValuationLogic.determineBarterYield(5, 0.5));

        // Score = 6, Random = 0.9 -> Effective = 6 * 1.4 = 8.4 (>= 5.0 and < 10.0)
        assertEquals(PechTradeValuationLogic.BarterYield.UNCOMMON, PechTradeValuationLogic.determineBarterYield(6, 0.9));
    }

    @Test
    public void testDetermineBarterYield_RareYield() {
        // Score = 10, Random = 0.5 -> Effective = 10 * 1.0 = 10.0 (>= 10.0 and < 20.0)
        assertEquals(PechTradeValuationLogic.BarterYield.RARE, PechTradeValuationLogic.determineBarterYield(10, 0.5));

        // Score = 13, Random = 0.9 -> Effective = 13 * 1.4 = 18.2 (>= 10.0 and < 20.0)
        assertEquals(PechTradeValuationLogic.BarterYield.RARE, PechTradeValuationLogic.determineBarterYield(13, 0.9));
    }

    @Test
    public void testDetermineBarterYield_ArtifactYield() {
        // Score = 20, Random = 0.5 -> Effective = 20 * 1.0 = 20.0 (>= 20.0)
        assertEquals(PechTradeValuationLogic.BarterYield.ARTIFACT, PechTradeValuationLogic.determineBarterYield(20, 0.5));

        // Score = 30, Random = 0.9 -> Effective = 30 * 1.4 = 42.0 (>= 20.0)
        assertEquals(PechTradeValuationLogic.BarterYield.ARTIFACT, PechTradeValuationLogic.determineBarterYield(30, 0.9));
    }
}
