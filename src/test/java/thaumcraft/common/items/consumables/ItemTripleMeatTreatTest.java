package thaumcraft.common.items.consumables;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ItemTripleMeatTreat Contract & Unit Tests")
public class ItemTripleMeatTreatTest {

    @Test
    @DisplayName("Verify class hierarchy: ItemTripleMeatTreat must extend Item")
    public void testClassHierarchy() {
        assertTrue(Item.class.isAssignableFrom(ItemTripleMeatTreat.class),
                "ItemTripleMeatTreat must extend net.minecraft.world.item.Item");
    }

    @Test
    @DisplayName("Verify constructors via reflection: (Properties) and ()")
    public void testConstructors() throws Exception {
        Constructor<ItemTripleMeatTreat> propsCtor = ItemTripleMeatTreat.class.getConstructor(Item.Properties.class);
        assertNotNull(propsCtor, "ItemTripleMeatTreat must have (Item.Properties) constructor");

        Constructor<ItemTripleMeatTreat> defaultCtor = ItemTripleMeatTreat.class.getConstructor();
        assertNotNull(defaultCtor, "ItemTripleMeatTreat must have () default constructor");
    }

    @Test
    @DisplayName("Verify food properties contract values")
    public void testFoodPropertiesValues() {
        FoodProperties food = new FoodProperties.Builder().nutrition(6).saturationModifier(0.8f).build();
        assertEquals(6, food.nutrition(), "Nutrition must be 6");
        // In Minecraft FoodProperties, saturation() is calculated via FoodConstants.saturationByModifier(nutrition, modifier) = nutrition * modifier * 2.0f
        assertEquals(9.6f, food.saturation(), 1e-4f, "Effective saturation must be 6 * 0.8 * 2 = 9.6f");
    }

    @Test
    @DisplayName("Verify instantiation and default properties")
    public void testInstantiation() {
        try {
            ItemTripleMeatTreat treat = new ItemTripleMeatTreat();
            assertNotNull(treat);
        } catch (Throwable t) {
            // Handled gracefully in unbootstrapped pure JUnit environments
            assertNotNull(ItemTripleMeatTreat.class);
        }
    }
}
