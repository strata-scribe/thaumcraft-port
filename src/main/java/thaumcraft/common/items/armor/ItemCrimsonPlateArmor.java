package thaumcraft.common.items.armor;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;
import thaumcraft.api.items.IWarpingGear;

/**
 * Crimson Plate Armor:
 * - Worn by Crimson Knights
 * - Implements IWarpingGear (+1 warp per worn piece)
 * - Durability multiplier 20
 */
public class ItemCrimsonPlateArmor extends Item implements IWarpingGear {

    private final ArmorType armorType;

    public ItemCrimsonPlateArmor(ArmorType armorType, Properties properties) {
        super(properties);
        this.armorType = armorType;
    }

    public ItemCrimsonPlateArmor(ArmorType armorType) {
        this(armorType, new Item.Properties()
                .stacksTo(1)
                .durability(armorType.getDurability(20))
                .component(DataComponents.EQUIPPABLE, Equippable.builder(armorType.getSlot()).build()));
    }

    public ItemCrimsonPlateArmor(Properties properties) {
        this(ArmorType.CHESTPLATE, properties);
    }

    public ItemCrimsonPlateArmor() {
        this(ArmorType.CHESTPLATE);
    }

    public ArmorType getArmorType() {
        return this.armorType;
    }

    @Override
    public int getWarp(ItemStack itemstack, Player player) {
        return 1;
    }
}
