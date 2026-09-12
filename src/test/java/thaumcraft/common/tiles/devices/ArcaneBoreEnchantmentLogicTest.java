package thaumcraft.common.tiles.devices;

import org.junit.jupiter.api.Test;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ArcaneBoreEnchantmentLogicTest {

    @Test
    void testShouldSilkTouch() {
        assertTrue(ArcaneBoreEnchantmentLogic.shouldSilkTouch(true, true), "Should silk touch if both enchantment and block support it");
        assertFalse(ArcaneBoreEnchantmentLogic.shouldSilkTouch(true, false), "Should not silk touch if block does not support it");
        assertFalse(ArcaneBoreEnchantmentLogic.shouldSilkTouch(false, true), "Should not silk touch if bore lacks enchantment");
        assertFalse(ArcaneBoreEnchantmentLogic.shouldSilkTouch(false, false), "Should not silk touch if neither supports it");
    }

    @Test
    void testCalculateFortuneDropCountNoFortune() {
        Random random = new Random();
        assertEquals(1, ArcaneBoreEnchantmentLogic.calculateFortuneDropCount(1, 0, random), "Yield should be base count for fortune 0");
        assertEquals(2, ArcaneBoreEnchantmentLogic.calculateFortuneDropCount(2, 0, random), "Yield should be base count for fortune 0");
    }

    @Test
    void testCalculateFortuneDropCountWithFortune() {
        // We will create a deterministic Random to test all branches
        // Random.nextInt(fortuneLevel + 2) for fortune = 3 gives values [0, 1, 2, 3, 4]

        Random mockRandom0 = new Random() {
            @Override
            public int nextInt(int bound) {
                return 0; // multiplier = 0 - 1 = -1 -> 0. Yield = base * 1 = base
            }
        };
        assertEquals(1, ArcaneBoreEnchantmentLogic.calculateFortuneDropCount(1, 3, mockRandom0));

        Random mockRandom1 = new Random() {
            @Override
            public int nextInt(int bound) {
                return 1; // multiplier = 1 - 1 = 0 -> 0. Yield = base * 1 = base
            }
        };
        assertEquals(1, ArcaneBoreEnchantmentLogic.calculateFortuneDropCount(1, 3, mockRandom1));

        Random mockRandom2 = new Random() {
            @Override
            public int nextInt(int bound) {
                return 2; // multiplier = 2 - 1 = 1 -> 1. Yield = base * 2
            }
        };
        assertEquals(2, ArcaneBoreEnchantmentLogic.calculateFortuneDropCount(1, 3, mockRandom2));

        Random mockRandom3 = new Random() {
            @Override
            public int nextInt(int bound) {
                return 3; // multiplier = 3 - 1 = 2 -> 2. Yield = base * 3
            }
        };
        assertEquals(3, ArcaneBoreEnchantmentLogic.calculateFortuneDropCount(1, 3, mockRandom3));
        assertEquals(6, ArcaneBoreEnchantmentLogic.calculateFortuneDropCount(2, 3, mockRandom3));

        Random mockRandom4 = new Random() {
            @Override
            public int nextInt(int bound) {
                return 4; // multiplier = 4 - 1 = 3 -> 3. Yield = base * 4
            }
        };
        assertEquals(4, ArcaneBoreEnchantmentLogic.calculateFortuneDropCount(1, 3, mockRandom4));
    }

    @Test
    void testGetFinalYieldSilkTouchOverrides() {
        Random mockRandom4 = new Random() {
            @Override
            public int nextInt(int bound) {
                return 4;
            }
        };

        // If silk touch is active and block supports it, should return 1 despite high fortune
        int yield = ArcaneBoreEnchantmentLogic.getFinalYield(true, true, 1, 3, mockRandom4);
        assertEquals(1, yield, "Silk Touch should override Fortune and return exactly 1");
    }

    @Test
    void testGetFinalYieldNoSilkTouchFallbackToFortune() {
        Random mockRandom4 = new Random() {
            @Override
            public int nextInt(int bound) {
                return 4; // max bonus for fortune 3
            }
        };

        // If block doesn't support silk touch but we have the enchant, fall back to Fortune
        int yield = ArcaneBoreEnchantmentLogic.getFinalYield(true, false, 1, 3, mockRandom4);
        assertEquals(4, yield, "Should fall back to Fortune if block doesn't support Silk Touch");

        // If we don't have silk touch, use fortune
        int yield2 = ArcaneBoreEnchantmentLogic.getFinalYield(false, true, 1, 3, mockRandom4);
        assertEquals(4, yield2, "Should use Fortune if bore lacks Silk Touch");
    }
}
