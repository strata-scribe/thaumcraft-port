package thaumcraft.data.recipes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thaumcraft.api.aspects.Aspect;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ArcaneCraftingRecipeBuilderTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void testShapedBuilder() throws Exception {
        // We will just unit test the builder logic using reflection
        // to bypass any instantiation of Minecraft classes.
        
        ArcaneCraftingRecipeBuilder.ShapedBuilder builder = new ArcaneCraftingRecipeBuilder.ShapedBuilder(null) {
            @Override
            public void save(net.minecraft.data.recipes.RecipeOutput output, net.minecraft.resources.Identifier id) {
                // not called in this limited test
            }
        };
        
        builder.group("test_group")
            .research("TEST_RESEARCH")
            .vis(50)
            .crystal(Aspect.FIRE, 2)
            .crystal(Aspect.ORDER, 1)
            .pattern(" D ")
            .pattern(" D ")
            .pattern(" S ")
            .define('D', (net.minecraft.world.item.crafting.Ingredient) null)
            .define('S', (net.minecraft.world.item.crafting.Ingredient) null);

        // Assert basic Builder fields
        Field groupF = ArcaneCraftingRecipeBuilder.Builder.class.getDeclaredField("group");
        groupF.setAccessible(true);
        assertEquals("test_group", groupF.get(builder));

        Field resF = ArcaneCraftingRecipeBuilder.Builder.class.getDeclaredField("research");
        resF.setAccessible(true);
        assertEquals("TEST_RESEARCH", resF.get(builder));

        Field visF = ArcaneCraftingRecipeBuilder.Builder.class.getDeclaredField("vis");
        visF.setAccessible(true);
        assertEquals(50, visF.get(builder));

        Field cryF = ArcaneCraftingRecipeBuilder.Builder.class.getDeclaredField("crystals");
        cryF.setAccessible(true);
        thaumcraft.api.aspects.AspectList crystals = (thaumcraft.api.aspects.AspectList) cryF.get(builder);
        assertNotNull(crystals);
        assertEquals(2, crystals.getAmount(Aspect.FIRE));
        assertEquals(1, crystals.getAmount(Aspect.ORDER));

        // Assert ShapedBuilder specific fields
        Field rowsF = ArcaneCraftingRecipeBuilder.ShapedBuilder.class.getDeclaredField("rows");
        rowsF.setAccessible(true);
        List<String> rows = (List<String>) rowsF.get(builder);
        assertEquals(3, rows.size());
        assertEquals(" D ", rows.get(0));
        assertEquals(" D ", rows.get(1));
        assertEquals(" S ", rows.get(2));

        Field keyF = ArcaneCraftingRecipeBuilder.ShapedBuilder.class.getDeclaredField("key");
        keyF.setAccessible(true);
        Map<Character, ?> key = (Map<Character, ?>) keyF.get(builder);
        assertTrue(key.containsKey('D'));
        assertTrue(key.containsKey('S'));
        
        // Check pattern invalidation
        assertThrows(IllegalArgumentException.class, () -> builder.pattern("  "));
        
        // Check define invalidation
        assertThrows(IllegalArgumentException.class, () -> builder.define(' ', (net.minecraft.world.item.crafting.Ingredient) null));
        assertThrows(IllegalArgumentException.class, () -> builder.define('D', (net.minecraft.world.item.crafting.Ingredient) null));
    }

    @Test
    void testShapelessBuilder() throws Exception {
        ArcaneCraftingRecipeBuilder.ShapelessBuilder builder = new ArcaneCraftingRecipeBuilder.ShapelessBuilder(null) {
            @Override
            public void save(net.minecraft.data.recipes.RecipeOutput output, net.minecraft.resources.Identifier id) {
                // not called in this limited test
            }
            
            // Bypass the real add, just test logic
            @Override
            public ArcaneCraftingRecipeBuilder.ShapelessBuilder requires(net.minecraft.world.item.crafting.Ingredient ingredient, int quantity) {
                try {
                    Field ingF = ArcaneCraftingRecipeBuilder.ShapelessBuilder.class.getDeclaredField("ingredients");
                    ingF.setAccessible(true);
                    List<net.minecraft.world.item.crafting.Ingredient> ingredients = (List<net.minecraft.world.item.crafting.Ingredient>) ingF.get(this);
                    
                    for (int i = 0; i < quantity; i++) {
                        // Just bypass adding real items to avoid FML/Registry bootstrap in tests
                    }
                    return this;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        
        builder.group("test_group_shapeless")
            .research("TEST_RESEARCH_2")
            .vis(100)
            .crystal(Aspect.EARTH, 5);

        // Assert basic Builder fields
        Field groupF = ArcaneCraftingRecipeBuilder.Builder.class.getDeclaredField("group");
        groupF.setAccessible(true);
        assertEquals("test_group_shapeless", groupF.get(builder));

        Field resF = ArcaneCraftingRecipeBuilder.Builder.class.getDeclaredField("research");
        resF.setAccessible(true);
        assertEquals("TEST_RESEARCH_2", resF.get(builder));

        Field visF = ArcaneCraftingRecipeBuilder.Builder.class.getDeclaredField("vis");
        visF.setAccessible(true);
        assertEquals(100, visF.get(builder));

        Field cryF = ArcaneCraftingRecipeBuilder.Builder.class.getDeclaredField("crystals");
        cryF.setAccessible(true);
        thaumcraft.api.aspects.AspectList crystals = (thaumcraft.api.aspects.AspectList) cryF.get(builder);
        assertNotNull(crystals);
        assertEquals(5, crystals.getAmount(Aspect.EARTH));
    }
}
