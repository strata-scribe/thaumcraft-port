package thaumcraft.common.items.casters.foci;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.common.casters.FocusLogic;

public class FocusEffectFrost extends FocusEffect {

    public FocusEffectFrost() {
        super();
    }

    @Override
    public String getResearch() {
        return "FOCUSELEMENTAL";
    }

    @Override
    public String getKey() {
        return "thaumcraft.FROST";
    }

    @Override
    public Aspect getAspect() {
        return Aspect.COLD;
    }

    @Override
    public int getComplexity() {
        return FocusLogic.calculateFrostComplexity(getSettingValue("power"), getSettingValue("duration"));
    }

    @Override
    public float getDamageForDisplay(float finalPower) {
        return FocusLogic.calculateFrostDamage(getSettingValue("power"), finalPower);
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
            int duration = FocusLogic.calculateFrostSlownessDuration(getSettingValue("duration"));
            int potency = FocusLogic.calculateFrostSlownessPotency(getSettingValue("power"), finalPower);

            hitEntity.hurt(world.damageSources().indirectMagic(caster, caster), damage);
            if (hitEntity instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, duration, potency));
            }
            return true;
        } else if (target instanceof BlockHitResult bhr) {
            float radius = FocusLogic.calculateFrostFreezeRadius(getSettingValue("power"), finalPower);
            BlockPos center = bhr.getBlockPos();
            int r = (int) Math.ceil(radius);
            for (BlockPos pos : BlockPos.betweenClosed(center.offset(-r, -r, -r), center.offset(r, r, r))) {
                if (pos.distSqr(center) <= radius * radius) {
                    BlockState state = world.getBlockState(pos);
                    if (state.is(Blocks.WATER) && state.getValue(LiquidBlock.LEVEL) == 0) {
                        world.setBlock(pos, Blocks.FROSTED_ICE.defaultBlockState(), 11);
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public NodeSetting[] createSettings() {
        return new NodeSetting[] {
                new NodeSetting("power", "focus.common.power", new NodeSetting.NodeSettingIntRange(1, 5)),
                new NodeSetting("duration", "focus.common.duration", new NodeSetting.NodeSettingIntRange(2, 10))
        };
    }

    @Override
    public void renderParticleFX(Level world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
        // Particles handled client side
    }

    @Override
    public void onCast(Entity caster) {
        if (caster != null && caster.level() != null) {
            caster.level().playSound(null, caster.blockPosition().above(), SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.PLAYERS, 0.2f, 1.0f + (float) (caster.level().getRandom().nextGaussian() * 0.05));
        }
    }
}
