package thaumcraft.api.damagesource;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;

public class DamageSourceThaumcraft {
    public static final ResourceKey<DamageType> TAINT = ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath("thaumcraft", "taint"));
    public static final ResourceKey<DamageType> TENTACLE = ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath("thaumcraft", "tentacle"));
    public static final ResourceKey<DamageType> SWARM = ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath("thaumcraft", "swarm"));
    public static final ResourceKey<DamageType> DISSOLVE = ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath("thaumcraft", "dissolve"));

    public static DamageSource getSource(Level level, ResourceKey<DamageType> key) {
        return new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(key));
    }

    public static DamageSource getSource(Level level, ResourceKey<DamageType> key, Entity attacker) {
        return new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(key), attacker);
    }

    public static DamageSource getSource(Level level, ResourceKey<DamageType> key, Entity cause, Entity attacker) {
        return new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(key), cause, attacker);
    }
}
