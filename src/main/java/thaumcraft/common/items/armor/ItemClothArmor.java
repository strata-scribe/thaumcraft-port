package thaumcraft.common.items.armor;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;
import thaumcraft.api.items.IVisDiscountGear;

/**
 * Cloth Armor (Thaumaturge's Robes):
 * - Robe Chestplate, Leggings, Boots
 * - Dyed cloth armor with default purple color 0x6a3860
 * - Implements IVisDiscountGear (+3% vis discount per piece)
 * - Durability multiplier 8
 */
public class ItemClothArmor extends Item implements IVisDiscountGear {

    private final ArmorType armorType;

    public ItemClothArmor(ArmorType armorType, Properties properties) {
        super(properties);
        this.armorType = armorType;
    }

    public ItemClothArmor(ArmorType armorType) {
        this(armorType, new Item.Properties()
                .stacksTo(1)
                .durability(armorType.getDurability(8))
                .component(DataComponents.EQUIPPABLE, Equippable.builder(armorType.getSlot()).build())
                .component(DataComponents.DYED_COLOR, new DyedItemColor(ClothArmorLogic.getDefaultClothColor())));
    }

    public ItemClothArmor(Properties properties) {
        this(ArmorType.CHESTPLATE, properties);
    }

    public ItemClothArmor() {
        this(ArmorType.CHESTPLATE);
    }

    public ArmorType getArmorType() {
        return this.armorType;
    }

    @Override
    public int getVisDiscount(ItemStack stack, Player player) {
        return ClothArmorLogic.calculateClothVisDiscount(1);
    }
}
