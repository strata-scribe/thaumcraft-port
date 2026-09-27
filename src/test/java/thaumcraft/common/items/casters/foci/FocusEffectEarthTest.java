package thaumcraft.common.items.casters.foci;

import org.junit.jupiter.api.Test;
import thaumcraft.common.casters.FocusLogic;

import static org.junit.jupiter.api.Assertions.*;

public class FocusEffectEarthTest {

    @Test
    public void testEarthComplexity() {
        FocusEffectEarth earth = new FocusEffectEarth() {
            @Override
            public int getSettingValue(String key) {
                if ("power".equals(key)) return 3;
                return super.getSettingValue(key);
            }
        };

        // calculateEarthComplexity(power=3) => max(0, 3) * 3 = 9
        assertEquals(9, earth.getComplexity(), "Complexity should be 9 for power=3");

        FocusEffectEarth earth2 = new FocusEffectEarth() {
            @Override
            public int getSettingValue(String key) {
                if ("power".equals(key)) return 0; // Test negative/zero edge case
                return super.getSettingValue(key);
            }
        };
        assertEquals(0, earth2.getComplexity(), "Complexity should be 0 for power=0");
    }

    @Test
    public void testEarthDamage() {
        FocusEffectEarth earth = new FocusEffectEarth() {
            @Override
            public int getSettingValue(String key) {
                if ("power".equals(key)) return 4;
                return super.getSettingValue(key);
            }
        };

        // calculateEarthDamage(power=4, finalPower=2.0f) => 2.0f * max(0, 4) * max(0.0f, 2.0f) = 2.0 * 4 * 2.0 = 16.0
        assertEquals(16.0f, earth.getDamageForDisplay(2.0f), 0.001f, "Damage should be 16.0 for power=4, finalPower=2.0f");

        // calculateEarthDamage(power=4, finalPower=1.0f) => 2.0 * 4 * 1.0 = 8.0
        assertEquals(8.0f, earth.getDamageForDisplay(1.0f), 0.001f, "Damage should be 8.0 for power=4, finalPower=1.0f");

        // Final power = 0 => damage 0
        assertEquals(0.0f, earth.getDamageForDisplay(0.0f), 0.001f, "Damage should be 0 for finalPower=0");
    }

    @Test
    public void testEarthMaxBreakHardness() {
        // Impact force
        // calculateEarthMaxBreakHardness(power=4, finalPower=2.0f) = calculateEarthDamage(4, 2.0f) / 25.0f = 16.0f / 25.0f = 0.64f
        assertEquals(0.64f, FocusLogic.calculateEarthMaxBreakHardness(4, 2.0f), 0.001f, "Max break hardness should be 0.64 for power=4, finalPower=2.0f");

        // calculateEarthMaxBreakHardness(power=5, finalPower=1.0f) = calculateEarthDamage(5, 1.0f) / 25.0f = (2 * 5 * 1.0) / 25 = 10 / 25 = 0.4f
        assertEquals(0.4f, FocusLogic.calculateEarthMaxBreakHardness(5, 1.0f), 0.001f, "Max break hardness should be 0.4 for power=5, finalPower=1.0f");
    }
}
