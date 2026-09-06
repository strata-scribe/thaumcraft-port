package thaumcraft.common.entities.monster.tainted;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;

public class EntityTaintSeed extends Entity {

    public EntityTaintSeed(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound, net.minecraft.core.HolderLookup.Provider provider) {
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound, net.minecraft.core.HolderLookup.Provider provider) {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {}
}
