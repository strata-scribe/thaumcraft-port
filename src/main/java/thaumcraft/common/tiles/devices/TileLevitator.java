package thaumcraft.common.tiles.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import thaumcraft.common.blocks.devices.BlockLevitator;
import thaumcraft.common.blocks.entities.ThaumcraftBlockEntities;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public class TileLevitator extends BlockEntity {

    public TileLevitator(BlockPos pos, BlockState state) {
        super(ThaumcraftBlockEntities.LEVITATOR.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TileLevitator tile) {
        AABB beamBox = getBeamBoxIfActive(pos, state, (p) -> {
            BlockState checkState = level.getBlockState(p);
            return checkState.isCollisionShapeFullBlock(level, p);
        });

        if (beamBox == null) {
            return;
        }

        Direction facing = state.getValue(BlockLevitator.FACING);
        List<Entity> entities = level.getEntitiesOfClass(Entity.class, beamBox);
        for (Entity entity : entities) {
            applyLevitation(entity, facing);
        }
    }

    @Nullable
    public static AABB getBeamBoxIfActive(BlockPos pos, BlockState state, java.util.function.Predicate<BlockPos> isSolid) {
        if (state == null || !state.hasProperty(BlockLevitator.POWERED) || !state.hasProperty(BlockLevitator.FACING)) {
            return null;
        }

        if (state.getValue(BlockLevitator.POWERED)) {
            return null;
        }

        Direction facing = state.getValue(BlockLevitator.FACING);
        int distance = TileLevitatorHelper.calculateBeamDistance(pos, facing, isSolid);

        if (distance == 0) {
            return null;
        }

        return TileLevitatorHelper.calculateBeamBox(pos, facing, distance);
    }

    public static void applyLevitation(Entity entity, Direction facing) {
        Vec3 motion = entity.getDeltaMovement();
        double targetMotion = facing == Direction.UP ? 0.2D : -0.2D;

        if (entity instanceof Player player && player.isCrouching()) {
            if (facing == Direction.UP) {
                targetMotion = -0.1D;
            } else {
                targetMotion = -0.05D;
            }
        }

        entity.setDeltaMovement(motion.x, targetMotion, motion.z);
        entity.fallDistance = 0;
    }
}
