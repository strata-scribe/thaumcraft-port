package thaumcraft.common.entities;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;
import thaumcraft.common.entities.monster.boss.EntityFluxRift;
import thaumcraft.common.entities.monster.tainted.EntityTaintSeed;

public class ThaumcraftEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, Thaumcraft.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<EntityFluxRift>> FLUX_RIFT =
            ENTITIES.register("flux_rift", () ->
                    EntityType.Builder.<EntityFluxRift>of(EntityFluxRift::new, MobCategory.MISC)
                            .sized(2.0f, 2.0f)
                            .build(ResourceKey.create(Registries.ENTITY_TYPE,
                                    Identifier.fromNamespaceAndPath(Thaumcraft.MODID, "flux_rift"))));

    public static final DeferredHolder<EntityType<?>, EntityType<EntityTaintSeed>> TAINT_SEED =
            ENTITIES.register("taint_seed", () ->
                    EntityType.Builder.<EntityTaintSeed>of(EntityTaintSeed::new, MobCategory.MONSTER)
                            .sized(1.0f, 1.0f)
                            .build(ResourceKey.create(Registries.ENTITY_TYPE,
                                    Identifier.fromNamespaceAndPath(Thaumcraft.MODID, "taint_seed"))));
}

