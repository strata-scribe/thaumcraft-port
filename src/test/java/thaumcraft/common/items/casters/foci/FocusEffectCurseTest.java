package thaumcraft.common.items.casters.foci;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import thaumcraft.common.casters.FocusLogic;
import thaumcraft.api.casters.focus.FocusEffectLogic;

public class FocusEffectCurseTest {

    @Test
    public void testCalculateCurseComplexity() {
        FocusEffectCurse curse = new FocusEffectCurse();

        curse.getSetting("power").setValue(1);
        curse.getSetting("duration").setValue(1);

        assertEquals(4, FocusLogic.calculateCurseComplexity(1, 1));
        assertEquals(4, curse.getComplexity());

        curse.getSetting("power").setValue(5);
        curse.getSetting("duration").setValue(5);
        assertEquals(20, FocusLogic.calculateCurseComplexity(5, 5));
        assertEquals(20, curse.getComplexity());
    }

    @Test
    public void testDamageFormulas() {
        FocusEffectCurse curse = new FocusEffectCurse();

        curse.getSetting("power").setValue(1);
        assertEquals(2.0f, FocusLogic.calculateCurseDamage(1, 1.0f));
        assertEquals(2.0f, curse.getDamageForDisplay(1.0f));

        curse.getSetting("power").setValue(5);
        assertEquals(9.0f, FocusLogic.calculateCurseDamage(5, 1.5f));
        assertEquals(9.0f, curse.getDamageForDisplay(1.5f));

        // API damage formula
        assertEquals(3.0f, FocusEffectLogic.calculateDamage(1, 1.0f));
        assertEquals(15.0f, FocusEffectLogic.calculateDamage(5, 1.0f));
    }

    @Test
    public void testWitherAndWeaknessDurationScaling() {
        // Weakness duration scaling (common package) uses FocusLogic.calculateCurseDebuffDuration
        assertEquals(20, FocusLogic.calculateCurseDebuffDuration(1)); // 1 * 20 = 20 ticks
        assertEquals(100, FocusLogic.calculateCurseDebuffDuration(5)); // 5 * 20 = 100 ticks

        // Wither duration scaling (api package) uses FocusEffectLogic.calculateDuration
        assertEquals(2, FocusEffectLogic.calculateDuration(1, 1.0f)); // duration * 20 in execute
        assertEquals(10, FocusEffectLogic.calculateDuration(5, 1.0f));
        assertEquals(15, FocusEffectLogic.calculateDuration(5, 1.5f));
    }
}
