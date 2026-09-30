package thaumcraft.common.items.tools;

import net.minecraft.world.item.Item;
import thaumcraft.common.items.tools.logic.ThaumcraftToolLogic;

/**
 * Thaumium Axe:
 * - High durability magical axe crafted from Thaumium metal.
 * - Stacks to 1, durability 500.
 */
public class ItemThaumiumAxe extends Item {

    public ItemThaumiumAxe(Properties properties) {
        super(properties);
    }

    public ItemThaumiumAxe() {
        this(new Item.Properties().stacksTo(1).durability(ThaumcraftToolLogic.getThaumiumDurability()));
    }
}
