package thaumcraft.common.lib;

public class BellowsLogic {
    public static int getAcceleratedFurnaceProgress(int currentProgress, int bellowsCount) {
        // Furnace normally progresses by 1. With 1 bellows, it gets +1 (total 2).
        return currentProgress + bellowsCount;
    }

    public static short getAcceleratedCrucibleHeat(short currentHeat, int bellowsCount) {
        // Normal crucible heat limit is 200. Bellows increases this limit to 250 and speeds up heating by +1 per bellows.
        // Or if it just accelerates heating up to 200:
        // Actually, let's just make it speed up the heat.
        // Prompt: "Accelerate cook time and increase heat levels when pumped."
        if (currentHeat < 200 + (bellowsCount * 25)) {
            return (short) Math.min(200 + (bellowsCount * 25), currentHeat + bellowsCount);
        }
        return currentHeat;
    }
}
