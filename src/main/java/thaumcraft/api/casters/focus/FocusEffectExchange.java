package thaumcraft.api.casters.focus;

import javax.annotation.Nullable;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.NodeSetting;

public class FocusEffectExchange extends FocusEffect {

    @Override
    public String getKey() {
        return "thaumcraft.EXCHANGE";
    }

    @Override
    public String getResearch() {
        return "FOCUSEXCHANGE";
    }

    @Override
    public Aspect getAspect() {
        return Aspect.EXCHANGE;
    }

    @Override
    public int getComplexity() {
        return FocusExchangeLogic.calculateExchangeComplexity(getSettingValue("power"));
    }

    @Override
    public NodeSetting[] createSettings() {
        return new NodeSetting[] {
            new NodeSetting("power", "focus.common.power", new NodeSetting.NodeSettingIntRange(1, 5))
        };
    }

    @Override
    public boolean execute(HitResult target, @Nullable Trajectory trajectory, float finalPower, int num) {
        return false;
    }

    @Override
    public void renderParticleFX(Level world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
        // Particle logic
    }
}
