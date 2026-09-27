package thaumcraft.common.items.casters.foci;

import org.junit.jupiter.api.Test;
import thaumcraft.api.casters.focus.FocusExchangeLogic;
import thaumcraft.common.casters.FocusLogic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;

import static org.junit.jupiter.api.Assertions.*;

public class FocusEffectBreakExchangeTest {

    private static final double EPSILON = 0.0001;

    // FocusEffectExchange tests

    @Test
    public void testExchangeHardnessPermissions() {
        assertTrue(FocusExchangeLogic.hasExchangePermission(1.0f, 1.0f));
        assertTrue(FocusExchangeLogic.hasExchangePermission(0.0f, 0.0f));
        assertFalse(FocusExchangeLogic.hasExchangePermission(-1.0f, 1.0f)); // Target bedrock
        assertFalse(FocusExchangeLogic.hasExchangePermission(1.0f, -1.0f)); // Source bedrock
    }

    @Test
    public void testExchangeHardnessCompatibility() {
        // Source <= Target * 2.0 + 1.0
        // Target = 1.0 -> max Source = 3.0
        assertTrue(FocusExchangeLogic.isHardnessCompatible(1.0f, 1.0f));
        assertTrue(FocusExchangeLogic.isHardnessCompatible(1.0f, 3.0f));
        assertFalse(FocusExchangeLogic.isHardnessCompatible(1.0f, 3.1f));

        // Target = 2.0 -> max Source = 5.0
        assertTrue(FocusExchangeLogic.isHardnessCompatible(2.0f, 5.0f));
        assertFalse(FocusExchangeLogic.isHardnessCompatible(2.0f, 5.1f));
    }

    @Test
    public void testExchangeVisCostCalculation() {
        // Vis Cost = 0.5 + (hardnessDiff * 0.5) + distanceFactor
        // distanceFactor = max(0, distance - 1) * 0.1

        // targetHardness = 1.0, sourceHardness = 1.0, distance = 1.0
        // cost = 0.5 + 0.0 + 0.0 = 0.5
        assertEquals(0.5f, FocusExchangeLogic.calculateVisCost(1.0f, 1.0f, 1.0), EPSILON);

        // targetHardness = 1.0, sourceHardness = 3.0, distance = 1.0
        // cost = 0.5 + 1.0 + 0.0 = 1.5
        assertEquals(1.5f, FocusExchangeLogic.calculateVisCost(1.0f, 3.0f, 1.0), EPSILON);

        // targetHardness = 1.0, sourceHardness = 1.0, distance = 11.0
        // cost = 0.5 + 0.0 + 1.0 = 1.5
        assertEquals(1.5f, FocusExchangeLogic.calculateVisCost(1.0f, 1.0f, 11.0), EPSILON);
    }

    @Test
    public void testExchangeComplexity() {
        // Complexity = Math.max(0, power) * 3
        assertEquals(0, FocusExchangeLogic.calculateExchangeComplexity(0));
        assertEquals(3, FocusExchangeLogic.calculateExchangeComplexity(1));
        assertEquals(15, FocusExchangeLogic.calculateExchangeComplexity(5));
        assertEquals(0, FocusExchangeLogic.calculateExchangeComplexity(-1));
    }

    static class TestableFocusEffectExchange extends FocusEffectExchange {
        @Override
        public int getSettingValue(String key) {
            if ("power".equals(key)) return 3;
            return 0;
        }
    }

    @Test
    public void testExchangeExecutionCalculations() {
        TestableFocusEffectExchange effect = new TestableFocusEffectExchange();

        // Power 3
        // complexity = 3 * 3 = 9
        assertEquals(9, effect.getComplexity());
    }

    @Test
    public void testExchangeTargetReplacementLogic() {
        // Verify Target Replacement Logic based on isHardnessCompatible rules
        // For exchange, replacement is valid if sourceHardness <= targetHardness * 2 + 1

        float targetHardness = 1.5f; // e.g., Stone
        float maxSourceHardness = targetHardness * 2.0f + 1.0f; // 4.0

        assertTrue(FocusExchangeLogic.isHardnessCompatible(targetHardness, 2.0f)); // Dirt
        assertTrue(FocusExchangeLogic.isHardnessCompatible(targetHardness, 4.0f)); // Max valid
        assertFalse(FocusExchangeLogic.isHardnessCompatible(targetHardness, 4.1f)); // Invalid replacement

        // Target is obsidian
        float obsidianHardness = 50.0f;
        assertTrue(FocusExchangeLogic.isHardnessCompatible(obsidianHardness, 100.0f));
        assertFalse(FocusExchangeLogic.isHardnessCompatible(obsidianHardness, 102.0f));
    }

    // FocusEffectBreak tests

    @Test
    public void testBreakComplexity() {
        // Complexity = max(0, power) * 3 + (silk > 0 ? 4 : 0) + (fortune > 0 ? (fortune + 1) * 3 : 0)

        // base case: power 1, no silk, no fortune
        assertEquals(3, FocusLogic.calculateBreakComplexity(1, 0, 0));

        // max power, no silk, no fortune
        assertEquals(15, FocusLogic.calculateBreakComplexity(5, 0, 0));

        // power 3, silk, no fortune
        assertEquals(13, FocusLogic.calculateBreakComplexity(3, 1, 0));

        // power 3, no silk, fortune 2
        assertEquals(18, FocusLogic.calculateBreakComplexity(3, 0, 2)); // 9 + (3 * 3) = 18

        // power 3, silk, fortune 3
        assertEquals(25, FocusLogic.calculateBreakComplexity(3, 1, 3)); // 9 + 4 + (4 * 3) = 25
    }

    @Test
    public void testBreakDurability() {
        // Durability = sqrt(max(0, blockHardness) * 100)
        assertEquals(10.0f, FocusLogic.calculateBreakDurability(1.0f), EPSILON); // sqrt(100)
        assertEquals(20.0f, FocusLogic.calculateBreakDurability(4.0f), EPSILON); // sqrt(400)
        assertEquals(0.0f, FocusLogic.calculateBreakDurability(0.0f), EPSILON);  // sqrt(0)
        assertEquals(0.0f, FocusLogic.calculateBreakDurability(-1.0f), EPSILON); // max(0, -1) -> 0
    }

    @Test
    public void testBreakDelayTicks() {
        // Delay Ticks = durability / strength / 3.0 * max(0, index)

        // Zero strength should return 0
        assertEquals(0, FocusLogic.calculateBreakDelayTicks(10.0f, 0.0f, 1));

        // Negative strength should return 0
        assertEquals(0, FocusLogic.calculateBreakDelayTicks(10.0f, -1.0f, 1));

        // Normal case
        assertEquals(2, FocusLogic.calculateBreakDelayTicks(18.0f, 3.0f, 1)); // 18 / 3 / 3 * 1 = 2

        // Index scaling
        assertEquals(4, FocusLogic.calculateBreakDelayTicks(18.0f, 3.0f, 2)); // 18 / 3 / 3 * 2 = 4
    }

    @Test
    public void testBreakVisFactor() {
        // Vis Factor = 0.25 + (silk ? 0.25 : 0.0) + fortune * 0.1

        // Base case
        assertEquals(0.25f, FocusLogic.calculateBreakVisFactor(0, false), EPSILON);

        // Silk touch
        assertEquals(0.50f, FocusLogic.calculateBreakVisFactor(0, true), EPSILON);

        // Fortune
        assertEquals(0.35f, FocusLogic.calculateBreakVisFactor(1, false), EPSILON);
        assertEquals(0.55f, FocusLogic.calculateBreakVisFactor(3, false), EPSILON);

        // Both
        assertEquals(0.80f, FocusLogic.calculateBreakVisFactor(3, true), EPSILON);
    }

    static class TestableFocusEffectBreak extends FocusEffectBreak {
        @Override
        public int getSettingValue(String key) {
            if ("power".equals(key)) return 3;
            if ("silk".equals(key)) return 1;
            if ("fortune".equals(key)) return 2;
            return 0;
        }
    }

    @Test
    public void testBreakExecutionCalculations() {
        TestableFocusEffectBreak effect = new TestableFocusEffectBreak();
        assertEquals("FOCUSBREAK", effect.getResearch());
        assertEquals("thaumcraft.BREAK", effect.getKey());

        // power 3, silk 1, fortune 2
        // complexity = 3 * 3 + 4 + (2+1)*3 = 9 + 4 + 9 = 22
        assertEquals(22, effect.getComplexity());
    }

    @Test
    public void testHarvestLevelCalculations() {
        // Verifying delay and scaling logic simulating harvest level calculations
        assertEquals(3, FocusLogic.calculateBreakDelayTicks(27.0f, 3.0f, 1));
        assertEquals(18, FocusLogic.calculateBreakComplexity(3, 0, 2));
    }
}
