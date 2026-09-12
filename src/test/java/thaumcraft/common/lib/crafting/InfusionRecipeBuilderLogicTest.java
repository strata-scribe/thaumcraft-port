package thaumcraft.common.lib.crafting;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InfusionRecipeBuilderLogicTest {

    @Test
    public void testValidRecipe() {
        InfusionRecipeBuilderLogic.InfusionRecipeData recipe = new InfusionRecipeBuilderLogic()
            .name("boots_traveller")
            .research("BOOTS_TRAVELLER")
            .centralItem("minecraft:leather_boots")
            .outputItem("thaumcraft:traveller_boots")
            .instability(4)
            .addAspect("aer", 20)
            .addAspect("volatus", 20)
            .addAspect("iter", 20)
            .addIngredient("thaumcraft:fabric")
            .addIngredient("thaumcraft:fabric")
            .addIngredient("minecraft:feather")
            .addIngredient("minecraft:fish")
            .build();

        assertEquals("boots_traveller", recipe.name);
        assertEquals("BOOTS_TRAVELLER", recipe.research);
        assertEquals("minecraft:leather_boots", recipe.centralItem);
        assertEquals("thaumcraft:traveller_boots", recipe.outputItem);
        assertEquals(4, recipe.instability);
        assertEquals(3, recipe.aspects.size());
        assertEquals(20, recipe.aspects.get("aer"));
        assertEquals(4, recipe.ingredients.size());
        assertEquals("thaumcraft:fabric", recipe.ingredients.get(0));
    }

    @Test
    public void testMissingName() {
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> {
            new InfusionRecipeBuilderLogic()
                .research("BOOTS_TRAVELLER")
                .centralItem("minecraft:leather_boots")
                .outputItem("thaumcraft:traveller_boots")
                .instability(4)
                .addAspect("aer", 20)
                .addIngredient("thaumcraft:fabric")
                .build();
        });
        assertEquals("Recipe name is required", e.getMessage());
    }

    @Test
    public void testMissingResearch() {
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> {
            new InfusionRecipeBuilderLogic()
                .name("boots_traveller")
                .centralItem("minecraft:leather_boots")
                .outputItem("thaumcraft:traveller_boots")
                .instability(4)
                .addAspect("aer", 20)
                .addIngredient("thaumcraft:fabric")
                .build();
        });
        assertEquals("Research is required", e.getMessage());
    }

    @Test
    public void testMissingCentralItem() {
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> {
            new InfusionRecipeBuilderLogic()
                .name("boots_traveller")
                .research("BOOTS_TRAVELLER")
                .outputItem("thaumcraft:traveller_boots")
                .instability(4)
                .addAspect("aer", 20)
                .addIngredient("thaumcraft:fabric")
                .build();
        });
        assertEquals("Central item is required", e.getMessage());
    }

    @Test
    public void testMissingOutputItem() {
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> {
            new InfusionRecipeBuilderLogic()
                .name("boots_traveller")
                .research("BOOTS_TRAVELLER")
                .centralItem("minecraft:leather_boots")
                .instability(4)
                .addAspect("aer", 20)
                .addIngredient("thaumcraft:fabric")
                .build();
        });
        assertEquals("Output item is required", e.getMessage());
    }

    @Test
    public void testNegativeInstability() {
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> {
            new InfusionRecipeBuilderLogic()
                .name("boots_traveller")
                .research("BOOTS_TRAVELLER")
                .centralItem("minecraft:leather_boots")
                .outputItem("thaumcraft:traveller_boots")
                .instability(-1)
                .addAspect("aer", 20)
                .addIngredient("thaumcraft:fabric")
                .build();
        });
        assertEquals("Instability cannot be negative", e.getMessage());
    }

    @Test
    public void testMissingAspects() {
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> {
            new InfusionRecipeBuilderLogic()
                .name("boots_traveller")
                .research("BOOTS_TRAVELLER")
                .centralItem("minecraft:leather_boots")
                .outputItem("thaumcraft:traveller_boots")
                .instability(4)
                .addIngredient("thaumcraft:fabric")
                .build();
        });
        assertEquals("At least one aspect is required", e.getMessage());
    }

    @Test
    public void testMissingIngredients() {
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> {
            new InfusionRecipeBuilderLogic()
                .name("boots_traveller")
                .research("BOOTS_TRAVELLER")
                .centralItem("minecraft:leather_boots")
                .outputItem("thaumcraft:traveller_boots")
                .instability(4)
                .addAspect("aer", 20)
                .build();
        });
        assertEquals("At least one ingredient is required", e.getMessage());
    }
}
