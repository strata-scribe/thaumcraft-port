package thaumcraft.common.entities.monster.boss;

public class FluxRiftLogic {

    public static float calculateSizeGrowth(float currentSize, float drainedFlux) {
        if (drainedFlux > 0) {
            return currentSize + drainedFlux * 0.01f;
        }
        return currentSize;
    }

    public static float calculateStabilityDecay(float currentStability, float riftSize) {
        return currentStability - (riftSize * 0.05f);
    }

    public static boolean shouldCollapse(float stability, float riftSize) {
        return stability <= 0.0f || riftSize >= 50.0f;
    }

    public static boolean shouldSpawnTaintSeed(float riftSize) {
        return riftSize >= 20.0f;
    }
}

