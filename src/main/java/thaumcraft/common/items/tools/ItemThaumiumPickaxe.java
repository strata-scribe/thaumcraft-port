package thaumcraft.common.items.tools;

import net.minecraft.world.item.Item;
import thaumcraft.common.items.tools.logic.ThaumcraftToolLogic;

/**
 * Thaumium Pickaxe:
 * - High durability magical pickaxe crafted from Thaumium metal.
 * - Stacks to 1, durability 500.
 */
public class ItemThaumiumPickaxe extends Item {

    public ItemThaumiumPickaxe(Properties properties) {
        super(properties);
    }

    public ItemThaumiumPickaxe() {
        this(new Item.Properties().stacksTo(1).durability(ThaumcraftToolLogic.getThaumiumDurability()));
    }
}
