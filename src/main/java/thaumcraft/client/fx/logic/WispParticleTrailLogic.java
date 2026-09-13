package thaumcraft.client.fx.logic;

/**
 * Pure Java logic class for calculating wisp particle trail properties.
 * Decoupled from Minecraft and Forge dependencies for easy testing.
 */
public class WispParticleTrailLogic {

    public static class RGBColor {
        public final int r;
        public final int g;
        public final int b;

        public RGBColor(int r, int g, int b) {
            this.r = Math.max(0, Math.min(255, r));
            this.g = Math.max(0, Math.min(255, g));
            this.b = Math.max(0, Math.min(255, b));
        }

        public RGBColor(int hexColor) {
            this.r = (hexColor >> 16) & 0xFF;
            this.g = (hexColor >> 8) & 0xFF;
            this.b = hexColor & 0xFF;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            RGBColor other = (RGBColor) obj;
            return r == other.r && g == other.g && b == other.b;
        }

        @Override
        public String toString() {
            return String.format("RGB(%d, %d, %d)", r, g, b);
        }
    }

    /**
     * Calculates the decayed lifetime of a wisp particle based on its aspect type.
     * @param primeAspect The primary aspect tag of the wisp.
     * @param baseLifetime The default base lifetime of the particle.
     * @return The scaled lifetime for the given aspect.
     */
    public static double calculateLifetime(String primeAspect, double baseLifetime) {
        if (primeAspect == null) {
            return baseLifetime;
        }

        switch (primeAspect.toLowerCase()) {
            case "terra":
            case "ordo":
                return baseLifetime * 1.5;
            case "aer":
            case "ignis":
                return baseLifetime * 0.8;
            case "perditio":
                return baseLifetime * 0.5;
            case "aqua":
                return baseLifetime * 1.0;
            default:
                return baseLifetime * 1.0;
        }
    }

    /**
     * Gets the base color for a primal aspect tag.
     * @param aspectTag The aspect tag.
     * @return The hex color representing the aspect, defaulting to white if unknown.
     */
    public static int getAspectColor(String aspectTag) {
        if (aspectTag == null) return 0xFFFFFF;

        switch (aspectTag.toLowerCase()) {
            case "aer": return 0xffff7e;
            case "terra": return 0x56c000;
            case "ignis": return 0xff5a01;
            case "aqua": return 0x3cd4fc;
            case "ordo": return 0xd5d4ec;
            case "perditio": return 0x404040;
            default: return 0xFFFFFF; // Default unknown aspect color
        }
    }

    /**
     * Calculates the current blended color of the particle based on its life ratio and aspect.
     * @param primeAspect The aspect tag.
     * @param lifeRatio The ratio of remaining life (1.0 = new, 0.0 = dead).
     * @return The blended RGBColor.
     */
    public static RGBColor getTrailColor(String primeAspect, double lifeRatio) {
        lifeRatio = Math.max(0.0, Math.min(1.0, lifeRatio));

        int baseHex = getAspectColor(primeAspect);
        RGBColor baseColor = new RGBColor(baseHex);

        RGBColor targetColor;

        if (primeAspect == null) {
            targetColor = new RGBColor(0, 0, 0); // default to black
        } else {
            switch (primeAspect.toLowerCase()) {
                case "ignis":
                case "perditio":
                case "terra":
                    targetColor = new RGBColor(0, 0, 0); // black
                    break;
                case "aer":
                case "ordo":
                    targetColor = new RGBColor(255, 255, 255); // white
                    break;
                case "aqua":
                    targetColor = new RGBColor(0, 0, 255); // deep blue
                    break;
                default:
                    targetColor = new RGBColor(0, 0, 0);
                    break;
            }
        }

        // Blend colors: baseColor * lifeRatio + targetColor * (1.0 - lifeRatio)
        int r = (int) Math.round(baseColor.r * lifeRatio + targetColor.r * (1.0 - lifeRatio));
        int g = (int) Math.round(baseColor.g * lifeRatio + targetColor.g * (1.0 - lifeRatio));
        int b = (int) Math.round(baseColor.b * lifeRatio + targetColor.b * (1.0 - lifeRatio));

        return new RGBColor(r, g, b);
    }
}
