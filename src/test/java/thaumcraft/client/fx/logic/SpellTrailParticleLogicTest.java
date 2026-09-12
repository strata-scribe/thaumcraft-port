package thaumcraft.client.fx.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SpellTrailParticleLogicTest {

    private static final double EPSILON = 0.0001;

    @Test
    public void testCalculateLinearOffset() {
        SpellTrailParticleLogic.Vec3 start = new SpellTrailParticleLogic.Vec3(0, 0, 0);
        SpellTrailParticleLogic.Vec3 end = new SpellTrailParticleLogic.Vec3(10, 20, 30);

        SpellTrailParticleLogic.Vec3 mid = SpellTrailParticleLogic.calculateLinearOffset(start, end, 0.5);
        assertEquals(5.0, mid.x, EPSILON);
        assertEquals(10.0, mid.y, EPSILON);
        assertEquals(15.0, mid.z, EPSILON);

        SpellTrailParticleLogic.Vec3 startPoint = SpellTrailParticleLogic.calculateLinearOffset(start, end, 0.0);
        assertEquals(0.0, startPoint.x, EPSILON);
        assertEquals(0.0, startPoint.y, EPSILON);
        assertEquals(0.0, startPoint.z, EPSILON);

        SpellTrailParticleLogic.Vec3 endPoint = SpellTrailParticleLogic.calculateLinearOffset(start, end, 1.0);
        assertEquals(10.0, endPoint.x, EPSILON);
        assertEquals(20.0, endPoint.y, EPSILON);
        assertEquals(30.0, endPoint.z, EPSILON);

        SpellTrailParticleLogic.Vec3 outOfBounds = SpellTrailParticleLogic.calculateLinearOffset(start, end, 1.5);
        assertEquals(10.0, outOfBounds.x, EPSILON);
        assertEquals(20.0, outOfBounds.y, EPSILON);
        assertEquals(30.0, outOfBounds.z, EPSILON);

        SpellTrailParticleLogic.Vec3 outOfBoundsNeg = SpellTrailParticleLogic.calculateLinearOffset(start, end, -0.5);
        assertEquals(0.0, outOfBoundsNeg.x, EPSILON);
        assertEquals(0.0, outOfBoundsNeg.y, EPSILON);
        assertEquals(0.0, outOfBoundsNeg.z, EPSILON);
    }

    @Test
    public void testCalculateSpiralOffset() {
        SpellTrailParticleLogic.Vec3 start = new SpellTrailParticleLogic.Vec3(0, 0, 0);
        SpellTrailParticleLogic.Vec3 end = new SpellTrailParticleLogic.Vec3(10, 0, 0);

        // At progress 0, angle is 0. Base linear position is (0,0,0).
        // Direction is (1,0,0). Arbitrary up is (0,1,0).
        // U = up x dir = (0,1,0) x (1,0,0) = (0, 0, -1)
        // V = dir x U = (1,0,0) x (0,0,-1) = (0, 1, 0)
        // Offset = radius * (U * cos(0) + V * sin(0)) = radius * U = (0, 0, -radius)

        double radius = 2.0;
        double frequency = 1.0;

        SpellTrailParticleLogic.Vec3 pos0 = SpellTrailParticleLogic.calculateSpiralOffset(start, end, 0.0, radius, frequency);
        assertEquals(0.0, pos0.x, EPSILON);
        assertEquals(0.0, pos0.y, EPSILON);
        assertEquals(-2.0, pos0.z, EPSILON);

        // At progress 0.25 (quarter turn), angle is PI/2.
        // Base linear position is (2.5, 0, 0)
        // Offset = radius * (U * cos(PI/2) + V * sin(PI/2)) = radius * V = (0, radius, 0)
        SpellTrailParticleLogic.Vec3 pos025 = SpellTrailParticleLogic.calculateSpiralOffset(start, end, 0.25, radius, frequency);
        assertEquals(2.5, pos025.x, EPSILON);
        assertEquals(2.0, pos025.y, EPSILON);
        assertEquals(0.0, pos025.z, EPSILON);

        // At progress 0.5 (half turn), angle is PI.
        // Base linear position is (5.0, 0, 0)
        // Offset = radius * (U * cos(PI) + V * sin(PI)) = radius * (-U) = (0, 0, radius)
        SpellTrailParticleLogic.Vec3 pos05 = SpellTrailParticleLogic.calculateSpiralOffset(start, end, 0.5, radius, frequency);
        assertEquals(5.0, pos05.x, EPSILON);
        assertEquals(0.0, pos05.y, EPSILON);
        assertEquals(2.0, pos05.z, EPSILON);

        // At progress 1.0 (full turn), angle is 2*PI.
        // Base linear position is (10.0, 0, 0)
        // Offset = radius * (U * cos(2*PI) + V * sin(2*PI)) = radius * U = (0, 0, -radius)
        SpellTrailParticleLogic.Vec3 pos1 = SpellTrailParticleLogic.calculateSpiralOffset(start, end, 1.0, radius, frequency);
        assertEquals(10.0, pos1.x, EPSILON);
        assertEquals(0.0, pos1.y, EPSILON);
        assertEquals(-2.0, pos1.z, EPSILON);
    }

    @Test
    public void testCalculateSpiralOffsetVertical() {
        SpellTrailParticleLogic.Vec3 start = new SpellTrailParticleLogic.Vec3(0, 0, 0);
        SpellTrailParticleLogic.Vec3 end = new SpellTrailParticleLogic.Vec3(0, 10, 0);

        // Direction is (0,1,0). It is collinear with default up (0,1,0), so logic should pick (1,0,0) as up.
        // Let's ensure it handles collinearity without NaN.
        SpellTrailParticleLogic.Vec3 pos0 = SpellTrailParticleLogic.calculateSpiralOffset(start, end, 0.0, 2.0, 1.0);
        assertFalse(Double.isNaN(pos0.x));
        assertFalse(Double.isNaN(pos0.y));
        assertFalse(Double.isNaN(pos0.z));
    }

    @Test
    public void testGetElementalColor() {
        // Fire
        SpellTrailParticleLogic.RGBColor[] fire = SpellTrailParticleLogic.getElementalColor("Fire");
        assertEquals(1.0f, fire[0].r, EPSILON);
        assertEquals(0.5f, fire[0].g, EPSILON);
        assertEquals(0.0f, fire[0].b, EPSILON);
        assertEquals(1.0f, fire[1].r, EPSILON);
        assertEquals(0.0f, fire[1].g, EPSILON);
        assertEquals(0.0f, fire[1].b, EPSILON);

        // Frost
        SpellTrailParticleLogic.RGBColor[] frost = SpellTrailParticleLogic.getElementalColor("FROST");
        assertEquals(0.0f, frost[0].r, EPSILON);
        assertEquals(1.0f, frost[0].g, EPSILON);
        assertEquals(1.0f, frost[0].b, EPSILON);
        assertEquals(1.0f, frost[1].r, EPSILON);
        assertEquals(1.0f, frost[1].g, EPSILON);
        assertEquals(1.0f, frost[1].b, EPSILON);

        // Flux
        SpellTrailParticleLogic.RGBColor[] flux = SpellTrailParticleLogic.getElementalColor("fLuX");
        assertEquals(1.0f, flux[0].r, EPSILON);
        assertEquals(0.0f, flux[0].g, EPSILON);
        assertEquals(1.0f, flux[0].b, EPSILON);
        assertEquals(0.0f, flux[1].r, EPSILON);
        assertEquals(0.0f, flux[1].g, EPSILON);
        assertEquals(0.0f, flux[1].b, EPSILON);

        // Default / Unknown
        SpellTrailParticleLogic.RGBColor[] unknown = SpellTrailParticleLogic.getElementalColor("Earth");
        assertEquals(1.0f, unknown[0].r, EPSILON);
        assertEquals(1.0f, unknown[0].g, EPSILON);
        assertEquals(1.0f, unknown[0].b, EPSILON);
        assertEquals(1.0f, unknown[1].r, EPSILON);
        assertEquals(1.0f, unknown[1].g, EPSILON);
        assertEquals(1.0f, unknown[1].b, EPSILON);

        // Null
        SpellTrailParticleLogic.RGBColor[] nullType = SpellTrailParticleLogic.getElementalColor(null);
        assertEquals(1.0f, nullType[0].r, EPSILON);
        assertEquals(1.0f, nullType[0].g, EPSILON);
        assertEquals(1.0f, nullType[0].b, EPSILON);
    }
}
