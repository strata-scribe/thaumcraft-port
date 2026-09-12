package thaumcraft.common.golems.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GolemCombatComboLogicTest {

    @Test
    public void testCalculateComboDamage() {
        assertEquals(10.0, GolemCombatComboLogic.calculateComboDamage(10.0, 0), 0.001, "0 combos should return base damage");
        assertEquals(12.0, GolemCombatComboLogic.calculateComboDamage(10.0, 1), 0.001, "1 combo should scale by 1.2");
        assertEquals(14.0, GolemCombatComboLogic.calculateComboDamage(10.0, 2), 0.001, "2 combos should scale by 1.4");
        assertEquals(20.0, GolemCombatComboLogic.calculateComboDamage(10.0, 5), 0.001, "5 combos should scale by 2.0");
        assertEquals(10.0, GolemCombatComboLogic.calculateComboDamage(10.0, -1), 0.001, "Negative combos should return base damage");
    }

    @Test
    public void testCalculateStunProbability() {
        assertEquals(0.1, GolemCombatComboLogic.calculateStunProbability(0.1, 0, false), 0.001, "0 combos no modifier");
        assertEquals(0.15, GolemCombatComboLogic.calculateStunProbability(0.1, 1, false), 0.001, "1 combo no modifier");
        assertEquals(0.3, GolemCombatComboLogic.calculateStunProbability(0.1, 0, true), 0.001, "0 combos with modifier");
        assertEquals(0.35, GolemCombatComboLogic.calculateStunProbability(0.1, 1, true), 0.001, "1 combo with modifier");

        // Edge cases
        assertEquals(1.0, GolemCombatComboLogic.calculateStunProbability(0.9, 5, true), 0.001, "Probability should be capped at 1.0");
        assertEquals(0.0, GolemCombatComboLogic.calculateStunProbability(-0.1, 0, false), 0.001, "Probability should be capped at 0.0");
        assertEquals(0.1, GolemCombatComboLogic.calculateStunProbability(0.1, -2, false), 0.001, "Negative combos handled properly");
    }
}
