package thaumcraft.common.entities.monster;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import thaumcraft.common.entities.EntityCombatLogic;

public class EntityMindSpider extends Spider {

    private boolean harmless = true;
    private int lifeSpan = 1200;

    public EntityMindSpider(EntityType<? extends Spider> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Spider.createAttributes()
                .add(Attributes.MAX_HEALTH, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 1.0D);
    }

    public boolean isHarmless() {
        return harmless;
    }

    public void setHarmless(boolean harmless) {
        this.harmless = harmless;
    }

    public int getLifeSpan() {
        return lifeSpan;
    }

    public void setLifeSpan(int lifeSpan) {
        this.lifeSpan = lifeSpan;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide()) {
            // Despawn after harmless lifespan expires (1200 ticks / 60 seconds)
            if (EntityCombatLogic.isHarmlessExpired(this.tickCount, this.lifeSpan)) {
                this.discard();
            }
        }
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        // Harmless hallucination spider deals 0 damage and cancels attack
        if (!EntityCombatLogic.canMindSpiderAttack(this.harmless)) {
            return false;
        }
        return super.doHurtTarget(level, target);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        return super.hurtServer(level, source, amount);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.harmless = input.getBooleanOr("Harmless", true);
        this.lifeSpan = input.getIntOr("LifeSpan", 1200);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("Harmless", this.harmless);
        output.putInt("LifeSpan", this.lifeSpan);
    }
}
