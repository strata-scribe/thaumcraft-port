package thaumcraft.common.entities.monster.tainted;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaintedSlimeMergeLogicTest {

    @Test
    public void testCalculateSizeScaling_ZeroResidue() {
        assertEquals(1, TaintedSlimeMergeLogic.calculateSizeScaling(1, 0.0));
        assertEquals(4, TaintedSlimeMergeLogic.calculateSizeScaling(4, -5.0));
    }

    @Test
    public void testCalculateSizeScaling_NegativeSize() {
        assertEquals(1, TaintedSlimeMergeLogic.calculateSizeScaling(-2, 0.0));
        assertEquals(2, TaintedSlimeMergeLogic.calculateSizeScaling(-2, 2.0)); // Treats initial size as 1, requires 2 residue to reach size 2.
    }

    @Test
    public void testCalculateSizeScaling_NormalProgression() {
        // currentSize = 1
        // To reach size 2, needs 2 residue.
        assertEquals(1, TaintedSlimeMergeLogic.calculateSizeScaling(1, 1.9));
        assertEquals(2, TaintedSlimeMergeLogic.calculateSizeScaling(1, 2.0));

        // From size 1, needs 2 residue to reach size 2, then 3 residue to reach size 3. (Total 5 residue)
        assertEquals(2, TaintedSlimeMergeLogic.calculateSizeScaling(1, 4.9));
        assertEquals(3, TaintedSlimeMergeLogic.calculateSizeScaling(1, 5.0));

        // From size 4, needs 5 residue to reach size 5.
        assertEquals(5, TaintedSlimeMergeLogic.calculateSizeScaling(4, 5.0));
    }

    @Test
    public void testCalculateSplitHealthThreshold_SizeOneOrLess() {
        assertEquals(0.0, TaintedSlimeMergeLogic.calculateSplitHealthThreshold(1), 0.001);
        assertEquals(0.0, TaintedSlimeMergeLogic.calculateSplitHealthThreshold(0), 0.001);
        assertEquals(0.0, TaintedSlimeMergeLogic.calculateSplitHealthThreshold(-5), 0.001);
    }

    @Test
    public void testCalculateSplitHealthThreshold_LargerSizes() {
        assertEquals(2.0, TaintedSlimeMergeLogic.calculateSplitHealthThreshold(2), 0.001);
        assertEquals(4.5, TaintedSlimeMergeLogic.calculateSplitHealthThreshold(3), 0.001);
        assertEquals(8.0, TaintedSlimeMergeLogic.calculateSplitHealthThreshold(4), 0.001);
        assertEquals(12.5, TaintedSlimeMergeLogic.calculateSplitHealthThreshold(5), 0.001);
    }
}
