package thaumcraft.api.potions;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.damagesource.DamageSourceThaumcraft;
import thaumcraft.api.entities.ITaintedMob;

public class PotionFluxTaint extends MobEffect {
    public static MobEffect instance = null; // will be instantiated at runtime

    public PotionFluxTaint(boolean isBad, int color) {
        super(MobEffectCategory.HARMFUL, color);
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity target, int amplifier) {
        AttributeInstance cai = target.getAttribute(BuiltInRegistries.ATTRIBUTE.wrapAsHolder(ThaumcraftApiHelper.CHAMPION_MOD));
        if (target instanceof ITaintedMob || (cai != null && (int) cai.getValue() == 13)) {
            target.heal(1.0f);
        } else {
            if (!target.isInvertedHealAndHarm() && !(target instanceof Player)) {
                target.hurt(DamageSourceThaumcraft.getSource(level, DamageSourceThaumcraft.TAINT), 1.0f);
            } else if (!target.isInvertedHealAndHarm() && (target.getMaxHealth() > 1.0f || (target instanceof Player))) {
                target.hurt(DamageSourceThaumcraft.getSource(level, DamageSourceThaumcraft.TAINT), 1.0f);
            }
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        int k = 40 >> amplifier;
        return k > 0 ? duration % k == 0 : true;
    }
}
