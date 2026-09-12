package thaumcraft.data.recipes;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import thaumcraft.common.lib.crafting.InfusionRecipeBuilderLogic.InfusionRecipeData;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.advancements.AdvancementHolder;
import org.jetbrains.annotations.Nullable;

public class InfusionRecipeBuilder {

    private final InfusionRecipeData recipeData;

    public InfusionRecipeBuilder(InfusionRecipeData recipeData) {
        this.recipeData = recipeData;
    }

    public static InfusionRecipeBuilder infusion(InfusionRecipeData recipeData) {
        return new InfusionRecipeBuilder(recipeData);
    }

    public void save(RecipeOutput recipeOutput, Identifier id) {
        // Output a CustomRecipe to fulfill datagen RecipeOutput
        recipeOutput.accept(
            ResourceKey.create(Registries.RECIPE, id),
            new DummyInfusionRecipe(recipeData),
            null
        );
    }

    public static class DummyInfusionRecipe extends CustomRecipe {
        public final InfusionRecipeData recipeData;

        public DummyInfusionRecipe(InfusionRecipeData recipeData) {
            this.recipeData = recipeData;
        }

        @Override
        public RecipeSerializer<? extends CustomRecipe> getSerializer() {
            return null;
        }

        @Override
        public boolean matches(CraftingInput input, Level level) {
            return false;
        }

        @Override
        public ItemStack assemble(CraftingInput input) {
            return ItemStack.EMPTY;
        }
    }
}
