package thaumcraft.common.tiles.crafting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class InfusionSymmetryLogicTest {

    private InfusionSymmetryLogic logic;
    private TestWorldScannable world;
    private InfusionSymmetryLogic.Coordinate center;
    private Set<InfusionSymmetryLogic.Coordinate> stabilisers;

    private static class BlockData {
        Object type;
        float stabAmt;
        boolean hasPenalty;
        float penaltyAmt;

        BlockData(Object type, float stabAmt, boolean hasPenalty, float penaltyAmt) {
            this.type = type;
            this.stabAmt = stabAmt;
            this.hasPenalty = hasPenalty;
            this.penaltyAmt = penaltyAmt;
        }
    }

    private static class TestWorldScannable implements InfusionSymmetryLogic.IWorldScannable {
        Map<InfusionSymmetryLogic.Coordinate, BlockData> blocks = new HashMap<>();

        void setBlock(InfusionSymmetryLogic.Coordinate pos, Object type, float stabAmt) {
            blocks.put(pos, new BlockData(type, stabAmt, false, 0));
        }

        void setBlockWithPenalty(InfusionSymmetryLogic.Coordinate pos, Object type, float stabAmt, float penaltyAmt) {
            blocks.put(pos, new BlockData(type, stabAmt, true, penaltyAmt));
        }

        @Override
        public Object getBlockType(InfusionSymmetryLogic.Coordinate pos) {
            return blocks.containsKey(pos) ? blocks.get(pos).type : "AIR";
        }

        @Override
        public float getStabilizationAmount(InfusionSymmetryLogic.Coordinate pos) {
            return blocks.containsKey(pos) ? blocks.get(pos).stabAmt : 0.1f;
        }

        @Override
        public boolean hasSymmetryPenalty(InfusionSymmetryLogic.Coordinate pos1, InfusionSymmetryLogic.Coordinate pos2) {
            if (blocks.containsKey(pos1) && blocks.get(pos1).hasPenalty) return true;
            if (blocks.containsKey(pos2) && blocks.get(pos2).hasPenalty) return true;
            return false;
        }

        @Override
        public float getSymmetryPenalty(InfusionSymmetryLogic.Coordinate pos) {
            return blocks.containsKey(pos) ? blocks.get(pos).penaltyAmt : 0f;
        }
    }

    @BeforeEach
    void setUp() {
        logic = new InfusionSymmetryLogic();
        world = new TestWorldScannable();
        center = new InfusionSymmetryLogic.Coordinate(0, 0, 0);
        stabilisers = new HashSet<>();
    }

    @Test
    void testPerfectSymmetry() {
        InfusionSymmetryLogic.Coordinate c1 = new InfusionSymmetryLogic.Coordinate(2, 0, 0);
        InfusionSymmetryLogic.Coordinate c2 = new InfusionSymmetryLogic.Coordinate(-2, 0, 0);

        world.setBlock(c1, "CANDLE", 0.1f);
        world.setBlock(c2, "CANDLE", 0.1f);

        stabilisers.add(c1);
        stabilisers.add(c2);

        logic.evaluateSymmetry(center, stabilisers, world);

        assertEquals(0.1f, logic.getStabilityReplenish(), 0.001f);
        assertTrue(logic.getProblemBlocks().isEmpty());
    }

    @Test
    void testAsymmetryDifferentBlocks() {
        InfusionSymmetryLogic.Coordinate c1 = new InfusionSymmetryLogic.Coordinate(2, 0, 0);
        InfusionSymmetryLogic.Coordinate c2 = new InfusionSymmetryLogic.Coordinate(-2, 0, 0);

        world.setBlock(c1, "CANDLE", 0.1f);
        world.setBlock(c2, "SKULL", 0.1f);

        stabilisers.add(c1);
        stabilisers.add(c2);

        logic.evaluateSymmetry(center, stabilisers, world);

        assertEquals(-0.1f, logic.getStabilityReplenish(), 0.001f);
        assertTrue(logic.getProblemBlocks().contains(c1) || logic.getProblemBlocks().contains(c2));
    }

    @Test
    void testAsymmetryMissingBlock() {
        InfusionSymmetryLogic.Coordinate c1 = new InfusionSymmetryLogic.Coordinate(2, 0, 0);

        world.setBlock(c1, "CANDLE", 0.1f);

        stabilisers.add(c1);

        logic.evaluateSymmetry(center, stabilisers, world);

        assertEquals(-0.1f, logic.getStabilityReplenish(), 0.001f);
        assertTrue(logic.getProblemBlocks().contains(c1));
    }

    @Test
    void testDiminishingReturns() {
        // Two pairs of candles
        InfusionSymmetryLogic.Coordinate c1 = new InfusionSymmetryLogic.Coordinate(2, 0, 0);
        InfusionSymmetryLogic.Coordinate c2 = new InfusionSymmetryLogic.Coordinate(-2, 0, 0);
        InfusionSymmetryLogic.Coordinate c3 = new InfusionSymmetryLogic.Coordinate(0, 0, 2);
        InfusionSymmetryLogic.Coordinate c4 = new InfusionSymmetryLogic.Coordinate(0, 0, -2);

        world.setBlock(c1, "CANDLE", 0.1f);
        world.setBlock(c2, "CANDLE", 0.1f);
        world.setBlock(c3, "CANDLE", 0.1f);
        world.setBlock(c4, "CANDLE", 0.1f);

        stabilisers.add(c1);
        stabilisers.add(c2);
        stabilisers.add(c3);
        stabilisers.add(c4);

        logic.evaluateSymmetry(center, stabilisers, world);

        // First pair gives 0.1
        // Second pair gives 0.1 * 0.75 = 0.075
        // Total = 0.175
        assertEquals(0.175f, logic.getStabilityReplenish(), 0.001f);
    }

    @Test
    void testSymmetryPenalty() {
        InfusionSymmetryLogic.Coordinate c1 = new InfusionSymmetryLogic.Coordinate(2, 0, 0);
        InfusionSymmetryLogic.Coordinate c2 = new InfusionSymmetryLogic.Coordinate(-2, 0, 0);

        world.setBlockWithPenalty(c1, "PEDESTAL", 0.1f, 0.2f);
        world.setBlockWithPenalty(c2, "PEDESTAL", 0.1f, 0.2f);

        stabilisers.add(c1);
        stabilisers.add(c2);

        logic.evaluateSymmetry(center, stabilisers, world);

        // Penalty subtracted: -0.2f
        assertEquals(-0.2f, logic.getStabilityReplenish(), 0.001f);
        assertTrue(logic.getProblemBlocks().contains(c1) || logic.getProblemBlocks().contains(c2));
    }
}
