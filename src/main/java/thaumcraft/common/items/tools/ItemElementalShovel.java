package thaumcraft.common.items.tools;

import net.minecraft.world.item.Item;

/**
 * Shovel of the Earthmover:
 * - Excavates and places 3x3 planar earth/gravel/sand grids aligned to the clicked block face.
 */
public class ItemElementalShovel extends Item {

    public ItemElementalShovel(Properties properties) {
        super(properties);
    }

    public ItemElementalShovel() {
        this(new Item.Properties().stacksTo(1).durability(1500));
    }
}
