package thaumcraft.api.casters.focus;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FocusExchangeLogicTest {

    @Test
    public void testCalculateExchangeComplexity() {
        assertEquals(3, FocusExchangeLogic.calculateExchangeComplexity(1));
        assertEquals(15, FocusExchangeLogic.calculateExchangeComplexity(5));
        assertEquals(0, FocusExchangeLogic.calculateExchangeComplexity(-1));
    }

    @Test
    public void testHasExchangePermission() {
        assertTrue(FocusExchangeLogic.hasExchangePermission(1.0f, 1.0f));
        assertFalse(FocusExchangeLogic.hasExchangePermission(-1.0f, 1.0f));
        assertFalse(FocusExchangeLogic.hasExchangePermission(1.0f, -1.0f));
    }

    @Test
    public void testIsHardnessCompatible() {
        // source <= target * 2 + 1
        assertTrue(FocusExchangeLogic.isHardnessCompatible(1.0f, 1.0f));
        assertTrue(FocusExchangeLogic.isHardnessCompatible(1.0f, 3.0f));
        assertFalse(FocusExchangeLogic.isHardnessCompatible(1.0f, 3.1f));

        // Unbreakable blocks
        assertFalse(FocusExchangeLogic.isHardnessCompatible(-1.0f, 1.0f));
        assertFalse(FocusExchangeLogic.isHardnessCompatible(1.0f, -1.0f));
    }

    @Test
    public void testCalculateVisCost() {
        // Cost = 0.5f + (hardnessDiff * 0.5f) + distanceFactor
        // If distance <= 1, distanceFactor = 0
        assertEquals(0.5f, FocusExchangeLogic.calculateVisCost(1.0f, 1.0f, 1.0), 0.01f);
        assertEquals(1.0f, FocusExchangeLogic.calculateVisCost(2.0f, 1.0f, 1.0), 0.01f);
        assertEquals(0.6f, FocusExchangeLogic.calculateVisCost(1.0f, 1.0f, 2.0), 0.01f);
        assertEquals(1.1f, FocusExchangeLogic.calculateVisCost(2.0f, 1.0f, 2.0), 0.01f);
    }
}
