package thaumcraft.common.entities.logic;

public class VitiumGasLogic {

    public record GasState(double volume, double concentration) {}
    public record Position(double x, double y, double z) {}

    /**
     * Simulates the diffusion of a flux gas cloud over a given time interval.
     * Mass is conserved (volume * concentration = constant).
     *
     * @param currentVolume the current volume of the gas cloud
     * @param currentConcentration the current concentration of vitium in the gas
     * @param diffusionRate the rate at which the volume expands
     * @param deltaTime the time interval for the simulation step
     * @return a new GasState with the updated volume and concentration
     */
    public static GasState simulateDiffusion(double currentVolume, double currentConcentration, double diffusionRate, double deltaTime) {
        if (currentVolume <= 0) {
            return new GasState(0, 0);
        }

        double newVolume = currentVolume + (currentVolume * diffusionRate * deltaTime);
        if (newVolume <= 0) {
            return new GasState(0, 0);
        }

        double mass = currentVolume * currentConcentration;
        double newConcentration = mass / newVolume;

        return new GasState(newVolume, newConcentration);
    }

    /**
     * Simulates the wind drift of a flux gas cloud over time.
     *
     * @param x current x position
     * @param y current y position
     * @param z current z position
     * @param windX wind vector x component
     * @param windY wind vector y component
     * @param windZ wind vector z component
     * @param driftCoefficient how strongly the gas is affected by the wind
     * @param deltaTime the time interval for the simulation step
     * @return a new Position after applying wind drift
     */
    public static Position simulateWindDrift(double x, double y, double z, double windX, double windY, double windZ, double driftCoefficient, double deltaTime) {
        return new Position(
            x + (windX * driftCoefficient * deltaTime),
            y + (windY * driftCoefficient * deltaTime),
            z + (windZ * driftCoefficient * deltaTime)
        );
    }

    /**
     * Calculates the toxic damage a flux gas cloud inflicts over a given time interval.
     *
     * @param concentration the current concentration of the gas
     * @param baseDamageRate the damage per unit of concentration over the safe threshold per second
     * @param safeThreshold concentration levels at or below this value inflict no damage
     * @param exposureTime the duration the entity is exposed to the gas
     * @return the total damage to apply
     */
    public static double calculateToxicDamage(double concentration, double baseDamageRate, double safeThreshold, double exposureTime) {
        if (concentration <= safeThreshold) {
            return 0.0;
        }
        return (concentration - safeThreshold) * baseDamageRate * exposureTime;
    }
}
