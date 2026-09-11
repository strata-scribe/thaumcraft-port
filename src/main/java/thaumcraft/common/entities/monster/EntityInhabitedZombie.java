package thaumcraft.common.entities.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import thaumcraft.common.entities.EldritchMobLogic;
import thaumcraft.common.entities.ThaumcraftEntities;

public class EntityInhabitedZombie extends Zombie {

    public EntityInhabitedZombie(EntityType<? extends Zombie> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.MOVEMENT_SPEED, EldritchMobLogic.calculateAugmentedSpeed(0.23D))
                .add(Attributes.ARMOR, EldritchMobLogic.calculateAugmentedArmor(2.0D))
                .add(Attributes.ATTACK_DAMAGE, 4.0D);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        return super.hurtServer(level, source, amount);
    }

    @Override
    public void die(DamageSource cause) {
        super.die(cause);

        if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
            // Spawn crab on death
            EntityEldritchCrab crab = new EntityEldritchCrab(ThaumcraftEntities.ELDRITCH_CRAB.get(), serverLevel);
            crab.setPos(this.getX(), this.getY() + 1.0, this.getZ());
            serverLevel.addFreshEntity(crab);
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
    }
}
