package thaumcraft.common.entities.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import thaumcraft.common.entities.EldritchMobLogic;
import thaumcraft.common.entities.ThaumcraftEntities;
import java.util.List;

public class EntityEldritchCrab extends Monster {

    public EntityEldritchCrab(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D);
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
    public void tick() {
        super.tick();

        if (!this.level().isClientSide() && this.isAlive()) {
            findAndPossessTarget();
        }
    }

    private void findAndPossessTarget() {
        AABB boundingBox = this.getBoundingBox().inflate(5.0D);
        List<Zombie> nearbyZombies = this.level().getEntitiesOfClass(Zombie.class, boundingBox,
            entity -> entity.isAlive() && !(entity instanceof EntityInhabitedZombie));

        for (Zombie zombie : nearbyZombies) {
            double distanceToZombie = this.distanceTo(zombie);

            if (EldritchMobLogic.canPossess(true, false, distanceToZombie, 2.0)) {
                // Possess!
                if (this.level() instanceof ServerLevel serverLevel) {
                    EntityInhabitedZombie inhabitedZombie = new EntityInhabitedZombie(ThaumcraftEntities.INHABITED_ZOMBIE.get(), serverLevel);
                    inhabitedZombie.setPos(zombie.getX(), zombie.getY(), zombie.getZ());
                    inhabitedZombie.setHealth(zombie.getHealth());

                    serverLevel.addFreshEntity(inhabitedZombie);
                    zombie.discard();
                    this.discard();
                    return;
                }
            } else if (distanceToZombie <= 5.0 && this.onGround()) {
                // Leap towards
                double dx = zombie.getX() - this.getX();
                double dy = zombie.getY() - this.getY();
                double dz = zombie.getZ() - this.getZ();
                double distance = Math.sqrt(dx * dx + dz * dz);

                double[] trajectory = EldritchMobLogic.calculateCrabLeapTrajectory(dx, dy, dz, distance, 0.5);
                this.setDeltaMovement(trajectory[0], trajectory[1], trajectory[2]);
                break;
            }
        }
    }
}
