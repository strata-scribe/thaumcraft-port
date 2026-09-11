package thaumcraft.common.tiles.essentia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Decoupled logic class for essentia tube physics.
 * This class has zero Minecraft/Forge imports, allowing for easy unit testing.
 */
public class TubePhysicsLogic {

    /**
     * Calculates the suction drop over a single tube step.
     * Each tube step decreases suction by 1.
     * Minimum suction is 0.
     *
     * @param baseSuction the incoming suction amount
     * @return the suction amount after dropping by 1
     */
    public static int calculateSuctionDrop(int baseSuction) {
        return Math.max(0, baseSuction - 1);
    }

    /**
     * Calculates the backpressure drop over a single tube step.
     * Each tube step decreases backpressure by 1.
     * Minimum backpressure is 0.
     *
     * @param baseBackpressure the incoming backpressure amount
     * @return the backpressure amount after dropping by 1
     */
    public static int calculateBackpressureDrop(int baseBackpressure) {
        return Math.max(0, baseBackpressure - 1);
    }

    /**
     * Determines if suction flow can pass through a valve.
     * Valves only allow suction to flow in the opposite direction of the valve.
     * e.g., If a valve points NORTH (0), suction flows SOUTH -> NORTH. Thus, if checking if suction
     * can flow *from* SOUTH (1), it returns true.
     *
     * @param isValve        true if the current tube is a valve
     * @param valveDirection the integer direction the valve is facing (e.g. 0-5)
     * @param flowDirection  the integer direction the suction is attempting to flow (e.g. 0-5)
     * @return true if the flow is permitted, false otherwise
     */
    public static boolean canFlowThroughValve(boolean isValve, int valveDirection, int flowDirection) {
        if (!isValve) {
            return true;
        }
        // Assume valveDirection and flowDirection correspond to 3D directions where 0=DOWN, 1=UP, 2=NORTH, 3=SOUTH, 4=WEST, 5=EAST
        // Suction flows backwards against the direction the valve points.
        // If valve points NORTH (2), it accepts flow coming from SOUTH (3), meaning flow is towards NORTH (2).
        // Therefore, flow is allowed only if the flow direction equals the valve direction.
        return valveDirection == flowDirection;
    }

    /**
     * Represents a node in the pressure graph.
     */
    public static class Node {
        public final int id;
        public int suction;
        public int essentia;
        public int backpressure;

        public Node(int id) {
            this.id = id;
            this.suction = 0;
            this.essentia = 0;
            this.backpressure = 0;
        }
    }

    /**
     * Represents a connection between two nodes.
     */
    public static class Connection {
        public final Node source;
        public final Node target;
        public final boolean isValve;
        public final int valveDirection; // Relative to the connection flow
        public final int flowDirection;

        public Connection(Node source, Node target, boolean isValve, int valveDirection, int flowDirection) {
            this.source = source;
            this.target = target;
            this.isValve = isValve;
            this.valveDirection = valveDirection;
            this.flowDirection = flowDirection;
        }
    }

    /**
     * Simulates suction propagation across a graph of nodes.
     *
     * @param nodes       list of all nodes
     * @param connections list of all connections
     */
    public static void simulateSuction(List<Node> nodes, List<Connection> connections) {
        boolean changed = true;
        int maxIterations = 1000;
        int iterations = 0;

        while (changed && iterations < maxIterations) {
            changed = false;
            iterations++;

            Map<Integer, Integer> newSuctions = new HashMap<>();

            for (Connection conn : connections) {
                // Propagate from source to target
                if (canFlowThroughValve(conn.isValve, conn.valveDirection, conn.flowDirection)) {
                    int propagatedSuction = calculateSuctionDrop(conn.source.suction);
                    if (propagatedSuction > conn.target.suction) {
                        int currentBest = newSuctions.getOrDefault(conn.target.id, conn.target.suction);
                        if (propagatedSuction > currentBest) {
                            newSuctions.put(conn.target.id, propagatedSuction);
                            changed = true;
                        }
                    }
                }
            }

            for (Map.Entry<Integer, Integer> entry : newSuctions.entrySet()) {
                for (Node node : nodes) {
                    if (node.id == entry.getKey()) {
                        node.suction = entry.getValue();
                        break;
                    }
                }
            }
        }
    }

    /**
     * Simulates backpressure propagation across a graph of nodes.
     *
     * @param nodes       list of all nodes
     * @param connections list of all connections
     */
    public static void simulateBackpressure(List<Node> nodes, List<Connection> connections) {
        boolean changed = true;
        int maxIterations = 1000;
        int iterations = 0;

        while (changed && iterations < maxIterations) {
            changed = false;
            iterations++;

            Map<Integer, Integer> newBackpressures = new HashMap<>();

            for (Connection conn : connections) {
                // Propagate from source to target
                if (canFlowThroughValve(conn.isValve, conn.valveDirection, conn.flowDirection)) {
                    int propagatedBackpressure = calculateBackpressureDrop(conn.source.backpressure);
                    if (propagatedBackpressure > conn.target.backpressure) {
                        int currentBest = newBackpressures.getOrDefault(conn.target.id, conn.target.backpressure);
                        if (propagatedBackpressure > currentBest) {
                            newBackpressures.put(conn.target.id, propagatedBackpressure);
                            changed = true;
                        }
                    }
                }
            }

            for (Map.Entry<Integer, Integer> entry : newBackpressures.entrySet()) {
                for (Node node : nodes) {
                    if (node.id == entry.getKey()) {
                        node.backpressure = entry.getValue();
                        break;
                    }
                }
            }
        }
    }
}
