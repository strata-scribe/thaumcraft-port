package thaumcraft.client.gui.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ThaumonomiconLayoutMathLogicTest {

    @Test
    public void testGetTabY() {
        assertEquals(30, ThaumonomiconLayoutMathLogic.getTabY(0));
        assertEquals(30 + 32, ThaumonomiconLayoutMathLogic.getTabY(1));
        assertEquals(30 + 32 * 2, ThaumonomiconLayoutMathLogic.getTabY(2));
    }

    @Test
    public void testIsTabHovered() {
        // Tab 0 starts at x=5, y=30, w=28, h=28
        // Inside
        assertTrue(ThaumonomiconLayoutMathLogic.isTabHovered(5, 30, 0));
        assertTrue(ThaumonomiconLayoutMathLogic.isTabHovered(33, 58, 0));
        assertTrue(ThaumonomiconLayoutMathLogic.isTabHovered(15, 40, 0));

        // Outside X
        assertFalse(ThaumonomiconLayoutMathLogic.isTabHovered(4, 40, 0));
        assertFalse(ThaumonomiconLayoutMathLogic.isTabHovered(34, 40, 0));

        // Outside Y
        assertFalse(ThaumonomiconLayoutMathLogic.isTabHovered(15, 29, 0));
        assertFalse(ThaumonomiconLayoutMathLogic.isTabHovered(15, 59, 0));

        // Tab 1 starts at x=5, y=62, w=28, h=28
        assertTrue(ThaumonomiconLayoutMathLogic.isTabHovered(15, 65, 1));
        assertFalse(ThaumonomiconLayoutMathLogic.isTabHovered(15, 60, 1)); // between tab 0 and 1
    }

    @Test
    public void testGetIconX() {
        assertEquals(11, ThaumonomiconLayoutMathLogic.getIconX());
    }

    @Test
    public void testGetIconY() {
        assertEquals(30 + 6, ThaumonomiconLayoutMathLogic.getIconY(0));
        assertEquals(62 + 6, ThaumonomiconLayoutMathLogic.getIconY(1));
    }

    @Test
    public void testGetSelectedIndicatorY() {
        assertEquals(30 + 4, ThaumonomiconLayoutMathLogic.getSelectedIndicatorY(0));
        assertEquals(62 + 4, ThaumonomiconLayoutMathLogic.getSelectedIndicatorY(1));
    }

    @Test
    public void testGetSelectedIndicatorHeight() {
        assertEquals(20, ThaumonomiconLayoutMathLogic.getSelectedIndicatorHeight());
    }
}
