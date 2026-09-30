package thaumcraft.common.items.tools;

import net.minecraft.world.item.Item;
import thaumcraft.common.items.tools.logic.ThaumcraftToolLogic;

/**
 * Thaumium Shovel:
 * - High durability magical shovel crafted from Thaumium metal.
 * - Stacks to 1, durability 500.
 */
public class ItemThaumiumShovel extends Item {

    public ItemThaumiumShovel(Properties properties) {
        super(properties);
    }

    public ItemThaumiumShovel() {
        this(new Item.Properties().stacksTo(1).durability(ThaumcraftToolLogic.getThaumiumDurability()));
    }
}
