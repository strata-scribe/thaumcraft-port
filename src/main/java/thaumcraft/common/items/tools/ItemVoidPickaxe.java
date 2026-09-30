package thaumcraft.common.items.tools;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.items.IWarpingGear;
import thaumcraft.common.items.tools.logic.ThaumcraftToolLogic;

/**
 * Void Pickaxe:
 * - Void metal pickaxe that passively self-repairs.
 * - Implements IWarpingGear (+1 warp).
 * - Stacks to 1, durability 150.
 */
public class ItemVoidPickaxe extends Item implements IWarpingGear {

    public ItemVoidPickaxe(Properties properties) {
        super(properties);
    }

    public ItemVoidPickaxe() {
        this(new Item.Properties().stacksTo(1).durability(ThaumcraftToolLogic.getVoidDurability()));
    }

    @Override
    public int getWarp(ItemStack itemstack, Player player) {
        return ThaumcraftToolLogic.getVoidToolWarp();
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        super.inventoryTick(itemStack, level, entity, slot);
        if (entity != null && itemStack.isDamaged() && entity.tickCount % 20 == 0) {
            int newDamage = ThaumcraftToolLogic.calculateVoidSelfRepair(itemStack.getDamageValue(), entity.tickCount);
            itemStack.setDamageValue(newDamage);
        }
    }
}
