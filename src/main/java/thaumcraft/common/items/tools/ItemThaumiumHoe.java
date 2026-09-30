package thaumcraft.common.items.tools;

import net.minecraft.world.item.Item;
import thaumcraft.common.items.tools.logic.ThaumcraftToolLogic;

/**
 * Thaumium Hoe:
 * - High durability magical hoe crafted from Thaumium metal.
 * - Stacks to 1, durability 500.
 */
public class ItemThaumiumHoe extends Item {

    public ItemThaumiumHoe(Properties properties) {
        super(properties);
    }

    public ItemThaumiumHoe() {
        this(new Item.Properties().stacksTo(1).durability(ThaumcraftToolLogic.getThaumiumDurability()));
    }
}
