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

public class FocusEffectFire extends FocusEffect {

    @Override
    public String getKey() {
        return "thaumcraft.FIRE";
    }

    @Override
    public String getResearch() {
        return "FOCUSFIRE";
    }

    @Override
    public Aspect getAspect() {
        return Aspect.FIRE;
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
            if (duration > 0) {
                ehr.getEntity().igniteForSeconds(duration);
            }
            return ehr.getEntity().hurtServer(serverLevel, serverLevel.damageSources().inFire(), damage);
        }
        return false;
    }

    @Override
    public void renderParticleFX(Level world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
        // Particle rendering logic here
    }
}
