package thaumcraft.common.items.armor;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;
import thaumcraft.api.items.IWarpingGear;

/**
 * Void Armor (Helmet, Chestplate, Leggings, Boots):
 * - Crafted from Void Metal
 * - Passive tick-based self-repair (repairs 1 durability every 20 ticks)
 * - Implements IWarpingGear (+1 warp per worn piece)
 *
 * Mathematical rules are delegated to {@link EquipmentLogic}.
 */
public class ItemVoidArmor extends Item implements IWarpingGear {

    private final ArmorType armorType;

    public ItemVoidArmor(ArmorType armorType, Properties properties) {
        super(properties);
        this.armorType = armorType;
    }

    public ItemVoidArmor(ArmorType armorType) {
        this(armorType, new Item.Properties()
                .stacksTo(1)
                .durability(armorType.getDurability(15))
                .component(DataComponents.EQUIPPABLE, Equippable.builder(armorType.getSlot()).build()));
    }

    public ItemVoidArmor(Properties properties) {
        this(ArmorType.CHESTPLATE, properties);
    }

    public ItemVoidArmor() {
        this(ArmorType.CHESTPLATE);
    }

    public ArmorType getArmorType() {
        return this.armorType;
    }

    @Override
    public int getWarp(ItemStack itemstack, Player player) {
        return EquipmentLogic.getVoidArmorWarp();
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        super.inventoryTick(itemStack, level, entity, slot);
        if (itemStack.isDamaged() && entity.tickCount % 20 == 0) {
            int newDamage = EquipmentLogic.calculateVoidRepair(itemStack.getDamageValue(), entity.tickCount);
            itemStack.setDamageValue(newDamage);
        }
    }
}
