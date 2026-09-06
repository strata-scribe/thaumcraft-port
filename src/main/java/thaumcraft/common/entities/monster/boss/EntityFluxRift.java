package thaumcraft.common.entities.monster.boss;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.entities.ThaumcraftEntities;
import thaumcraft.common.entities.monster.tainted.EntityTaintSeed;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LightningBolt;

public class EntityFluxRift extends Entity {

    private static final EntityDataAccessor<Float> RIFT_SIZE = SynchedEntityData.defineId(EntityFluxRift.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> STABILITY = SynchedEntityData.defineId(EntityFluxRift.class, EntityDataSerializers.FLOAT);

    public EntityFluxRift(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(RIFT_SIZE, 1.0f);
        builder.define(STABILITY, 100.0f);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound, net.minecraft.core.HolderLookup.Provider provider) {
        if (compound.contains("RiftSize")) this.setRiftSize(compound.getFloat("RiftSize"));
        if (compound.contains("Stability")) this.setStability(compound.getFloat("Stability"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound, net.minecraft.core.HolderLookup.Provider provider) {
        compound.putFloat("RiftSize", this.getRiftSize());
        compound.putFloat("Stability", this.getStability());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {}

    public float getRiftSize() {
        return this.entityData.get(RIFT_SIZE);
    }

    public void setRiftSize(float size) {
        this.entityData.set(RIFT_SIZE, size);
    }

    public float getStability() {
        return this.entityData.get(STABILITY);
    }

    public void setStability(float stability) {
        this.entityData.set(STABILITY, stability);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level() != null && !this.level().isClientSide()) {
            tickFluxAndStability();
        }
    }

    public void tickFluxAndStability() {
        float flux = 0.0f;
        if (this.level() != null) {
            flux = AuraHelper.getFlux(this.level(), this.blockPosition());
        }

        if (flux > 0) {
            float toDrain = Math.min(flux, 5.0f);
            float drained = toDrain;
            if (this.level() != null) {
                drained = AuraHelper.drainFlux(this.level(), this.blockPosition(), toDrain, false);
            }
            if (drained > 0) {
                this.setRiftSize(this.getRiftSize() + drained * 0.01f);
            }
        }

        // Decrease stability based on size
        this.setStability(this.getStability() - (this.getRiftSize() * 0.05f));

        if (this.getStability() < 20.0f) {
            if (this.random.nextInt(5) == 0) {
                BlockPos target = this.blockPosition().offset(
                    this.random.nextInt(11) - 5,
                    this.random.nextInt(11) - 5,
                    this.random.nextInt(11) - 5
                );
                if (!this.level().isEmptyBlock(target)) {
                    this.level().removeBlock(target, false);
                }
            }
            if (this.random.nextInt(20) == 0) {
                LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(this.level());
                if (bolt != null) {
                    bolt.moveTo(this.position());
                    this.level().addFreshEntity(bolt);
                }
            }
        }

        if (this.getStability() <= 0.0f || this.getRiftSize() >= 50.0f) {
            collapse();
        }
    }

    public void collapse() {
        if (this.level() != null) {
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), 4.0f, Level.ExplosionInteraction.NONE);
            if (this.getRiftSize() >= 20.0f) {
                EntityTaintSeed seed = new EntityTaintSeed(ThaumcraftEntities.TAINT_SEED, this.level());
                seed.setPos(this.position());
                this.level().addFreshEntity(seed);
            } else {
                // Drop rare pearls
                this.spawnAtLocation(thaumcraft.api.items.ThaumcraftItems.primordialPearl.get());
            }
        }
        this.discard();
    }
}
