package thaumcraft.common.items.armor;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;
import thaumcraft.api.items.IGoggles;
import thaumcraft.api.items.IRevealer;

public class ItemGoggles extends Item implements IGoggles, IRevealer {

    public ItemGoggles(Properties properties) {
        super(properties.equippable(EquipmentSlot.HEAD));
    }

    public ItemGoggles(ArmorType type, Properties properties) {
        super(properties.equippable(EquipmentSlot.HEAD));
    }

    @Override
    public boolean showIngamePopups(ItemStack itemstack, LivingEntity player) {
        return true;
    }

    @Override
    public boolean showNodes(ItemStack itemstack, LivingEntity player) {
        return true;
    }
}
