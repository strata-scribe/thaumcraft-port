package thaumcraft.common.items.consumables;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

/**
 * Triple Meat Treat:
 * - A hearty consumable crafted from three kinds of meat.
 * - Restores 6 hunger (nutrition) and 0.8f saturation modifier.
 */
public class ItemTripleMeatTreat extends Item {

    public ItemTripleMeatTreat(Properties properties) {
        super(properties);
    }

    public ItemTripleMeatTreat() {
        this(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationModifier(0.8f).build()));
    }
}
