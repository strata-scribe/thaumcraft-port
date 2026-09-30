package thaumcraft.data.recipes;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.blocks.ThaumcraftBlocks;
import thaumcraft.api.items.ThaumcraftItems;

public class ArcaneCraftingRecipeProvider extends RecipeProvider {

    public static final int RECIPE_COUNT = 4;

    public ArcaneCraftingRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        // Arcane Workbench: shaped recipe yielding ThaumcraftBlocks.arcaneWorkbench
        ArcaneCraftingRecipeBuilder.shaped(ThaumcraftBlocks.arcaneWorkbench)
            .research("FIRSTSTEPS")
            .vis(0)
            .pattern(" S ")
            .pattern(" W ")
            .define('S', ThaumcraftItems.salisMundus)
            .define('W', Blocks.CRAFTING_TABLE)
            .save(this.output, Identifier.fromNamespaceAndPath("thaumcraft", "arcane_workbench"));

        // Thaumometer: shaped recipe yielding ThaumcraftItems.thaumometer
        ArcaneCraftingRecipeBuilder.shaped(ThaumcraftItems.thaumometer)
            .research("FIRSTSTEPS@2")
            .vis(20)
            .crystal(Aspect.AIR, 1)
            .crystal(Aspect.FIRE, 1)
            .crystal(Aspect.WATER, 1)
            .crystal(Aspect.EARTH, 1)
            .crystal(Aspect.ORDER, 1)
            .crystal(Aspect.ENTROPY, 1)
            .pattern(" G ")
            .pattern("GPG")
            .pattern(" G ")
            .define('G', Items.GOLD_INGOT)
            .define('P', Items.GLASS_PANE)
            .save(this.output, Identifier.fromNamespaceAndPath("thaumcraft", "thaumometer"));

        // Goggles of Revealing: shaped recipe yielding ThaumcraftItems.goggles
        ArcaneCraftingRecipeBuilder.shaped(ThaumcraftItems.goggles)
            .research("BASEARTIFICE")
            .vis(50)
            .pattern(" L ")
            .pattern("T T")
            .define('L', Items.LEATHER)
            .define('T', ThaumcraftItems.thaumometer)
            .save(this.output, Identifier.fromNamespaceAndPath("thaumcraft", "goggles"));

        // Scribing Tools: shapeless recipe yielding ThaumcraftItems.scribingTools
        ArcaneCraftingRecipeBuilder.shapeless(ThaumcraftItems.scribingTools)
            .research("THEORYRESEARCH")
            .vis(0)
            .requires(Items.FEATHER)
            .requires(Items.INK_SAC)
            .requires(Items.GLASS_BOTTLE)
            .save(this.output, Identifier.fromNamespaceAndPath("thaumcraft", "scribing_tools"));
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new ArcaneCraftingRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "Thaumcraft Arcane Crafting Recipes";
        }
    }
}
