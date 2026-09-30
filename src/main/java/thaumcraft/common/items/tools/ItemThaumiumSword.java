package thaumcraft.common.items.tools;

import net.minecraft.world.item.Item;
import thaumcraft.common.items.tools.logic.ThaumcraftToolLogic;

/**
 * Thaumium Sword:
 * - High durability magical sword crafted from Thaumium metal.
 * - Stacks to 1, durability 500.
 */
public class ItemThaumiumSword extends Item {

    public ItemThaumiumSword(Properties properties) {
        super(properties);
    }

    public ItemThaumiumSword() {
        this(new Item.Properties().stacksTo(1).durability(ThaumcraftToolLogic.getThaumiumDurability()));
    }
}
