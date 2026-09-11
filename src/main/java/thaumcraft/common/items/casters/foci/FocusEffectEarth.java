package thaumcraft.common.items.casters.foci;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.common.casters.FocusLogic;

public class FocusEffectEarth extends FocusEffect {

    public FocusEffectEarth() {
        super();
    }

    @Override
    public String getResearch() {
        return "FOCUSELEMENTAL";
    }

    @Override
    public String getKey() {
        return "thaumcraft.EARTH";
    }

    @Override
    public Aspect getAspect() {
        return Aspect.EARTH;
    }

    @Override
    public int getComplexity() {
        return FocusLogic.calculateEarthComplexity(getSettingValue("power"));
    }

    @Override
    public float getDamageForDisplay(float finalPower) {
        return FocusLogic.calculateEarthDamage(getSettingValue("power"), finalPower);
    }

    @Override
    public boolean execute(HitResult target, @Nullable Trajectory trajectory, float finalPower, int num) {
        if (target == null || getPackage() == null || getPackage().world == null) {
            return false;
        }
        Level world = getPackage().world;
        LivingEntity caster = getPackage().getCaster();

        if (target instanceof EntityHitResult ehr && ehr.getEntity() != null) {
            float damage = getDamageForDisplay(finalPower);
            ehr.getEntity().hurt(world.damageSources().thrown(caster, caster), damage);
            return true;
        } else if (target instanceof BlockHitResult bhr) {
            BlockPos pos = bhr.getBlockPos();
            BlockState state = world.getBlockState(pos);
            float hardness = state.getDestroySpeed(world, pos);
            if (hardness >= 0.0f && hardness <= FocusLogic.calculateEarthMaxBreakHardness(getSettingValue("power"), finalPower)) {
                world.destroyBlock(pos, true, caster);
                return true;
            }
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
        // Handled client side
    }

    @Override
    public void onCast(Entity caster) {
        if (caster != null && caster.level() != null) {
            caster.level().playSound(null, caster.blockPosition().above(), SoundEvents.ENDER_DRAGON_FLAP, SoundSource.PLAYERS, 0.25f, 1.0f + (float) (caster.level().getRandom().nextGaussian() * 0.05));
        }
    }
}
