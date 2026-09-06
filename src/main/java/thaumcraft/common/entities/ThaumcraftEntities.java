package thaumcraft.common.entities;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.common.entities.monster.boss.EntityFluxRift;
import thaumcraft.common.entities.monster.tainted.EntityTaintSeed;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceLocation;

@EventBusSubscriber(modid = Thaumcraft.MODID)
public class ThaumcraftEntities {

    public static EntityType<EntityFluxRift> FLUX_RIFT;
    public static EntityType<EntityTaintSeed> TAINT_SEED;

    @SubscribeEvent
    public static void registerEntities(RegisterEvent event) {
        event.register(Registries.ENTITY_TYPE, helper -> {
            ResourceKey<EntityType<?>> riftKey = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "flux_rift"));
            ResourceKey<EntityType<?>> seedKey = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "taint_seed"));

            FLUX_RIFT = EntityType.Builder.of(EntityFluxRift::new, MobCategory.MISC)
                .sized(2.0f, 2.0f)
                .build(riftKey);
            TAINT_SEED = EntityType.Builder.of(EntityTaintSeed::new, MobCategory.MONSTER)
                .sized(1.0f, 1.0f)
                .build(seedKey);

            helper.register(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "flux_rift"), FLUX_RIFT);
            helper.register(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "taint_seed"), TAINT_SEED);
        });
    }
}
