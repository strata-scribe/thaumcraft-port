package thaumcraft.common.items.tools;

import net.minecraft.world.item.Item;

/**
 * Pickaxe of the Core:
 * - Detects rare subterranean ores via sounding pulses.
 * - Yields bonus native clusters during ore excavation.
 */
public class ItemElementalPickaxe extends Item {

    public ItemElementalPickaxe(Properties properties) {
        super(properties);
    }

    public ItemElementalPickaxe() {
        this(new Item.Properties().stacksTo(1).durability(1500));
    }
}
