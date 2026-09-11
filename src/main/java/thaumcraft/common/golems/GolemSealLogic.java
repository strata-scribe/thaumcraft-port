package thaumcraft.common.golems;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Pure Java logic engine for Golemancy seals, priority scoring, task queues,
 * trait modifications, area geometry, and inventory logistics.
 *
 * Invariant: ZERO imports from net.minecraft.* or net.neoforged.* to allow
 * complete JUnit 5 test isolation without classloader bootstrap crashes.
 */
public final class GolemSealLogic {

    public static final double PRIORITY_WEIGHT = 256.0;

    private GolemSealLogic() {}

    // =========================================================================
    // 1. Priority Scoring & Task Sorting
    // =========================================================================

    /**
     * Calculates the effective distance of a task given its squared Euclidean
     * distance and task priority level.
     *
     * Formula: distSq - (priority * 256.0)
     * Every +1 priority is mathematically equivalent to 256 blocks^2 (16 blocks closer).
     */
    public static double effectiveDistance(double distSq, int priority) {
        return distSq - (priority * PRIORITY_WEIGHT);
    }

    /**
     * Alias for {@link #effectiveDistance(double, int)}.
     */
    public static double calculatePriorityScore(double distSq, int priority) {
        return effectiveDistance(distSq, priority);
    }

    /**
     * Candidate representation for evaluating task assignment.
     */
    public record TaskCandidate(
            int taskId,
            double distanceSq,
            int priority,
            byte color,
            Set<String> requiredTraits,
            Set<String> forbiddenTraits
    ) {}

    /**
     * Determines whether a golem with given color and traits can accept a task.
     *
     * Rules:
     * - Color 0 on golem or task means universal match.
     * - If both golem and task are colored, they must match.
     * - If the golem possesses ANY forbidden trait, the task is rejected.
     * - If the golem is missing ANY required trait, the task is rejected.
     */
    public static boolean isTaskValidForGolem(TaskCandidate task, byte golemColor, Set<String> golemTraits) {
        if (task == null) return false;

        // Color check
        if (golemColor != 0 && task.color() != 0 && golemColor != task.color()) {
            return false;
        }

        // Set up lowercase traits set
        Set<String> traitsLower = (golemTraits == null) ? Set.of() : golemTraits.stream()
                .filter(s -> s != null)
                .map(String::toLowerCase)
                .collect(java.util.stream.Collectors.toSet());

        // Check forbidden traits
        if (task.forbiddenTraits() != null) {
            for (String f : task.forbiddenTraits()) {
                if (f != null && traitsLower.contains(f.toLowerCase())) {
                    return false;
                }
            }
        }

        // Check required traits
        if (task.requiredTraits() != null) {
            for (String r : task.requiredTraits()) {
                if (r != null && !traitsLower.contains(r.toLowerCase())) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Sorts candidate tasks in ascending order of effective distance (highest priority & closest first).
     */
    public static List<TaskCandidate> sortTasksByPriority(List<TaskCandidate> candidates) {
        if (candidates == null) return List.of();
        List<TaskCandidate> copy = new ArrayList<>(candidates);
        copy.sort(Comparator.comparingDouble(t -> effectiveDistance(t.distanceSq(), t.priority())));
        return copy;
    }

    // =========================================================================
    // 2. Task Lifespan & Expiration
    // =========================================================================

    /**
     * Checks if a task has expired based on elapsed ticks vs configured lifespan.
     */
    public static boolean isTaskExpired(int currentTick, int spawnTick, int lifespan) {
        if (lifespan <= 0) return true;
        return (currentTick - spawnTick) >= lifespan;
    }

    /**
     * Checks if a task is expired based on remaining lifespan ticks or suspension flag.
     */
    public static boolean isTaskExpired(short lifespan, boolean suspended) {
        return suspended || lifespan <= 0;
    }

    /**
     * Decrements task lifespan by 1 tick, floored at 0.
     */
    public static short decrementLifespan(short currentLifespan) {
        return (short) Math.max(0, currentLifespan - 1);
    }

    // =========================================================================
    // 3. Carrying Capacity & Item Logistics
    // =========================================================================

    /**
     * Checks whether a golem can carry incoming items given current items and max capacity.
     */
    public static boolean canGolemCarry(int currentItems, int maxCapacity, int incoming) {
        if (incoming <= 0) return true;
        if (maxCapacity <= 0) return false;
        if (currentItems < 0) return false;
        return (long) currentItems + incoming <= maxCapacity;
    }

    /**
     * Returns remaining capacity.
     */
    public static int remainingCapacity(int currentItems, int maxCapacity) {
        if (maxCapacity <= 0) return 0;
        return Math.max(0, maxCapacity - Math.max(0, currentItems));
    }

    /**
     * Calculates the amount of items to deposit into a container respecting stack limits.
     *
     * @param currentContainerCount items already in container
     * @param stackLimit maximum items allowed in container (0 or negative means no limit)
     * @param golemCarryingCount items currently carried by golem
     * @return count to transfer
     */
    public static int calculateDepositAmount(int currentContainerCount, int stackLimit, int golemCarryingCount) {
        if (golemCarryingCount <= 0) return 0;
        if (stackLimit <= 0) return golemCarryingCount; // No limit
        int space = stackLimit - currentContainerCount;
        return Math.max(0, Math.min(golemCarryingCount, space));
    }

    // =========================================================================
    // 4. Whitelist / Blacklist Item Filtering
    // =========================================================================

    /**
     * Checks if an item ID matches a filter list under whitelist or blacklist mode.
     * Supports optional ignoreMeta (ignoring suffix after '#').
     */
    public static boolean matchesFilter(String itemId, List<String> filterList, boolean isBlacklist, boolean ignoreMeta) {
        if (itemId == null) return isBlacklist;
        if (filterList == null || filterList.isEmpty()) {
            // Empty blacklist matches everything; empty whitelist matches nothing
            return isBlacklist;
        }

        String target = ignoreMeta && itemId.contains("#") ? itemId.substring(0, itemId.indexOf('#')) : itemId;
        boolean matched = false;

        for (String f : filterList) {
            if (f == null) continue;
            String filterEntry = ignoreMeta && f.contains("#") ? f.substring(0, f.indexOf('#')) : f;
            if (target.equalsIgnoreCase(filterEntry)) {
                matched = true;
                break;
            }
        }

        return isBlacklist ? !matched : matched;
    }

    /**
     * Rich item specification for component/tag-aware logistics filtering.
     */
    public record ItemSpec(String itemId, int count, String tagOrComponentHash) {}

    /**
     * Filters rich item specifications.
     */
    public static boolean matchesFilter(ItemSpec candidate, List<ItemSpec> filter, boolean isBlacklist, boolean matchComponents) {
        if (candidate == null) return isBlacklist;
        if (filter == null || filter.isEmpty()) return isBlacklist;

        boolean matched = false;
        for (ItemSpec f : filter) {
            if (f == null) continue;
            if (f.itemId().equalsIgnoreCase(candidate.itemId())) {
                if (!matchComponents) {
                    matched = true;
                    break;
                } else {
                    String h1 = candidate.tagOrComponentHash() == null ? "" : candidate.tagOrComponentHash();
                    String h2 = f.tagOrComponentHash() == null ? "" : f.tagOrComponentHash();
                    if (h1.equals(h2)) {
                        matched = true;
                        break;
                    }
                }
            }
        }

        return isBlacklist ? !matched : matched;
    }

    // =========================================================================
    // 5. Trait Modifiers & Attribute Calculations
    // =========================================================================

    public record MaterialStats(double healthMod, double armor, double damage) {}

    public record AttributeResult(
            double maxHealth,
            double armor,
            double speed,
            double damage,
            double knockbackRes
    ) {}

    /**
     * Calculates final golem attributes based on base material stats and active traits.
     *
     * Base Stats:
     * - Health: 10.0 + material.healthMod
     * - Armor: 0.0 + material.armor
     * - Speed: 0.3
     * - Damage: 1.0 + material.damage
     * - Knockback Resistance: 0.0
     *
     * Trait Effects:
     * - light: speed *= 1.2
     * - heavy: speed *= 0.8, knockbackRes += 0.5
     * - fragile: maxHealth *= 0.75
     * - armored: armor += 4.0
     * - brutal: damage += 2.0
     * - fighter: damage += 2.0
     * - wheeled: speed *= 1.25
     * - breaker: damage += 1.0
     */
    public static AttributeResult calculateAttributes(MaterialStats mat, Set<String> traits) {
        double hp = 10.0 + (mat != null ? mat.healthMod() : 0.0);
        double arm = 0.0 + (mat != null ? mat.armor() : 0.0);
        double spd = 0.3;
        double dmg = 1.0 + (mat != null ? mat.damage() : 0.0);
        double kb = 0.0;

        Set<String> traitsLower = (traits == null) ? Set.of() : traits.stream()
                .filter(s -> s != null)
                .map(String::toLowerCase)
                .collect(java.util.stream.Collectors.toSet());

        if (traitsLower.contains("light")) spd *= 1.2;
        if (traitsLower.contains("heavy")) {
            spd *= 0.8;
            kb += 0.5;
        }
        if (traitsLower.contains("fragile")) hp *= 0.75;
        if (traitsLower.contains("armored")) arm += 4.0;
        if (traitsLower.contains("brutal")) dmg += 2.0;
        if (traitsLower.contains("fighter")) dmg += 2.0;
        if (traitsLower.contains("wheeled")) spd *= 1.25;
        if (traitsLower.contains("breaker")) dmg += 1.0;

        return new AttributeResult(hp, arm, spd, dmg, kb);
    }

    public static int getMaxCarryStacks(Set<String> traits) {
        if (traits == null) return 1;
        return traits.stream().anyMatch(t -> t != null && t.equalsIgnoreCase("hauler")) ? 2 : 1;
    }

    // =========================================================================
    // 6. Area Bounding Box Geometry
    // =========================================================================

    public record AreaBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {}

    /**
     * Recreates the exact area bounding box expansion from seal position, face step offset,
     * and configured area dimensions (X, Y, Z).
     */
    public static AreaBox calculateAreaBounds(int posX, int posY, int posZ,
                                              int stepX, int stepY, int stepZ,
                                              int areaX, int areaY, int areaZ) {
        int originX = posX + stepX;
        int originY = posY + stepY;
        int originZ = posZ + stepZ;

        int spanX = stepX != 0 ? (areaX - 1) * stepX : 0;
        int spanY = stepY != 0 ? (areaY - 1) * stepY : 0;
        int spanZ = stepZ != 0 ? (areaZ - 1) * stepZ : 0;

        int radX = stepX == 0 ? areaX - 1 : 0;
        int radY = stepY == 0 ? areaY - 1 : 0;
        int radZ = stepZ == 0 ? areaZ - 1 : 0;

        int minX = Math.min(originX, originX + spanX) - radX;
        int maxX = Math.max(originX, originX + spanX) + radX;
        int minY = Math.min(originY, originY + spanY) - radY;
        int maxY = Math.max(originY, originY + spanY) + radY;
        int minZ = Math.min(originZ, originZ + spanZ) - radZ;
        int maxZ = Math.max(originZ, originZ + spanZ) + radZ;

        return new AreaBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    /**
     * Checks if coordinates (x, y, z) lie within the specified 3D bounding box (inclusive).
     */
    public static boolean isWithinSealArea(int x, int y, int z,
                                           int minX, int minY, int minZ,
                                           int maxX, int maxY, int maxZ) {
        int lowX = Math.min(minX, maxX);
        int highX = Math.max(minX, maxX);
        int lowY = Math.min(minY, maxY);
        int highY = Math.max(minY, maxY);
        int lowZ = Math.min(minZ, maxZ);
        int highZ = Math.max(minZ, maxZ);

        return x >= lowX && x <= highX &&
               y >= lowY && y <= highY &&
               z >= lowZ && z <= highZ;
    }

    // =========================================================================
    // 7. Wander & Patrol Calculations
    // =========================================================================

    /**
     * Determines whether golem has exceeded its tether leash distance from home.
     * Default leash distSq is 1024.0 (32 blocks).
     */
    public static boolean shouldReturnHome(double distSqToHome, double maxLeashDistSq) {
        return distSqToHome > maxLeashDistSq;
    }

    /**
     * Determines whether golem has arrived home within acceptable radius.
     * Default arrival distSq is 5.0 blocks.
     */
    public static boolean isAtHome(double distSqToHome, double minArrivalDistSq) {
        return distSqToHome <= minArrivalDistSq;
    }

    /**
     * Advances to the next patrol waypoint index in cyclic order.
     */
    public static int calculatePatrolWaypointIndex(int currentWaypoint, int totalWaypoints) {
        if (totalWaypoints <= 0) return 0;
        return (currentWaypoint + 1) % totalWaypoints;
    }
}
