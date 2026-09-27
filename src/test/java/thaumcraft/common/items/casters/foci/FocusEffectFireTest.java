package thaumcraft.common.items.casters.foci;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import thaumcraft.api.casters.NodeSetting;
import thaumcraft.common.casters.FocusLogic;

public class FocusEffectFireTest {

    @Test
    public void testCalculateFireComplexity() {
        // formula: Math.max(0, duration) + Math.max(0, power) * 2
        assertEquals(0, FocusLogic.calculateFireComplexity(0, 0));
        assertEquals(2, FocusLogic.calculateFireComplexity(1, 0));
        assertEquals(1, FocusLogic.calculateFireComplexity(0, 1));
        assertEquals(3, FocusLogic.calculateFireComplexity(1, 1));
        assertEquals(12, FocusLogic.calculateFireComplexity(5, 2));
        assertEquals(15, FocusLogic.calculateFireComplexity(5, 5));

        // Edge cases
        assertEquals(0, FocusLogic.calculateFireComplexity(-1, -1));
        assertEquals(2, FocusLogic.calculateFireComplexity(1, -1));
        assertEquals(1, FocusLogic.calculateFireComplexity(-1, 1));
    }

    @Test
    public void testCalculateFireDamage() {
        // formula: (3.0f + Math.max(0, power)) * Math.max(0.0f, finalPower)
        assertEquals(3.0f, FocusLogic.calculateFireDamage(0, 1.0f));
        assertEquals(4.0f, FocusLogic.calculateFireDamage(1, 1.0f));
        assertEquals(8.0f, FocusLogic.calculateFireDamage(5, 1.0f));

        // with finalPower scaling
        assertEquals(2.0f, FocusLogic.calculateFireDamage(1, 0.5f));
        assertEquals(6.0f, FocusLogic.calculateFireDamage(1, 1.5f));
        assertEquals(0.0f, FocusLogic.calculateFireDamage(5, 0.0f));

        // Edge cases
        assertEquals(0.0f, FocusLogic.calculateFireDamage(1, -1.0f));
        assertEquals(3.0f, FocusLogic.calculateFireDamage(-1, 1.0f));
    }

    @Test
    public void testCalculateFireBurnDuration() {
        // formula: (1.0f + (float) (dur * dur)) * Math.max(0.0f, finalPower) where dur = Math.max(0, duration)
        assertEquals(1.0f, FocusLogic.calculateFireBurnDuration(0, 1.0f));
        assertEquals(2.0f, FocusLogic.calculateFireBurnDuration(1, 1.0f));
        assertEquals(5.0f, FocusLogic.calculateFireBurnDuration(2, 1.0f));
        assertEquals(26.0f, FocusLogic.calculateFireBurnDuration(5, 1.0f));

        // with finalPower scaling
        assertEquals(1.0f, FocusLogic.calculateFireBurnDuration(1, 0.5f));
        assertEquals(3.0f, FocusLogic.calculateFireBurnDuration(1, 1.5f));
        assertEquals(0.0f, FocusLogic.calculateFireBurnDuration(5, 0.0f));

        // Edge cases
        assertEquals(0.0f, FocusLogic.calculateFireBurnDuration(1, -1.0f));
        assertEquals(1.0f, FocusLogic.calculateFireBurnDuration(-1, 1.0f));
    }

    @Test
    public void testFocusEffectFireSettings() {
        TestableFocusEffectFire effect = new TestableFocusEffectFire();
        NodeSetting[] settings = effect.createSettings();

        assertNotNull(settings);
        assertEquals(2, settings.length);

        // Find power setting
        NodeSetting powerSetting = null;
        NodeSetting durationSetting = null;
        for (NodeSetting setting : settings) {
            if ("power".equals(setting.key)) {
                powerSetting = setting;
            } else if ("duration".equals(setting.key)) {
                durationSetting = setting;
            }
        }

        assertNotNull(powerSetting, "Missing 'power' setting");
        assertNotNull(durationSetting, "Missing 'duration' setting");

        // Verify power setting range (1 to 5)
        assertEquals("focus.common.power", powerSetting.getLocalizedName());
        assertTrue(powerSetting.getType() instanceof NodeSetting.NodeSettingIntRange);
        NodeSetting.NodeSettingIntRange powerRange = (NodeSetting.NodeSettingIntRange) powerSetting.getType();
        assertEquals(1, powerRange.getDefault());
        assertEquals(1, powerRange.clamp(-1));
        assertEquals(1, powerRange.clamp(0));
        assertEquals(1, powerRange.clamp(1));
        assertEquals(5, powerRange.clamp(5));
        assertEquals(5, powerRange.clamp(6));

        // Verify duration setting range (0 to 5)
        assertEquals("focus.fire.burn", durationSetting.getLocalizedName());
        assertTrue(durationSetting.getType() instanceof NodeSetting.NodeSettingIntRange);
        NodeSetting.NodeSettingIntRange durationRange = (NodeSetting.NodeSettingIntRange) durationSetting.getType();
        assertEquals(0, durationRange.getDefault());
        assertEquals(0, durationRange.clamp(-1));
        assertEquals(0, durationRange.clamp(0));
        assertEquals(5, durationRange.clamp(5));
        assertEquals(5, durationRange.clamp(6));
    }

    /**
     * Subclass to expose createSettings for testing without bootstrapping Minecraft.
     */
    private static class TestableFocusEffectFire extends FocusEffectFire {
        public TestableFocusEffectFire() {
            super();
        }

        // Override initialize to prevent issues with components translating in superclass
        @Override
        public void initialize() {
            // Do nothing to avoid calling NodeSetting constructor which accesses Component.translatable
        }
    }
}
