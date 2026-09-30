package thaumcraft.common.items.baubles;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;
import thaumcraft.common.items.baubles.logic.BaublesCurioLogic;

public class ItemAmuletVis extends Item implements IRechargable {

    public ItemAmuletVis(Properties properties) {
        super(properties.stacksTo(1));
    }

    public ItemAmuletVis() {
        this(new Item.Properties());
    }

    @Override
    public int getMaxCharge(ItemStack stack, LivingEntity player) {
        return BaublesCurioLogic.calculateVisAmuletMaxCharge();
    }

    @Override
    public EnumChargeDisplay showInHud(ItemStack stack, LivingEntity player) {
        return EnumChargeDisplay.NORMAL;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        super.inventoryTick(stack, level, entity, slot);
        if (!level.isClientSide() && entity instanceof Player player) {
            // Passive recharge check from aura
            if (player.tickCount % 20 == 0) {
                int maxCharge = getMaxCharge(stack, player);
                if (RechargeHelper.getCharge(stack) < maxCharge) {
                    RechargeHelper.rechargeItem(level, stack, player.blockPosition(), player, 1);
                }
            }
            // Passive recharge transfer to other rechargable items in inventory
            if (player.tickCount % 10 == 0 && RechargeHelper.getCharge(stack) > 0) {
                for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                    ItemStack target = player.getInventory().getItem(i);
                    if (!target.isEmpty() && target != stack && target.getItem() instanceof IRechargable rechargable) {
                        int needed = rechargable.getMaxCharge(target, player) - RechargeHelper.getCharge(target);
                        if (needed > 0) {
                            int currentCharge = RechargeHelper.getCharge(stack);
                            int transfer = BaublesCurioLogic.calculateRechargeTransfer(currentCharge, needed, 1);
                            if (transfer > 0 && RechargeHelper.consumeCharge(stack, player, transfer)) {
                                RechargeHelper.rechargeItemBlindly(target, player, transfer);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}
