package thaumcraft.common.tiles.crafting;

import java.util.ArrayList;
import java.util.List;

/**
 * Pure Java logic for calculating multi-segment cubic Bezier arcs between the Infusion Matrix and Pedestals.
 * Fully decoupled from Minecraft/Forge APIs for unit testing.
 */
public class InfusionArcBezierLogic {

    public static class Vector3D {
        public final double x;
        public final double y;
        public final double z;

        public Vector3D(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Vector3D add(Vector3D other) {
            return new Vector3D(this.x + other.x, this.y + other.y, this.z + other.z);
        }

        public Vector3D multiply(double scalar) {
            return new Vector3D(this.x * scalar, this.y * scalar, this.z * scalar);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Vector3D other = (Vector3D) obj;
            return Double.compare(other.x, x) == 0 &&
                   Double.compare(other.y, y) == 0 &&
                   Double.compare(other.z, z) == 0;
        }

        @Override
        public String toString() {
            return String.format("Vector3D[%.4f, %.4f, %.4f]", x, y, z);
        }
    }

    /**
     * Calculates the 4 control points for a cubic Bezier curve arcing upwards between two points.
     *
     * @param start  The starting point (e.g., Matrix).
     * @param end    The ending point (e.g., Pedestal).
     * @param height The vertical height of the arc's control points relative to the start/end points.
     * @return An array of 4 Vector3D control points (P0, P1, P2, P3).
     */
    public static Vector3D[] calculateControlPoints(Vector3D start, Vector3D end, double height) {
        // P0 is the start point
        Vector3D p0 = start;

        // P3 is the end point
        Vector3D p3 = end;

        // Direction vector from start to end
        Vector3D dir = new Vector3D(end.x - start.x, end.y - start.y, end.z - start.z);

        // P1 is 1/3 along the path, raised by height
        Vector3D p1 = new Vector3D(
            start.x + dir.x / 3.0,
            start.y + dir.y / 3.0 + height,
            start.z + dir.z / 3.0
        );

        // P2 is 2/3 along the path, raised by height
        Vector3D p2 = new Vector3D(
            start.x + dir.x * 2.0 / 3.0,
            start.y + dir.y * 2.0 / 3.0 + height,
            start.z + dir.z * 2.0 / 3.0
        );

        return new Vector3D[]{p0, p1, p2, p3};
    }

    /**
     * Calculates a list of points representing segments along a cubic Bezier curve.
     *
     * @param controlPoints The 4 control points of the cubic Bezier curve.
     * @param segments      The number of segments to divide the curve into. The returned list will have segments + 1 points.
     * @return A list of Vector3D points lying on the curve.
     */
    public static List<Vector3D> calculateSegments(Vector3D[] controlPoints, int segments) {
        if (controlPoints == null || controlPoints.length != 4) {
            throw new IllegalArgumentException("Control points array must contain exactly 4 points.");
        }
        if (segments <= 0) {
            throw new IllegalArgumentException("Segments must be greater than 0.");
        }

        List<Vector3D> points = new ArrayList<>(segments + 1);
        Vector3D p0 = controlPoints[0];
        Vector3D p1 = controlPoints[1];
        Vector3D p2 = controlPoints[2];
        Vector3D p3 = controlPoints[3];

        for (int i = 0; i <= segments; i++) {
            double t = (double) i / segments;
            points.add(calculateBezierPoint(t, p0, p1, p2, p3));
        }

        return points;
    }

    /**
     * Calculates a point on a cubic Bezier curve at parameter t.
     * Formula: B(t) = (1-t)^3 * P0 + 3(1-t)^2 * t * P1 + 3(1-t) * t^2 * P2 + t^3 * P3
     *
     * @param t  Parameter ranging from 0.0 to 1.0.
     * @param p0 Control point 0.
     * @param p1 Control point 1.
     * @param p2 Control point 2.
     * @param p3 Control point 3.
     * @return The Vector3D point on the curve at parameter t.
     */
    private static Vector3D calculateBezierPoint(double t, Vector3D p0, Vector3D p1, Vector3D p2, Vector3D p3) {
        double u = 1.0 - t;
        double tt = t * t;
        double uu = u * u;
        double uuu = uu * u;
        double ttt = tt * t;

        Vector3D pt = p0.multiply(uuu); // (1-t)^3 * P0
        pt = pt.add(p1.multiply(3 * uu * t)); // 3(1-t)^2 * t * P1
        pt = pt.add(p2.multiply(3 * u * tt)); // 3(1-t) * t^2 * P2
        pt = pt.add(p3.multiply(ttt)); // t^3 * P3

        return pt;
    }
}
