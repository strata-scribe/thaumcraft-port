package thaumcraft.common.casters;

public class FocusRiftLogic {

    /**
     * Calculates the complexity for a Rift focus node based on the duration setting.
     * Complexity increases with duration.
     */
    public static int calculateRiftComplexity(int duration) {
        return 4 + Math.max(0, duration) * 3;
    }

    /**
     * Calculates the falloff multiplier (gravitational pull strength) based on distance.
     * Reaches maximum pull when closer to the center, and 0 at Max Radius.
     */
    public static double calculateGravitationalFalloff(double distance, double maxRadius) {
        if (distance <= 0) return 1.0;
        if (distance >= maxRadius) return 0.0;

        // Inverse square like falloff or linear, let's use linear falloff for simplicity
        // 1.0 at center, 0.0 at maxRadius
        return 1.0 - (distance / maxRadius);
    }

    /**
     * Calculates the required velocity vector magnitude to attract an entity towards the rift.
     */
    public static double calculateAttractionVelocity(double distance, double maxRadius) {
        if (distance >= maxRadius || distance <= 0.1) return 0.0;

        double falloff = calculateGravitationalFalloff(distance, maxRadius);
        // Base pull speed multiplier. Let's make it strong enough to overcome normal movement
        return falloff * 0.15; // velocity units per tick
    }

    /**
     * Calculates the duration in ticks for the rift to stay open.
     */
    public static int calculateRiftDurationTicks(int durationSetting) {
        return 20 + Math.max(0, durationSetting) * 20; // Base 1s + 1s per setting level
    }

    /**
     * Calculates the maximum effect radius of the rift.
     */
    public static double calculateRiftRadius(int durationSetting) {
        return 4.0 + (durationSetting * 0.5); // base 4 blocks, + 0.5 per duration level
    }
}
