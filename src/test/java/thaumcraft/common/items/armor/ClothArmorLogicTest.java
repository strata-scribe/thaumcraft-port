package thaumcraft.common.items.armor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("ClothArmorLogic Headless Unit Tests")
public class ClothArmorLogicTest {

    @Test
    @DisplayName("Verify calculateClothVisDiscount calculates 3% per piece and clamps boundaries")
    public void testCalculateClothVisDiscount() {
        assertEquals(0, ClothArmorLogic.calculateClothVisDiscount(0), "0 pieces should yield 0% discount");
        assertEquals(3, ClothArmorLogic.calculateClothVisDiscount(1), "1 piece should yield 3% discount");
        assertEquals(6, ClothArmorLogic.calculateClothVisDiscount(2), "2 pieces should yield 6% discount");
        assertEquals(9, ClothArmorLogic.calculateClothVisDiscount(3), "3 pieces should yield 9% discount");

        // Negative input clamped to 0
        assertEquals(0, ClothArmorLogic.calculateClothVisDiscount(-1), "Negative count should clamp to 0");
        assertEquals(0, ClothArmorLogic.calculateClothVisDiscount(-5), "Negative count should clamp to 0");

        // Upper bound clamped to 100
        assertEquals(99, ClothArmorLogic.calculateClothVisDiscount(33), "33 pieces should yield 99%");
        assertEquals(100, ClothArmorLogic.calculateClothVisDiscount(34), "34 pieces should clamp to 100%");
        assertEquals(100, ClothArmorLogic.calculateClothVisDiscount(100), "100 pieces should clamp to 100%");
    }

    @Test
    @DisplayName("Verify getClothDurabilityFactor returns 8")
    public void testGetClothDurabilityFactor() {
        assertEquals(8, ClothArmorLogic.getClothDurabilityFactor(), "Cloth durability factor must be 8");
    }

    @Test
    @DisplayName("Verify getDefaultClothColor returns 0x6a3860")
    public void testGetDefaultClothColor() {
        assertEquals(0x6a3860, ClothArmorLogic.getDefaultClothColor(), "Default cloth color must be 0x6a3860");
    }
}
