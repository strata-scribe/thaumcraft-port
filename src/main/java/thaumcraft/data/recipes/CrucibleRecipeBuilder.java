package thaumcraft.data.recipes;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import thaumcraft.api.crafting.CrucibleRecipeJsonLogic.CrucibleRecipeData;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;

import java.util.LinkedHashMap;
import java.util.Map;

public class CrucibleRecipeBuilder {
    private String research;
    private String group;
    private String result;
    private String catalyst;
    private Map<String, Integer> aspects = new LinkedHashMap<>();

    public static CrucibleRecipeBuilder crucible() {
        return new CrucibleRecipeBuilder();
    }

    public CrucibleRecipeBuilder research(String research) {
        this.research = research;
        return this;
    }

    public CrucibleRecipeBuilder group(String group) {
        this.group = group;
        return this;
    }

    public CrucibleRecipeBuilder result(String result) {
        this.result = result;
        return this;
    }

    public CrucibleRecipeBuilder catalyst(String catalyst) {
        this.catalyst = catalyst;
        return this;
    }

    public CrucibleRecipeBuilder addAspect(String aspect, int amount) {
        this.aspects.put(aspect, amount);
        return this;
    }

    public void save(RecipeOutput recipeOutput, Identifier id) {
        CrucibleRecipeData<String, String> data = new CrucibleRecipeData<>(research, group, result, catalyst, aspects);
        recipeOutput.accept(
            ResourceKey.create(Registries.RECIPE, id),
            new DummyCrucibleRecipe(data),
            null
        );
    }

    public static class DummyCrucibleRecipe extends CustomRecipe {
        public final CrucibleRecipeData<String, String> recipeData;

        public DummyCrucibleRecipe(CrucibleRecipeData<String, String> recipeData) {
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
