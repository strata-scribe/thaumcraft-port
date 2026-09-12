package thaumcraft.common.items.armor;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GogglesRevealingRangeLogicTest {

    @Test
    public void testMaximumResolution() {
        assertEquals(GogglesRevealingRangeLogic.DetailResolution.MAXIMUM, GogglesRevealingRangeLogic.getResolutionForDistance(0.0));
        assertEquals(GogglesRevealingRangeLogic.DetailResolution.MAXIMUM, GogglesRevealingRangeLogic.getResolutionForDistance(1.5));
        assertEquals(GogglesRevealingRangeLogic.DetailResolution.MAXIMUM, GogglesRevealingRangeLogic.getResolutionForDistance(2.0));
    }

    @Test
    public void testHighResolution() {
        assertEquals(GogglesRevealingRangeLogic.DetailResolution.HIGH, GogglesRevealingRangeLogic.getResolutionForDistance(2.1));
        assertEquals(GogglesRevealingRangeLogic.DetailResolution.HIGH, GogglesRevealingRangeLogic.getResolutionForDistance(3.0));
        assertEquals(GogglesRevealingRangeLogic.DetailResolution.HIGH, GogglesRevealingRangeLogic.getResolutionForDistance(4.0));
    }

    @Test
    public void testMediumResolution() {
        assertEquals(GogglesRevealingRangeLogic.DetailResolution.MEDIUM, GogglesRevealingRangeLogic.getResolutionForDistance(4.1));
        assertEquals(GogglesRevealingRangeLogic.DetailResolution.MEDIUM, GogglesRevealingRangeLogic.getResolutionForDistance(6.0));
        assertEquals(GogglesRevealingRangeLogic.DetailResolution.MEDIUM, GogglesRevealingRangeLogic.getResolutionForDistance(8.0));
    }

    @Test
    public void testLowResolution() {
        assertEquals(GogglesRevealingRangeLogic.DetailResolution.LOW, GogglesRevealingRangeLogic.getResolutionForDistance(8.1));
        assertEquals(GogglesRevealingRangeLogic.DetailResolution.LOW, GogglesRevealingRangeLogic.getResolutionForDistance(12.0));
        assertEquals(GogglesRevealingRangeLogic.DetailResolution.LOW, GogglesRevealingRangeLogic.getResolutionForDistance(16.0));
    }

    @Test
    public void testNoneResolution() {
        assertEquals(GogglesRevealingRangeLogic.DetailResolution.NONE, GogglesRevealingRangeLogic.getResolutionForDistance(16.1));
        assertEquals(GogglesRevealingRangeLogic.DetailResolution.NONE, GogglesRevealingRangeLogic.getResolutionForDistance(20.0));
    }

    @Test
    public void testNegativeDistance() {
        assertEquals(GogglesRevealingRangeLogic.DetailResolution.NONE, GogglesRevealingRangeLogic.getResolutionForDistance(-1.0));
    }
}
