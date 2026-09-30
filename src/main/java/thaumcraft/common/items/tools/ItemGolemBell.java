package thaumcraft.common.items.tools;

import net.minecraft.world.item.Item;

/**
 * Golem Bell (Golemancer's Bell):
 * - Used to command and configure Thaumcraft golems and seals.
 * - Stacks to 1.
 */
public class ItemGolemBell extends Item {

    public ItemGolemBell(Properties properties) {
        super(properties);
    }

    public ItemGolemBell() {
        this(new Item.Properties().stacksTo(1));
    }
}
