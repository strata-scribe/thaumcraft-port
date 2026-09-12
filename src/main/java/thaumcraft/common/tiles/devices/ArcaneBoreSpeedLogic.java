package thaumcraft.common.tiles.devices;

public class ArcaneBoreSpeedLogic {

    public static int calculateMiningTime(float blockHardness, int boreBaseSpeed) {
        if (blockHardness < 0) {
            return -1;
        }
        return Math.max(1, (int) (blockHardness / boreBaseSpeed));
    }

    public static float calculateVisConsumption(float blockHardness, float visPerHardness) {
        if (blockHardness < 0) {
            return 0.0f;
        }
        return blockHardness * visPerHardness;
    }
}
