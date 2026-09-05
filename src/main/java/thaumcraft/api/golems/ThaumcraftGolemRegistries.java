package thaumcraft.api.golems;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.RegistryBuilder;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.api.golems.parts.GolemMaterial;

public class ThaumcraftGolemRegistries {
    public static final ResourceKey<Registry<GolemMaterial>> MATERIAL_REG_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath("thaumcraft", "golem_material"));
    public static final ResourceKey<Registry<EnumGolemTrait>> TRAIT_REG_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath("thaumcraft", "golem_trait"));

    public static final DeferredRegister<GolemMaterial> GOLEM_MATERIALS = DeferredRegister.create(MATERIAL_REG_KEY, "thaumcraft");
    public static final DeferredRegister<EnumGolemTrait> GOLEM_TRAITS = DeferredRegister.create(TRAIT_REG_KEY, "thaumcraft");

    public static void register(IEventBus modBus) {
        GOLEM_MATERIALS.register(modBus);
        GOLEM_TRAITS.register(modBus);
        modBus.addListener(ThaumcraftGolemRegistries::onNewRegistry);
        EnumGolemTrait.init();
    }

    private static void onNewRegistry(NewRegistryEvent event) {
        event.register(new RegistryBuilder<>(MATERIAL_REG_KEY).sync(true).create());
        event.register(new RegistryBuilder<>(TRAIT_REG_KEY).sync(true).create());
    }
}
