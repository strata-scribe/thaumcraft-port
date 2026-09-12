package thaumcraft.common.tiles.devices.logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FocalManipulatorCapacityLogicTest {

    @Test
    public void testInvalidTier() {
        assertEquals(0, FocalManipulatorCapacityLogic.calculateComplexityBudget(0, 100));
        assertEquals(0, FocalManipulatorCapacityLogic.calculateComplexityBudget(-1, 100));
    }

    @Test
    public void testTier1() {
        // Base: 15 * 1 = 15
        // Max bonus: 10 * 1 = 10

        // 0 XP -> 15 + 0 = 15
        assertEquals(15, FocalManipulatorCapacityLogic.calculateComplexityBudget(1, 0));

        // 5 XP -> 15 + 5 = 20
        assertEquals(20, FocalManipulatorCapacityLogic.calculateComplexityBudget(1, 5));

        // 10 XP -> 15 + 10 = 25
        assertEquals(25, FocalManipulatorCapacityLogic.calculateComplexityBudget(1, 10));

        // 20 XP (Cap) -> 15 + 10 = 25
        assertEquals(25, FocalManipulatorCapacityLogic.calculateComplexityBudget(1, 20));
    }

    @Test
    public void testTier2() {
        // Base: 15 * 2 = 30
        // Max bonus: 10 * 2 = 20

        // 0 XP -> 30 + 0 = 30
        assertEquals(30, FocalManipulatorCapacityLogic.calculateComplexityBudget(2, 0));

        // 15 XP -> 30 + 15 = 45
        assertEquals(45, FocalManipulatorCapacityLogic.calculateComplexityBudget(2, 15));

        // 20 XP -> 30 + 20 = 50
        assertEquals(50, FocalManipulatorCapacityLogic.calculateComplexityBudget(2, 20));

        // 100 XP (Cap) -> 30 + 20 = 50
        assertEquals(50, FocalManipulatorCapacityLogic.calculateComplexityBudget(2, 100));
    }

    @Test
    public void testTier3() {
        // Base: 15 * 3 = 45
        // Max bonus: 10 * 3 = 30

        // 0 XP -> 45 + 0 = 45
        assertEquals(45, FocalManipulatorCapacityLogic.calculateComplexityBudget(3, 0));

        // 25 XP -> 45 + 25 = 70
        assertEquals(70, FocalManipulatorCapacityLogic.calculateComplexityBudget(3, 25));

        // 30 XP -> 45 + 30 = 75
        assertEquals(75, FocalManipulatorCapacityLogic.calculateComplexityBudget(3, 30));

        // 50 XP (Cap) -> 45 + 30 = 75
        assertEquals(75, FocalManipulatorCapacityLogic.calculateComplexityBudget(3, 50));
    }
}
