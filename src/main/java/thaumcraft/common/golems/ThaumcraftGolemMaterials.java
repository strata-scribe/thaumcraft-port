package thaumcraft.common.golems;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.ThaumcraftGolemRegistries;
import thaumcraft.api.golems.parts.GolemMaterial;

public class ThaumcraftGolemMaterials {
    // - Wood: Agile, lightweight, lower health. (Traits: LIGHT, FRAGILE)
    public static final DeferredHolder<GolemMaterial, GolemMaterial> WOOD = ThaumcraftGolemRegistries.GOLEM_MATERIALS.register("wood",
        () -> new GolemMaterial("WOOD", new String[]{}, Identifier.fromNamespaceAndPath("thaumcraft", "textures/entity/golem_wood.png"),
                0, -2, 0, 0, ItemStack.EMPTY, ItemStack.EMPTY, new EnumGolemTrait[]{EnumGolemTrait.LIGHT.get(), EnumGolemTrait.FRAGILE.get()}));

    // - Clay: Fireproof, steady. (Traits: FIREPROOF, HEAVY)
    public static final DeferredHolder<GolemMaterial, GolemMaterial> CLAY = ThaumcraftGolemRegistries.GOLEM_MATERIALS.register("clay",
        () -> new GolemMaterial("CLAY", new String[]{}, Identifier.fromNamespaceAndPath("thaumcraft", "textures/entity/golem_clay.png"),
                0, 0, 0, 0, ItemStack.EMPTY, ItemStack.EMPTY, new EnumGolemTrait[]{EnumGolemTrait.FIREPROOF.get(), EnumGolemTrait.HEAVY.get()}));

    // - Iron: High armor, heavy, knockback resistant. (Traits: ARMORED, HEAVY, BRUTAL)
    public static final DeferredHolder<GolemMaterial, GolemMaterial> IRON = ThaumcraftGolemRegistries.GOLEM_MATERIALS.register("iron",
        () -> new GolemMaterial("IRON", new String[]{}, Identifier.fromNamespaceAndPath("thaumcraft", "textures/entity/golem_iron.png"),
                0, 2, 4, 2, ItemStack.EMPTY, ItemStack.EMPTY, new EnumGolemTrait[]{EnumGolemTrait.ARMORED.get(), EnumGolemTrait.HEAVY.get(), EnumGolemTrait.BRUTAL.get()}));

    // - Brass: Extra upgrade slots. (Traits: SMART)
    public static final DeferredHolder<GolemMaterial, GolemMaterial> BRASS = ThaumcraftGolemRegistries.GOLEM_MATERIALS.register("brass",
        () -> new GolemMaterial("BRASS", new String[]{}, Identifier.fromNamespaceAndPath("thaumcraft", "textures/entity/golem_brass.png"),
                0, 0, 1, 1, ItemStack.EMPTY, ItemStack.EMPTY, new EnumGolemTrait[]{EnumGolemTrait.SMART.get()}));

    // - Thaumium: High health, regenerating. (Traits: REPAIR, ARMORED)
    public static final DeferredHolder<GolemMaterial, GolemMaterial> THAUMIUM = ThaumcraftGolemRegistries.GOLEM_MATERIALS.register("thaumium",
        () -> new GolemMaterial("THAUMIUM", new String[]{}, Identifier.fromNamespaceAndPath("thaumcraft", "textures/entity/golem_thaumium.png"),
                0, 6, 2, 3, ItemStack.EMPTY, ItemStack.EMPTY, new EnumGolemTrait[]{EnumGolemTrait.REPAIR.get(), EnumGolemTrait.ARMORED.get()}));

    // - Void: Self-repairing, warp emission. (Traits: REPAIR)
    public static final DeferredHolder<GolemMaterial, GolemMaterial> VOID = ThaumcraftGolemRegistries.GOLEM_MATERIALS.register("void",
        () -> new GolemMaterial("VOID", new String[]{}, Identifier.fromNamespaceAndPath("thaumcraft", "textures/entity/golem_void.png"),
                0, 4, 1, 4, ItemStack.EMPTY, ItemStack.EMPTY, new EnumGolemTrait[]{EnumGolemTrait.REPAIR.get()}));

}
