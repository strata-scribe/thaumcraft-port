package thaumcraft.common.items.tools;

import net.minecraft.world.item.Item;
import thaumcraft.common.items.tools.logic.ThaumcraftToolLogic;

/**
 * Scribing Tools:
 * - Used at the Research Table for writing research and theorycraft notes.
 * - Stacks to 1, durability 100.
 */
public class ItemScribingTools extends Item {

    public ItemScribingTools(Properties properties) {
        super(properties);
    }

    public ItemScribingTools() {
        this(new Item.Properties().stacksTo(1).durability(ThaumcraftToolLogic.getScribingToolsDurability()));
    }
}
