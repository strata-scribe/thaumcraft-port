package thaumcraft.common.items.curios;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.particles.ParticleTypes;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.items.tools.SanitySoapLogic;

public class ItemSanitySoap extends Item {
    public ItemSanitySoap(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 200;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.EAT;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide() && entity instanceof ServerPlayer player) {

            IPlayerWarp warp = player.getData(ThaumcraftCapabilities.WARP_ATTACHMENT);
            int currentTempWarp = warp.get(IPlayerWarp.EnumWarpType.TEMPORARY);
            int washCount = SanitySoapLogic.getConsecutiveWashes(player.getUUID(), level.getGameTime());
            int reduction = SanitySoapLogic.calculateWarpReduction(currentTempWarp, washCount);

            if (reduction > 0) {
                warp.reduce(IPlayerWarp.EnumWarpType.TEMPORARY, reduction);
            }
            SanitySoapLogic.recordWash(player.getUUID(), level.getGameTime());

            if (level.getRandom().nextInt(10) < 1) {
                warp.reduce(IPlayerWarp.EnumWarpType.NORMAL, 1);
            }

            warp.sync(player);

            ((ServerLevel) level).sendParticles(ParticleTypes.BUBBLE_POP,
                    player.getX(), player.getY() + 1.0, player.getZ(),
                    20, 0.5, 0.5, 0.5, 0.1);

            level.playSound(null, player.blockPosition(), SoundsTC.BUBBLE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }

        if (entity instanceof Player player && !player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        return super.finishUsingItem(stack, level, entity);
    }
}
