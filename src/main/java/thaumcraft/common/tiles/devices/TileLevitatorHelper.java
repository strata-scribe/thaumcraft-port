package thaumcraft.common.tiles.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;

import java.util.function.Predicate;

public class TileLevitatorHelper {

    public static final int MAX_RANGE = 10;

    public static int calculateBeamDistance(BlockPos pos, Direction facing, Predicate<BlockPos> isSolid) {
        int distance = 0;
        for (int i = 1; i <= MAX_RANGE; i++) {
            BlockPos checkPos = pos.relative(facing, i);
            if (isSolid.test(checkPos)) {
                break;
            }
            distance = i;
        }
        return distance;
    }

    public static AABB calculateBeamBox(BlockPos pos, Direction facing, int distance) {
        if (facing == Direction.UP) {
            return new AABB(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX() + 1, pos.getY() + 1 + distance, pos.getZ() + 1);
        } else {
            return new AABB(pos.getX(), pos.getY() - distance, pos.getZ(), pos.getX() + 1, pos.getY(), pos.getZ() + 1);
        }
    }
}
