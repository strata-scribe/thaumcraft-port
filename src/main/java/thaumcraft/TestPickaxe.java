package thaumcraft;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class TestPickaxe {
    public static void check() {
        System.out.println("TEST PICKAXE: " + BuiltInRegistries.ITEM.getOptional(Identifier.parse("minecraft:iron_pickaxe")));
    }
}
