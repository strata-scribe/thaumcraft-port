package thaumcraft.data.recipes;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import thaumcraft.common.lib.crafting.InfusionRecipeBuilderLogic;
import thaumcraft.common.lib.crafting.InfusionRecipeBuilderLogic.InfusionRecipeData;

public class InfusionRecipeProvider extends RecipeProvider {

    public InfusionRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        // Elemental Tools
        InfusionRecipeData elementalAxe = new InfusionRecipeBuilderLogic()
            .name("elemental_axe")
            .research("ELEMENTALTOOLS")
            .centralItem("thaumcraft:thaumium_axe")
            .outputItem("thaumcraft:elemental_axe")
            .instability(1)
            .addAspect("aqua", 15)
            .addAspect("instrumentum", 15)
            .addIngredient("minecraft:water_bucket")
            .addIngredient("thaumcraft:amber")
            .addIngredient("thaumcraft:salis_mundus")
            .build();

        InfusionRecipeData elementalPickaxe = new InfusionRecipeBuilderLogic()
            .name("elemental_pickaxe")
            .research("ELEMENTALTOOLS")
            .centralItem("thaumcraft:thaumium_pick")
            .outputItem("thaumcraft:elemental_pick")
            .instability(1)
            .addAspect("ignis", 15)
            .addAspect("sensus", 15)
            .addIngredient("minecraft:lava_bucket")
            .addIngredient("thaumcraft:amber")
            .addIngredient("thaumcraft:salis_mundus")
            .build();

        InfusionRecipeData elementalSword = new InfusionRecipeBuilderLogic()
            .name("elemental_sword")
            .research("ELEMENTALTOOLS")
            .centralItem("thaumcraft:thaumium_sword")
            .outputItem("thaumcraft:elemental_sword")
            .instability(1)
            .addAspect("aer", 15)
            .addAspect("motus", 15)
            .addIngredient("minecraft:feather")
            .addIngredient("thaumcraft:amber")
            .addIngredient("thaumcraft:salis_mundus")
            .build();

        InfusionRecipeData elementalShovel = new InfusionRecipeBuilderLogic()
            .name("elemental_shovel")
            .research("ELEMENTALTOOLS")
            .centralItem("thaumcraft:thaumium_shovel")
            .outputItem("thaumcraft:elemental_shovel")
            .instability(1)
            .addAspect("terra", 15)
            .addAspect("perditio", 15)
            .addIngredient("minecraft:flint")
            .addIngredient("thaumcraft:amber")
            .addIngredient("thaumcraft:salis_mundus")
            .build();

        InfusionRecipeData elementalHoe = new InfusionRecipeBuilderLogic()
            .name("elemental_hoe")
            .research("ELEMENTALTOOLS")
            .centralItem("thaumcraft:thaumium_hoe")
            .outputItem("thaumcraft:elemental_hoe")
            .instability(1)
            .addAspect("ordo", 15)
            .addAspect("herba", 15)
            .addIngredient("minecraft:wheat_seeds")
            .addIngredient("thaumcraft:amber")
            .addIngredient("thaumcraft:salis_mundus")
            .build();

        // Boots of the Traveller
        InfusionRecipeData bootsTraveller = new InfusionRecipeBuilderLogic()
            .name("boots_traveller")
            .research("BOOTSTRAVELLER")
            .centralItem("minecraft:leather_boots")
            .outputItem("thaumcraft:traveller_boots")
            .instability(4)
            .addAspect("aer", 20)
            .addAspect("volatus", 20)
            .addAspect("iter", 20)
            .addIngredient("thaumcraft:fabric")
            .addIngredient("thaumcraft:fabric")
            .addIngredient("minecraft:feather")
            .addIngredient("minecraft:salmon")
            .build();

        // Thaumostatic Harness
        InfusionRecipeData thaumostaticHarness = new InfusionRecipeBuilderLogic()
            .name("thaumostatic_harness")
            .research("THAUMOSTATICHARNESS")
            .centralItem("thaumcraft:thaumium_chestplate")
            .outputItem("thaumcraft:thaumostatic_harness")
            .instability(8)
            .addAspect("aer", 50)
            .addAspect("volatus", 50)
            .addAspect("motus", 50)
            .addAspect("machina", 50)
            .addIngredient("thaumcraft:fabric")
            .addIngredient("thaumcraft:fabric")
            .addIngredient("thaumcraft:mechanism_complex")
            .addIngredient("thaumcraft:ingot_brass")
            .addIngredient("thaumcraft:ingot_brass")
            .addIngredient("minecraft:feather")
            .build();

        InfusionRecipeBuilder.infusion(elementalAxe).save(this.output, Identifier.fromNamespaceAndPath("thaumcraft", "elemental_axe"));
        InfusionRecipeBuilder.infusion(elementalPickaxe).save(this.output, Identifier.fromNamespaceAndPath("thaumcraft", "elemental_pickaxe"));
        InfusionRecipeBuilder.infusion(elementalSword).save(this.output, Identifier.fromNamespaceAndPath("thaumcraft", "elemental_sword"));
        InfusionRecipeBuilder.infusion(elementalShovel).save(this.output, Identifier.fromNamespaceAndPath("thaumcraft", "elemental_shovel"));
        InfusionRecipeBuilder.infusion(elementalHoe).save(this.output, Identifier.fromNamespaceAndPath("thaumcraft", "elemental_hoe"));
        InfusionRecipeBuilder.infusion(bootsTraveller).save(this.output, Identifier.fromNamespaceAndPath("thaumcraft", "boots_traveller"));
        InfusionRecipeBuilder.infusion(thaumostaticHarness).save(this.output, Identifier.fromNamespaceAndPath("thaumcraft", "thaumostatic_harness"));
    }
}
