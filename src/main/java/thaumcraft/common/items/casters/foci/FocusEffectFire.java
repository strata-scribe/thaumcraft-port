package thaumcraft.common.items.casters.foci;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.common.casters.FocusLogic;

public class FocusEffectFire extends FocusEffect {

    public FocusEffectFire() {
        super();
    }

    @Override
    public String getResearch() {
        return "BASEAUROMANCY";
    }

    @Override
    public String getKey() {
        return "thaumcraft.FIRE";
    }

    @Override
    public Aspect getAspect() {
        return Aspect.FIRE;
    }

    @Override
    public int getComplexity() {
        return FocusLogic.calculateFireComplexity(getSettingValue("power"), getSettingValue("duration"));
    }

    @Override
    public float getDamageForDisplay(float finalPower) {
        return FocusLogic.calculateFireDamage(getSettingValue("power"), finalPower);
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
            if (hitEntity.fireImmune()) {
                return false;
            }
            float damage = getDamageForDisplay(finalPower);
            float burnSeconds = FocusLogic.calculateFireBurnDuration(getSettingValue("duration"), finalPower);

            hitEntity.hurt(world.damageSources().inFire(), damage);
            if (burnSeconds > 0.0f) {
                hitEntity.igniteForSeconds(burnSeconds);
            }
            return true;
        } else if (target instanceof BlockHitResult bhr && getSettingValue("duration") > 0) {
            BlockPos firePos = bhr.getBlockPos().relative(bhr.getDirection());
            if (world.isEmptyBlock(firePos) && world.getRandom().nextFloat() < finalPower) {
                world.playSound(null, firePos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0f, world.getRandom().nextFloat() * 0.4f + 0.8f);
                world.setBlock(firePos, Blocks.FIRE.defaultBlockState(), 11);
                return true;
            }
        }
        return false;
    }

    @Override
    public NodeSetting[] createSettings() {
        return new NodeSetting[] {
                new NodeSetting("power", "focus.common.power", new NodeSetting.NodeSettingIntRange(1, 5)),
                new NodeSetting("duration", "focus.fire.burn", new NodeSetting.NodeSettingIntRange(0, 5))
        };
    }

    @Override
    public void renderParticleFX(Level world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
        // Particles handled client side
    }

    @Override
    public void onCast(Entity caster) {
        if (caster != null && caster.level() != null) {
            caster.level().playSound(null, caster.blockPosition().above(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.0f, 1.0f + (float) (caster.level().getRandom().nextGaussian() * 0.05));
        }
    }
}
