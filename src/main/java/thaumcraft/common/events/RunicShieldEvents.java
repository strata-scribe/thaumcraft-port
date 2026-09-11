package thaumcraft.common.events;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.common.lib.capabilities.RunicShieldAttachment;
import thaumcraft.common.lib.capabilities.RunicShieldLogic;
import thaumcraft.api.aura.AuraHelper;

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

                RunicShieldLogic.DamageResult result = RunicShieldLogic.calculateDamageRemaining(currentShield, damage);

                shield.setCurrentShield(result.newShield);

                if (result.remainingDamage <= 0) {
                    event.setAmount(0);
                    event.setCanceled(true); // Completely absorbed
                } else {
                    event.setAmount(result.remainingDamage); // Partially absorbed
                }

                shield.setRechargeDelay(RunicShieldLogic.RECHARGE_DELAY);
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
            RunicShieldLogic.TickResult result = RunicShieldLogic.processTick(
                shield.getCurrentShield(),
                shield.getMaxShield(),
                shield.getRechargeDelay(),
                player.tickCount
            );

            shield.setRechargeDelay(result.newRechargeDelay);

            if (result.wantsToRecharge) {
                float drained = AuraHelper.drainVis(player.level(), player.blockPosition(), RunicShieldLogic.VIS_COST, false);
                if (drained > 0) {
                    shield.setCurrentShield(Math.min(shield.getCurrentShield() + 1, shield.getMaxShield()));
                }
            }
        }
    }
}
