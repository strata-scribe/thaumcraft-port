package thaumcraft.client.render;

import java.util.ArrayList;
import java.util.List;

public class MatrixRenderMathLogic {

    public record Point3D(double x, double y, double z) {}

    /**
     * Calculates the matrix hover bobbing based on tick time.
     * Uses a sine wave to create a smooth up and down motion.
     * @param tickTime the current tick time
     * @param amplitude the maximum distance to move up or down
     * @param frequency the speed of the bobbing motion
     * @return the bobbing offset
     */
    public static double calculateHoverBobbing(long tickTime, double amplitude, double frequency) {
        return Math.sin(tickTime * frequency) * amplitude;
    }

    /**
     * Calculates the matrix rotation angle based on tick time.
     * @param tickTime the current tick time
     * @param speed the rotation speed
     * @return the rotation angle in degrees (0 to 360)
     */
    public static double calculateRotationAngle(long tickTime, double speed) {
        double rawAngle = (tickTime * speed) % 360.0;
        if (rawAngle < 0) {
            rawAngle += 360.0;
        }
        return rawAngle;
    }

    /**
     * Computes the 4 cubic Bezier control points for lightning arcs from matrix to pedestals.
     * @param start the start point (e.g., matrix center)
     * @param end the end point (e.g., pedestal top)
     * @param arcHeight the height to elevate the intermediate points for the arc
     * @return an array of 4 Point3D control points
     */
    public static Point3D[] computeBezierControlPoints(Point3D start, Point3D end, double arcHeight) {
        // P0 is start
        // P3 is end
        // P1 and P2 are calculated by interpolating between start and end, and adding height
        Point3D p1 = new Point3D(
            start.x() + (end.x() - start.x()) * 0.33,
            start.y() + (end.y() - start.y()) * 0.33 + arcHeight,
            start.z() + (end.z() - start.z()) * 0.33
        );
        Point3D p2 = new Point3D(
            start.x() + (end.x() - start.x()) * 0.67,
            start.y() + (end.y() - start.y()) * 0.67 + arcHeight,
            start.z() + (end.z() - start.z()) * 0.67
        );

        return new Point3D[]{start, p1, p2, end};
    }

    /**
     * Calculates an interpolated point along a cubic Bezier curve.
     * @param p0 control point 0 (start)
     * @param p1 control point 1
     * @param p2 control point 2
     * @param p3 control point 3 (end)
     * @param t interpolation parameter from 0.0 to 1.0
     * @return the interpolated Point3D
     */
    public static Point3D interpolateBezierSpline(Point3D p0, Point3D p1, Point3D p2, Point3D p3, double t) {
        double u = 1 - t;
        double tt = t * t;
        double uu = u * u;
        double uuu = uu * u;
        double ttt = tt * t;

        double x = uuu * p0.x() + 3 * uu * t * p1.x() + 3 * u * tt * p2.x() + ttt * p3.x();
        double y = uuu * p0.y() + 3 * uu * t * p1.y() + 3 * u * tt * p2.y() + ttt * p3.y();
        double z = uuu * p0.z() + 3 * uu * t * p1.z() + 3 * u * tt * p2.z() + ttt * p3.z();

        return new Point3D(x, y, z);
    }
}
