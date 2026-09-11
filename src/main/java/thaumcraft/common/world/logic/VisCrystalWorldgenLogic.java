package thaumcraft.common.world.logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class VisCrystalWorldgenLogic {

    public static class CrystalPlacement {
        public final int x;
        public final int y;
        public final int z;
        public final int stage;

        public CrystalPlacement(int x, int y, int z, int stage) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.stage = stage;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CrystalPlacement that = (CrystalPlacement) o;
            return x == that.x && y == that.y && z == that.z;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, z);
        }
    }

    /**
     * Determines aspect affinity based on biome category.
     * Ignis in Nether/Desert, Aqua in Ocean/River, Aer in Mountains.
     * Default returns "terra".
     */
    public String getAspectForBiome(String biomeCategory) {
        if (biomeCategory == null) {
            return "terra";
        }
        String lower = biomeCategory.toLowerCase();
        if (lower.contains("nether") || lower.contains("desert")) {
            return "ignis";
        }
        if (lower.contains("ocean") || lower.contains("river") || lower.contains("water")) {
            return "aqua";
        }
        if (lower.contains("mountain") || lower.contains("hills") || lower.contains("extreme_hills")) {
            return "aer";
        }
        if (lower.contains("forest") || lower.contains("jungle") || lower.contains("swamp")) {
            return "terra";
        }
        if (lower.contains("plains")) {
            return "ordo";
        }
        if (lower.contains("taiga") || lower.contains("ice")) {
            return "perditio";
        }

        return "terra"; // fallback
    }

    /**
     * Calculates multi-block crystal cluster generation on cave surfaces.
     * Generates a central crystal at max stage (3), and randomly scatters others around it.
     */
    public List<CrystalPlacement> calculateClusterGeneration(int startX, int startY, int startZ, int size, long seed) {
        List<CrystalPlacement> placements = new ArrayList<>();
        if (size <= 0) return placements;

        Random random = new Random(seed);

        // Central placement (max stage)
        placements.add(new CrystalPlacement(startX, startY, startZ, 3));

        Set<CrystalPlacement> placedCoords = new HashSet<>();
        placedCoords.add(new CrystalPlacement(startX, startY, startZ, 3));

        int attempts = 0;
        int maxAttempts = size * 5; // To prevent infinite loops

        while (placements.size() < size && attempts < maxAttempts) {
            attempts++;
            // Generate offset within a small radius (e.g. -2 to +2 in each axis)
            int offsetX = startX + random.nextInt(5) - 2;
            int offsetY = startY + random.nextInt(5) - 2;
            int offsetZ = startZ + random.nextInt(5) - 2;

            // Random stage 0 to 2 for surrounding crystals
            int stage = random.nextInt(3);

            CrystalPlacement p = new CrystalPlacement(offsetX, offsetY, offsetZ, stage);
            if (!placedCoords.contains(p)) {
                placements.add(p);
                placedCoords.add(p);
            }
        }

        return placements;
    }
}
