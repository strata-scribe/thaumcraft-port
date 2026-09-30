package thaumcraft.common.items.armor;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;
import thaumcraft.api.items.IWarpingGear;

/**
 * Crimson Praetor Armor:
 * - Worn by Crimson Praetors
 * - Implements IWarpingGear (+2 warp per worn piece)
 * - Durability multiplier 30
 */
public class ItemCrimsonPraetorArmor extends Item implements IWarpingGear {

    private final ArmorType armorType;

    public ItemCrimsonPraetorArmor(ArmorType armorType, Properties properties) {
        super(properties);
        this.armorType = armorType;
    }

    public ItemCrimsonPraetorArmor(ArmorType armorType) {
        this(armorType, new Item.Properties()
                .stacksTo(1)
                .durability(armorType.getDurability(30))
                .component(DataComponents.EQUIPPABLE, Equippable.builder(armorType.getSlot()).build()));
    }

    public ItemCrimsonPraetorArmor(Properties properties) {
        this(ArmorType.CHESTPLATE, properties);
    }

    public ItemCrimsonPraetorArmor() {
        this(ArmorType.CHESTPLATE);
    }

    public ArmorType getArmorType() {
        return this.armorType;
    }

    @Override
    public int getWarp(ItemStack itemstack, Player player) {
        return 2;
    }
}
