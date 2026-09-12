package thaumcraft.common.tiles.crafting;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InfusionArcBezierLogicTest {

    @Test
    public void testCalculateControlPoints() {
        InfusionArcBezierLogic.Vector3D start = new InfusionArcBezierLogic.Vector3D(0, 0, 0);
        InfusionArcBezierLogic.Vector3D end = new InfusionArcBezierLogic.Vector3D(3, 0, 0);
        double height = 2.0;

        InfusionArcBezierLogic.Vector3D[] controlPoints = InfusionArcBezierLogic.calculateControlPoints(start, end, height);

        assertNotNull(controlPoints);
        assertEquals(4, controlPoints.length);

        // P0 should be start
        assertEquals(start, controlPoints[0]);

        // P3 should be end
        assertEquals(end, controlPoints[3]);

        // P1 should be 1/3 way across, and up by height
        assertEquals(new InfusionArcBezierLogic.Vector3D(1, 2, 0), controlPoints[1]);

        // P2 should be 2/3 way across, and up by height
        assertEquals(new InfusionArcBezierLogic.Vector3D(2, 2, 0), controlPoints[2]);
    }

    @Test
    public void testCalculateSegments() {
        InfusionArcBezierLogic.Vector3D p0 = new InfusionArcBezierLogic.Vector3D(0, 0, 0);
        InfusionArcBezierLogic.Vector3D p1 = new InfusionArcBezierLogic.Vector3D(1, 2, 0);
        InfusionArcBezierLogic.Vector3D p2 = new InfusionArcBezierLogic.Vector3D(2, 2, 0);
        InfusionArcBezierLogic.Vector3D p3 = new InfusionArcBezierLogic.Vector3D(3, 0, 0);

        InfusionArcBezierLogic.Vector3D[] controlPoints = new InfusionArcBezierLogic.Vector3D[]{p0, p1, p2, p3};

        int segments = 10;
        List<InfusionArcBezierLogic.Vector3D> points = InfusionArcBezierLogic.calculateSegments(controlPoints, segments);

        assertNotNull(points);
        assertEquals(segments + 1, points.size(), "Should have segments + 1 points");

        // The first point should be exactly P0 (t=0)
        assertEquals(p0.x, points.get(0).x, 0.0001);
        assertEquals(p0.y, points.get(0).y, 0.0001);
        assertEquals(p0.z, points.get(0).z, 0.0001);

        // The last point should be exactly P3 (t=1)
        assertEquals(p3.x, points.get(segments).x, 0.0001);
        assertEquals(p3.y, points.get(segments).y, 0.0001);
        assertEquals(p3.z, points.get(segments).z, 0.0001);

        // Calculate expected point for t=0.5
        // B(0.5) = 0.125 * P0 + 0.375 * P1 + 0.375 * P2 + 0.125 * P3
        // x = 0.125 * 0 + 0.375 * 1 + 0.375 * 2 + 0.125 * 3 = 0 + 0.375 + 0.75 + 0.375 = 1.5
        // y = 0.125 * 0 + 0.375 * 2 + 0.375 * 2 + 0.125 * 0 = 0 + 0.75 + 0.75 + 0 = 1.5
        // z = 0
        InfusionArcBezierLogic.Vector3D midPoint = points.get(segments / 2);
        assertEquals(1.5, midPoint.x, 0.0001);
        assertEquals(1.5, midPoint.y, 0.0001);
        assertEquals(0.0, midPoint.z, 0.0001);
    }

    @Test
    public void testCalculateSegmentsInvalidInput() {
        InfusionArcBezierLogic.Vector3D p0 = new InfusionArcBezierLogic.Vector3D(0, 0, 0);
        InfusionArcBezierLogic.Vector3D[] badControlPoints = new InfusionArcBezierLogic.Vector3D[]{p0};

        assertThrows(IllegalArgumentException.class, () -> {
            InfusionArcBezierLogic.calculateSegments(badControlPoints, 10);
        });

        InfusionArcBezierLogic.Vector3D p1 = new InfusionArcBezierLogic.Vector3D(1, 2, 0);
        InfusionArcBezierLogic.Vector3D p2 = new InfusionArcBezierLogic.Vector3D(2, 2, 0);
        InfusionArcBezierLogic.Vector3D p3 = new InfusionArcBezierLogic.Vector3D(3, 0, 0);
        InfusionArcBezierLogic.Vector3D[] controlPoints = new InfusionArcBezierLogic.Vector3D[]{p0, p1, p2, p3};

        assertThrows(IllegalArgumentException.class, () -> {
            InfusionArcBezierLogic.calculateSegments(controlPoints, 0);
        });
    }

    @Test
    public void testVectorMath() {
        InfusionArcBezierLogic.Vector3D v1 = new InfusionArcBezierLogic.Vector3D(1, 2, 3);
        InfusionArcBezierLogic.Vector3D v2 = new InfusionArcBezierLogic.Vector3D(4, 5, 6);

        InfusionArcBezierLogic.Vector3D sum = v1.add(v2);
        assertEquals(new InfusionArcBezierLogic.Vector3D(5, 7, 9), sum);

        InfusionArcBezierLogic.Vector3D product = v1.multiply(2);
        assertEquals(new InfusionArcBezierLogic.Vector3D(2, 4, 6), product);
    }
}
