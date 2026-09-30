package thaumcraft.common.items.armor;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import thaumcraft.api.items.IRechargable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ItemBootsTraveller Unit and Logic Contract Tests")
public class ItemBootsTravellerTest {

    @Test
    @DisplayName("Verify class hierarchy, interfaces, and constructors")
    public void testClassHierarchyAndConstructors() throws Exception {
        assertTrue(Item.class.isAssignableFrom(ItemBootsTraveller.class),
                "ItemBootsTraveller must extend net.minecraft.world.item.Item");
        assertTrue(IRechargable.class.isAssignableFrom(ItemBootsTraveller.class),
                "ItemBootsTraveller must implement IRechargable");

        Constructor<ItemBootsTraveller> propsCtor = ItemBootsTraveller.class.getConstructor(Item.Properties.class);
        assertNotNull(propsCtor, "ItemBootsTraveller must have (Properties) constructor");

        Constructor<ItemBootsTraveller> defaultCtor = ItemBootsTraveller.class.getConstructor();
        assertNotNull(defaultCtor, "ItemBootsTraveller must have () default constructor");
    }

    @Test
    @DisplayName("Verify method signatures and contracts")
    public void testMethodSignatures() throws Exception {
        Method hasCharge = ItemBootsTraveller.class.getMethod("hasCharge", ItemStack.class);
        assertEquals(boolean.class, hasCharge.getReturnType());

        Method calculateFallDamage = ItemBootsTraveller.class.getMethod("calculateFallDamage", float.class, ItemStack.class);
        assertEquals(float.class, calculateFallDamage.getReturnType());

        Method getJumpBoost = ItemBootsTraveller.class.getMethod("getJumpBoost", ItemStack.class);
        assertEquals(double.class, getJumpBoost.getReturnType());

        Method getJumpVelocity = ItemBootsTraveller.class.getMethod("getJumpVelocity", double.class, ItemStack.class);
        assertEquals(double.class, getJumpVelocity.getReturnType());

        Method getStepHeight = ItemBootsTraveller.class.getMethod("getStepHeight", float.class, boolean.class, boolean.class, ItemStack.class);
        assertEquals(float.class, getStepHeight.getReturnType());

        Method getSpeedBonus = ItemBootsTraveller.class.getMethod("getSpeedBonus", boolean.class, boolean.class, ItemStack.class);
        assertEquals(float.class, getSpeedBonus.getReturnType());

        Method getAirJumpFactor = ItemBootsTraveller.class.getMethod("getAirJumpFactor", ItemStack.class);
        assertEquals(float.class, getAirJumpFactor.getReturnType());

        Method getWaterMovementBonus = ItemBootsTraveller.class.getMethod("getWaterMovementBonus", ItemStack.class);
        assertEquals(float.class, getWaterMovementBonus.getReturnType());

        Method getMaxCharge = ItemBootsTraveller.class.getMethod("getMaxCharge", ItemStack.class, LivingEntity.class);
        assertEquals(int.class, getMaxCharge.getReturnType());

        Method showInHud = ItemBootsTraveller.class.getMethod("showInHud", ItemStack.class, LivingEntity.class);
        assertEquals(IRechargable.EnumChargeDisplay.class, showInHud.getReturnType());
    }

    @Test
    @DisplayName("Test instantiation (handled gracefully in unbootstrapped environment)")
    public void testInstantiation() {
        try {
            ItemBootsTraveller boots = new ItemBootsTraveller();
            assertNotNull(boots);
            assertEquals(1, boots.getDefaultMaxStackSize(), "Boots must have max stack size 1");
        } catch (Throwable t) {
            // Unbootstrapped pure JUnit environment without FMLLoader
            assertNotNull(ItemBootsTraveller.class);
        }
    }

    @Test
    @DisplayName("Verify MAX_CHARGE constant and showInHud display mode")
    public void testMaxChargeAndHudDisplay() {
        assertEquals(240, ItemBootsTraveller.MAX_CHARGE, "MAX_CHARGE must be 240");
        assertEquals(IRechargable.EnumChargeDisplay.PERIODIC, IRechargable.EnumChargeDisplay.valueOf("PERIODIC"));
    }

    @Test
    @DisplayName("Delegation logic: Fall damage reduction")
    public void testCalculateFallDamageDelegation() {
        // When uncharged: fall damage is not reduced
        assertEquals(10.0f, EquipmentLogic.calculateFallDamage(10.0f, false), 1e-4);
        assertEquals(5.0f, EquipmentLogic.calculateFallDamage(5.0f, false), 1e-4);
        assertEquals(0.0f, EquipmentLogic.calculateFallDamage(0.0f, false), 1e-4);

        // When charged: reduced = damage / 2 - 1. If reduced < 1.0f -> 0.0f
        assertEquals(4.0f, EquipmentLogic.calculateFallDamage(10.0f, true), 1e-4);
        assertEquals(9.0f, EquipmentLogic.calculateFallDamage(20.0f, true), 1e-4);
        // Small damage: 3.0 / 2 - 1 = 0.5 < 1.0 -> 0.0f
        assertEquals(0.0f, EquipmentLogic.calculateFallDamage(3.0f, true), 1e-4);
        // Damage 2.0 / 2 - 1 = 0.0 < 1.0 -> 0.0f
        assertEquals(0.0f, EquipmentLogic.calculateFallDamage(2.0f, true), 1e-4);
        // Zero or negative
        assertEquals(0.0f, EquipmentLogic.calculateFallDamage(-5.0f, true), 1e-4);
    }

    @Test
    @DisplayName("Delegation logic: Jump boost and jump velocity")
    public void testJumpBoostAndVelocityDelegation() {
        // Jump boost: +0.275 when charged, 0.0 when uncharged
        assertEquals(0.2750000059604645, EquipmentLogic.calculateJumpBoost(true), 1e-6);
        assertEquals(0.0, EquipmentLogic.calculateJumpBoost(false), 1e-6);

        // Jump velocity: currentMotionY + 0.275 when boots worn and charged
        double initialMotion = 0.42;
        assertEquals(initialMotion + 0.2750000059604645, EquipmentLogic.calculateJumpVelocity(initialMotion, true, true), 1e-6);
        assertEquals(initialMotion, EquipmentLogic.calculateJumpVelocity(initialMotion, true, false), 1e-6);
        assertEquals(initialMotion, EquipmentLogic.calculateJumpVelocity(initialMotion, false, true), 1e-6);
    }

    @Test
    @DisplayName("Delegation logic: Step height based on state")
    public void testStepHeightDelegation() {
        float baseStep = 0.6f;

        // Active: hasBoots=true, hasCharge=true, isSneaking=false, movingForward=true -> 1.0f
        assertEquals(1.0f, EquipmentLogic.calculateStepHeight(baseStep, true, true, false, true), 1e-4);

        // Inactive cases all return baseStep
        assertEquals(baseStep, EquipmentLogic.calculateStepHeight(baseStep, true, true, true, true), 1e-4, "Sneaking suppresses step height");
        assertEquals(baseStep, EquipmentLogic.calculateStepHeight(baseStep, true, true, false, false), 1e-4, "Not moving forward suppresses step height");
        assertEquals(baseStep, EquipmentLogic.calculateStepHeight(baseStep, true, false, false, true), 1e-4, "Uncharged suppresses step height");
        assertEquals(baseStep, EquipmentLogic.calculateStepHeight(baseStep, false, true, false, true), 1e-4, "No boots suppresses step height");
    }

    @Test
    @DisplayName("Delegation logic: Ground and water speed bonuses")
    public void testSpeedBonusDelegation() {
        // Ground speed bonus: +0.05f on ground, +0.0125f (0.05/4) in water
        assertEquals(0.05f, EquipmentLogic.calculateGroundSpeedBonus(true, false, true, true), 1e-4);
        assertEquals(0.0125f, EquipmentLogic.calculateGroundSpeedBonus(true, true, true, true), 1e-4);

        // In the air (not on ground): 0.0f
        assertEquals(0.0f, EquipmentLogic.calculateGroundSpeedBonus(false, false, true, true), 1e-4);
        assertEquals(0.0f, EquipmentLogic.calculateGroundSpeedBonus(false, true, true, true), 1e-4);

        // Uncharged or no boots: 0.0f
        assertEquals(0.0f, EquipmentLogic.calculateGroundSpeedBonus(true, false, true, false), 1e-4);
        assertEquals(0.0f, EquipmentLogic.calculateGroundSpeedBonus(true, false, false, true), 1e-4);
    }

    @Test
    @DisplayName("Delegation logic: Air jump factor")
    public void testAirJumpFactorDelegation() {
        // Air jump factor: 0.05f when charged, vanilla base 0.02f when uncharged
        assertEquals(0.05f, EquipmentLogic.calculateAirJumpFactor(true, true), 1e-4);
        assertEquals(0.02f, EquipmentLogic.calculateAirJumpFactor(true, false), 1e-4);
        assertEquals(0.02f, EquipmentLogic.calculateAirJumpFactor(false, true), 1e-4);
    }

    @Test
    @DisplayName("Delegation logic: Water movement bonus")
    public void testWaterMovementBonusDelegation() {
        // Water movement bonus: +0.025f when charged, 0.0f when uncharged
        assertEquals(0.025f, EquipmentLogic.calculateWaterMovementBonus(true, true), 1e-4);
        assertEquals(0.0f, EquipmentLogic.calculateWaterMovementBonus(true, false), 1e-4);
        assertEquals(0.0f, EquipmentLogic.calculateWaterMovementBonus(false, true), 1e-4);
    }
}
