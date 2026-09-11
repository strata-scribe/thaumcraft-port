package thaumcraft.common.items.armor;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;
import thaumcraft.api.items.IGoggles;
import thaumcraft.api.items.IRevealer;
import thaumcraft.api.items.IVisDiscountGear;
import thaumcraft.api.items.IWarpingGear;

/**
 * Void Robe Armor (Helm, Chest, Legs):
 * - Infused with void magic and woven cloth
 * - Provides 5% Vis discount per piece (IVisDiscountGear)
 * - Built-in Goggles of Revealing on Helmet (IGoggles, IRevealer)
 * - Warping gear: +3 Warp per piece (IWarpingGear)
 * - Tick-based passive self-repair
 * - Dyeable with custom colors (default 0x6a3860 / 6961280)
 *
 * Mathematical rules are delegated to {@link EquipmentLogic}.
 */
public class ItemVoidRobeArmor extends Item implements IVisDiscountGear, IWarpingGear, IGoggles, IRevealer {

    public static final int DEFAULT_COLOR = 0x6a3860; // 6961280
    private final ArmorType armorType;

    public ItemVoidRobeArmor(ArmorType armorType, Properties properties) {
        super(properties);
        this.armorType = armorType;
    }

    public ItemVoidRobeArmor(ArmorType armorType) {
        this(armorType, new Item.Properties()
                .stacksTo(1)
                .durability(armorType.getDurability(18))
                .component(DataComponents.EQUIPPABLE, Equippable.builder(armorType.getSlot()).build())
                .component(DataComponents.DYED_COLOR, new DyedItemColor(DEFAULT_COLOR)));
    }

    public ItemVoidRobeArmor(Properties properties) {
        this(ArmorType.CHESTPLATE, properties);
    }

    public ItemVoidRobeArmor() {
        this(ArmorType.CHESTPLATE);
    }

    public ArmorType getArmorType() {
        return this.armorType;
    }

    @Override
    public int getVisDiscount(ItemStack stack, Player player) {
        return EquipmentLogic.calculateVisDiscount(1);
    }

    @Override
    public int getWarp(ItemStack itemstack, Player player) {
        return EquipmentLogic.getVoidRobeWarp();
    }

    @Override
    public boolean showIngamePopups(ItemStack itemstack, LivingEntity player) {
        return this.armorType == ArmorType.HELMET;
    }

    @Override
    public boolean showNodes(ItemStack itemstack, LivingEntity player) {
        return this.armorType == ArmorType.HELMET;
    }

    public boolean hasColor(ItemStack stack) {
        return stack != null && stack.has(DataComponents.DYED_COLOR);
    }

    public int getColor(ItemStack stack) {
        if (stack == null) return DEFAULT_COLOR;
        return DyedItemColor.getOrDefault(stack, DEFAULT_COLOR);
    }

    public void setColor(ItemStack stack, int color) {
        if (stack != null) {
            stack.set(DataComponents.DYED_COLOR, new DyedItemColor(color));
        }
    }

    public void removeColor(ItemStack stack) {
        if (stack != null) {
            stack.remove(DataComponents.DYED_COLOR);
        }
    }

    public float calculateDamageAbsorption(EquipmentLogic.DamageSourceType source, int totalArmor, float damage) {
        return EquipmentLogic.calculateArmorAbsorption(source, totalArmor, damage);
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
