package thaumcraft.common.casters;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class FocusChainLogicTest {

    private static class DummyTarget implements FocusChainLogic.ChainTarget {
        private final int id;
        private final double x;
        private final double y;
        private final double z;

        public DummyTarget(int id, double x, double y, double z) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override public int getId() { return id; }
        @Override public double getX() { return x; }
        @Override public double getY() { return y; }
        @Override public double getZ() { return z; }
    }

    @Test
    @DisplayName("Damage falloff curve (20% reduction per jump)")
    void testDamageFalloff() {
        assertEquals(100.0f, FocusChainLogic.calculateFalloff(100.0f, 0), 0.001f);
        assertEquals(80.0f, FocusChainLogic.calculateFalloff(100.0f, 1), 0.001f);
        assertEquals(64.0f, FocusChainLogic.calculateFalloff(100.0f, 2), 0.001f);
        assertEquals(51.2f, FocusChainLogic.calculateFalloff(100.0f, 3), 0.001f);
    }

    @Test
    @DisplayName("Target selection logic avoids already hit targets and respects radius")
    void testTargetSelection() {
        DummyTarget primary = new DummyTarget(1, 0, 0, 0);
        List<FocusChainLogic.ChainTarget> available = new ArrayList<>();

        DummyTarget t2 = new DummyTarget(2, 2, 0, 0);
        DummyTarget t3 = new DummyTarget(3, 3, 0, 0);
        DummyTarget t4 = new DummyTarget(4, 10, 0, 0);

        available.add(t2);
        available.add(t3);
        available.add(t4);

        List<FocusChainLogic.ChainTarget> chain = FocusChainLogic.getSecondaryTargets(available, primary, 5, 5.0);

        assertEquals(2, chain.size());
        assertEquals(2, chain.get(0).getId());
        assertEquals(3, chain.get(1).getId());
    }

    @Test
    @DisplayName("Target selection logic respects max secondary target count (k)")
    void testMaxSecondaryCount() {
        DummyTarget primary = new DummyTarget(1, 0, 0, 0);
        List<FocusChainLogic.ChainTarget> available = new ArrayList<>();

        available.add(new DummyTarget(2, 1, 0, 0));
        available.add(new DummyTarget(3, 2, 0, 0));
        available.add(new DummyTarget(4, 3, 0, 0));
        available.add(new DummyTarget(5, 4, 0, 0));

        List<FocusChainLogic.ChainTarget> chain = FocusChainLogic.getSecondaryTargets(available, primary, 2, 5.0);

        assertEquals(2, chain.size());
        assertEquals(2, chain.get(0).getId());
        assertEquals(3, chain.get(1).getId());
    }

    @Test
    @DisplayName("Target selection logic prioritizes closest targets dynamically")
    void testClosestTargetPriority() {
        DummyTarget primary = new DummyTarget(1, 0, 0, 0);
        List<FocusChainLogic.ChainTarget> available = new ArrayList<>();

        DummyTarget t2 = new DummyTarget(2, 0, 4, 0);
        DummyTarget t3 = new DummyTarget(3, 0, -2, 0);
        DummyTarget t4 = new DummyTarget(4, 0, -5, 0);

        available.add(t2);
        available.add(t3);
        available.add(t4);

        List<FocusChainLogic.ChainTarget> chain = FocusChainLogic.getSecondaryTargets(available, primary, 3, 5.0);

        assertEquals(2, chain.size());
        assertEquals(3, chain.get(0).getId());
        assertEquals(4, chain.get(1).getId());
    }

    @Test
    @DisplayName("Target selection logic drops targets outside radius dynamically")
    void testRadiusDrop() {
        DummyTarget primary = new DummyTarget(1, 0, 0, 0);
        List<FocusChainLogic.ChainTarget> available = new ArrayList<>();

        DummyTarget t2 = new DummyTarget(2, 0, 4, 0);
        DummyTarget t3 = new DummyTarget(3, 0, -2, 0);
        DummyTarget t4 = new DummyTarget(4, 0, -6, 0);

        available.add(t2);
        available.add(t3);
        available.add(t4);

        List<FocusChainLogic.ChainTarget> chain = FocusChainLogic.getSecondaryTargets(available, primary, 3, 4.1);

        assertEquals(2, chain.size());
        assertEquals(3, chain.get(0).getId());
        assertEquals(4, chain.get(1).getId());
    }
}
