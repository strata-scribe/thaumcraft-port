package thaumcraft.client.render;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JarRenderMathLogicTest {

    @Test
    public void testCalculateFluidYScale() {
        assertEquals(0.0f, JarRenderMathLogic.calculateFluidYScale(0), 0.001f);
        assertEquals(0.375f, JarRenderMathLogic.calculateFluidYScale(125), 0.001f);
        assertEquals(0.75f, JarRenderMathLogic.calculateFluidYScale(250), 0.001f);

        // Out of bounds checking
        assertEquals(0.0f, JarRenderMathLogic.calculateFluidYScale(-50), 0.001f);
        assertEquals(0.75f, JarRenderMathLogic.calculateFluidYScale(500), 0.001f);
    }

    @Test
    public void testCalculateBillboardRotation() {
        JarRenderMathLogic.BillboardRotation rot = JarRenderMathLogic.calculateBillboardRotation(45.0f, 90.0f);
        assertEquals(90.0f, rot.yRot(), 0.001f); // 180 - 90 = 90
        assertEquals(-45.0f, rot.xRot(), 0.001f); // -45
    }

    @Test
    public void testCalculateLabelUVs() {
        JarRenderMathLogic.LabelUV uv = JarRenderMathLogic.calculateLabelUVs(0.1f, 0.2f, 0.3f, 0.4f);
        assertEquals(0.1f, uv.minU(), 0.001f);
        assertEquals(0.2f, uv.maxU(), 0.001f);
        assertEquals(0.3f, uv.minV(), 0.001f);
        assertEquals(0.4f, uv.maxV(), 0.001f);
    }

    @Test
    public void testGetAspectColor() {
        assertEquals(0xffff7e, JarRenderMathLogic.getAspectColor("aer"));
        assertEquals(0x56c000, JarRenderMathLogic.getAspectColor("terra"));
        assertEquals(0xFFFFFF, JarRenderMathLogic.getAspectColor("unknown"));
        assertEquals(0xFFFFFF, JarRenderMathLogic.getAspectColor(null));
        // Case insensitivity check
        assertEquals(0xff5a01, JarRenderMathLogic.getAspectColor("IGNIS"));
    }
}
