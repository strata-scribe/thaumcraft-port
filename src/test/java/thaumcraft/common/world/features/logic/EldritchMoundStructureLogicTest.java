package thaumcraft.common.world.features.logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EldritchMoundStructureLogicTest {

    @Test
    void testMoundGeneration() {
        EldritchMoundStructureLogic logic = new EldritchMoundStructureLogic();
        int radius = 5;
        int height = 5;
        EldritchMoundStructureLogic.BlockType[][][] matrix = logic.generateMoundMatrix(radius, height);

        int diameter = radius * 2 + 1;
        assertEquals(diameter, matrix.length);
        assertEquals(height, matrix[0].length);
        assertEquals(diameter, matrix[0][0].length);

        // Base should have some crusted sandstone
        assertTrue(matrix[radius][0][radius] == EldritchMoundStructureLogic.BlockType.EMPTY); // Hole in middle
        assertEquals(EldritchMoundStructureLogic.BlockType.CRUSTED_SANDSTONE, matrix[0][0][radius]); // Edge at base

        // Hole goes all the way down
        for (int y = 0; y < height; y++) {
            assertEquals(EldritchMoundStructureLogic.BlockType.EMPTY, matrix[radius][y][radius]);
        }
    }

    @Test
    void testSpiralStairsGeneration() {
        EldritchMoundStructureLogic logic = new EldritchMoundStructureLogic();
        int depth = 10;
        EldritchMoundStructureLogic.BlockType[][][] matrix = logic.generateSpiralStairsMatrix(depth);

        assertEquals(3, matrix.length);
        assertEquals(depth, matrix[0].length);
        assertEquals(3, matrix[0][0].length);

        // Central pillar
        for (int y = 0; y < depth; y++) {
            assertEquals(EldritchMoundStructureLogic.BlockType.CRUSTED_SANDSTONE, matrix[1][y][1]);
        }

        // Step verification
        assertEquals(EldritchMoundStructureLogic.BlockType.STAIRS, matrix[1][0][0]);
        assertEquals(EldritchMoundStructureLogic.BlockType.STAIRS, matrix[2][1][1]);
        assertEquals(EldritchMoundStructureLogic.BlockType.STAIRS, matrix[1][2][2]);
        assertEquals(EldritchMoundStructureLogic.BlockType.STAIRS, matrix[0][3][1]);
    }

    @Test
    void testLabyrinthGeneration() {
        EldritchMoundStructureLogic logic = new EldritchMoundStructureLogic();
        int size = 30;
        EldritchMoundStructureLogic.BlockType[][] matrix = logic.generateLabyrinthMatrix(12345L, size);

        assertEquals(size, matrix.length);
        assertEquals(size, matrix[0].length);

        // Center should be corridor
        assertEquals(EldritchMoundStructureLogic.BlockType.CORRIDOR, matrix[size / 2][size / 2]);

        boolean hasSpawners = false;
        boolean hasTreasure = false;

        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                if (matrix[x][z] == EldritchMoundStructureLogic.BlockType.SPAWNER_ROOM) {
                    hasSpawners = true;
                }
                if (matrix[x][z] == EldritchMoundStructureLogic.BlockType.TREASURE_ROOM) {
                    hasTreasure = true;
                }
            }
        }

        assertTrue(hasSpawners, "Should have spawner rooms");
        assertTrue(hasTreasure, "Should have treasure rooms");
    }
}
