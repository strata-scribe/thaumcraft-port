package thaumcraft.data;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.data.recipes.ArcaneCraftingRecipeProvider;
import thaumcraft.data.recipes.CrucibleRecipeProvider;
import thaumcraft.data.recipes.InfusionRecipeProvider;
import thaumcraft.data.tags.ThaumcraftBiomeTagProvider;
import thaumcraft.data.tags.ThaumcraftBlockTagProvider;

@EventBusSubscriber(modid = Thaumcraft.MODID)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        // Recipe providers
        generator.addProvider(true, new ArcaneCraftingRecipeProvider.Runner(packOutput, lookupProvider));
        generator.addProvider(true, new RecipeProvider.Runner(packOutput, lookupProvider) {
            @Override
            protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
                return new CrucibleRecipeProvider(registries, output);
            }

            @Override
            public String getName() {
                return "Thaumcraft Crucible Recipes";
            }
        });
        generator.addProvider(true, new RecipeProvider.Runner(packOutput, lookupProvider) {
            @Override
            protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
                return new InfusionRecipeProvider(registries, output);
            }

            @Override
            public String getName() {
                return "Thaumcraft Infusion Recipes";
            }
        });

        // Tag providers
        ThaumcraftBlockTagProvider blockTags = new ThaumcraftBlockTagProvider(packOutput, lookupProvider);
        generator.addProvider(true, blockTags);
        generator.addProvider(true, new ThaumcraftBiomeTagProvider(packOutput, lookupProvider));
    }
}
