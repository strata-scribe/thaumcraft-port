package thaumcraft.common.items.armor;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;
import thaumcraft.api.items.IGoggles;
import thaumcraft.api.items.IRevealer;

/**
 * Thaumium Fortress Armor (Helm, Chest, Legs):
 * - Heavy thaumium armor set with composite damage absorption
 * - Multi-piece set bonuses (+1 Armor/Toughness at 2 pieces, +2 at 3 pieces)
 * - Modular Helmet attachments:
 *     - Goggles of Revealing upgrade (IGoggles, IRevealer)
 *     - Mask 0 (Grinning Devil): Warp event severity dampening
 *     - Mask 1 (Angry Ghost): Wither retaliation when attacked
 *     - Mask 2 (Sipping Fiend): Lifesteal healing on melee hit
 *
 * Mathematical rules and absorption calculations are delegated to {@link EquipmentLogic}.
 */
public class ItemFortressArmor extends Item implements IGoggles, IRevealer {

    public static final int MASK_NONE = -1;
    public static final int MASK_GRINNING_DEVIL = 0;
    public static final int MASK_ANGRY_GHOST = 1;
    public static final int MASK_SIPPING_FIEND = 2;

    private final ArmorType armorType;

    public ItemFortressArmor(ArmorType armorType, Properties properties) {
        super(properties);
        this.armorType = armorType;
    }

    public ItemFortressArmor(ArmorType armorType) {
        this(armorType, new Item.Properties()
                .stacksTo(1)
                .durability(armorType.getDurability(25))
                .component(DataComponents.EQUIPPABLE, Equippable.builder(armorType.getSlot()).build()));
    }

    public ItemFortressArmor(Properties properties) {
        this(ArmorType.CHESTPLATE, properties);
    }

    public ItemFortressArmor() {
        this(ArmorType.CHESTPLATE);
    }

    public ArmorType getArmorType() {
        return this.armorType;
    }

    public static boolean hasGoggles(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return tag.getBooleanOr("goggles", false);
    }

    public static void setGoggles(ItemStack stack, boolean goggles) {
        if (stack == null || stack.isEmpty()) return;
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putBoolean("goggles", goggles);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static int getMask(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return MASK_NONE;
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return tag.getIntOr("mask", MASK_NONE);
    }

    public static void setMask(ItemStack stack, int mask) {
        if (stack == null || stack.isEmpty()) return;
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putInt("mask", mask);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    @Override
    public boolean showIngamePopups(ItemStack itemstack, LivingEntity player) {
        return this.armorType == ArmorType.HELMET && hasGoggles(itemstack);
    }

    @Override
    public boolean showNodes(ItemStack itemstack, LivingEntity player) {
        return this.armorType == ArmorType.HELMET && hasGoggles(itemstack);
    }

    public double getAbsorptionRatio(EquipmentLogic.DamageSourceType source, int totalArmor) {
        return EquipmentLogic.calculateArmorAbsorptionRatio(source, totalArmor);
    }

    public float calculateDamageAbsorption(EquipmentLogic.DamageSourceType source, int totalArmor, float damage) {
        return EquipmentLogic.calculateArmorAbsorption(source, totalArmor, damage);
    }

    public int getBonusArmor(int wornPieces) {
        return EquipmentLogic.calculateFortressBonusArmor(wornPieces);
    }

    public int getBonusToughness(int wornPieces) {
        return EquipmentLogic.calculateFortressBonusToughness(wornPieces);
    }

    public int reduceWarp(int baseRoll, int rand4) {
        return EquipmentLogic.calculateMaskWarpReduction(baseRoll, rand4);
    }

    public boolean shouldRetaliateWither(float incomingDamage, float roll) {
        return EquipmentLogic.shouldTriggerMaskWither(incomingDamage, roll);
    }

    public boolean shouldLifesteal(float outgoingDamage, float roll) {
        return EquipmentLogic.shouldTriggerMaskLifesteal(outgoingDamage, roll);
    }
}
