package thaumcraft.common.items.tools;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * Sword of the Zephyr:
 * - Unleashes wind bursts sweeping away nearby enemies and deflecting incoming projectiles.
 */
public class ItemElementalSword extends Item {

    public ItemElementalSword(Properties properties) {
        super(properties);
    }

    public ItemElementalSword() {
        this(new Item.Properties().stacksTo(1).durability(1500));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
            double radius = ElementalToolLogic.ZEPHYR_MAX_RADIUS;
            AABB box = player.getBoundingBox().inflate(radius);
            List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, box, e -> e != player && e.isAlive());

            for (LivingEntity target : targets) {
                ElementalToolLogic.Vector3D impulse = ElementalToolLogic.calculateZephyrImpulse(
                        player.getX(), player.getY(), player.getZ(),
                        target.getX(), target.getY(), target.getZ(),
                        radius);

                if (impulse.length() > 0) {
                    target.setDeltaMovement(target.getDeltaMovement().add(impulse.x(), impulse.y(), impulse.z()));
                    target.hurtMarked = true;
                }
            }
            EquipmentSlot slot = hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
            player.getItemInHand(hand).hurtAndBreak(1, player, slot);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }
}
