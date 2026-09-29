package thaumcraft.common.items.curios;

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
import thaumcraft.common.items.baubles.logic.VerdantCharmLogic;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.network.chat.Component;
import java.util.List;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.ChatFormatting;

public class ItemVerdantCharm extends Item implements IRechargable {

    public static final int MAX_CHARGE = 200;

    // Type 0 = Base (clears poison)
    // Type 1 = Lifegiver (regenerates health)
    // Type 2 = Sustainer (replenishes hunger/air)

    public ItemVerdantCharm(Properties properties) {
        super(properties);
    }

    public ItemVerdantCharm() {
        this(new Item.Properties().stacksTo(1).component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.BODY).build()));
    }

    @Override
    public int getMaxCharge(ItemStack stack, LivingEntity player) {
        return MAX_CHARGE;
    }

    @Override
    public EnumChargeDisplay showInHud(ItemStack stack, LivingEntity player) {
        return EnumChargeDisplay.PERIODIC;
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        super.inventoryTick(itemStack, level, entity, slot);

        if (!level.isClientSide() && entity instanceof Player player && slot != null) {

            if (RechargeHelper.getCharge(itemStack) > 0) {
                int type = getType(itemStack);
                boolean consumed = false;

                if (type == 1) { // Lifegiver
                    int interval = VerdantCharmLogic.calculateHealingPulseInterval(player.getHealth(), player.getMaxHealth());
                    if (player.tickCount % interval == 0 && player.getHealth() < player.getMaxHealth()) {
                        player.heal(1.0f);
                        consumed = true;
                    }
                } else if (type == 2) { // Sustainer
                    if (player.tickCount % 20 == 0) {
                        boolean action = false;
                        if (player.getAirSupply() < player.getMaxAirSupply()) {
                            player.setAirSupply(Math.min(player.getMaxAirSupply(), player.getAirSupply() + 20));
                            action = true;
                        }
                        if (player.getFoodData().needsFood() && player.tickCount % 100 == 0) {
                            player.getFoodData().eat(1, VerdantCharmLogic.calculateSaturationConversionRatio(player.getFoodData().getFoodLevel(), 20));
                            action = true;
                        }
                        if (action) {
                            consumed = true;
                        }
                    }
                } else { // Base
                    if (player.tickCount % 20 == 0) {
                        if (player.hasEffect(MobEffects.POISON) || player.hasEffect(MobEffects.WITHER)) {
                            player.removeEffect(MobEffects.POISON);
                            player.removeEffect(MobEffects.WITHER);
                            consumed = true;
                        }
                    }
                }

                if (consumed) {
                    RechargeHelper.consumeCharge(itemStack, player, 1);
                }
            }
        }
    }

    public int getType(ItemStack stack) {
        if (stack.has(DataComponents.CUSTOM_DATA)) {
            return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getInt("type").orElse(0);
        }
        return 0;
    }
}
