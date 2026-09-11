package thaumcraft.common.world;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import thaumcraft.common.world.features.SilverwoodTreeLogic;

import static org.junit.jupiter.api.Assertions.*;

public class SilverwoodTreeLogicTest {

    @Test
    @DisplayName("Single trunk logic validation")
    public void testIsSingleTrunk() {
        assertTrue(SilverwoodTreeLogic.isSingleTrunk(0, 0), "Center (0,0) must be a trunk");
        assertFalse(SilverwoodTreeLogic.isSingleTrunk(1, 0), "Offset (1,0) should not be trunk");
        assertFalse(SilverwoodTreeLogic.isSingleTrunk(-1, 0), "Offset (-1,0) should not be trunk");
        assertFalse(SilverwoodTreeLogic.isSingleTrunk(0, 1), "Offset (0,1) should not be trunk");
        assertFalse(SilverwoodTreeLogic.isSingleTrunk(1, 1), "Offset (1,1) should not be trunk");
    }

    @Test
    @DisplayName("Pure aura node heart block at mid-height validation")
    public void testIsPureNodeHeart() {
        int height = 10;
        assertTrue(SilverwoodTreeLogic.isPureNodeHeart(0, 5, 0, height), "Mid-height (0,5,0) with height 10 must be pure node heart");
        assertFalse(SilverwoodTreeLogic.isPureNodeHeart(0, 4, 0, height), "Off mid-height should not be node heart");
        assertFalse(SilverwoodTreeLogic.isPureNodeHeart(1, 5, 0, height), "Off-center x should not be node heart");
        assertFalse(SilverwoodTreeLogic.isPureNodeHeart(0, 5, 1, height), "Off-center z should not be node heart");

        height = 9;
        assertTrue(SilverwoodTreeLogic.isPureNodeHeart(0, 4, 0, height), "Mid-height (0,4,0) with height 9 must be pure node heart");
        assertFalse(SilverwoodTreeLogic.isPureNodeHeart(0, 5, 0, height), "Off mid-height should not be node heart");
    }

    @Test
    @DisplayName("High-vis shimmer foliage bound validation")
    public void testIsHighVisShimmerFoliage() {
        int radiusSq = 9;
        assertTrue(SilverwoodTreeLogic.isHighVisShimmerFoliage(0, 0, 0, radiusSq), "Center inside foliage");
        assertTrue(SilverwoodTreeLogic.isHighVisShimmerFoliage(1, 2, 2, radiusSq), "distSq 9 <= 9");
        assertTrue(SilverwoodTreeLogic.isHighVisShimmerFoliage(3, 0, 0, radiusSq), "distSq 9 <= 9");
        assertTrue(SilverwoodTreeLogic.isHighVisShimmerFoliage(2, 2, 0, radiusSq), "distSq 8 <= 9");
        assertFalse(SilverwoodTreeLogic.isHighVisShimmerFoliage(3, 1, 0, radiusSq), "distSq 10 > 9");
        assertFalse(SilverwoodTreeLogic.isHighVisShimmerFoliage(2, 2, 2, radiusSq), "distSq 12 > 9");
    }
}
