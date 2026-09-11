package thaumcraft.api.casters.focus;

import javax.annotation.Nullable;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.NodeSetting;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;

public class FocusEffectBreak extends FocusEffect {

    @Override
    public String getKey() {
        return "thaumcraft.BREAK";
    }

    @Override
    public String getResearch() {
        return "FOCUSBREAK";
    }

    @Override
    public Aspect getAspect() {
        return Aspect.ENTROPY;
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
        if (target instanceof BlockHitResult bhr) {
            BlockPos pos = bhr.getBlockPos();
            if (this.getPackage() != null && this.getPackage().world instanceof ServerLevel serverLevel) {
                // FocusEffectLogic handles whether it actually breaks based on power/hardness
                boolean canBreak = FocusEffectLogic.canBreakBlock(getSettingValue("power"), finalPower);
                if (canBreak && !serverLevel.isEmptyBlock(pos)) {
                     serverLevel.destroyBlock(pos, true);
                     return true;
                }
            }
        }
        return false;
    }

    @Override
    public void renderParticleFX(Level world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
        // Particle rendering logic here
    }
}
