package thaumcraft.common.items.casters.foci;

import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.common.casters.FocusLogic;

public class FocusEffectCurse extends FocusEffect {

    public FocusEffectCurse() {
        super();
    }

    @Override
    public String getResearch() {
        return "FOCUSCURSE";
    }

    @Override
    public String getKey() {
        return "thaumcraft.CURSE";
    }

    @Override
    public Aspect getAspect() {
        return Aspect.DEATH;
    }

    @Override
    public int getComplexity() {
        return FocusLogic.calculateCurseComplexity(getSettingValue("power"), getSettingValue("duration"));
    }

    @Override
    public float getDamageForDisplay(float finalPower) {
        return FocusLogic.calculateCurseDamage(getSettingValue("power"), finalPower);
    }

    @Override
    public boolean execute(HitResult target, @Nullable Trajectory trajectory, float finalPower, int num) {
        if (target == null || getPackage() == null || getPackage().world == null) {
            return false;
        }
        Level world = getPackage().world;
        LivingEntity caster = getPackage().getCaster();

        if (target instanceof EntityHitResult ehr && ehr.getEntity() != null) {
            Entity hitEntity = ehr.getEntity();
            float damage = getDamageForDisplay(finalPower);
            int duration = FocusLogic.calculateCurseDebuffDuration(getSettingValue("duration"));
            int potency = FocusLogic.calculateCurseDebuffPotency(getSettingValue("power"), finalPower);

            hitEntity.hurt(world.damageSources().indirectMagic(caster, caster), damage);
            if (hitEntity instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(MobEffects.POISON, duration, potency));
                float chance = 0.85f;
                if (world.getRandom().nextFloat() < chance) {
                    living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, duration, potency));
                    chance -= 0.15f;
                }
                if (world.getRandom().nextFloat() < chance) {
                    living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, duration, potency));
                    chance -= 0.15f;
                }
                if (world.getRandom().nextFloat() < chance) {
                    living.addEffect(new MobEffectInstance(MobEffects.MINING_FATIGUE, duration * 2, potency));
                    chance -= 0.15f;
                }
                if (world.getRandom().nextFloat() < chance) {
                    living.addEffect(new MobEffectInstance(MobEffects.HUNGER, duration * 3, potency));
                    chance -= 0.15f;
                }
                if (world.getRandom().nextFloat() < chance) {
                    living.addEffect(new MobEffectInstance(MobEffects.UNLUCK, duration * 3, potency));
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public NodeSetting[] createSettings() {
        return new NodeSetting[] {
                new NodeSetting("power", "focus.common.power", new NodeSetting.NodeSettingIntRange(1, 5)),
                new NodeSetting("duration", "focus.common.duration", new NodeSetting.NodeSettingIntRange(1, 10))
        };
    }

    @Override
    public void renderParticleFX(Level world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
        // Client side
    }

    @Override
    public void onCast(Entity caster) {
        if (caster != null && caster.level() != null) {
            caster.level().playSound(null, caster.blockPosition().above(), SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.PLAYERS, 0.15f, 1.0f);
        }
    }
}
