package thaumcraft.common.lib;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public class LevitatorRelayLogic {

    private static final double MAX_UPWARD_VELOCITY = 0.5;
    private static final double MAX_DOWNWARD_VELOCITY = -0.5;

    /**
     * Calculates the new propulsion velocity for an entity within the levitator beam.
     */
    public static Vec3 calculatePropulsion(Vec3 currentMotion, Direction facing, boolean inverted, boolean isSneaking) {
        double thrust = facing == Direction.UP ? 0.05D : -0.05D;

        if (inverted) {
            thrust = -thrust;
        }

        if (isSneaking) {
            thrust *= 0.5;
        }

        double targetMotionY = currentMotion.y + thrust;

        if (targetMotionY > MAX_UPWARD_VELOCITY) targetMotionY = MAX_UPWARD_VELOCITY;
        if (targetMotionY < MAX_DOWNWARD_VELOCITY) targetMotionY = MAX_DOWNWARD_VELOCITY;

        return new Vec3(currentMotion.x, targetMotionY, currentMotion.z);
    }

    public static int calculateRedstoneSignalFromAura(float vis, float maxVis) {
        if (maxVis <= 0 || vis <= 0) {
            return 0;
        }
        int signal = (int) Math.floor((vis / maxVis) * 15.0f);
        return Math.max(0, Math.min(15, signal));
    }

    public static int calculateRedstoneSignalFromEssentia(int amount, int capacity) {
        if (capacity <= 0 || amount <= 0) {
            return 0;
        }
        int signal = (int) Math.floor(((double) amount / capacity) * 15.0);
        return Math.max(0, Math.min(15, signal));
    }
}