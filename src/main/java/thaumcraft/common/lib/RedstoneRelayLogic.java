package thaumcraft.common.lib;

public class RedstoneRelayLogic {
    public interface IEnvironment {
        boolean isRedstoneRelay(int x, int y, int z);
        int getPower(int x, int y, int z);
        void setPower(int x, int y, int z, int power);
        boolean isLineOfSightBlocked(int x, int y, int z);
    }

    public static void transmitPower(IEnvironment env, int startX, int startY, int startZ, int power) {
        // Directions: DOWN, UP, NORTH, SOUTH, WEST, EAST
        int[][] dirs = {
            {0, -1, 0}, {0, 1, 0}, {0, 0, -1}, {0, 0, 1}, {-1, 0, 0}, {1, 0, 0}
        };

        for (int[] dir : dirs) {
            for (int i = 1; i <= 16; i++) {
                int targetX = startX + dir[0] * i;
                int targetY = startY + dir[1] * i;
                int targetZ = startZ + dir[2] * i;

                if (env.isRedstoneRelay(targetX, targetY, targetZ)) {
                    if (env.getPower(targetX, targetY, targetZ) != power) {
                        env.setPower(targetX, targetY, targetZ, power);
                    }
                    break;
                } else if (env.isLineOfSightBlocked(targetX, targetY, targetZ)) {
                    // Line of sight blocked
                    break;
                }
            }
        }
    }
}
