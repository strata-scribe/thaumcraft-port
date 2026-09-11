package thaumcraft.common.entities.projectile;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.common.casters.FocusRiftLogic;
import net.minecraft.nbt.CompoundTag;

import java.util.List;

public class EntityFocusRift extends Entity {

    private static final EntityDataAccessor<Integer> DURATION = SynchedEntityData.defineId(EntityFocusRift.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> RADIUS = SynchedEntityData.defineId(EntityFocusRift.class, EntityDataSerializers.FLOAT);

    private FocusPackage focusPackage;
    private int tickCounter = 0;

    public EntityFocusRift(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.noPhysics = true;
    }

    public void setDurationSetting(int durationSetting) {
        this.entityData.set(DURATION, FocusRiftLogic.calculateRiftDurationTicks(durationSetting));
        this.entityData.set(RADIUS, (float) FocusRiftLogic.calculateRiftRadius(durationSetting));
    }

    public void setFocusPackage(FocusPackage pack) {
        this.focusPackage = pack;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DURATION, 20);
        builder.define(RADIUS, 4.0f);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.entityData.set(DURATION, compound.getInt("duration"));
        this.entityData.set(RADIUS, compound.getFloat("radius"));
        this.tickCounter = compound.getInt("tickCounter");

        if (compound.contains("focusPackage")) {
            this.focusPackage = new FocusPackage();
            this.focusPackage.deserialize(compound.getCompound("focusPackage"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("duration", this.entityData.get(DURATION));
        compound.putFloat("radius", this.entityData.get(RADIUS));
        compound.putInt("tickCounter", this.tickCounter);
        if (this.focusPackage != null) {
            compound.put("focusPackage", this.focusPackage.serialize());
        }
    }

    @Override
    public void tick() {
        super.tick();

        tickCounter++;
        int maxDuration = this.entityData.get(DURATION);
        if (tickCounter >= maxDuration) {
            this.discard();
            return;
        }

        if (this.level() != null && !this.level().isClientSide()) {
            float radius = this.entityData.get(RADIUS);
            AABB searchBox = this.getBoundingBox().inflate(radius);
            List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, searchBox, e -> !e.isSpectator() && e.isPickable());

            for (LivingEntity e : entities) {
                double distance = this.position().distanceTo(e.position());
                double velocityMag = FocusRiftLogic.calculateAttractionVelocity(distance, radius);

                if (velocityMag > 0) {
                    Vec3 diff = this.position().subtract(e.position());
                    if (diff.lengthSqr() > 1.0E-4D) { // Safety check to prevent NaN normalize
                        Vec3 dir = diff.normalize();
                        e.setDeltaMovement(e.getDeltaMovement().add(dir.scale(velocityMag)));
                        e.hasImpulse = true;
                    }
                }

                // Periodically apply focus effects
                if (this.focusPackage != null && tickCounter % 10 == 0) { // Apply every 10 ticks
                    Trajectory trajectory = new Trajectory(this.position(), e.position().subtract(this.position()).normalize());
                    HitResult target = new EntityHitResult(e);
                    FocusEngine.runFocusPackage(this.focusPackage, new Trajectory[]{trajectory}, new HitResult[]{target});
                }
            }
        }
    }
}
