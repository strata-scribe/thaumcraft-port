package thaumcraft.common.items.armor;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.equipment.Equippable;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;

/**
 * Boots of the Traveller:
 * - Step height boost (1.0f when moving forward, not sneaking, and charged)
 * - Jump boost (+0.275 vertical velocity when charged)
 * - Ground/Water movement speed bonus
 * - Fall damage reduction
 * - Vis charge consumption (240 max charge)
 *
 * Mathematical rules are delegated directly to {@link EquipmentLogic}.
 */
public class ItemBootsTraveller extends Item implements IRechargable {

    public static final int MAX_CHARGE = 240;

    public ItemBootsTraveller(Properties properties) {
        super(properties);
    }

    public ItemBootsTraveller() {
        this(new Item.Properties()
                .stacksTo(1)
                .durability(350)
                .component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.FEET).build()));
    }

    public boolean hasCharge(ItemStack stack) {
        return RechargeHelper.getCharge(stack) > 0;
    }

    public float calculateFallDamage(float damage, ItemStack stack) {
        return EquipmentLogic.calculateFallDamage(damage, hasCharge(stack));
    }

    public double getJumpBoost(ItemStack stack) {
        return EquipmentLogic.calculateJumpBoost(hasCharge(stack));
    }

    public double getJumpVelocity(double currentMotionY, ItemStack stack) {
        return EquipmentLogic.calculateJumpVelocity(currentMotionY, true, hasCharge(stack));
    }

    public float getStepHeight(float baseStep, boolean isSneaking, boolean movingForward, ItemStack stack) {
        return EquipmentLogic.calculateStepHeight(baseStep, true, hasCharge(stack), isSneaking, movingForward);
    }

    public float getSpeedBonus(boolean onGround, boolean inWater, ItemStack stack) {
        return EquipmentLogic.calculateGroundSpeedBonus(onGround, inWater, true, hasCharge(stack));
    }

    public float getAirJumpFactor(ItemStack stack) {
        return EquipmentLogic.calculateAirJumpFactor(true, hasCharge(stack));
    }

    public float getWaterMovementBonus(ItemStack stack) {
        return EquipmentLogic.calculateWaterMovementBonus(true, hasCharge(stack));
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        super.inventoryTick(itemStack, level, entity, slot);
        if (slot == EquipmentSlot.FEET && entity instanceof Player player) {
            if (player.tickCount % 20 == 0) {
                CompoundTag tag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
                int e = tag.getIntOr("energy", 0);
                if (e > 0) {
                    --e;
                } else if (RechargeHelper.consumeCharge(itemStack, player, 1)) {
                    e = 60;
                }
                tag.putInt("energy", e);
                itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            }
        }
    }

    @Override
    public int getMaxCharge(ItemStack stack, LivingEntity player) {
        return MAX_CHARGE;
    }

    @Override
    public EnumChargeDisplay showInHud(ItemStack stack, LivingEntity player) {
        return EnumChargeDisplay.PERIODIC;
    }
}
