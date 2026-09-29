package thaumcraft.common.items.curios;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;

public class ItemCloudRing extends Item {
    public ItemCloudRing(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        super.inventoryTick(stack, level, entity, slot);
        if (entity instanceof Player player) {
            // Apply if the item is equipped in an equipment slot (or curio slot if it passes it as EquipmentSlot)
            if (CloudRingLogic.shouldApplySlowFall(player.isCrouching(), player.onGround(), true)) {
                Vec3 motion = player.getDeltaMovement();
                if (motion.y < CloudRingLogic.SLOW_FALL_SPEED_Y) {
                    player.setDeltaMovement(motion.x, CloudRingLogic.SLOW_FALL_SPEED_Y, motion.z);
                }
                player.fallDistance = 0.0F;
            }
        }
    }
}
