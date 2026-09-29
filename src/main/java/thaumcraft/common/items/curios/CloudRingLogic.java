package thaumcraft.common.items.curios;

public class CloudRingLogic {
    public static final float SLOW_FALL_SPEED_Y = -0.05F;

    public static boolean shouldApplySlowFall(boolean isSneaking, boolean isOnGround, boolean hasCloudRing) {
        return hasCloudRing && isSneaking && !isOnGround;
    }
}
