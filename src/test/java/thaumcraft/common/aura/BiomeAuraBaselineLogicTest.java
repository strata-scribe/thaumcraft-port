package thaumcraft.common.aura;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BiomeAuraBaselineLogicTest {

    @Test
    public void testMagicalBiomeReturns250() {
        short result = BiomeAuraBaselineLogic.getBaselineAura(Arrays.asList("magical", "forest"), (short) 100);
        assertEquals((short) 250, result);
    }

    @Test
    public void testWastelandBiomeReturns50() {
        short result = BiomeAuraBaselineLogic.getBaselineAura(Arrays.asList("wasteland", "dry"), (short) 150);
        assertEquals((short) 50, result);
    }

    @Test
    public void testDefaultBiomeReturnsDefaultBase() {
        short result = BiomeAuraBaselineLogic.getBaselineAura(Arrays.asList("plains", "forest"), (short) 150);
        assertEquals((short) 150, result);
    }

    @Test
    public void testCaseInsensitiveMagical() {
        short result = BiomeAuraBaselineLogic.getBaselineAura(Arrays.asList("MaGiCaL"), (short) 100);
        assertEquals((short) 250, result);
    }

    @Test
    public void testCaseInsensitiveWasteland() {
        short result = BiomeAuraBaselineLogic.getBaselineAura(Arrays.asList("WASTELAND"), (short) 150);
        assertEquals((short) 50, result);
    }

    @Test
    public void testNullBiomeTagsReturnsDefaultBase() {
        short result = BiomeAuraBaselineLogic.getBaselineAura(null, (short) 200);
        assertEquals((short) 200, result);
    }

    @Test
    public void testEmptyBiomeTagsReturnsDefaultBase() {
        short result = BiomeAuraBaselineLogic.getBaselineAura(Collections.emptyList(), (short) 120);
        assertEquals((short) 120, result);
    }
}
