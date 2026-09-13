package thaumcraft.common.entities.monster.wisp;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WispChainLightningLogicTest {

    @Test
    public void testFindSecondaryTargets_BasicChain() {
        WispChainLightningLogic.Target t1 = new WispChainLightningLogic.Target(2, 2.0, 0.0, 0.0);
        WispChainLightningLogic.Target t2 = new WispChainLightningLogic.Target(3, 4.0, 0.0, 0.0);
        WispChainLightningLogic.Target t3 = new WispChainLightningLogic.Target(4, 6.0, 0.0, 0.0);

        List<WispChainLightningLogic.Target> potentialTargets = Arrays.asList(t1, t2, t3);

        double[] initialPos = {0.0, 0.0, 0.0};

        List<WispChainLightningLogic.Target> selected = WispChainLightningLogic.findSecondaryTargets(1, initialPos, potentialTargets, 3.0, 3);

        assertEquals(3, selected.size());
        assertEquals(2, selected.get(0).entityId);
        assertEquals(3, selected.get(1).entityId);
        assertEquals(4, selected.get(2).entityId);
    }

    @Test
    public void testFindSecondaryTargets_PrioritizeClosest() {
        WispChainLightningLogic.Target far = new WispChainLightningLogic.Target(2, 3.0, 0.0, 0.0);
        WispChainLightningLogic.Target close = new WispChainLightningLogic.Target(3, 1.0, 0.0, 0.0);

        List<WispChainLightningLogic.Target> potentialTargets = Arrays.asList(far, close);

        double[] initialPos = {0.0, 0.0, 0.0};

        List<WispChainLightningLogic.Target> selected = WispChainLightningLogic.findSecondaryTargets(1, initialPos, potentialTargets, 5.0, 2);

        assertEquals(2, selected.size());
        assertEquals(3, selected.get(0).entityId); // Should hit close first
        assertEquals(2, selected.get(1).entityId);
    }

    @Test
    public void testFindSecondaryTargets_MaxBounces() {
        WispChainLightningLogic.Target t1 = new WispChainLightningLogic.Target(2, 1.0, 0.0, 0.0);
        WispChainLightningLogic.Target t2 = new WispChainLightningLogic.Target(3, 2.0, 0.0, 0.0);
        WispChainLightningLogic.Target t3 = new WispChainLightningLogic.Target(4, 3.0, 0.0, 0.0);

        List<WispChainLightningLogic.Target> potentialTargets = Arrays.asList(t1, t2, t3);

        double[] initialPos = {0.0, 0.0, 0.0};

        List<WispChainLightningLogic.Target> selected = WispChainLightningLogic.findSecondaryTargets(1, initialPos, potentialTargets, 5.0, 1);

        assertEquals(1, selected.size());
        assertEquals(2, selected.get(0).entityId);
    }

    @Test
    public void testFindSecondaryTargets_OutOfRange() {
        WispChainLightningLogic.Target close = new WispChainLightningLogic.Target(2, 1.0, 0.0, 0.0);
        WispChainLightningLogic.Target far = new WispChainLightningLogic.Target(3, 10.0, 0.0, 0.0); // Out of jump radius from close

        List<WispChainLightningLogic.Target> potentialTargets = Arrays.asList(close, far);

        double[] initialPos = {0.0, 0.0, 0.0};

        List<WispChainLightningLogic.Target> selected = WispChainLightningLogic.findSecondaryTargets(1, initialPos, potentialTargets, 5.0, 2);

        assertEquals(1, selected.size());
        assertEquals(2, selected.get(0).entityId);
    }

    @Test
    public void testFindSecondaryTargets_IgnoreInitial() {
        WispChainLightningLogic.Target initialTarget = new WispChainLightningLogic.Target(1, 1.0, 0.0, 0.0); // Same ID as initial
        WispChainLightningLogic.Target t2 = new WispChainLightningLogic.Target(2, 2.0, 0.0, 0.0);

        List<WispChainLightningLogic.Target> potentialTargets = Arrays.asList(initialTarget, t2);

        double[] initialPos = {1.0, 0.0, 0.0};

        List<WispChainLightningLogic.Target> selected = WispChainLightningLogic.findSecondaryTargets(1, initialPos, potentialTargets, 5.0, 2);

        assertEquals(1, selected.size());
        assertEquals(2, selected.get(0).entityId);
    }

    @Test
    public void testComputeDamageDegradation() {
        float initialDamage = 10.0f;
        float factor = 0.8f;

        assertEquals(10.0f, WispChainLightningLogic.computeDamageDegradation(initialDamage, 0, factor), 0.01f);
        assertEquals(8.0f, WispChainLightningLogic.computeDamageDegradation(initialDamage, 1, factor), 0.01f);
        assertEquals(6.4f, WispChainLightningLogic.computeDamageDegradation(initialDamage, 2, factor), 0.01f);
        assertEquals(5.12f, WispChainLightningLogic.computeDamageDegradation(initialDamage, 3, factor), 0.01f);
    }
}
