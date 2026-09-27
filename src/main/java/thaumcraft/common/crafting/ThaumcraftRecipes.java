package thaumcraft.common.crafting;

import java.util.function.Supplier;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.crafting.ShapelessArcaneRecipe;

public class ThaumcraftRecipes {

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, Thaumcraft.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, Thaumcraft.MODID);

    public static final Supplier<RecipeType<IArcaneRecipe>> ARCANE_CRAFTING = RECIPE_TYPES.register("arcane_crafting", () -> RecipeType.simple(Identifier.fromNamespaceAndPath(Thaumcraft.MODID, "arcane_crafting")));

    public static final Supplier<RecipeSerializer<ShapelessArcaneRecipe>> ARCANE_SHAPELESS = RECIPE_SERIALIZERS.register("arcane_shapeless", () -> new RecipeSerializer<>(ShapelessArcaneRecipe.CODEC, ShapelessArcaneRecipe.STREAM_CODEC));

    public static final Supplier<RecipeSerializer<ShapedArcaneRecipe>> ARCANE_SHAPED = RECIPE_SERIALIZERS.register("arcane_shaped", () -> new RecipeSerializer<>(ShapedArcaneRecipe.CODEC, ShapedArcaneRecipe.STREAM_CODEC));

}
