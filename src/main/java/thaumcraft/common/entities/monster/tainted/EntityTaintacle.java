package thaumcraft.common.entities.monster.tainted;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.api.potions.PotionFluxTaint;

public class EntityTaintacle extends Monster implements ITaintedMob {

    public EntityTaintacle(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 50.0D)
                .add(Attributes.ATTACK_DAMAGE, 7.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D); // Stationary
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected double getMeleeAttackRangeSqr(LivingEntity entity) {
        return 36.0D; // 6.0 squared for extended melee reach
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        if (!(target instanceof LivingEntity)) return false;

        double distance = this.position().distanceTo(target.position());
        double maxReach = 6.0; // extended melee reach

        if (TaintMobLogic.isWithinWhipReach(this.position(), target.position(), maxReach)) {
            float baseDamage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
            float finalDamage = TaintMobLogic.calculateTentacleDamage(baseDamage, distance);

            boolean flag = target.hurtServer(level, level.damageSources().mobAttack(this), finalDamage);

            if (flag) {
                // Apply knockback
                Vec3 knockback = TaintMobLogic.calculateKnockbackImpulse(this.position(), target.position(), 1.5);
                target.setDeltaMovement(target.getDeltaMovement().add(knockback));

                // Apply Taint Poison
                if (target instanceof LivingEntity livingTarget && BuiltInRegistries.MOB_EFFECT.wrapAsHolder(PotionFluxTaint.instance) != null) {
                     livingTarget.addEffect(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(PotionFluxTaint.instance), 100, 0));
                }
            }
            return flag;
        }
        return false;
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        return super.hurtServer(level, source, amount);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }
}
