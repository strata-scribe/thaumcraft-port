package thaumcraft.client.fx.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RunicShieldFlashLogicTest {

    @Test
    void testCalculateFlash_FullShield() {
        RunicShieldFlashLogic.FlashResult result = RunicShieldFlashLogic.calculateFlash(10, 10);

        assertEquals(0.3f, result.opacity(), 0.001f);
        assertEquals(10, result.duration());
        assertEquals(0xFFD700, result.colorTint());
    }

    @Test
    void testCalculateFlash_HalfShield() {
        RunicShieldFlashLogic.FlashResult result = RunicShieldFlashLogic.calculateFlash(5, 10);

        assertEquals(0.65f, result.opacity(), 0.001f);
        assertEquals(20, result.duration());
        assertEquals(0xFF6B00, result.colorTint()); // 215 * 0.5 = 107 -> 0x6B
    }

    @Test
    void testCalculateFlash_EmptyShield() {
        RunicShieldFlashLogic.FlashResult result = RunicShieldFlashLogic.calculateFlash(0, 10);

        assertEquals(1.0f, result.opacity(), 0.001f);
        assertEquals(30, result.duration());
        assertEquals(0xFF0000, result.colorTint());
    }

    @Test
    void testCalculateFlash_NegativeCurrentShield() {
        RunicShieldFlashLogic.FlashResult result = RunicShieldFlashLogic.calculateFlash(-5, 10);

        assertEquals(1.0f, result.opacity(), 0.001f);
        assertEquals(30, result.duration());
        assertEquals(0xFF0000, result.colorTint());
    }

    @Test
    void testCalculateFlash_OverchargedShield() {
        RunicShieldFlashLogic.FlashResult result = RunicShieldFlashLogic.calculateFlash(15, 10);

        assertEquals(0.3f, result.opacity(), 0.001f);
        assertEquals(10, result.duration());
        assertEquals(0xFFD700, result.colorTint());
    }

    @Test
    void testCalculateFlash_ZeroMaxShield() {
        RunicShieldFlashLogic.FlashResult result = RunicShieldFlashLogic.calculateFlash(5, 0);

        assertEquals(0.0f, result.opacity(), 0.001f);
        assertEquals(0, result.duration());
        assertEquals(0x000000, result.colorTint());
    }
}
