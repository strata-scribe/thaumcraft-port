package thaumcraft.data.recipes;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ArcaneCraftingRecipeProviderTest {

    @Test
    public void testClassStructure() throws Exception {
        assertTrue(RecipeProvider.class.isAssignableFrom(ArcaneCraftingRecipeProvider.class),
            "ArcaneCraftingRecipeProvider must extend RecipeProvider");

        Constructor<?> constructor = ArcaneCraftingRecipeProvider.class.getConstructor(
            HolderLookup.Provider.class,
            RecipeOutput.class
        );
        assertNotNull(constructor, "Constructor(HolderLookup.Provider, RecipeOutput) must exist");
        assertTrue(Modifier.isPublic(constructor.getModifiers()), "Constructor must be public");

        Method buildRecipesMethod = ArcaneCraftingRecipeProvider.class.getDeclaredMethod("buildRecipes");
        assertNotNull(buildRecipesMethod, "buildRecipes() must exist");
        assertEquals(Void.TYPE, buildRecipesMethod.getReturnType(), "buildRecipes() must return void");

        assertEquals(4, ArcaneCraftingRecipeProvider.RECIPE_COUNT,
            "RECIPE_COUNT must be 4 (Arcane Workbench, Thaumometer, Goggles of Revealing, Scribing Tools)");
    }

    @Test
    public void testRunnerClassStructure() throws Exception {
        assertTrue(RecipeProvider.Runner.class.isAssignableFrom(ArcaneCraftingRecipeProvider.Runner.class),
            "ArcaneCraftingRecipeProvider.Runner must extend RecipeProvider.Runner");

        Constructor<?> runnerConstructor = ArcaneCraftingRecipeProvider.Runner.class.getConstructor(
            PackOutput.class,
            CompletableFuture.class
        );
        assertNotNull(runnerConstructor, "Runner constructor(PackOutput, CompletableFuture) must exist");

        ArcaneCraftingRecipeProvider.Runner runner = new ArcaneCraftingRecipeProvider.Runner(null, null);
        assertNotNull(runner.getName(), "Runner.getName() must not be null");
        assertTrue(runner.getName().contains("Arcane Crafting"),
            "Runner name must mention Arcane Crafting");

        Method createRecipeProviderMethod = ArcaneCraftingRecipeProvider.Runner.class.getDeclaredMethod(
            "createRecipeProvider",
            HolderLookup.Provider.class,
            RecipeOutput.class
        );
        assertNotNull(createRecipeProviderMethod, "createRecipeProvider must exist on Runner");
    }
}
