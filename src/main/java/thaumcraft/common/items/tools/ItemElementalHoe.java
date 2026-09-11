package thaumcraft.common.items.tools;

import net.minecraft.world.item.Item;

/**
 * Hoe of the Growth:
 * - Accelerates crop growth across an expanded area similar to bonemeal.
 */
public class ItemElementalHoe extends Item {

    public ItemElementalHoe(Properties properties) {
        super(properties);
    }

    public ItemElementalHoe() {
        this(new Item.Properties().stacksTo(1).durability(1500));
    }
}
