package thaumcraft.common.items.casters.foci;

import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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

public class FocusEffectAir extends FocusEffect {

    public FocusEffectAir() {
        super();
    }

    @Override
    public String getResearch() {
        return "FOCUSELEMENTAL";
    }

    @Override
    public String getKey() {
        return "thaumcraft.AIR";
    }

    @Override
    public Aspect getAspect() {
        return Aspect.AIR;
    }

    @Override
    public int getComplexity() {
        return FocusLogic.calculateAirComplexity(getSettingValue("power"));
    }

    @Override
    public float getDamageForDisplay(float finalPower) {
        return FocusLogic.calculateAirDamage(getSettingValue("power"), finalPower);
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
            hitEntity.hurt(world.damageSources().thrown(caster, caster), damage);

            if (hitEntity instanceof LivingEntity living) {
                float knockback = FocusLogic.calculateAirKnockback(getSettingValue("power"), finalPower);
                if (trajectory != null && trajectory.direction != null) {
                    living.knockback(knockback, -trajectory.direction.x, -trajectory.direction.z);
                } else if (caster != null) {
                    living.knockback(knockback, caster.getX() - hitEntity.getX(), caster.getZ() - hitEntity.getZ());
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public NodeSetting[] createSettings() {
        return new NodeSetting[] {
                new NodeSetting("power", "focus.common.power", new NodeSetting.NodeSettingIntRange(1, 5))
        };
    }

    @Override
    public void renderParticleFX(Level world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
        // Client side
    }

    @Override
    public void onCast(Entity caster) {
        if (caster != null && caster.level() != null) {
            caster.level().playSound(null, caster.blockPosition().above(), SoundEvents.PHANTOM_FLAP, SoundSource.PLAYERS, 0.5f, 2.0f);
        }
    }
}
