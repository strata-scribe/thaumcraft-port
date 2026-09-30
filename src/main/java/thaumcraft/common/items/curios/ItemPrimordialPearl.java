package thaumcraft.common.items.curios;

import net.minecraft.world.item.Item;

public class ItemPrimordialPearl extends Item {

    public ItemPrimordialPearl(Properties properties) {
        super(properties);
    }

    public ItemPrimordialPearl() {
        this(new Item.Properties().stacksTo(1).durability(8));
    }
}
