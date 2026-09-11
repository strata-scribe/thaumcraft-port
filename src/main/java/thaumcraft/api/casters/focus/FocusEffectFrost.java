package thaumcraft.api.casters.focus;

import javax.annotation.Nullable;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.NodeSetting;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class FocusEffectFrost extends FocusEffect {

    @Override
    public String getKey() {
        return "thaumcraft.FROST";
    }

    @Override
    public String getResearch() {
        return "FOCUSFROST";
    }

    @Override
    public Aspect getAspect() {
        return Aspect.COLD;
    }

    @Override
    public int getComplexity() {
        return FocusEffectLogic.getComplexity(this);
    }

    @Override
    public NodeSetting[] createSettings() {
        return new NodeSetting[] {
            new NodeSetting("power", "focus.common.power", new NodeSetting.NodeSettingIntRange(1, 5)),
            new NodeSetting("duration", "focus.common.duration", new NodeSetting.NodeSettingIntRange(0, 5))
        };
    }

    @Override
    public boolean execute(HitResult target, @Nullable Trajectory trajectory, float finalPower, int num) {
        if (target instanceof EntityHitResult ehr && ehr.getEntity().level() instanceof ServerLevel serverLevel) {
            float damage = FocusEffectLogic.calculateDamage(getSettingValue("power"), finalPower);
            int duration = FocusEffectLogic.calculateDuration(getSettingValue("duration"), finalPower);
            if (duration > 0 && ehr.getEntity() instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(net.minecraft.world.effect.MobEffects.SLOWNESS, duration * 20, getSettingValue("power") - 1));
            }
            return ehr.getEntity().hurtServer(serverLevel, serverLevel.damageSources().freeze(), damage);
        }
        return false;
    }

    @Override
    public void renderParticleFX(Level world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
        // Particle rendering logic here
    }
}
