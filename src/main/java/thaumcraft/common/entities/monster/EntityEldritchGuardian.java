package thaumcraft.common.entities.monster;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import thaumcraft.common.entities.EntityCombatLogic;

public class EntityEldritchGuardian extends Monster {

    private boolean isOuterDimension = false;

    public EntityEldritchGuardian(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 15;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 50.0D)
                .add(Attributes.ARMOR, 4.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 7.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public boolean isOuterDimension() {
        return isOuterDimension;
    }

    public void setOuterDimension(boolean outerDimension) {
        this.isOuterDimension = outerDimension;
        if (outerDimension) {
            float absorption = EntityCombatLogic.calculateGuardianAbsorption(true);
            this.setAbsorptionAmount(absorption);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide()) {
            // Passive absorption shield regeneration in outer dimension (or when flagged)
            if ((this.isOuterDimension || this.getAbsorptionAmount() > 0.0f) && this.tickCount % 25 == 0) {
                if (this.getAbsorptionAmount() < 25.0f) {
                    this.setAbsorptionAmount(Math.min(25.0f, this.getAbsorptionAmount() + 1.0f));
                }
            }
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        // Eldritch Guardian: Magic damage halving (damage * 0.5f)
        boolean isMagic = source.is(DamageTypes.MAGIC)
                || source.is(DamageTypes.INDIRECT_MAGIC)
                || source.is(DamageTypeTags.WITCH_RESISTANT_TO);

        float mitigatedDamage = EntityCombatLogic.calculateGuardianMagicDamage(amount, isMagic);
        return super.hurtServer(level, source, mitigatedDamage);
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        boolean success = super.doHurtTarget(level, target);
        if (success && target instanceof Player player) {
            // Sonic Screech attack trigger (15% chance)
            if (EntityCombatLogic.shouldGuardianScreech(this.random.nextFloat())) {
                player.addEffect(new MobEffectInstance(MobEffects.WITHER, 400, 0), this);
            }
        }
        return success;
    }

    /**
     * Calculates the magic splash damage for an Eldritch Orb fired by this guardian.
     */
    public float getOrbDamage() {
        return EntityCombatLogic.calculateEldritchOrbDamage((float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setOuterDimension(input.getBooleanOr("OuterDimension", false));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("OuterDimension", this.isOuterDimension);
    }
}
