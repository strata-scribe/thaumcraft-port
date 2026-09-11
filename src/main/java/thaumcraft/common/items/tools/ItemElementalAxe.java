package thaumcraft.common.items.tools;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Axe of the Stream:
 * - Chops connected logs in trees from top to bottom.
 * - Magnetic attraction pulling nearby dropped items to the player while held/used.
 */
public class ItemElementalAxe extends Item {

    public ItemElementalAxe(Properties properties) {
        super(properties);
    }

    public ItemElementalAxe() {
        this(new Item.Properties().stacksTo(1).durability(1500));
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        super.inventoryTick(stack, level, entity, slot);
        if (slot == EquipmentSlot.MAINHAND && entity instanceof Player player) {
            pullNearbyItems(level, player);
        }
    }

    public void pullNearbyItems(ServerLevel level, Player player) {
        double radius = ElementalToolLogic.DEFAULT_MAGNET_RADIUS;
        AABB box = player.getBoundingBox().inflate(radius);
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, box);

        for (ItemEntity item : items) {
            if (item.isAlive() && !item.hasPickUpDelay()) {
                Vec3 itemPos = item.position();
                ElementalToolLogic.Vector3D vel = ElementalToolLogic.calculateMagnetVelocity(
                        itemPos.x, itemPos.y, itemPos.z,
                        player.getX(), player.getEyeY(), player.getZ(),
                        radius);

                if (vel.length() > 0) {
                    item.setDeltaMovement(item.getDeltaMovement().add(vel.x(), vel.y(), vel.z()));
                }
            }
        }
    }
}
