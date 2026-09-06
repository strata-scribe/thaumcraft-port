package thaumcraft.common.events;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.common.lib.capabilities.RunicShieldAttachment;

@EventBusSubscriber(modid = Thaumcraft.MODID)
public class RunicShieldEvents {

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (event.getSource().is(net.minecraft.tags.DamageTypeTags.BYPASSES_ENCHANTMENTS) || event.getSource().is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                return; // Damage types that shouldn't be absorbed by the shield
            }

            RunicShieldAttachment shield = ThaumcraftCapabilities.getRunicShield(player);
            if (shield != null && shield.getCurrentShield() > 0) {
                float damage = event.getAmount();
                int currentShield = shield.getCurrentShield();

                if (currentShield >= damage) {
                    shield.setCurrentShield(currentShield - (int) Math.ceil(damage));
                    event.setAmount(0);
                    event.setCanceled(true); // Completely absorbed
                } else {
                    shield.setCurrentShield(0);
                    event.setAmount(damage - currentShield); // Partially absorbed
                }

                shield.setRechargeDelay(40); // Set recharge delay to 40 ticks
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Pre event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) {
            return;
        }

        RunicShieldAttachment shield = ThaumcraftCapabilities.getRunicShield(player);
        if (shield != null) {
            int delay = shield.getRechargeDelay();
            if (delay > 0) {
                shield.setRechargeDelay(delay - 1);
            } else if (shield.getCurrentShield() < shield.getMaxShield()) {
                if (player.tickCount % 20 == 0) {
                    shield.setCurrentShield(Math.min(shield.getCurrentShield() + 1, shield.getMaxShield()));
                }
            }
        }
    }
}
