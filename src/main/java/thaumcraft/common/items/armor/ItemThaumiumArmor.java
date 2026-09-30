package thaumcraft.common.items.armor;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;
import thaumcraft.common.items.tools.logic.ThaumcraftToolLogic;

/**
 * Thaumium Armor (Helmet, Chestplate, Leggings, Boots):
 * - Forged from enchanted Thaumium metal.
 * - Non-warping, durability factor 25.
 */
public class ItemThaumiumArmor extends Item {

    private final ArmorType armorType;

    public ItemThaumiumArmor(ArmorType armorType, Properties properties) {
        super(properties);
        this.armorType = armorType;
    }

    public ItemThaumiumArmor(ArmorType armorType) {
        this(armorType, new Item.Properties()
                .stacksTo(1)
                .durability(armorType.getDurability(ThaumcraftToolLogic.getThaumiumArmorDurabilityFactor()))
                .component(DataComponents.EQUIPPABLE, Equippable.builder(armorType.getSlot()).build()));
    }

    public ItemThaumiumArmor(Properties properties) {
        this(ArmorType.CHESTPLATE, properties);
    }

    public ItemThaumiumArmor() {
        this(ArmorType.CHESTPLATE);
    }

    public ArmorType getArmorType() {
        return this.armorType;
    }
}
