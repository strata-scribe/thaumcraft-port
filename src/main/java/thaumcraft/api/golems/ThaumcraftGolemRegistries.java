package thaumcraft.api.golems;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.RegistryBuilder;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.api.golems.parts.GolemMaterial;
import thaumcraft.api.golems.parts.GolemHead;
import thaumcraft.api.golems.parts.GolemArm;
import thaumcraft.api.golems.parts.GolemLeg;
import thaumcraft.api.golems.parts.GolemAddon;

public class ThaumcraftGolemRegistries {
    public static final ResourceKey<Registry<GolemMaterial>> MATERIAL_REG_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath("thaumcraft", "golem_material"));
    public static final ResourceKey<Registry<EnumGolemTrait>> TRAIT_REG_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath("thaumcraft", "golem_trait"));
    public static final ResourceKey<Registry<GolemHead>> HEAD_REG_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath("thaumcraft", "golem_head"));
    public static final ResourceKey<Registry<GolemArm>> ARM_REG_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath("thaumcraft", "golem_arm"));
    public static final ResourceKey<Registry<GolemLeg>> LEG_REG_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath("thaumcraft", "golem_leg"));
    public static final ResourceKey<Registry<GolemAddon>> ADDON_REG_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath("thaumcraft", "golem_addon"));

    public static final DeferredRegister<GolemMaterial> GOLEM_MATERIALS = DeferredRegister.create(MATERIAL_REG_KEY, "thaumcraft");
    public static final DeferredRegister<EnumGolemTrait> GOLEM_TRAITS = DeferredRegister.create(TRAIT_REG_KEY, "thaumcraft");
    public static final DeferredRegister<GolemHead> GOLEM_HEADS = DeferredRegister.create(HEAD_REG_KEY, "thaumcraft");
    public static final DeferredRegister<GolemArm> GOLEM_ARMS = DeferredRegister.create(ARM_REG_KEY, "thaumcraft");
    public static final DeferredRegister<GolemLeg> GOLEM_LEGS = DeferredRegister.create(LEG_REG_KEY, "thaumcraft");
    public static final DeferredRegister<GolemAddon> GOLEM_ADDONS = DeferredRegister.create(ADDON_REG_KEY, "thaumcraft");

    public static void register(IEventBus modBus) {
        GOLEM_MATERIALS.register(modBus);
        GOLEM_TRAITS.register(modBus);
        GOLEM_HEADS.register(modBus);
        GOLEM_ARMS.register(modBus);
        GOLEM_LEGS.register(modBus);
        GOLEM_ADDONS.register(modBus);
        modBus.addListener(ThaumcraftGolemRegistries::onNewRegistry);
        EnumGolemTrait.init();
    }

    private static void onNewRegistry(NewRegistryEvent event) {
        event.register(new RegistryBuilder<>(MATERIAL_REG_KEY).sync(true).create());
        event.register(new RegistryBuilder<>(TRAIT_REG_KEY).sync(true).create());
        event.register(new RegistryBuilder<>(HEAD_REG_KEY).sync(true).create());
        event.register(new RegistryBuilder<>(ARM_REG_KEY).sync(true).create());
        event.register(new RegistryBuilder<>(LEG_REG_KEY).sync(true).create());
        event.register(new RegistryBuilder<>(ADDON_REG_KEY).sync(true).create());
    }
}
