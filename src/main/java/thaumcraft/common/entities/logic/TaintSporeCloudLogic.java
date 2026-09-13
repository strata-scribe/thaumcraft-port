package thaumcraft.common.entities.logic;

public class TaintSporeCloudLogic {

    public record CloudState(double volume, double density) {}
    public record Position(double x, double y, double z) {}

    /**
     * Computes the expansion of a taint spore cloud over time.
     * Mass (volume * density) is conserved.
     *
     * @param currentVolume the current volume of the cloud
     * @param currentDensity the current density of the spores
     * @param expansionRate the rate at which the volume expands
     * @param deltaTime the time interval for expansion
     * @return a new CloudState with the updated volume and density
     */
    public static CloudState computeExpansion(double currentVolume, double currentDensity, double expansionRate, double deltaTime) {
        if (currentVolume <= 0) {
            return new CloudState(0, 0);
        }

        double newVolume = currentVolume + (currentVolume * expansionRate * deltaTime);
        if (newVolume <= 0) {
            return new CloudState(0, 0);
        }

        double mass = currentVolume * currentDensity;
        double newDensity = mass / newVolume;

        return new CloudState(newVolume, newDensity);
    }

    /**
     * Computes the wind displacement of the cloud over time.
     *
     * @param x current x position
     * @param y current y position
     * @param z current z position
     * @param windX wind vector x component
     * @param windY wind vector y component
     * @param windZ wind vector z component
     * @param displacementCoefficient how strongly the cloud is affected by the wind
     * @param deltaTime the time interval
     * @return a new Position after applying wind displacement
     */
    public static Position computeWindDisplacement(double x, double y, double z, double windX, double windY, double windZ, double displacementCoefficient, double deltaTime) {
        return new Position(
            x + (windX * displacementCoefficient * deltaTime),
            y + (windY * displacementCoefficient * deltaTime),
            z + (windZ * displacementCoefficient * deltaTime)
        );
    }

    /**
     * Computes the duration of the poison debuff inflicted by the spore cloud.
     *
     * @param density the current density of the cloud
     * @param safeThreshold density levels at or below this value inflict no debuff
     * @param baseDurationMultiplier multiplier for duration calculation per unit density above threshold
     * @param exposureTime the duration the entity is exposed to the cloud
     * @return the total poison debuff duration
     */
    public static double computePoisonDebuffDuration(double density, double safeThreshold, double baseDurationMultiplier, double exposureTime) {
        if (density <= safeThreshold) {
            return 0.0;
        }
        return (density - safeThreshold) * baseDurationMultiplier * exposureTime;
    }
}