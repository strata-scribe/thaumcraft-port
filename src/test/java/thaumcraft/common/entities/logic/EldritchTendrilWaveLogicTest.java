package thaumcraft.common.entities.logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class EldritchTendrilWaveLogicTest {

    @Test
    public void testOscillatingAngles() {
        float time = 10.0f;
        int tendrilIndex = 0;
        float speed = 0.1f;
        float amplitude = 1.0f;

        float angleX = EldritchTendrilWaveLogic.calculateTendrilAngleX(time, tendrilIndex, speed, amplitude);
        float expectedX = (float) Math.sin(time * speed);
        assertEquals(expectedX, angleX, 0.001f, "X angle oscillation mismatch");

        float angleY = EldritchTendrilWaveLogic.calculateTendrilAngleY(time, tendrilIndex, speed, amplitude);
        float expectedY = (float) Math.cos(time * speed);
        assertEquals(expectedY, angleY, 0.001f, "Y angle oscillation mismatch");

        float angleZ = EldritchTendrilWaveLogic.calculateTendrilAngleZ(time, tendrilIndex, speed, amplitude);
        float expectedZ = (float) (Math.sin(time * speed * 0.8f) * amplitude * 0.5f);
        assertEquals(expectedZ, angleZ, 0.001f, "Z angle oscillation mismatch");
    }

    @Test
    public void testDifferentTendrilIndicesProduceDifferentOffsets() {
        float time = 5.0f;
        float speed = 0.2f;
        float amplitude = 2.0f;

        float angleX0 = EldritchTendrilWaveLogic.calculateTendrilAngleX(time, 0, speed, amplitude);
        float angleX1 = EldritchTendrilWaveLogic.calculateTendrilAngleX(time, 1, speed, amplitude);
        float angleX2 = EldritchTendrilWaveLogic.calculateTendrilAngleX(time, 2, speed, amplitude);

        assertNotEquals(angleX0, angleX1, 0.001f, "Tendrils 0 and 1 should have different X angles");
        assertNotEquals(angleX1, angleX2, 0.001f, "Tendrils 1 and 2 should have different X angles");
        assertNotEquals(angleX0, angleX2, 0.001f, "Tendrils 0 and 2 should have different X angles");

        float angleY0 = EldritchTendrilWaveLogic.calculateTendrilAngleY(time, 0, speed, amplitude);
        float angleY1 = EldritchTendrilWaveLogic.calculateTendrilAngleY(time, 1, speed, amplitude);

        assertNotEquals(angleY0, angleY1, 0.001f, "Tendrils 0 and 1 should have different Y angles");

        float angleZ0 = EldritchTendrilWaveLogic.calculateTendrilAngleZ(time, 0, speed, amplitude);
        float angleZ1 = EldritchTendrilWaveLogic.calculateTendrilAngleZ(time, 1, speed, amplitude);

        assertNotEquals(angleZ0, angleZ1, 0.001f, "Tendrils 0 and 1 should have different Z angles");
    }

    @Test
    public void testZeroAmplitude() {
        float time = 10.0f;
        int tendrilIndex = 1;
        float speed = 0.5f;
        float amplitude = 0.0f;

        assertEquals(0.0f, EldritchTendrilWaveLogic.calculateTendrilAngleX(time, tendrilIndex, speed, amplitude), 0.001f);
        assertEquals(0.0f, EldritchTendrilWaveLogic.calculateTendrilAngleY(time, tendrilIndex, speed, amplitude), 0.001f);
        assertEquals(0.0f, EldritchTendrilWaveLogic.calculateTendrilAngleZ(time, tendrilIndex, speed, amplitude), 0.001f);
    }
}
