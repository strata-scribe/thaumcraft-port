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
 * Thaumostatic Harness:
 * - 3D Flight mechanics and anti-gravity levitation
 * - Hover stability and velocity damping
 * - Vis charge consumption based on flight mode (1 charge/s hover, 2.5 charges/s flight)
 * - Emergency parachute descent damping when fuel depletes
 *
 * Mathematical rules and simulation algorithms are delegated to {@link HarnessFlightLogic}.
 */
public class ItemThaumostaticHarness extends Item implements IRechargable {

    public static final int MAX_CHARGE = 300;

    public ItemThaumostaticHarness(Properties properties) {
        super(properties);
    }

    public ItemThaumostaticHarness() {
        this(new Item.Properties()
                .stacksTo(1)
                .durability(400)
                .component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.CHEST).build()));
    }

    public boolean hasCharge(ItemStack stack) {
        return RechargeHelper.getCharge(stack) > 0;
    }

    public boolean isHovering(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return tag.getBooleanOr("hover", false);
    }

    public void setHovering(ItemStack stack, boolean hover) {
        if (stack == null || stack.isEmpty()) return;
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putBoolean("hover", hover);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public float getVisDrainPerSecond(boolean isFlying, boolean isHovering, boolean isSprinting) {
        return HarnessFlightLogic.calculateHarnessVisDrain(isFlying, isHovering, isSprinting);
    }

    public float getVisDrainPerTick(boolean isFlying, boolean isHovering, boolean isSprinting) {
        return HarnessFlightLogic.calculateHarnessDrainPerTick(isFlying, isHovering, isSprinting);
    }

    public double getAdjustedSpeed(double currentMotion, double targetSpeed) {
        return HarnessFlightLogic.calculateHarnessSpeed(currentMotion, targetSpeed);
    }

    public double getVerticalMotion(double currentMotionY, boolean jumpHeld, boolean sneakHeld, boolean hoverActive) {
        return HarnessFlightLogic.calculateHarnessVerticalMotion(currentMotionY, jumpHeld, sneakHeld, hoverActive);
    }

    public double getDescentDamping(double currentMotionY) {
        return HarnessFlightLogic.calculateHarnessDescentDamping(currentMotionY);
    }

    public double getForwardSpeedBoost(double currentMotion, boolean isSprinting) {
        return HarnessFlightLogic.calculateForwardSpeedBoost(currentMotion, isSprinting);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        super.inventoryTick(itemStack, level, entity, slot);
        if (slot == EquipmentSlot.CHEST && entity instanceof Player player) {
            boolean active = hasCharge(itemStack);
            if (active) {
                boolean flying = player.getAbilities().flying;
                boolean hovering = isHovering(itemStack);

                if (flying || hovering) {
                    // Drain vis periodically
                    if (player.tickCount % 20 == 0) {
                        int drain = Math.round(HarnessFlightLogic.calculateHarnessVisDrain(flying, hovering, player.isSprinting()));
                        if (drain > 0) {
                            RechargeHelper.consumeCharge(itemStack, player, drain);
                        }
                    }
                }
            } else {
                // Out of charge: disable creative flight and dampen descent
                if (player.getAbilities().flying && !player.isCreative() && !player.isSpectator()) {
                    player.getAbilities().flying = false;
                }
            }
        }
    }

    @Override
    public int getMaxCharge(ItemStack stack, LivingEntity player) {
        return MAX_CHARGE;
    }

    @Override
    public EnumChargeDisplay showInHud(ItemStack stack, LivingEntity player) {
        return EnumChargeDisplay.NORMAL;
    }
}
