package thaumcraft.client.render;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MatrixRenderMathLogicTest {

    @Test
    public void testCalculateHoverBobbing() {
        // test bobbing offset at tickTime = 0
        assertEquals(0.0, MatrixRenderMathLogic.calculateHoverBobbing(0, 1.0, 1.0), 0.0001);

        // test bobbing offset at tickTime = pi / (2 * frequency)
        long tickTime = 100;
        double frequency = Math.PI / (2 * tickTime);
        assertEquals(1.0, MatrixRenderMathLogic.calculateHoverBobbing(tickTime, 1.0, frequency), 0.0001);

        // test with a different amplitude
        assertEquals(5.0, MatrixRenderMathLogic.calculateHoverBobbing(tickTime, 5.0, frequency), 0.0001);
    }

    @Test
    public void testCalculateRotationAngle() {
        // basic test
        assertEquals(0.0, MatrixRenderMathLogic.calculateRotationAngle(0, 1.0), 0.0001);

        // speed = 1.0
        assertEquals(90.0, MatrixRenderMathLogic.calculateRotationAngle(90, 1.0), 0.0001);
        assertEquals(180.0, MatrixRenderMathLogic.calculateRotationAngle(180, 1.0), 0.0001);
        assertEquals(270.0, MatrixRenderMathLogic.calculateRotationAngle(270, 1.0), 0.0001);

        // wraparound positive
        assertEquals(0.0, MatrixRenderMathLogic.calculateRotationAngle(360, 1.0), 0.0001);
        assertEquals(10.0, MatrixRenderMathLogic.calculateRotationAngle(370, 1.0), 0.0001);

        // wraparound negative
        assertEquals(350.0, MatrixRenderMathLogic.calculateRotationAngle(-10, 1.0), 0.0001);
    }

    @Test
    public void testComputeBezierControlPoints() {
        MatrixRenderMathLogic.Point3D start = new MatrixRenderMathLogic.Point3D(0, 0, 0);
        MatrixRenderMathLogic.Point3D end = new MatrixRenderMathLogic.Point3D(30, 0, 30);
        double arcHeight = 10.0;

        MatrixRenderMathLogic.Point3D[] controlPoints = MatrixRenderMathLogic.computeBezierControlPoints(start, end, arcHeight);
        assertEquals(4, controlPoints.length);

        // p0 is start
        assertEquals(start.x(), controlPoints[0].x(), 0.0001);
        assertEquals(start.y(), controlPoints[0].y(), 0.0001);
        assertEquals(start.z(), controlPoints[0].z(), 0.0001);

        // p1
        assertEquals(9.9, controlPoints[1].x(), 0.0001);
        assertEquals(10.0, controlPoints[1].y(), 0.0001);
        assertEquals(9.9, controlPoints[1].z(), 0.0001);

        // p2
        assertEquals(20.1, controlPoints[2].x(), 0.0001);
        assertEquals(10.0, controlPoints[2].y(), 0.0001);
        assertEquals(20.1, controlPoints[2].z(), 0.0001);

        // p3 is end
        assertEquals(end.x(), controlPoints[3].x(), 0.0001);
        assertEquals(end.y(), controlPoints[3].y(), 0.0001);
        assertEquals(end.z(), controlPoints[3].z(), 0.0001);
    }

    @Test
    public void testInterpolateBezierSpline() {
        MatrixRenderMathLogic.Point3D p0 = new MatrixRenderMathLogic.Point3D(0, 0, 0);
        MatrixRenderMathLogic.Point3D p1 = new MatrixRenderMathLogic.Point3D(0, 10, 0);
        MatrixRenderMathLogic.Point3D p2 = new MatrixRenderMathLogic.Point3D(10, 10, 0);
        MatrixRenderMathLogic.Point3D p3 = new MatrixRenderMathLogic.Point3D(10, 0, 0);

        // t = 0
        MatrixRenderMathLogic.Point3D result0 = MatrixRenderMathLogic.interpolateBezierSpline(p0, p1, p2, p3, 0.0);
        assertEquals(p0.x(), result0.x(), 0.0001);
        assertEquals(p0.y(), result0.y(), 0.0001);
        assertEquals(p0.z(), result0.z(), 0.0001);

        // t = 1
        MatrixRenderMathLogic.Point3D result1 = MatrixRenderMathLogic.interpolateBezierSpline(p0, p1, p2, p3, 1.0);
        assertEquals(p3.x(), result1.x(), 0.0001);
        assertEquals(p3.y(), result1.y(), 0.0001);
        assertEquals(p3.z(), result1.z(), 0.0001);

        // t = 0.5 (midpoint)
        MatrixRenderMathLogic.Point3D resultHalf = MatrixRenderMathLogic.interpolateBezierSpline(p0, p1, p2, p3, 0.5);
        assertEquals(5.0, resultHalf.x(), 0.0001);
        assertEquals(7.5, resultHalf.y(), 0.0001);
        assertEquals(0.0, resultHalf.z(), 0.0001);
    }
}
