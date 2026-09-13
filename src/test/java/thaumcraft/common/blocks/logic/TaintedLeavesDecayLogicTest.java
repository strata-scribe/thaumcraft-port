package thaumcraft.common.blocks.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TaintedLeavesDecayLogicTest {

    @Test
    public void testCalculateDesiccationSupported() {
        assertEquals(0, TaintedLeavesDecayLogic.calculateDesiccation(0, 3, 5));
        assertEquals(0, TaintedLeavesDecayLogic.calculateDesiccation(2, 5, 5));
    }

    @Test
    public void testCalculateDesiccationUnsupported() {
        assertEquals(1, TaintedLeavesDecayLogic.calculateDesiccation(0, 6, 5));
        assertEquals(3, TaintedLeavesDecayLogic.calculateDesiccation(2, 7, 5));
    }

    @Test
    public void testDetermineDropsHealthy() {
        // Base chances: Sapling 5%, Fiber 5%
        assertEquals(TaintedLeavesDecayLogic.SAPLING, TaintedLeavesDecayLogic.determineDrops(0.04, 0, false));
        assertEquals(TaintedLeavesDecayLogic.TAINT_FIBER, TaintedLeavesDecayLogic.determineDrops(0.06, 0, false));
        assertEquals(TaintedLeavesDecayLogic.TAINT_FIBER, TaintedLeavesDecayLogic.determineDrops(0.09, 0, false));
        assertEquals(TaintedLeavesDecayLogic.NONE, TaintedLeavesDecayLogic.determineDrops(0.11, 0, false));
    }

    @Test
    public void testDetermineDropsHealthyWithFortune() {
        // Fortune 2: Sapling 5% + 4% = 9%, Fiber 5% + 10% = 15% (Range: Sapling 0-0.09, Fiber 0.09-0.24)
        assertEquals(TaintedLeavesDecayLogic.SAPLING, TaintedLeavesDecayLogic.determineDrops(0.08, 2, false));
        assertEquals(TaintedLeavesDecayLogic.TAINT_FIBER, TaintedLeavesDecayLogic.determineDrops(0.15, 2, false));
        assertEquals(TaintedLeavesDecayLogic.NONE, TaintedLeavesDecayLogic.determineDrops(0.25, 2, false));
    }

    @Test
    public void testDetermineDropsDesiccated() {
        // Base chances: Sapling 0%, Fiber 20% (Range: Sapling none, Fiber 0-0.20)
        assertEquals(TaintedLeavesDecayLogic.TAINT_FIBER, TaintedLeavesDecayLogic.determineDrops(0.01, 0, true));
        assertEquals(TaintedLeavesDecayLogic.TAINT_FIBER, TaintedLeavesDecayLogic.determineDrops(0.19, 0, true));
        assertEquals(TaintedLeavesDecayLogic.NONE, TaintedLeavesDecayLogic.determineDrops(0.21, 0, true));
    }

    @Test
    public void testDetermineDropsDesiccatedWithFortune() {
        // Fortune 3: Sapling 0%, Fiber 20% + 15% = 35% (Range: Sapling none, Fiber 0-0.35)
        assertEquals(TaintedLeavesDecayLogic.TAINT_FIBER, TaintedLeavesDecayLogic.determineDrops(0.05, 3, true));
        assertEquals(TaintedLeavesDecayLogic.TAINT_FIBER, TaintedLeavesDecayLogic.determineDrops(0.34, 3, true));
        assertEquals(TaintedLeavesDecayLogic.NONE, TaintedLeavesDecayLogic.determineDrops(0.36, 3, true));
    }
}
