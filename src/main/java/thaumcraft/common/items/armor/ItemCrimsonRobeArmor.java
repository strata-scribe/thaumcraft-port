package thaumcraft.common.items.armor;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;
import thaumcraft.api.items.IVisDiscountGear;
import thaumcraft.api.items.IWarpingGear;

/**
 * Crimson Robe Armor:
 * - Worn by Crimson Clerics
 * - Implements IWarpingGear (+1 warp per worn piece)
 * - Implements IVisDiscountGear (+5% vis discount per piece)
 * - Durability multiplier 17
 */
public class ItemCrimsonRobeArmor extends Item implements IWarpingGear, IVisDiscountGear {

    private final ArmorType armorType;

    public ItemCrimsonRobeArmor(ArmorType armorType, Properties properties) {
        super(properties);
        this.armorType = armorType;
    }

    public ItemCrimsonRobeArmor(ArmorType armorType) {
        this(armorType, new Item.Properties()
                .stacksTo(1)
                .durability(armorType.getDurability(17))
                .component(DataComponents.EQUIPPABLE, Equippable.builder(armorType.getSlot()).build()));
    }

    public ItemCrimsonRobeArmor(Properties properties) {
        this(ArmorType.CHESTPLATE, properties);
    }

    public ItemCrimsonRobeArmor() {
        this(ArmorType.CHESTPLATE);
    }

    public ArmorType getArmorType() {
        return this.armorType;
    }

    @Override
    public int getWarp(ItemStack itemstack, Player player) {
        return 1;
    }

    @Override
    public int getVisDiscount(ItemStack stack, Player player) {
        return 5;
    }
}
