package thaumcraft.data.recipes;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;

public class CrucibleRecipeProvider extends RecipeProvider {

    public CrucibleRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        CrucibleRecipeBuilder.crucible()
            .research("METALLURGY@1")
            .result("thaumcraft:ingot_brass")
            .catalyst("minecraft:stone")
            .addAspect("tool", 1)
            .save(this.output, Identifier.fromNamespaceAndPath("thaumcraft", "brassingot"));

        CrucibleRecipeBuilder.crucible()
            .research("METALLURGY@2")
            .result("thaumcraft:ingot_thaumium")
            .catalyst("minecraft:stone")
            .addAspect("tool", 1)
            .addAspect("magic", 1)
            .save(this.output, Identifier.fromNamespaceAndPath("thaumcraft", "thaumiumingot"));

        CrucibleRecipeBuilder.crucible()
            .research("ALUMENTUM")
            .result("thaumcraft:alumentum")
            .catalyst("minecraft:stone")
            .addAspect("aer", 1)
            .save(this.output, Identifier.fromNamespaceAndPath("thaumcraft", "alumentum"));

        CrucibleRecipeBuilder.crucible()
            .research("BASEALCHEMY")
            .result("thaumcraft:nitor_yellow")
            .catalyst("minecraft:stone")
            .addAspect("aer", 1)
            .save(this.output, Identifier.fromNamespaceAndPath("thaumcraft", "nitor"));
    }
}
