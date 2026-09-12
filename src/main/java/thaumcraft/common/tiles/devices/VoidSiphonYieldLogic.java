package thaumcraft.common.tiles.devices;

import java.util.Random;

public class VoidSiphonYieldLogic {
    public static float calculateDropChance(float riftStability, int processingCycles) {
        float chance = 0.05f; // 5% base chance

        // Lower stability gives higher chance
        if (riftStability < 50.0f) {
            chance += (50.0f - Math.max(0, riftStability)) * 0.005f;
        }

        // More processing cycles gives higher chance
        chance += processingCycles * 0.01f;

        return Math.min(1.0f, chance);
    }

    public static boolean shouldGenerateSeed(float riftStability, int processingCycles, Random random) {
        return random.nextFloat() < calculateDropChance(riftStability, processingCycles);
    }
}
