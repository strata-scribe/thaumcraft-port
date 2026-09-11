package thaumcraft.common.golems;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Pure Java logic class for Golem Harvest Seals.
 * Decoupled from Minecraft/Forge to allow for fast, reliable unit testing.
 */
public class SealHarvestLogic {

    /**
     * Represents a simple 3D position to avoid Minecraft BlockPos dependency.
     */
    public record Pos(int x, int y, int z) implements Comparable<Pos> {
        @Override
        public int compareTo(Pos o) {
            if (this.y != o.y) return Integer.compare(this.y, o.y);
            if (this.x != o.x) return Integer.compare(this.x, o.x);
            return Integer.compare(this.z, o.z);
        }
    }

    /**
     * Represents the state of a crop in the world.
     */
    public record CropState(Pos pos, boolean isCrop, int age, int maxAge) {}

    /**
     * Represents a task generated for a golem to perform.
     */
    public record HarvestTask(Pos pos, boolean queueReplant) {}

    /**
     * Checks if the given crop state represents a mature crop that is ready to harvest.
     *
     * @param crop The state of the crop to check.
     * @return true if the crop is a valid crop and its age is equal to or greater than its max age.
     */
    public static boolean isMatureCrop(CropState crop) {
        if (crop == null || !crop.isCrop()) return false;
        return crop.age() >= crop.maxAge();
    }

    /**
     * Scans a list of crop states within the seal area, filters for fully grown crops,
     * prioritizes them based on position, and creates harvest tasks.
     *
     * @param crops List of crop states observed in the seal's area.
     * @return A list of harvest tasks, sorted deterministically by position.
     */
    public static List<HarvestTask> scanAndQueueHarvestTasks(List<CropState> crops) {
        if (crops == null || crops.isEmpty()) {
            return new ArrayList<>();
        }

        return crops.stream()
                .filter(SealHarvestLogic::isMatureCrop)
                .sorted(Comparator.comparing(CropState::pos))
                .map(crop -> new HarvestTask(crop.pos(), true))
                .collect(Collectors.toList());
    }
}
