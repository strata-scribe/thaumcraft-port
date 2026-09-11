package thaumcraft.common.world.features.logic;

import java.util.Random;

public class EldritchMoundStructureLogic {

    public enum BlockType {
        EMPTY,
        CRUSTED_SANDSTONE,
        STAIRS,
        CORRIDOR,
        SPAWNER_ROOM,
        TREASURE_ROOM,
        WALL,
        FLOOR
    }

    /**
     * Generates a 3D matrix representing a crusted sandstone hill on the surface.
     * Dimensions are [width][height][depth], where width and depth are (radius * 2 + 1).
     */
    public BlockType[][][] generateMoundMatrix(int radius, int height) {
        int diameter = radius * 2 + 1;
        BlockType[][][] matrix = new BlockType[diameter][height][diameter];

        for (int y = 0; y < height; y++) {
            // Rough approximation of a hill - radius decreases as height increases
            double currentRadius = radius * (1.0 - ((double) y / height));

            for (int x = 0; x < diameter; x++) {
                for (int z = 0; z < diameter; z++) {
                    double dx = x - radius;
                    double dz = z - radius;
                    double dist = Math.sqrt(dx * dx + dz * dz);

                    if (dist <= currentRadius) {
                        matrix[x][y][z] = BlockType.CRUSTED_SANDSTONE;
                    } else {
                        matrix[x][y][z] = BlockType.EMPTY;
                    }
                }
            }
        }

        // Carve out a center hole for the stairs entrance
        for (int y = 0; y < height; y++) {
            matrix[radius][y][radius] = BlockType.EMPTY;
            if (radius - 1 >= 0) matrix[radius - 1][y][radius] = BlockType.EMPTY;
            if (radius + 1 < diameter) matrix[radius + 1][y][radius] = BlockType.EMPTY;
            if (radius - 1 >= 0) matrix[radius][y][radius - 1] = BlockType.EMPTY;
            if (radius + 1 < diameter) matrix[radius][y][radius + 1] = BlockType.EMPTY;
        }

        return matrix;
    }

    /**
     * Generates a 3D matrix representing downward spiral stairs.
     * The stairs form a 3x3 shaft. Dimensions are [3][depth][3].
     */
    public BlockType[][][] generateSpiralStairsMatrix(int depth) {
        BlockType[][][] matrix = new BlockType[3][depth][3];

        for (int y = 0; y < depth; y++) {
            for (int x = 0; x < 3; x++) {
                for (int z = 0; z < 3; z++) {
                    matrix[x][y][z] = BlockType.EMPTY;
                }
            }

            // Central pillar
            matrix[1][y][1] = BlockType.CRUSTED_SANDSTONE;

            // Spiral logic (descending from top y=depth-1 to bottom y=0)
            int stepIndex = y % 4; // 0 to 3

            // Depending on step index, place a stair in one of the 4 adjacent spots
            if (stepIndex == 0) {
                matrix[1][y][0] = BlockType.STAIRS;
            } else if (stepIndex == 1) {
                matrix[2][y][1] = BlockType.STAIRS;
            } else if (stepIndex == 2) {
                matrix[1][y][2] = BlockType.STAIRS;
            } else if (stepIndex == 3) {
                matrix[0][y][1] = BlockType.STAIRS;
            }
        }

        return matrix;
    }

    /**
     * Generates a 2D floor plan matrix for the underground labyrinth.
     * Dimensions are [size][size].
     */
    public BlockType[][] generateLabyrinthMatrix(long seed, int size) {
        BlockType[][] matrix = new BlockType[size][size];
        Random rand = new Random(seed);

        // Initialize all as wall
        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                matrix[x][z] = BlockType.WALL;
            }
        }

        // Center start
        int centerX = size / 2;
        int centerZ = size / 2;
        matrix[centerX][centerZ] = BlockType.CORRIDOR;

        // Simple random walk for corridors
        int x = centerX;
        int z = centerZ;
        for (int i = 0; i < size * size / 4; i++) {
            int dir = rand.nextInt(4);
            int nextX = x;
            int nextZ = z;

            if (dir == 0) nextX++;
            else if (dir == 1) nextX--;
            else if (dir == 2) nextZ++;
            else if (dir == 3) nextZ--;

            if (nextX > 0 && nextX < size - 1 && nextZ > 0 && nextZ < size - 1) {
                x = nextX;
                z = nextZ;
                matrix[x][z] = BlockType.CORRIDOR;
            }
        }

        // Generate rooms
        int numSpawnerRooms = 2 + rand.nextInt(3);
        int numTreasureRooms = 1 + rand.nextInt(2);

        placeRooms(matrix, rand, size, BlockType.SPAWNER_ROOM, numSpawnerRooms);
        placeRooms(matrix, rand, size, BlockType.TREASURE_ROOM, numTreasureRooms);

        return matrix;
    }

    private void placeRooms(BlockType[][] matrix, Random rand, int size, BlockType roomType, int numRooms) {
        int roomsPlaced = 0;
        int attempts = 0;
        while (roomsPlaced < numRooms && attempts < 100) {
            attempts++;
            int rx = 2 + rand.nextInt(size - 4);
            int rz = 2 + rand.nextInt(size - 4);

            // Check if room overlaps
            boolean canPlace = true;
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (matrix[rx + dx][rz + dz] != BlockType.WALL && matrix[rx + dx][rz + dz] != BlockType.CORRIDOR) {
                        canPlace = false;
                        break;
                    }
                }
                if (!canPlace) break;
            }

            if (canPlace) {
                // Check if it connects to a corridor
                boolean connects = false;
                for (int dx = -2; dx <= 2; dx++) {
                    for (int dz = -2; dz <= 2; dz++) {
                        if (rx + dx >= 0 && rx + dx < size && rz + dz >= 0 && rz + dz < size) {
                             if (matrix[rx + dx][rz + dz] == BlockType.CORRIDOR) {
                                 connects = true;
                             }
                        }
                    }
                }

                if (connects) {
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dz = -1; dz <= 1; dz++) {
                            matrix[rx + dx][rz + dz] = roomType;
                        }
                    }
                    roomsPlaced++;
                }
            }
        }
    }
}
