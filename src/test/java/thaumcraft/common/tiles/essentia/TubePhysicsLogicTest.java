package thaumcraft.common.tiles.essentia;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TubePhysicsLogicTest {

    @Test
    public void testCalculateSuctionDrop() {
        assertEquals(99, TubePhysicsLogic.calculateSuctionDrop(100));
        assertEquals(0, TubePhysicsLogic.calculateSuctionDrop(1));
        assertEquals(0, TubePhysicsLogic.calculateSuctionDrop(0));
    }

    @Test
    public void testCalculateBackpressureDrop() {
        assertEquals(99, TubePhysicsLogic.calculateBackpressureDrop(100));
        assertEquals(0, TubePhysicsLogic.calculateBackpressureDrop(1));
        assertEquals(0, TubePhysicsLogic.calculateBackpressureDrop(0));
    }

    @Test
    public void testCanFlowThroughValve() {
        // Not a valve - always passes
        assertTrue(TubePhysicsLogic.canFlowThroughValve(false, 0, 1));
        assertTrue(TubePhysicsLogic.canFlowThroughValve(false, 2, 2));

        // Is a valve
        // Flow direction matches valve direction - passes
        assertTrue(TubePhysicsLogic.canFlowThroughValve(true, 2, 2));

        // Flow direction does not match valve direction - blocked
        assertFalse(TubePhysicsLogic.canFlowThroughValve(true, 2, 3));
    }

    @Test
    public void testSimulateSuction_BasicLinear() {
        TubePhysicsLogic.Node n1 = new TubePhysicsLogic.Node(1);
        TubePhysicsLogic.Node n2 = new TubePhysicsLogic.Node(2);
        TubePhysicsLogic.Node n3 = new TubePhysicsLogic.Node(3);

        n1.suction = 10;

        List<TubePhysicsLogic.Node> nodes = Arrays.asList(n1, n2, n3);
        List<TubePhysicsLogic.Connection> connections = Arrays.asList(
                new TubePhysicsLogic.Connection(n1, n2, false, 0, 0),
                new TubePhysicsLogic.Connection(n2, n3, false, 0, 0)
        );

        TubePhysicsLogic.simulateSuction(nodes, connections);

        assertEquals(10, n1.suction);
        assertEquals(9, n2.suction);
        assertEquals(8, n3.suction);
    }

    @Test
    public void testSimulateSuction_ValveBlocksReverseFlow() {
        TubePhysicsLogic.Node n1 = new TubePhysicsLogic.Node(1);
        TubePhysicsLogic.Node n2 = new TubePhysicsLogic.Node(2);
        TubePhysicsLogic.Node n3 = new TubePhysicsLogic.Node(3);

        n1.suction = 10;

        List<TubePhysicsLogic.Node> nodes = Arrays.asList(n1, n2, n3);
        // n1 -> n2 -> n3 (valve at n2 points backward to n1's flow direction? let's define carefully)
        // valveDirection = 2, flowDirection = 3 (Mismatch -> Blocked)
        List<TubePhysicsLogic.Connection> connections = Arrays.asList(
                new TubePhysicsLogic.Connection(n1, n2, false, 0, 0),
                new TubePhysicsLogic.Connection(n2, n3, true, 2, 3)
        );

        TubePhysicsLogic.simulateSuction(nodes, connections);

        assertEquals(10, n1.suction);
        assertEquals(9, n2.suction);
        assertEquals(0, n3.suction); // Blocked
    }

    @Test
    public void testSimulateSuction_ValveAllowsForwardFlow() {
        TubePhysicsLogic.Node n1 = new TubePhysicsLogic.Node(1);
        TubePhysicsLogic.Node n2 = new TubePhysicsLogic.Node(2);
        TubePhysicsLogic.Node n3 = new TubePhysicsLogic.Node(3);

        n1.suction = 10;

        List<TubePhysicsLogic.Node> nodes = Arrays.asList(n1, n2, n3);
        // n1 -> n2 -> n3
        // valveDirection = 2, flowDirection = 2 (Match -> Allowed)
        List<TubePhysicsLogic.Connection> connections = Arrays.asList(
                new TubePhysicsLogic.Connection(n1, n2, false, 0, 0),
                new TubePhysicsLogic.Connection(n2, n3, true, 2, 2)
        );

        TubePhysicsLogic.simulateSuction(nodes, connections);

        assertEquals(10, n1.suction);
        assertEquals(9, n2.suction);
        assertEquals(8, n3.suction);
    }

    @Test
    public void testSimulateBackpressure_BasicLinear() {
        TubePhysicsLogic.Node n1 = new TubePhysicsLogic.Node(1);
        TubePhysicsLogic.Node n2 = new TubePhysicsLogic.Node(2);
        TubePhysicsLogic.Node n3 = new TubePhysicsLogic.Node(3);

        n1.backpressure = 10;

        List<TubePhysicsLogic.Node> nodes = Arrays.asList(n1, n2, n3);
        List<TubePhysicsLogic.Connection> connections = Arrays.asList(
                new TubePhysicsLogic.Connection(n1, n2, false, 0, 0),
                new TubePhysicsLogic.Connection(n2, n3, false, 0, 0)
        );

        TubePhysicsLogic.simulateBackpressure(nodes, connections);

        assertEquals(10, n1.backpressure);
        assertEquals(9, n2.backpressure);
        assertEquals(8, n3.backpressure);
    }

    @Test
    public void testSimulateBackpressure_ValveBlocksReverseFlow() {
        TubePhysicsLogic.Node n1 = new TubePhysicsLogic.Node(1);
        TubePhysicsLogic.Node n2 = new TubePhysicsLogic.Node(2);
        TubePhysicsLogic.Node n3 = new TubePhysicsLogic.Node(3);

        n1.backpressure = 10;

        List<TubePhysicsLogic.Node> nodes = Arrays.asList(n1, n2, n3);
        // n1 -> n2 -> n3 (valve at n2 points backward to n1's flow direction)
        // valveDirection = 2, flowDirection = 3 (Mismatch -> Blocked)
        List<TubePhysicsLogic.Connection> connections = Arrays.asList(
                new TubePhysicsLogic.Connection(n1, n2, false, 0, 0),
                new TubePhysicsLogic.Connection(n2, n3, true, 2, 3)
        );

        TubePhysicsLogic.simulateBackpressure(nodes, connections);

        assertEquals(10, n1.backpressure);
        assertEquals(9, n2.backpressure);
        assertEquals(0, n3.backpressure); // Blocked
    }

    @Test
    public void testSimulateBackpressure_ValveAllowsForwardFlow() {
        TubePhysicsLogic.Node n1 = new TubePhysicsLogic.Node(1);
        TubePhysicsLogic.Node n2 = new TubePhysicsLogic.Node(2);
        TubePhysicsLogic.Node n3 = new TubePhysicsLogic.Node(3);

        n1.backpressure = 10;

        List<TubePhysicsLogic.Node> nodes = Arrays.asList(n1, n2, n3);
        // n1 -> n2 -> n3
        // valveDirection = 2, flowDirection = 2 (Match -> Allowed)
        List<TubePhysicsLogic.Connection> connections = Arrays.asList(
                new TubePhysicsLogic.Connection(n1, n2, false, 0, 0),
                new TubePhysicsLogic.Connection(n2, n3, true, 2, 2)
        );

        TubePhysicsLogic.simulateBackpressure(nodes, connections);

        assertEquals(10, n1.backpressure);
        assertEquals(9, n2.backpressure);
        assertEquals(8, n3.backpressure);
    }
}
