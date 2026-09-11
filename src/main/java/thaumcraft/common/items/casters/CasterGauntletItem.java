package thaumcraft.common.items.casters;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.casters.ICaster;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.FocusPackage;
import java.util.Optional;

public class CasterGauntletItem extends Item implements ICaster {

    public CasterGauntletItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public float getConsumptionModifier(ItemStack is, Player player, boolean crafting) {
        java.util.List<Integer> discounts = new java.util.ArrayList<>();
        if (player != null && player.getInventory() != null) {
            // Check worn armor/baubles slots. We evaluate all inventory slots but only apply discounts for actually worn equipment
            // to support baubles that might sit in bauble slots if Curios is present or standard armor slots.
            // But since this issue requested evaluating "worn robes, goggles, and baubles", we need to check the inventory slots
            // assuming baubles may have their own slots added to the player inventory or are tracked via some bauble container.
            // As a fallback in environments without full curio integration, any item implementing IVisDiscountGear in the main inventory might not be worn.
            // But checking EquipmentSlot guarantees we get the worn armor (robes, goggles).
            for (net.minecraft.world.entity.EquipmentSlot slot : net.minecraft.world.entity.EquipmentSlot.values()) {
                if (slot.getType() == net.minecraft.world.entity.EquipmentSlot.Type.HUMANOID_ARMOR) {
                    ItemStack stack = player.getItemBySlot(slot);
                    if (stack != null && !stack.isEmpty() && stack.getItem() instanceof thaumcraft.api.items.IVisDiscountGear gear) {
                        discounts.add(gear.getVisDiscount(stack, player));
                    }
                }
            }
        }
        int totalDiscount = thaumcraft.common.casters.CasterGauntletLogic.calculateTotalVisDiscount(discounts);
        return thaumcraft.common.casters.CasterGauntletLogic.calculateConsumptionModifier(totalDiscount);
    }

    @Override
    public boolean consumeVis(ItemStack is, Player player, float amount, boolean crafting, boolean simulate) {
        if (player == null || player.level() == null) return false;
        Level level = player.level();
        BlockPos pos = player.blockPosition();

        float available = AuraHelper.getVis(level, pos);
        if (available >= amount) {
            if (!simulate) {
                AuraHelper.drainVis(level, pos, amount, false);
            }
            return true;
        }
        return false;
    }

    @Override
    public Item getFocus(ItemStack stack) {
        ItemStack focusStack = getFocusStack(stack);
        return focusStack.isEmpty() ? null : focusStack.getItem();
    }

    @Override
    public ItemStack getFocusStack(ItemStack stack) {
        if (stack.has(DataComponents.CUSTOM_DATA)) {
            CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            if (tag.contains("focus_item")) {
                CompoundTag focusData = tag.getCompound("focus_item").orElse(new CompoundTag());
                Optional<ItemStack> parsed = ItemStack.OPTIONAL_CODEC.parse(NbtOps.INSTANCE, focusData).result();
                if (parsed.isPresent()) {
                    return parsed.get();
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setFocus(ItemStack stack, ItemStack focus) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (focus == null || focus.isEmpty()) {
            tag.remove("focus_item");
        } else {
            // Using OPTIONAL_CODEC for correct NeoForge 1.21.4 item serialization to NBT including components
            ItemStack.OPTIONAL_CODEC.encodeStart(NbtOps.INSTANCE, focus).result().ifPresent(res -> {
                tag.put("focus_item", res);
            });
        }

        if (tag.isEmpty()) {
            stack.remove(DataComponents.CUSTOM_DATA);
        } else {
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
    }

    @Override
    public ItemStack getPickedBlock(ItemStack stack) {
        return ItemStack.EMPTY;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) {
            // Shift-click opens focus pouch / selection
            return InteractionResult.SUCCESS;
        } else {
            ItemStack focus = getFocusStack(stack);
            if (!focus.isEmpty()) {
                FocusPackage fp = ItemFocus.getPackage(focus);
                if (fp == null) {
                    fp = new FocusPackage(player);
                    if (focus.has(DataComponents.CUSTOM_DATA)) {
                        CompoundTag customData = focus.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
                        fp.deserialize(customData);
                    }
                }
                float visCost = thaumcraft.common.casters.FocusLogic.calculateVisCost(fp.getComplexity(), getConsumptionModifier(stack, player, false));
                if (visCost > 0.0f && !consumeVis(stack, player, visCost, false, false)) {
                    return InteractionResult.FAIL;
                }
                FocusEngine.castFocusPackage(player, fp);
                int cooldown = thaumcraft.common.casters.CasterGauntletLogic.calculateCooldownTicks(fp.getComplexity());
                player.getCooldowns().addCooldown(stack, cooldown);
            }
            return InteractionResult.SUCCESS;
        }
    }
}
