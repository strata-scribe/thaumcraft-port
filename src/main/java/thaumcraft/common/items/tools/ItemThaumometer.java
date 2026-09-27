package thaumcraft.common.items.tools;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import thaumcraft.common.items.tools.logic.ThaumometerZoomLogic;
import java.util.Optional;

public class ItemThaumometer extends Item {

    public ItemThaumometer(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        // Use ThaumometerZoomLogic
        double zoomLevel = 0.5; // Default or configured zoom level
        double fovScaling = ThaumometerZoomLogic.calculateFovScaling(zoomLevel, 90.0);

        HitResult hitResult = getPlayerPOVHitResult(level, player, net.minecraft.world.level.ClipContext.Fluid.NONE);

        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = ((BlockHitResult) hitResult).getBlockPos();
        }

        // Implement scan raycast detecting targeted BlockPos or Entity within 16 blocks.
        double scanDistance = 16.0;
        Vec3 eyePosition = player.getEyePosition(1.0f);
        Vec3 viewVector = player.getViewVector(1.0f);
        Vec3 targetVector = eyePosition.add(viewVector.x * scanDistance, viewVector.y * scanDistance, viewVector.z * scanDistance);

        AABB aabb = player.getBoundingBox().expandTowards(viewVector.scale(scanDistance)).inflate(1.0D, 1.0D, 1.0D);

        HitResult entityHitResult = null;

        for (Entity e : level.getEntities(player, aabb, ent -> !ent.isSpectator() && ent.isPickable())) {
            AABB entityAabb = e.getBoundingBox().inflate((double) e.getPickRadius());
            Optional<Vec3> optional = entityAabb.clip(eyePosition, targetVector);

            if (optional.isPresent()) {
                entityHitResult = new EntityHitResult(e, optional.get());
                break;
            }
        }

        if (entityHitResult != null && entityHitResult.getType() == HitResult.Type.ENTITY) {
            Entity targetEntity = ((EntityHitResult) entityHitResult).getEntity();
        }

        return InteractionResult.SUCCESS;
    }
}
