package thaumcraft.common.events;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.common.items.armor.BootsLiquidLogic;
import thaumcraft.common.items.armor.ItemBootsTraveller;

@EventBusSubscriber(modid = Thaumcraft.MODID)
public class BootsEvents {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Pre event) {
        Player player = event.getEntity();
        Level level = player.level();

        // Server-side mainly to calculate energy consumption (though movement is technically client authoritative,
        // syncing motion/position happens naturally, but applying effects is generally done on both)
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);

        if (boots.getItem() instanceof ItemBootsTraveller travellerBoots) {
            boolean hasCharge = travellerBoots.hasCharge(boots);
            boolean isMovingForward = player.zza > 0.0F; // zza is forward movement
            boolean isSneaking = player.isCrouching();
            boolean isSprinting = player.isSprinting();

            if (BootsLiquidLogic.canWalkOnLiquid(hasCharge, isMovingForward, isSneaking, isSprinting)) {
                // Check block underneath the player
                BlockPos pos = player.blockPosition();
                BlockPos posBelow = pos.below();

                BlockState state = level.getBlockState(pos);
                BlockState stateBelow = level.getBlockState(posBelow);

                // If the player's current block or block below contains a liquid
                if (!state.getFluidState().isEmpty() || !stateBelow.getFluidState().isEmpty()) {
                    BlockState targetState = !state.getFluidState().isEmpty() ? state : stateBelow;
                    BlockPos targetPos = !state.getFluidState().isEmpty() ? pos : posBelow;

                    float liquidHeight = targetState.getFluidState().getHeight(level, targetPos);
                    double liquidSurfaceY = targetPos.getY() + liquidHeight;

                    if (BootsLiquidLogic.isAtOrAboveLiquidSurface(player.getY(), liquidSurfaceY)) {
                        double currentMotionY = player.getDeltaMovement().y;
                        double newMotionY = BootsLiquidLogic.calculateVerticalVelocityOverride(currentMotionY);

                        if (currentMotionY != newMotionY) {
                            player.setDeltaMovement(player.getDeltaMovement().x, newMotionY, player.getDeltaMovement().z);
                            // Also adjust position to ensure they sit directly atop the liquid if they were falling through
                            if (player.getY() < liquidSurfaceY && player.getY() >= liquidSurfaceY - 0.1) {
                                player.setPos(player.getX(), liquidSurfaceY, player.getZ());
                            }
                            player.setOnGround(true);
                        }
                    }
                }
            }
        }
    }
}
