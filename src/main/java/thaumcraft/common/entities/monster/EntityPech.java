package thaumcraft.common.entities.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

/**
 * EntityPech forwarder in monster package for backwards compatibility.
 * Delegates to canonical implementation in thaumcraft.common.entities.monster.pech.EntityPech.
 */
public class EntityPech extends thaumcraft.common.entities.monster.pech.EntityPech {

    public EntityPech(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }
}
