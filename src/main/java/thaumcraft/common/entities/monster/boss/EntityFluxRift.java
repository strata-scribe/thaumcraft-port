package thaumcraft.common.entities.monster.boss;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.entities.ThaumcraftEntities;
import thaumcraft.common.entities.monster.tainted.EntityTaintSeed;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
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
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        this.setRiftSize(input.getFloatOr("RiftSize", 1.0f));
        this.setStability(input.getFloatOr("Stability", 100.0f));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putFloat("RiftSize", this.getRiftSize());
        output.putFloat("Stability", this.getStability());
    }

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
                this.setRiftSize(FluxRiftLogic.calculateSizeGrowth(this.getRiftSize(), drained));
            }
        }

        // Decrease stability based on size
        this.setStability(FluxRiftLogic.calculateStabilityDecay(this.getStability(), this.getRiftSize()));

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
            if (this.random.nextInt(20) == 0 && this.level() instanceof ServerLevel serverLevel) {
                LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(serverLevel, EntitySpawnReason.NATURAL);
                if (bolt != null) {
                    bolt.setPos(this.position());
                    serverLevel.addFreshEntity(bolt);
                }
            }
        }

        if (FluxRiftLogic.shouldCollapse(this.getStability(), this.getRiftSize())) {
            collapse();
        }
    }

    public void collapse() {
        if (this.level() != null) {
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), 4.0f, Level.ExplosionInteraction.NONE);
            if (FluxRiftLogic.shouldSpawnTaintSeed(this.getRiftSize())) {
                EntityTaintSeed seed = new EntityTaintSeed(ThaumcraftEntities.TAINT_SEED.get(), this.level());
                seed.setPos(this.position());
                this.level().addFreshEntity(seed);
            } else if (this.level() instanceof ServerLevel serverLevel) {
                // Drop rare pearls
                this.spawnAtLocation(serverLevel, thaumcraft.api.items.ThaumcraftItems.primordialPearl.get());
            }
        }
        this.discard();
    }
}

