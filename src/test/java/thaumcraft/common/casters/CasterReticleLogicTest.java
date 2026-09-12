package thaumcraft.common.casters;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CasterReticleLogicTest {

    @Test
    public void testComputeChargeProgress_Normal() {
        assertEquals(0.0, CasterReticleLogic.computeChargeProgress(0, 100));
        assertEquals(0.5, CasterReticleLogic.computeChargeProgress(50, 100));
        assertEquals(1.0, CasterReticleLogic.computeChargeProgress(100, 100));
    }

    @Test
    public void testComputeChargeProgress_OverCharge() {
        assertEquals(1.0, CasterReticleLogic.computeChargeProgress(150, 100));
    }

    @Test
    public void testComputeChargeProgress_NegativeCharge() {
        assertEquals(0.0, CasterReticleLogic.computeChargeProgress(-10, 100));
    }

    @Test
    public void testComputeChargeProgress_ZeroMaxCharge() {
        assertEquals(0.0, CasterReticleLogic.computeChargeProgress(50, 0));
        assertEquals(0.0, CasterReticleLogic.computeChargeProgress(50, -10));
    }

    @Test
    public void testComputeBloomRadius_MinProgress() {
        assertEquals(15.0, CasterReticleLogic.computeBloomRadius(0.0, 5.0, 10.0));
    }

    @Test
    public void testComputeBloomRadius_MidProgress() {
        assertEquals(10.0, CasterReticleLogic.computeBloomRadius(0.5, 5.0, 10.0));
    }

    @Test
    public void testComputeBloomRadius_MaxProgress() {
        assertEquals(5.0, CasterReticleLogic.computeBloomRadius(1.0, 5.0, 10.0));
    }

    @Test
    public void testComputeBloomRadius_OutOfBoundsProgress() {
        // Capped to 0.0 and 1.0 respectively
        assertEquals(15.0, CasterReticleLogic.computeBloomRadius(-0.5, 5.0, 10.0));
        assertEquals(5.0, CasterReticleLogic.computeBloomRadius(1.5, 5.0, 10.0));
    }

    @Test
    public void testBlendAspectColors_Empty() {
        assertEquals(0xFFFFFF, CasterReticleLogic.blendAspectColors());
        assertEquals(0xFFFFFF, CasterReticleLogic.blendAspectColors(null));
    }

    @Test
    public void testBlendAspectColors_SingleColor() {
        assertEquals(0xFF0000, CasterReticleLogic.blendAspectColors(0xFF0000));
    }

    @Test
    public void testBlendAspectColors_MultipleColors() {
        // Red = 0xFF0000, Blue = 0x0000FF
        // Blended: R = FF/2 = 7F, G = 00/2 = 00, B = FF/2 = 7F
        // Hex: 7F007F
        assertEquals(0x7F007F, CasterReticleLogic.blendAspectColors(0xFF0000, 0x0000FF));
    }

    @Test
    public void testBlendAspectColors_ThreeColors() {
        // R = FF0000, G = 00FF00, B = 0000FF
        // Blended: R=FF/3=55, G=FF/3=55, B=FF/3=55
        // Hex: 555555
        assertEquals(0x555555, CasterReticleLogic.blendAspectColors(0xFF0000, 0x00FF00, 0x0000FF));
    }
}
