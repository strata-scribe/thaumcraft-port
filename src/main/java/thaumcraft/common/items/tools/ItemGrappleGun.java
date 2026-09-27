package thaumcraft.common.items.tools;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import thaumcraft.common.items.tools.logic.GrappleGunLogic;

public class ItemGrappleGun extends Item {

    public ItemGrappleGun(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
            // Calculate starting velocity/position from player look angle
            Vec3 playerPos = player.position();
            Vec3 look = player.getLookAngle();

            GrappleGunLogic.Vector3d pos = new GrappleGunLogic.Vector3d(playerPos.x, playerPos.y + player.getEyeHeight(), playerPos.z);
            GrappleGunLogic.Vector3d vel = new GrappleGunLogic.Vector3d(look.x * 2.0, look.y * 2.0, look.z * 2.0);

            double gravity = 0.05;
            double drag = 0.01;

            GrappleGunLogic.Vector3d impact = GrappleGunLogic.calculateImpactCoordinate(
                pos, vel, gravity, drag, 50,
                v -> level.getBlockState(new net.minecraft.core.BlockPos((int)v.x(), (int)v.y(), (int)v.z())).isSolid()
            );

            if (impact != null) {
                GrappleGunLogic.Vector3d playerVel = new GrappleGunLogic.Vector3d(player.getDeltaMovement().x, player.getDeltaMovement().y, player.getDeltaMovement().z);
                GrappleGunLogic.Vector3d hookPos = impact;

                double springStiffness = 0.5;
                double dampingCoefficient = 0.2;

                GrappleGunLogic.Vector3d accel = GrappleGunLogic.calculatePullingAcceleration(
                    new GrappleGunLogic.Vector3d(playerPos.x, playerPos.y, playerPos.z),
                    playerVel,
                    hookPos,
                    springStiffness,
                    dampingCoefficient
                );

                player.setDeltaMovement(
                    player.getDeltaMovement().add(accel.x(), accel.y(), accel.z())
                );
            }
        }

        return InteractionResult.SUCCESS;
    }
}
