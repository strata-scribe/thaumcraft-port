package thaumcraft.api.casters.focus;

import javax.annotation.Nullable;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.level.Level;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.NodeSetting;
import net.minecraft.world.entity.LivingEntity;

public class FocusEffectHeal extends FocusEffect {

    @Override
    public String getKey() {
        return "thaumcraft.HEAL";
    }

    @Override
    public String getResearch() {
        return "FOCUSHEAL";
    }

    @Override
    public Aspect getAspect() {
        return Aspect.LIFE;
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
        if (target instanceof EntityHitResult ehr && ehr.getEntity() instanceof LivingEntity living) {
            float healAmount = FocusEffectLogic.calculateHealing(getSettingValue("power"), finalPower);
            living.heal(healAmount);
            return true;
        }
        return false;
    }

    @Override
    public void renderParticleFX(Level world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
        // Particle rendering logic here
    }
}
