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
import thaumcraft.common.lib.LevitatorRelayLogic;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public class TileLevitator extends BlockEntity {

    public TileLevitator(BlockPos pos, BlockState state) {
        super(ThaumcraftBlockEntities.LEVITATOR.get(), pos, state);
    }


    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        // Any specific levitator data can be loaded here. Currently, INVERTED is stored in the block state.
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        // Any specific levitator data can be saved here.
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
            boolean inverted = state.hasProperty(BlockLevitator.INVERTED) ? state.getValue(BlockLevitator.INVERTED) : false;
            applyLevitation(entity, facing, inverted);
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

    public static void applyLevitation(Entity entity, Direction facing, boolean inverted) {
        Vec3 currentMotion = entity.getDeltaMovement();
        boolean isSneaking = entity instanceof Player player && player.isCrouching();
        Vec3 newMotion = LevitatorRelayLogic.calculatePropulsion(currentMotion, facing, inverted, isSneaking);

        entity.setDeltaMovement(newMotion);
        entity.fallDistance = 0;
    }
}
