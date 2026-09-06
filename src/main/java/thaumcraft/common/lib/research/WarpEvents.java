package thaumcraft.common.lib.research;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.common.lib.SoundsTC;

@EventBusSubscriber(modid = Thaumcraft.MODID)
public class WarpEvents {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player && !player.level().isClientSide()) {
            IPlayerWarp warp = player.getData(ThaumcraftCapabilities.WARP_ATTACHMENT);
            int counter = warp.getCounter();

            if (counter >= 2000) {
                warp.setCounter(0);

                int totalWarp = warp.get(IPlayerWarp.EnumWarpType.PERMANENT) +
                                warp.get(IPlayerWarp.EnumWarpType.NORMAL) +
                                warp.get(IPlayerWarp.EnumWarpType.TEMPORARY);

                if (totalWarp > 0) {
                    if (totalWarp < 25) {
                        // Minor
                        player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 100, 0));
                        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0));
                    } else if (totalWarp <= 50) {
                        // Medium
                        player.level().playSound(null, player.blockPosition(), SoundsTC.WHISPERS.get(), net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);
                        player.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 100, 0)); // Nausea
                    } else {
                        // Major
                        player.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 0));
                        Entity spider = EntityType.CAVE_SPIDER.create(player.level(), EntitySpawnReason.EVENT);
                        if (spider != null) {
                            spider.setPos(player.getX() + (player.getRandom().nextFloat() - 0.5f) * 4,
                                          player.getY(),
                                          player.getZ() + (player.getRandom().nextFloat() - 0.5f) * 4);
                            player.level().addFreshEntity(spider);
                        }
                    }
                }
            } else {
                warp.setCounter(counter + 1);
            }
        }
    }
}
