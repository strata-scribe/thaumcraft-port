package thaumcraft.client.fx.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WispParticleTrailLogicTest {

    private static final double EPSILON = 0.0001;

    @Test
    public void testCalculateLifetime() {
        assertEquals(15.0, WispParticleTrailLogic.calculateLifetime("terra", 10.0), EPSILON);
        assertEquals(15.0, WispParticleTrailLogic.calculateLifetime("ordo", 10.0), EPSILON);

        assertEquals(8.0, WispParticleTrailLogic.calculateLifetime("aer", 10.0), EPSILON);
        assertEquals(8.0, WispParticleTrailLogic.calculateLifetime("ignis", 10.0), EPSILON);

        assertEquals(5.0, WispParticleTrailLogic.calculateLifetime("perditio", 10.0), EPSILON);

        assertEquals(10.0, WispParticleTrailLogic.calculateLifetime("aqua", 10.0), EPSILON);

        assertEquals(10.0, WispParticleTrailLogic.calculateLifetime("unknown", 10.0), EPSILON);
        assertEquals(10.0, WispParticleTrailLogic.calculateLifetime(null, 10.0), EPSILON);
    }

    @Test
    public void testGetTrailColorLifeRatio1() {
        // At lifeRatio = 1.0, color should match base exactly.
        WispParticleTrailLogic.RGBColor aer = WispParticleTrailLogic.getTrailColor("aer", 1.0);
        assertEquals(new WispParticleTrailLogic.RGBColor(0xffff7e), aer);

        WispParticleTrailLogic.RGBColor terra = WispParticleTrailLogic.getTrailColor("terra", 1.0);
        assertEquals(new WispParticleTrailLogic.RGBColor(0x56c000), terra);

        WispParticleTrailLogic.RGBColor ignis = WispParticleTrailLogic.getTrailColor("ignis", 1.0);
        assertEquals(new WispParticleTrailLogic.RGBColor(0xff5a01), ignis);

        WispParticleTrailLogic.RGBColor aqua = WispParticleTrailLogic.getTrailColor("aqua", 1.0);
        assertEquals(new WispParticleTrailLogic.RGBColor(0x3cd4fc), aqua);

        WispParticleTrailLogic.RGBColor ordo = WispParticleTrailLogic.getTrailColor("ordo", 1.0);
        assertEquals(new WispParticleTrailLogic.RGBColor(0xd5d4ec), ordo);

        WispParticleTrailLogic.RGBColor perditio = WispParticleTrailLogic.getTrailColor("perditio", 1.0);
        assertEquals(new WispParticleTrailLogic.RGBColor(0x404040), perditio);
    }

    @Test
    public void testGetTrailColorLifeRatio0() {
        // At lifeRatio = 0.0, color should match target exactly.
        WispParticleTrailLogic.RGBColor aer = WispParticleTrailLogic.getTrailColor("aer", 0.0);
        assertEquals(new WispParticleTrailLogic.RGBColor(255, 255, 255), aer);

        WispParticleTrailLogic.RGBColor ordo = WispParticleTrailLogic.getTrailColor("ordo", 0.0);
        assertEquals(new WispParticleTrailLogic.RGBColor(255, 255, 255), ordo);

        WispParticleTrailLogic.RGBColor terra = WispParticleTrailLogic.getTrailColor("terra", 0.0);
        assertEquals(new WispParticleTrailLogic.RGBColor(0, 0, 0), terra);

        WispParticleTrailLogic.RGBColor ignis = WispParticleTrailLogic.getTrailColor("ignis", 0.0);
        assertEquals(new WispParticleTrailLogic.RGBColor(0, 0, 0), ignis);

        WispParticleTrailLogic.RGBColor perditio = WispParticleTrailLogic.getTrailColor("perditio", 0.0);
        assertEquals(new WispParticleTrailLogic.RGBColor(0, 0, 0), perditio);

        WispParticleTrailLogic.RGBColor aqua = WispParticleTrailLogic.getTrailColor("aqua", 0.0);
        assertEquals(new WispParticleTrailLogic.RGBColor(0, 0, 255), aqua);

        WispParticleTrailLogic.RGBColor unknown = WispParticleTrailLogic.getTrailColor("unknown", 0.0);
        assertEquals(new WispParticleTrailLogic.RGBColor(0, 0, 0), unknown);

        WispParticleTrailLogic.RGBColor nullType = WispParticleTrailLogic.getTrailColor(null, 0.0);
        assertEquals(new WispParticleTrailLogic.RGBColor(0, 0, 0), nullType);
    }

    @Test
    public void testGetTrailColorLifeRatio05() {
        // At lifeRatio = 0.5, color should be blended
        WispParticleTrailLogic.RGBColor aqua = WispParticleTrailLogic.getTrailColor("aqua", 0.5);
        WispParticleTrailLogic.RGBColor baseAqua = new WispParticleTrailLogic.RGBColor(0x3cd4fc);
        assertEquals(Math.round(baseAqua.r * 0.5 + 0 * 0.5), aqua.r);
        assertEquals(Math.round(baseAqua.g * 0.5 + 0 * 0.5), aqua.g);
        assertEquals(Math.round(baseAqua.b * 0.5 + 255 * 0.5), aqua.b);
    }
}
