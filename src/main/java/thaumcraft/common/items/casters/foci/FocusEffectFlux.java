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

public class FocusEffectFlux extends FocusEffect {

    public FocusEffectFlux() {
        super();
    }

    @Override
    public String getResearch() {
        return "FOCUSFLUX";
    }

    @Override
    public String getKey() {
        return "thaumcraft.FLUX";
    }

    @Override
    public Aspect getAspect() {
        return Aspect.FLUX;
    }

    @Override
    public int getComplexity() {
        return FocusLogic.calculateFluxComplexity(getSettingValue("power"));
    }

    @Override
    public float getDamageForDisplay(float finalPower) {
        return FocusLogic.calculateFluxDamage(getSettingValue("power"), finalPower);
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
            ehr.getEntity().hurt(world.damageSources().indirectMagic(caster, caster), damage);
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
            caster.level().playSound(null, caster.blockPosition().above(), SoundEvents.CHORUS_FLOWER_GROW, SoundSource.PLAYERS, 2.0f, 2.0f);
        }
    }
}
