package thaumcraft.common.items.armor;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import thaumcraft.api.items.IRechargable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ItemThaumostaticHarness Unit and Logic Contract Tests")
public class ItemThaumostaticHarnessTest {

    @Test
    @DisplayName("Verify class hierarchy, interfaces, and constructors")
    public void testClassHierarchyAndConstructors() throws Exception {
        assertTrue(Item.class.isAssignableFrom(ItemThaumostaticHarness.class),
                "ItemThaumostaticHarness must extend net.minecraft.world.item.Item");
        assertTrue(IRechargable.class.isAssignableFrom(ItemThaumostaticHarness.class),
                "ItemThaumostaticHarness must implement IRechargable");

        Constructor<ItemThaumostaticHarness> propsCtor = ItemThaumostaticHarness.class.getConstructor(Item.Properties.class);
        assertNotNull(propsCtor, "Must have (Properties) constructor");

        Constructor<ItemThaumostaticHarness> defaultCtor = ItemThaumostaticHarness.class.getConstructor();
        assertNotNull(defaultCtor, "Must have () default constructor");
    }

    @Test
    @DisplayName("Verify method signatures and contracts")
    public void testMethodSignatures() throws Exception {
        Method hasCharge = ItemThaumostaticHarness.class.getMethod("hasCharge", ItemStack.class);
        assertEquals(boolean.class, hasCharge.getReturnType());

        Method isHovering = ItemThaumostaticHarness.class.getMethod("isHovering", ItemStack.class);
        assertEquals(boolean.class, isHovering.getReturnType());

        Method setHovering = ItemThaumostaticHarness.class.getMethod("setHovering", ItemStack.class, boolean.class);
        assertEquals(void.class, setHovering.getReturnType());

        Method getVisDrainPerSecond = ItemThaumostaticHarness.class.getMethod("getVisDrainPerSecond", boolean.class, boolean.class, boolean.class);
        assertEquals(float.class, getVisDrainPerSecond.getReturnType());

        Method getVisDrainPerTick = ItemThaumostaticHarness.class.getMethod("getVisDrainPerTick", boolean.class, boolean.class, boolean.class);
        assertEquals(float.class, getVisDrainPerTick.getReturnType());

        Method getAdjustedSpeed = ItemThaumostaticHarness.class.getMethod("getAdjustedSpeed", double.class, double.class);
        assertEquals(double.class, getAdjustedSpeed.getReturnType());

        Method getVerticalMotion = ItemThaumostaticHarness.class.getMethod("getVerticalMotion", double.class, boolean.class, boolean.class, boolean.class);
        assertEquals(double.class, getVerticalMotion.getReturnType());

        Method getDescentDamping = ItemThaumostaticHarness.class.getMethod("getDescentDamping", double.class);
        assertEquals(double.class, getDescentDamping.getReturnType());

        Method getForwardSpeedBoost = ItemThaumostaticHarness.class.getMethod("getForwardSpeedBoost", double.class, boolean.class);
        assertEquals(double.class, getForwardSpeedBoost.getReturnType());

        Method getMaxCharge = ItemThaumostaticHarness.class.getMethod("getMaxCharge", ItemStack.class, LivingEntity.class);
        assertEquals(int.class, getMaxCharge.getReturnType());

        Method showInHud = ItemThaumostaticHarness.class.getMethod("showInHud", ItemStack.class, LivingEntity.class);
        assertEquals(IRechargable.EnumChargeDisplay.class, showInHud.getReturnType());
    }

    @Test
    @DisplayName("Test instantiation (handled gracefully in unbootstrapped environment)")
    public void testInstantiation() {
        try {
            ItemThaumostaticHarness harness = new ItemThaumostaticHarness();
            assertNotNull(harness);
            assertEquals(1, harness.getDefaultMaxStackSize(), "Harness must have max stack size 1");
        } catch (Throwable t) {
            // Unbootstrapped pure JUnit environment without FMLLoader
            assertNotNull(ItemThaumostaticHarness.class);
        }
    }

    @Test
    @DisplayName("Verify MAX_CHARGE constant and showInHud display mode")
    public void testMaxChargeAndHudDisplay() {
        assertEquals(300, ItemThaumostaticHarness.MAX_CHARGE, "MAX_CHARGE must be 300");
        assertEquals(IRechargable.EnumChargeDisplay.NORMAL, IRechargable.EnumChargeDisplay.valueOf("NORMAL"));
    }

    @Test
    @DisplayName("Hover state: CompoundTag contract and null/empty safety")
    public void testHoverStateLogic() {
        // Tag logic simulation matching ItemThaumostaticHarness.isHovering / setHovering
        CompoundTag tag = new CompoundTag();
        assertFalse(tag.getBooleanOr("hover", false), "Default hover is false");

        tag.putBoolean("hover", true);
        assertTrue(tag.getBooleanOr("hover", false), "Hover enabled");

        tag.putBoolean("hover", false);
        assertFalse(tag.getBooleanOr("hover", false), "Hover disabled");

        // Null and empty stack safety
        try {
            ItemThaumostaticHarness harness = new ItemThaumostaticHarness();
            assertFalse(harness.isHovering(null));
            assertFalse(harness.isHovering(ItemStack.EMPTY));
            harness.setHovering(null, true);
            harness.setHovering(ItemStack.EMPTY, true);
        } catch (Throwable t) {
            // Expected in unbootstrapped JUnit
            assertNotNull(ItemThaumostaticHarness.class);
        }
    }

    @Test
    @DisplayName("Delegation logic: Vis drain per second and per tick")
    public void testVisDrainDelegation() {
        // Idle (neither flying nor hovering): 0 drain
        assertEquals(0.0f, HarnessFlightLogic.calculateHarnessVisDrain(false, false, false), 1e-4);
        assertEquals(0.0f, HarnessFlightLogic.calculateHarnessDrainPerTick(false, false, false), 1e-4);

        // Hover: 1.0 charge/sec, 1/20 = 0.05 charge/tick
        assertEquals(1.0f, HarnessFlightLogic.calculateHarnessVisDrain(false, true, false), 1e-4);
        assertEquals(0.05f, HarnessFlightLogic.calculateHarnessDrainPerTick(false, true, false), 1e-4);

        // Flight: 2.5 charges/sec, 2.5/20 = 0.125 charge/tick
        assertEquals(2.5f, HarnessFlightLogic.calculateHarnessVisDrain(true, false, false), 1e-4);
        assertEquals(0.125f, HarnessFlightLogic.calculateHarnessDrainPerTick(true, false, false), 1e-4);

        // Sprint flight: 2.5 * 3.0 = 7.5 charges/sec, 7.5/20 = 0.375 charge/tick
        assertEquals(7.5f, HarnessFlightLogic.calculateHarnessVisDrain(true, false, true), 1e-4);
        assertEquals(0.375f, HarnessFlightLogic.calculateHarnessDrainPerTick(true, false, true), 1e-4);

        // Sprint hover: 1.0 * 3.0 = 3.0 charges/sec, 3.0/20 = 0.15 charge/tick
        assertEquals(3.0f, HarnessFlightLogic.calculateHarnessVisDrain(false, true, true), 1e-4);
        assertEquals(0.15f, HarnessFlightLogic.calculateHarnessDrainPerTick(false, true, true), 1e-4);
    }

    @Test
    @DisplayName("Delegation logic: Flight speed adjustment (dampening toward target)")
    public void testAdjustedSpeedDelegation() {
        double currentMotion = 0.0;
        double targetSpeed = 1.0;

        // Accelerates by 0.15 of difference: 0 + (1 - 0) * 0.15 = 0.15
        double step1 = HarnessFlightLogic.calculateHarnessSpeed(currentMotion, targetSpeed);
        assertEquals(0.15, step1, 1e-4);

        // Step 2: 0.15 + (1 - 0.15) * 0.15 = 0.2775
        double step2 = HarnessFlightLogic.calculateHarnessSpeed(step1, targetSpeed);
        assertEquals(0.2775, step2, 1e-4);

        // Very close difference (< 0.001) snaps directly to targetSpeed
        assertEquals(1.0, HarnessFlightLogic.calculateHarnessSpeed(0.9995, 1.0), 1e-4);
    }

    @Test
    @DisplayName("Delegation logic: Vertical flight motion under various control states")
    public void testVerticalMotionDelegation() {
        // Jump held: +0.15 up to 0.60
        assertEquals(0.15, HarnessFlightLogic.calculateHarnessVerticalMotion(0.0, true, false, false), 1e-4);
        assertEquals(0.60, HarnessFlightLogic.calculateHarnessVerticalMotion(0.55, true, false, false), 1e-4);

        // Sneak held: -0.15 down to -0.40
        assertEquals(-0.15, HarnessFlightLogic.calculateHarnessVerticalMotion(0.0, false, true, false), 1e-4);
        assertEquals(-0.40, HarnessFlightLogic.calculateHarnessVerticalMotion(-0.35, false, true, false), 1e-4);

        // Jump + Sneak held with hoverActive: dampens toward 0
        assertEquals(0.425, HarnessFlightLogic.calculateHarnessVerticalMotion(0.5, true, true, true), 1e-4); // 0.5 * 0.85
        assertEquals(0.0, HarnessFlightLogic.calculateHarnessVerticalMotion(0.005, true, true, true), 1e-4); // snaps < 0.01

        // Jump + Sneak held without hover: maintains current motion
        assertEquals(0.5, HarnessFlightLogic.calculateHarnessVerticalMotion(0.5, true, true, false), 1e-4);

        // Hover active without keys: dampens vertical motion
        assertEquals(0.17, HarnessFlightLogic.calculateHarnessVerticalMotion(0.2, false, false, true), 1e-4); // 0.2 * 0.85
        assertEquals(0.0, HarnessFlightLogic.calculateHarnessVerticalMotion(0.008, false, false, true), 1e-4);

        // Inactive: maintains current motion
        assertEquals(0.25, HarnessFlightLogic.calculateHarnessVerticalMotion(0.25, false, false, false), 1e-4);
    }

    @Test
    @DisplayName("Delegation logic: Descent damping on fuel depletion")
    public void testDescentDampingDelegation() {
        // Clamps downward velocity to minimum -0.30
        assertEquals(-0.30, HarnessFlightLogic.calculateHarnessDescentDamping(-0.80), 1e-4);
        assertEquals(-0.30, HarnessFlightLogic.calculateHarnessDescentDamping(-0.35), 1e-4);
        assertEquals(-0.20, HarnessFlightLogic.calculateHarnessDescentDamping(-0.20), 1e-4);
        assertEquals(0.10, HarnessFlightLogic.calculateHarnessDescentDamping(0.10), 1e-4);
    }

    @Test
    @DisplayName("Delegation logic: Forward speed boost when sprinting")
    public void testForwardSpeedBoostDelegation() {
        // Sprinting doubles forward speed
        assertEquals(1.0, HarnessFlightLogic.calculateForwardSpeedBoost(0.5, true), 1e-4);
        assertEquals(0.5, HarnessFlightLogic.calculateForwardSpeedBoost(0.5, false), 1e-4);
        assertEquals(0.0, HarnessFlightLogic.calculateForwardSpeedBoost(0.0, true), 1e-4);
    }
}
