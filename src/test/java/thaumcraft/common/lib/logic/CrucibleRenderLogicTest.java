package thaumcraft.common.lib.logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CrucibleRenderLogicTest {

    @Test
    public void testCalculateBlendedColor_DefaultColor() {
        int defaultColor = 0xFFFFFF;

        // Null checks
        assertEquals(defaultColor, CrucibleRenderLogic.calculateBlendedColor(null, new int[]{1}, defaultColor));
        assertEquals(defaultColor, CrucibleRenderLogic.calculateBlendedColor(new int[]{0}, null, defaultColor));

        // Length mismatch
        assertEquals(defaultColor, CrucibleRenderLogic.calculateBlendedColor(new int[]{0}, new int[]{1, 2}, defaultColor));

        // Empty array
        assertEquals(defaultColor, CrucibleRenderLogic.calculateBlendedColor(new int[]{}, new int[]{}, defaultColor));

        // Zero total amount
        assertEquals(defaultColor, CrucibleRenderLogic.calculateBlendedColor(new int[]{0xFF0000}, new int[]{0}, defaultColor));
    }

    @Test
    public void testCalculateBlendedColor_SingleColor() {
        int[] colors = {0xFF0000};
        int[] amounts = {10};
        int defaultColor = 0xFFFFFF;

        assertEquals(0xFF0000, CrucibleRenderLogic.calculateBlendedColor(colors, amounts, defaultColor));
    }

    @Test
    public void testCalculateBlendedColor_MultipleColors() {
        int[] colors = {0xFF0000, 0x00FF00, 0x0000FF}; // Red, Green, Blue
        int[] amounts = {10, 10, 10}; // Equal amounts
        int defaultColor = 0xFFFFFF;

        // Average of red, green, blue should be 0x555555
        // Red: 255/3 = 85 (0x55)
        // Green: 255/3 = 85 (0x55)
        // Blue: 255/3 = 85 (0x55)
        assertEquals(0x555555, CrucibleRenderLogic.calculateBlendedColor(colors, amounts, defaultColor));
    }

    @Test
    public void testCalculateBlendedColor_UnequalAmounts() {
        int[] colors = {0xFF0000, 0x00FF00}; // Red, Green
        int[] amounts = {30, 10}; // 3 parts red, 1 part green
        int defaultColor = 0xFFFFFF;

        // Red: 255 * 30 / 40 = 191 (0xBF)
        // Green: 255 * 10 / 40 = 63 (0x3F)
        // Blue: 0
        assertEquals(0xBF3F00, CrucibleRenderLogic.calculateBlendedColor(colors, amounts, defaultColor));
    }

    @Test
    public void testComputeBubbleOffset() {
        // Test lower bounds
        float[] offset1 = CrucibleRenderLogic.computeBubbleOffset(0.0f, 0.0f);
        assertArrayEquals(new float[]{0.125f, 0.125f}, offset1, 0.0001f);

        // Test upper bounds
        float[] offset2 = CrucibleRenderLogic.computeBubbleOffset(1.0f, 1.0f);
        assertArrayEquals(new float[]{0.875f, 0.875f}, offset2, 0.0001f);

        // Test middle
        float[] offset3 = CrucibleRenderLogic.computeBubbleOffset(0.5f, 0.5f);
        assertArrayEquals(new float[]{0.5f, 0.5f}, offset3, 0.0001f);

        // Test mixed
        float[] offset4 = CrucibleRenderLogic.computeBubbleOffset(0.2f, 0.8f);
        // x: 0.125 + 0.2 * 0.75 = 0.275
        // z: 0.125 + 0.8 * 0.75 = 0.725
        assertArrayEquals(new float[]{0.275f, 0.725f}, offset4, 0.0001f);
    }
}
