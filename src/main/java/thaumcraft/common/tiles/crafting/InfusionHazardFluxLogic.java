package thaumcraft.common.tiles.crafting;

import java.util.Random;

public class InfusionHazardFluxLogic {

    /**
     * Determines if a flux hazard should occur based on the current instability level.
     * @param random The random instance to use.
     * @param instability The current instability of the infusion process.
     * @return True if a hazard should occur, false otherwise.
     */
    public static boolean shouldTriggerHazard(Random random, float instability) {
        // Example logic: hazard chance increases with instability.
        // Base chance is 0, increases linearly. At instability 100, chance could be 50%.
        float chance = instability * 0.005f;
        return random.nextFloat() < chance;
    }

    /**
     * Determines if the hazard should be Flux Goo instead of Vitium Aura Gas.
     * @param random The random instance to use.
     * @param instability The current instability.
     * @return True if Flux Goo should spawn, false if Vitium Aura Gas should be released.
     */
    public static boolean shouldSpawnFluxGoo(Random random, float instability) {
        // Flux Goo is more likely at higher instabilities, but Gas is more common overall.
        float gooChance = 0.1f + (instability * 0.002f);
        if (gooChance > 0.5f) gooChance = 0.5f; // Cap at 50%
        return random.nextFloat() < gooChance;
    }

    /**
     * Calculates the amount of Vitium Aura Gas to release based on instability.
     * @param instability The current instability.
     * @return The amount of gas to release.
     */
    public static float calculateGasEmissionAmount(float instability) {
        // Minimum amount of gas, scaling up with instability.
        return 1.0f + (instability * 0.1f);
    }
}
