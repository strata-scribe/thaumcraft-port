package thaumcraft.common.world.features;

import java.util.ArrayList;
import java.util.List;

/**
 * Pure Java logic helper for procedural branch generation.
 * Strict mathematical decoupling: contains zero Minecraft or NeoForge classloader imports.
 */
public class GreatwoodTreeLogic {

    /**
     * Calculates the blocks that make up a branch between two points.
     * Uses a simple 3D line algorithm.
     *
     * @param x0 Starting X coordinate
     * @param y0 Starting Y coordinate
     * @param z0 Starting Z coordinate
     * @param x1 Ending X coordinate
     * @param y1 Ending Y coordinate
     * @param z1 Ending Z coordinate
     * @return List of block coordinates [x, y, z] representing the branch
     */
    public static List<int[]> calculateBranchBlocks(int x0, int y0, int z0, int x1, int y1, int z1) {
        List<int[]> line = new ArrayList<>();

        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int dz = Math.abs(z1 - z0);

        int xs = (x1 > x0) ? 1 : -1;
        int ys = (y1 > y0) ? 1 : -1;
        int zs = (z1 > z0) ? 1 : -1;

        // Driving axis
        if (dx >= dy && dx >= dz) {
            int p1 = 2 * dy - dx;
            int p2 = 2 * dz - dx;
            while (x0 != x1) {
                line.add(new int[]{x0, y0, z0});
                x0 += xs;
                if (p1 >= 0) {
                    y0 += ys;
                    p1 -= 2 * dx;
                }
                if (p2 >= 0) {
                    z0 += zs;
                    p2 -= 2 * dx;
                }
                p1 += 2 * dy;
                p2 += 2 * dz;
            }
        } else if (dy >= dx && dy >= dz) {
            int p1 = 2 * dx - dy;
            int p2 = 2 * dz - dy;
            while (y0 != y1) {
                line.add(new int[]{x0, y0, z0});
                y0 += ys;
                if (p1 >= 0) {
                    x0 += xs;
                    p1 -= 2 * dy;
                }
                if (p2 >= 0) {
                    z0 += zs;
                    p2 -= 2 * dy;
                }
                p1 += 2 * dx;
                p2 += 2 * dz;
            }
        } else {
            int p1 = 2 * dy - dz;
            int p2 = 2 * dx - dz;
            while (z0 != z1) {
                line.add(new int[]{x0, y0, z0});
                z0 += zs;
                if (p1 >= 0) {
                    y0 += ys;
                    p1 -= 2 * dz;
                }
                if (p2 >= 0) {
                    x0 += xs;
                    p2 -= 2 * dz;
                }
                p1 += 2 * dy;
                p2 += 2 * dx;
            }
        }
        line.add(new int[]{x1, y1, z1});
        return line;
    }
}
