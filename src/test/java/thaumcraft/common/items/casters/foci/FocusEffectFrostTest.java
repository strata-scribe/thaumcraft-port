package thaumcraft.common.items.casters.foci;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import thaumcraft.common.casters.FocusLogic;

public class FocusEffectFrostTest {

    @Test
    public void testCalculateFrostComplexity() {
        // public static int calculateFrostComplexity(int power, int duration) {
        //     return Math.max(0, duration) + Math.max(0, power) * 2;
        // }

        // positive power, positive duration
        assertEquals(2 + 1 * 2, FocusLogic.calculateFrostComplexity(1, 2));
        assertEquals(5 + 5 * 2, FocusLogic.calculateFrostComplexity(5, 5));

        // negative power, positive duration
        assertEquals(5 + 0 * 2, FocusLogic.calculateFrostComplexity(-5, 5));

        // positive power, negative duration
        assertEquals(0 + 5 * 2, FocusLogic.calculateFrostComplexity(5, -5));

        // negative power, negative duration
        assertEquals(0 + 0 * 2, FocusLogic.calculateFrostComplexity(-5, -5));
    }

    @Test
    public void testCalculateFrostDamage() {
        // public static float calculateFrostDamage(int power, float finalPower) {
        //     return (3.0f + Math.max(0, power)) * Math.max(0.0f, finalPower);
        // }

        assertEquals((3.0f + 1) * 1.0f, FocusLogic.calculateFrostDamage(1, 1.0f));
        assertEquals((3.0f + 5) * 2.0f, FocusLogic.calculateFrostDamage(5, 2.0f));

        // negative power
        assertEquals((3.0f + 0) * 1.0f, FocusLogic.calculateFrostDamage(-1, 1.0f));

        // negative finalPower
        assertEquals((3.0f + 1) * 0.0f, FocusLogic.calculateFrostDamage(1, -1.0f));
    }

    @Test
    public void testCalculateFrostSlownessPotency() {
        // public static int calculateFrostSlownessPotency(int power, float finalPower) {
        //     return (int) (1.0f + Math.max(0, power) * Math.max(0.0f, finalPower) / 3.0f);
        // }

        assertEquals((int) (1.0f + 3 * 1.0f / 3.0f), FocusLogic.calculateFrostSlownessPotency(3, 1.0f));
        assertEquals((int) (1.0f + 5 * 2.0f / 3.0f), FocusLogic.calculateFrostSlownessPotency(5, 2.0f));

        // negative power
        assertEquals((int) (1.0f + 0 * 1.0f / 3.0f), FocusLogic.calculateFrostSlownessPotency(-5, 1.0f));

        // negative finalPower
        assertEquals((int) (1.0f + 5 * 0.0f / 3.0f), FocusLogic.calculateFrostSlownessPotency(5, -1.0f));
    }

    @Test
    public void testCalculateFrostSlownessDuration() {
        // public static int calculateFrostSlownessDuration(int duration) {
        //     return 20 * Math.max(0, duration);
        // }

        assertEquals(20 * 2, FocusLogic.calculateFrostSlownessDuration(2));
        assertEquals(20 * 5, FocusLogic.calculateFrostSlownessDuration(5));

        // negative duration
        assertEquals(20 * 0, FocusLogic.calculateFrostSlownessDuration(-5));
    }

    @Test
    public void testCalculateFrostFreezeRadius() {
        // public static float calculateFrostFreezeRadius(int power, float finalPower) {
        //     return Math.min(16.0f, 2.0f * Math.max(0, power) * Math.max(0.0f, finalPower));
        // }

        assertEquals(Math.min(16.0f, 2.0f * 3 * 1.0f), FocusLogic.calculateFrostFreezeRadius(3, 1.0f));
        assertEquals(Math.min(16.0f, 2.0f * 5 * 2.0f), FocusLogic.calculateFrostFreezeRadius(5, 2.0f));

        // negative power
        assertEquals(Math.min(16.0f, 2.0f * 0 * 1.0f), FocusLogic.calculateFrostFreezeRadius(-5, 1.0f));

        // negative finalPower
        assertEquals(Math.min(16.0f, 2.0f * 5 * 0.0f), FocusLogic.calculateFrostFreezeRadius(5, -1.0f));

        // hit max cap 16.0f
        assertEquals(16.0f, FocusLogic.calculateFrostFreezeRadius(10, 2.0f));
    }

    @Test
    public void testCalculateFrostedIceRadius() {
        // public static float calculateFrostedIceRadius(int power, float finalPower) {
        //     return calculateFrostFreezeRadius(power, finalPower);
        // }

        assertEquals(Math.min(16.0f, 2.0f * 3 * 1.0f), FocusLogic.calculateFrostedIceRadius(3, 1.0f));
        assertEquals(16.0f, FocusLogic.calculateFrostedIceRadius(10, 2.0f));
    }
}
