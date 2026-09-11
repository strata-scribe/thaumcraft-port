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
import thaumcraft.api.casters.focus.SpectralArrowLogic;

public class FocusEffectSpectralArrow extends FocusEffect {

    public FocusEffectSpectralArrow() {
        super();
    }

    @Override
    public String getResearch() {
        return "FOCUSELEMENTAL";
    }

    @Override
    public String getKey() {
        return "thaumcraft.SPECTRALARROW";
    }

    @Override
    public Aspect getAspect() {
        return Aspect.MAGIC;
    }

    @Override
    public int getComplexity() {
        int c = 3; // base
        if (getSettingList() != null) {
            for (String key : getSettingList()) {
                if (key.equals("power")) {
                    c += getSettingValue("power");
                } else if (key.equals("piercing")) {
                    c += getSettingValue("piercing") * 2;
                }
            }
        }
        return c;
    }

    @Override
    public float getDamageForDisplay(float finalPower) {
        return SpectralArrowLogic.calculateDamage(getSettingValue("power"), finalPower);
    }

    @Override
    public NodeSetting[] createSettings() {
        return new NodeSetting[] {
                new NodeSetting("power", "focus.common.power", new NodeSetting.NodeSettingIntRange(1, 5)),
                new NodeSetting("piercing", "focus.spectralarrow.piercing", new NodeSetting.NodeSettingIntRange(0, 5))
        };
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
            float pen = SpectralArrowLogic.calculateArmorPenetration(getSettingValue("piercing"));

            float bypassDamage = damage * pen;
            float normalDamage = damage - bypassDamage;



            if (normalDamage > 0) {
                hitEntity.hurt(world.damageSources().thrown(caster, caster), normalDamage);
            }
            if (bypassDamage > 0) {
                hitEntity.invulnerableTime = 0;
                hitEntity.hurt(world.damageSources().indirectMagic(caster, caster), bypassDamage) ;
            }
            return true;
        }
        return false;
    }

    @Override
    public void renderParticleFX(Level world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
        // Particles handled client side
    }

    @Override
    public void onCast(Entity caster) {
        if (caster != null && caster.level() != null) {
            caster.level().playSound(null, caster.blockPosition().above(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0f, 1.0f + (float) (caster.level().getRandom().nextGaussian() * 0.05));
        }
    }
}
