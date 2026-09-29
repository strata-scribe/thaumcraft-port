package thaumcraft.data.recipes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.crafting.ShapelessArcaneRecipe;

public class ArcaneCraftingRecipeBuilder {

    public static ShapedBuilder shaped(ItemLike result) {
        return shaped(result, 1);
    }
    
    public static ShapedBuilder shaped(ItemLike result, int count) {
        return new ShapedBuilder(new ItemStack(result.asItem(), count));
    }
    
    public static ShapelessBuilder shapeless(ItemLike result) {
        return shapeless(result, 1);
    }
    
    public static ShapelessBuilder shapeless(ItemLike result, int count) {
        return new ShapelessBuilder(new ItemStack(result.asItem(), count));
    }
    
    public static abstract class Builder<T extends Builder<T>> {
        protected final ItemStack result;
        protected String group = "";
        protected String research = "";
        protected int vis = 0;
        protected final AspectList crystals = new AspectList();
        
        public Builder(ItemStack result) {
            this.result = result;
        }
        
        @SuppressWarnings("unchecked")
        public T group(String group) {
            this.group = group;
            return (T) this;
        }
        
        @SuppressWarnings("unchecked")
        public T research(String research) {
            this.research = research;
            return (T) this;
        }
        
        @SuppressWarnings("unchecked")
        public T vis(int vis) {
            this.vis = vis;
            return (T) this;
        }
        
        @SuppressWarnings("unchecked")
        public T crystal(Aspect aspect, int amount) {
            this.crystals.add(aspect, amount);
            return (T) this;
        }
        
        public abstract void save(RecipeOutput output, Identifier id);
    }
    
    public static class ShapedBuilder extends Builder<ShapedBuilder> {
        private final List<String> rows = new ArrayList<>();
        private final Map<Character, Ingredient> key = new LinkedHashMap<>();
        
        public ShapedBuilder(ItemStack result) {
            super(result);
        }
        
        public ShapedBuilder pattern(String pattern) {
            if (!this.rows.isEmpty() && pattern.length() != this.rows.get(0).length()) {
                throw new IllegalArgumentException("Pattern must be same width on every line!");
            }
            this.rows.add(pattern);
            return this;
        }
        
        public ShapedBuilder define(Character symbol, Ingredient ingredient) {
            if (this.key.containsKey(symbol)) {
                throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined!");
            }
            if (symbol == ' ') {
                throw new IllegalArgumentException("Symbol ' ' (space) is reserved and cannot be defined");
            }
            this.key.put(symbol, ingredient);
            return this;
        }
        
        public ShapedBuilder define(Character symbol, ItemLike item) {
            return define(symbol, Ingredient.of(item));
        }

        @Override
        public void save(RecipeOutput output, Identifier id) {
            if (this.rows.isEmpty()) {
                throw new IllegalStateException("No pattern is defined for shaped recipe " + id + "!");
            }
            
            int height = this.rows.size();
            int width = this.rows.get(0).length();
            
            NonNullList<Ingredient> ingredients = NonNullList.withSize(width * height, Ingredient.of());
            for (int r = 0; r < height; r++) {
                String row = this.rows.get(r);
                for (int c = 0; c < width; c++) {
                    char symbol = row.charAt(c);
                    if (symbol != ' ') {
                        Ingredient ing = this.key.get(symbol);
                        if (ing == null) {
                            throw new IllegalStateException("Pattern references symbol '" + symbol + "' but it's not defined in the key");
                        }
                        ingredients.set(r * width + c, ing);
                    }
                }
            }
            
            ShapedArcaneRecipe recipe = new ShapedArcaneRecipe(
                this.group.isEmpty() ? null : Identifier.tryParse(this.group),
                this.research,
                this.vis,
                this.crystals.size() > 0 ? this.crystals : null,
                width,
                height,
                ingredients,
                this.result
            );
            
            output.accept(ResourceKey.create(Registries.RECIPE, id), recipe, null);
        }
    }
    
    public static class ShapelessBuilder extends Builder<ShapelessBuilder> {
        private final NonNullList<Ingredient> ingredients = NonNullList.create();
        
        public ShapelessBuilder(ItemStack result) {
            super(result);
        }
        
        public ShapelessBuilder requires(Ingredient ingredient) {
            return requires(ingredient, 1);
        }
        
        public ShapelessBuilder requires(Ingredient ingredient, int quantity) {
            for(int i = 0; i < quantity; ++i) {
                this.ingredients.add(ingredient);
            }
            return this;
        }
        
        public ShapelessBuilder requires(ItemLike item) {
            return requires(item, 1);
        }
        
        public ShapelessBuilder requires(ItemLike item, int quantity) {
            return requires(Ingredient.of(item), quantity);
        }

        @Override
        public void save(RecipeOutput output, Identifier id) {
            if (this.ingredients.isEmpty()) {
                throw new IllegalStateException("No ingredients for shapeless recipe " + id);
            }
            
            ShapelessArcaneRecipe recipe = new ShapelessArcaneRecipe(
                this.group.isEmpty() ? null : Identifier.tryParse(this.group),
                this.research,
                this.vis,
                this.crystals.size() > 0 ? this.crystals : null,
                this.ingredients,
                this.result
            );
            
            output.accept(ResourceKey.create(Registries.RECIPE, id), recipe, null);
        }
    }
}
