package thaumcraft.client.fx.logic;

/**
 * Pure Java logic class for calculating spell focus projectile particle trails.
 * Decoupled from Minecraft and Forge dependencies for easy testing.
 */
public class SpellTrailParticleLogic {

    public static class Vec3 {
        public final double x;
        public final double y;
        public final double z;

        public Vec3(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Vec3 add(Vec3 other) {
            return new Vec3(this.x + other.x, this.y + other.y, this.z + other.z);
        }
    }

    public static class RGBColor {
        public final float r;
        public final float g;
        public final float b;

        public RGBColor(float r, float g, float b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }
    }

    /**
     * Calculates the linear interpolated position between start and end vectors based on progress.
     * @param start The starting coordinate.
     * @param end The ending coordinate.
     * @param progress The progress from 0.0 to 1.0.
     * @return The interpolated position.
     */
    public static Vec3 calculateLinearOffset(Vec3 start, Vec3 end, double progress) {
        progress = Math.max(0.0, Math.min(1.0, progress));
        double x = start.x + (end.x - start.x) * progress;
        double y = start.y + (end.y - start.y) * progress;
        double z = start.z + (end.z - start.z) * progress;
        return new Vec3(x, y, z);
    }

    /**
     * Calculates a spiral offset position around a line between start and end vectors.
     * @param start The starting coordinate.
     * @param end The ending coordinate.
     * @param progress The progress from 0.0 to 1.0.
     * @param radius The radius of the spiral.
     * @param frequency The frequency of the spiral.
     * @return The spiral offset position.
     */
    public static Vec3 calculateSpiralOffset(Vec3 start, Vec3 end, double progress, double radius, double frequency) {
        Vec3 linearPos = calculateLinearOffset(start, end, progress);

        // Direction vector
        double dx = end.x - start.x;
        double dy = end.y - start.y;
        double dz = end.z - start.z;

        // Normalize direction vector
        double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (length > 0) {
            dx /= length;
            dy /= length;
            dz /= length;
        } else {
            // Default arbitrary axis if start == end
            dx = 0;
            dy = 1;
            dz = 0;
        }

        // Arbitrary up vector
        double upX = 0, upY = 1, upZ = 0;

        // If direction is too close to arbitrary up, use a different up vector
        if (Math.abs(dx) < 0.001 && Math.abs(dz) < 0.001) {
            upX = 1;
            upY = 0;
            upZ = 0;
        }

        // Cross product to get first orthogonal vector (U)
        double uX = upY * dz - upZ * dy;
        double uY = upZ * dx - upX * dz;
        double uZ = upX * dy - upY * dx;

        // Normalize U
        double uLen = Math.sqrt(uX * uX + uY * uY + uZ * uZ);
        uX /= uLen;
        uY /= uLen;
        uZ /= uLen;

        // Cross product of dir and U to get second orthogonal vector (V)
        double vX = dy * uZ - dz * uY;
        double vY = dz * uX - dx * uZ;
        double vZ = dx * uY - dy * uX;

        // Calculate the spiral offsets using sine and cosine based on frequency and progress
        double angle = progress * Math.PI * 2 * frequency;
        double cosA = Math.cos(angle);
        double sinA = Math.sin(angle);

        double offsetX = radius * (uX * cosA + vX * sinA);
        double offsetY = radius * (uY * cosA + vY * sinA);
        double offsetZ = radius * (uZ * cosA + vZ * sinA);

        return new Vec3(linearPos.x + offsetX, linearPos.y + offsetY, linearPos.z + offsetZ);
    }

    /**
     * Maps focus elemental effects to RGB color gradients.
     * @param elementType The elemental type (e.g. "Fire", "Frost", "Flux")
     * @return An array of RGBColor representing the gradient (start and end colors), or a default white gradient if unknown.
     */
    public static RGBColor[] getElementalColor(String elementType) {
        if (elementType == null) {
            return new RGBColor[]{new RGBColor(1.0f, 1.0f, 1.0f), new RGBColor(1.0f, 1.0f, 1.0f)};
        }

        switch (elementType.toLowerCase()) {
            case "fire":
                // Orange to Red
                return new RGBColor[]{new RGBColor(1.0f, 0.5f, 0.0f), new RGBColor(1.0f, 0.0f, 0.0f)};
            case "frost":
                // Cyan to White
                return new RGBColor[]{new RGBColor(0.0f, 1.0f, 1.0f), new RGBColor(1.0f, 1.0f, 1.0f)};
            case "flux":
                // Magenta to Black
                return new RGBColor[]{new RGBColor(1.0f, 0.0f, 1.0f), new RGBColor(0.0f, 0.0f, 0.0f)};
            default:
                // Default: White to White
                return new RGBColor[]{new RGBColor(1.0f, 1.0f, 1.0f), new RGBColor(1.0f, 1.0f, 1.0f)};
        }
    }
}
