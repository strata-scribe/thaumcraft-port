package thaumcraft.common.items.tools.logic;

import net.minecraft.world.item.equipment.ArmorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ThaumcraftToolLogic Unit and Boundary Tests")
public class ThaumcraftToolLogicTest {

    @Test
    @DisplayName("Verify static tool and material constants")
    public void testToolConstants() {
        assertEquals(500, ThaumcraftToolLogic.getThaumiumDurability(), "Thaumium durability must be 500");
        assertEquals(150, ThaumcraftToolLogic.getVoidDurability(), "Void durability must be 150");
        assertEquals(250, ThaumcraftToolLogic.getCrimsonBladeDurability(), "Crimson Blade durability must be 250");
        assertEquals(100, ThaumcraftToolLogic.getScribingToolsDurability(), "Scribing Tools durability must be 100");

        assertEquals(22, ThaumcraftToolLogic.getThaumiumEnchantability(), "Thaumium enchantability must be 22");
        assertEquals(10, ThaumcraftToolLogic.getVoidEnchantability(), "Void enchantability must be 10");

        assertEquals(7.0f, ThaumcraftToolLogic.getThaumiumDigSpeed(), 0.001f, "Thaumium dig speed must be 7.0f");
        assertEquals(8.0f, ThaumcraftToolLogic.getVoidDigSpeed(), 0.001f, "Void dig speed must be 8.0f");

        assertEquals(1, ThaumcraftToolLogic.getVoidToolWarp(), "Void tool warp must be 1");
        assertEquals(1, ThaumcraftToolLogic.getCrimsonBladeWarp(), "Crimson blade warp must be 1");

        assertEquals(60, ThaumcraftToolLogic.getWeaknessDurationTicks(), "Weakness duration ticks must be 60");
        assertEquals(25, ThaumcraftToolLogic.getThaumiumArmorDurabilityFactor(), "Thaumium armor durability factor must be 25");
    }

    @Test
    @DisplayName("Verify calculateVoidSelfRepair at tick 0, 20, 21, and zero/negative damage")
    public void testCalculateVoidSelfRepair() {
        // Tick 0: 0 % 20 == 0 -> repairs 1
        assertEquals(49, ThaumcraftToolLogic.calculateVoidSelfRepair(50, 0));

        // Tick 20: 20 % 20 == 0 -> repairs 1
        assertEquals(49, ThaumcraftToolLogic.calculateVoidSelfRepair(50, 20));

        // Tick 40: 40 % 20 == 0 -> repairs 1
        assertEquals(9, ThaumcraftToolLogic.calculateVoidSelfRepair(10, 40));

        // Tick 21: not divisible by 20 -> unchanged
        assertEquals(50, ThaumcraftToolLogic.calculateVoidSelfRepair(50, 21));

        // Tick 1, 19, 39: unchanged
        assertEquals(50, ThaumcraftToolLogic.calculateVoidSelfRepair(50, 1));
        assertEquals(50, ThaumcraftToolLogic.calculateVoidSelfRepair(50, 19));
        assertEquals(50, ThaumcraftToolLogic.calculateVoidSelfRepair(50, 39));

        // Zero damage: always returns 0
        assertEquals(0, ThaumcraftToolLogic.calculateVoidSelfRepair(0, 0));
        assertEquals(0, ThaumcraftToolLogic.calculateVoidSelfRepair(0, 20));
        assertEquals(0, ThaumcraftToolLogic.calculateVoidSelfRepair(0, 21));

        // Negative damage: clamped to 0
        assertEquals(0, ThaumcraftToolLogic.calculateVoidSelfRepair(-5, 20));
        assertEquals(0, ThaumcraftToolLogic.calculateVoidSelfRepair(-1, 0));
        assertEquals(0, ThaumcraftToolLogic.calculateVoidSelfRepair(-10, 21));

        // Boundary damage: 1 damage repaired to 0
        assertEquals(0, ThaumcraftToolLogic.calculateVoidSelfRepair(1, 20));
        assertEquals(1, ThaumcraftToolLogic.calculateVoidSelfRepair(1, 19));
    }

    @Test
    @DisplayName("Verify shouldInflictWeakness with positive damage values")
    public void testShouldInflictWeaknessPositive() {
        assertTrue(ThaumcraftToolLogic.shouldInflictWeakness(0.001f));
        assertTrue(ThaumcraftToolLogic.shouldInflictWeakness(1.0f));
        assertTrue(ThaumcraftToolLogic.shouldInflictWeakness(5.5f));
        assertTrue(ThaumcraftToolLogic.shouldInflictWeakness(100.0f));
    }

    @Test
    @DisplayName("Verify shouldInflictWeakness with zero and negative damage values")
    public void testShouldInflictWeaknessZeroAndNegative() {
        assertFalse(ThaumcraftToolLogic.shouldInflictWeakness(0.0f));
        assertFalse(ThaumcraftToolLogic.shouldInflictWeakness(-0.001f));
        assertFalse(ThaumcraftToolLogic.shouldInflictWeakness(-1.0f));
        assertFalse(ThaumcraftToolLogic.shouldInflictWeakness(-50.0f));
    }

    @Test
    @DisplayName("Verify armor durability calculation with factor 25")
    public void testThaumiumArmorDurabilityCalculation() {
        int factor = ThaumcraftToolLogic.getThaumiumArmorDurabilityFactor();
        assertEquals(275, ArmorType.HELMET.getDurability(factor));
        assertEquals(400, ArmorType.CHESTPLATE.getDurability(factor));
        assertEquals(375, ArmorType.LEGGINGS.getDurability(factor));
        assertEquals(325, ArmorType.BOOTS.getDurability(factor));
    }
}
