package thaumcraft.common.entities.monster.tainted;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TaintCrawlerPackLogicTest {

    @Test
    public void testSpeedMultiplier() {
        assertEquals(1.0f, TaintCrawlerPackLogic.calculateSpeedMultiplier(0), 0.001f);
        assertEquals(1.1f, TaintCrawlerPackLogic.calculateSpeedMultiplier(1), 0.001f);
        assertEquals(1.3f, TaintCrawlerPackLogic.calculateSpeedMultiplier(3), 0.001f);
        assertEquals(1.5f, TaintCrawlerPackLogic.calculateSpeedMultiplier(5), 0.001f);
        assertEquals(1.5f, TaintCrawlerPackLogic.calculateSpeedMultiplier(10), 0.001f); // Capped at 1.5
    }

    @Test
    public void testDamageMultiplier() {
        assertEquals(1.0f, TaintCrawlerPackLogic.calculateDamageMultiplier(0), 0.001f);
        assertEquals(1.2f, TaintCrawlerPackLogic.calculateDamageMultiplier(1), 0.001f);
        assertEquals(1.6f, TaintCrawlerPackLogic.calculateDamageMultiplier(3), 0.001f);
        assertEquals(2.0f, TaintCrawlerPackLogic.calculateDamageMultiplier(5), 0.001f);
        assertEquals(2.0f, TaintCrawlerPackLogic.calculateDamageMultiplier(10), 0.001f); // Capped at 2.0
    }

    @Test
    public void testLeaderFollowingBehavior() {
        assertTrue(TaintCrawlerPackLogic.isFollowingLeader(10.0));
        assertTrue(TaintCrawlerPackLogic.isFollowingLeader(16.0));
        assertFalse(TaintCrawlerPackLogic.isFollowingLeader(16.1));
        assertFalse(TaintCrawlerPackLogic.isFollowingLeader(20.0));
    }

    @Test
    public void testSwarmBurstOnEggSackDestruction() {
        assertEquals(0, TaintCrawlerPackLogic.getSwarmBurstCount(0));
        assertEquals(2, TaintCrawlerPackLogic.getSwarmBurstCount(1));
        assertEquals(6, TaintCrawlerPackLogic.getSwarmBurstCount(3));
        assertEquals(10, TaintCrawlerPackLogic.getSwarmBurstCount(5));
    }
}
