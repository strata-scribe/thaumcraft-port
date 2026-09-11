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
import thaumcraft.common.entities.monster.tainted.EntityTaintacle;
import thaumcraft.common.entities.monster.tainted.EntityTaintCrawler;
import thaumcraft.common.entities.monster.pech.EntityPech;
import thaumcraft.common.entities.monster.EntityEldritchCrab;
import thaumcraft.common.entities.monster.EntityInhabitedZombie;
import thaumcraft.common.entities.projectile.EntityFocusRift;

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

    public static final DeferredHolder<EntityType<?>, EntityType<EntityPech>> PECH =
            ENTITIES.register("pech", () ->
                    EntityType.Builder.<EntityPech>of(EntityPech::new, MobCategory.MONSTER)
                            .sized(0.6f, 1.5f)
                            .build(ResourceKey.create(Registries.ENTITY_TYPE,
                                    Identifier.fromNamespaceAndPath(Thaumcraft.MODID, "pech"))));

    public static final DeferredHolder<EntityType<?>, EntityType<EntityTaintacle>> TAINTACLE =
            ENTITIES.register("taintacle", () ->
                    EntityType.Builder.<EntityTaintacle>of(EntityTaintacle::new, MobCategory.MONSTER)
                            .sized(0.8f, 3.0f)
                            .build(ResourceKey.create(Registries.ENTITY_TYPE,
                                    Identifier.fromNamespaceAndPath(Thaumcraft.MODID, "taintacle"))));

    public static final DeferredHolder<EntityType<?>, EntityType<EntityTaintCrawler>> TAINT_CRAWLER =
            ENTITIES.register("taint_crawler", () ->
                    EntityType.Builder.<EntityTaintCrawler>of(EntityTaintCrawler::new, MobCategory.MONSTER)
                            .sized(0.5f, 0.4f)
                            .build(ResourceKey.create(Registries.ENTITY_TYPE,
                                    Identifier.fromNamespaceAndPath(Thaumcraft.MODID, "taint_crawler"))));

    public static final DeferredHolder<EntityType<?>, EntityType<EntityEldritchCrab>> ELDRITCH_CRAB =
            ENTITIES.register("eldritch_crab", () ->
                    EntityType.Builder.<EntityEldritchCrab>of(EntityEldritchCrab::new, MobCategory.MONSTER)
                            .sized(0.8f, 0.6f)
                            .build(ResourceKey.create(Registries.ENTITY_TYPE,
                                    Identifier.fromNamespaceAndPath(Thaumcraft.MODID, "eldritch_crab"))));

    public static final DeferredHolder<EntityType<?>, EntityType<EntityInhabitedZombie>> INHABITED_ZOMBIE =
            ENTITIES.register("inhabited_zombie", () ->
                    EntityType.Builder.<EntityInhabitedZombie>of(EntityInhabitedZombie::new, MobCategory.MONSTER)
                            .sized(0.6f, 1.95f)
                            .build(ResourceKey.create(Registries.ENTITY_TYPE,
                                    Identifier.fromNamespaceAndPath(Thaumcraft.MODID, "inhabited_zombie"))));

    public static final DeferredHolder<EntityType<?>, EntityType<EntityFocusRift>> FOCUS_RIFT =
            ENTITIES.register("focus_rift", () ->
                    EntityType.Builder.<EntityFocusRift>of(EntityFocusRift::new, MobCategory.MISC)
                            .sized(1.0f, 1.0f)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build(ResourceKey.create(Registries.ENTITY_TYPE,
                                    Identifier.fromNamespaceAndPath(Thaumcraft.MODID, "focus_rift"))));
}

