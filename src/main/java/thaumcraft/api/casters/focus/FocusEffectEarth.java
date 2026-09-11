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

public class FocusEffectEarth extends FocusEffect {

    @Override
    public String getKey() {
        return "thaumcraft.EARTH";
    }

    @Override
    public String getResearch() {
        return "FOCUSEARTH";
    }

    @Override
    public Aspect getAspect() {
        return Aspect.EARTH;
    }

    @Override
    public int getComplexity() {
        return FocusEffectLogic.getComplexity(this);
    }

    @Override
    public NodeSetting[] createSettings() {
        return new NodeSetting[] {
            new NodeSetting("power", "focus.common.power", new NodeSetting.NodeSettingIntRange(1, 5))
        };
    }

    @Override
    public boolean execute(HitResult target, @Nullable Trajectory trajectory, float finalPower, int num) {
        if (target instanceof EntityHitResult ehr && ehr.getEntity().level() instanceof ServerLevel serverLevel) {
            float damage = FocusEffectLogic.calculateDamage(getSettingValue("power"), finalPower);
            // Earth effects might just do standard magic damage or physical
            return ehr.getEntity().hurtServer(serverLevel, serverLevel.damageSources().magic(), damage);
        }
        return false;
    }

    @Override
    public void renderParticleFX(Level world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
        // Particle rendering logic here
    }
}
