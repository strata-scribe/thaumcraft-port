package thaumcraft.common.golems;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GolemSealLogic Pure Engine Tests")
public class GolemSealLogicTest {

    // =========================================================================
    // 1. Priority Scoring & Task Sorting
    // =========================================================================

    @Nested
    @DisplayName("Priority & Effective Distance Tests")
    class PriorityScoringTests {

        @Test
        @DisplayName("Effective distance formula: distSq - priority * 256.0")
        void testEffectiveDistanceCalculation() {
            // Priority 0: purely squared distance
            assertEquals(100.0, GolemSealLogic.effectiveDistance(100.0, 0), 0.001);
            assertEquals(100.0, GolemSealLogic.calculatePriorityScore(100.0, 0), 0.001);

            // Priority +1: subtracts 256.0 (equivalent to 16 blocks closer in squared space)
            assertEquals(144.0, GolemSealLogic.effectiveDistance(400.0, 1), 0.001);

            // Priority +2: subtracts 512.0
            assertEquals(-112.0, GolemSealLogic.effectiveDistance(400.0, 2), 0.001);

            // Negative Priority -1: adds 256.0
            assertEquals(356.0, GolemSealLogic.effectiveDistance(100.0, -1), 0.001);

            // Zero distance with high priority
            assertEquals(-1280.0, GolemSealLogic.effectiveDistance(0.0, 5), 0.001);
        }

        @Test
        @DisplayName("Priority overcomes physical distance in task ordering")
        void testPriorityOvercomesDistance() {
            // Task A: distSq = 169 (13 blocks away), priority 0 -> score = 169.0
            // Task B: distSq = 400 (20 blocks away), priority 1 -> score = 400 - 256 = 144.0
            // Task B should be ranked higher (lower score) than Task A!
            GolemSealLogic.TaskCandidate taskA = new GolemSealLogic.TaskCandidate(
                    1, 169.0, 0, (byte) 0, Set.of(), Set.of()
            );
            GolemSealLogic.TaskCandidate taskB = new GolemSealLogic.TaskCandidate(
                    2, 400.0, 1, (byte) 0, Set.of(), Set.of()
            );
            GolemSealLogic.TaskCandidate taskC = new GolemSealLogic.TaskCandidate(
                    3, 25.0, -1, (byte) 0, Set.of(), Set.of() // score: 25 - (-256) = 281
            );

            List<GolemSealLogic.TaskCandidate> sorted = GolemSealLogic.sortTasksByPriority(List.of(taskA, taskB, taskC));

            assertEquals(3, sorted.size());
            assertEquals(2, sorted.get(0).taskId(), "Task B (+1 prio, 20m) must rank first over Task A (0 prio, 13m)");
            assertEquals(1, sorted.get(1).taskId(), "Task A (0 prio, 13m) must rank second");
            assertEquals(3, sorted.get(2).taskId(), "Task C (-1 prio, 5m) must rank last due to negative priority");
        }

        @Test
        @DisplayName("Sorting empty and single-element lists")
        void testSortEdgeCases() {
            assertTrue(GolemSealLogic.sortTasksByPriority(null).isEmpty());
            assertTrue(GolemSealLogic.sortTasksByPriority(List.of()).isEmpty());

            GolemSealLogic.TaskCandidate single = new GolemSealLogic.TaskCandidate(
                    10, 50.0, 0, (byte) 0, Set.of(), Set.of()
            );
            List<GolemSealLogic.TaskCandidate> singleSorted = GolemSealLogic.sortTasksByPriority(List.of(single));
            assertEquals(1, singleSorted.size());
            assertEquals(10, singleSorted.get(0).taskId());
        }
    }

    // =========================================================================
    // 2. Task Validation & Color / Trait Filtering
    // =========================================================================

    @Nested
    @DisplayName("Task Candidate Validity Tests")
    class TaskValidityTests {

        @Test
        @DisplayName("Universal color 0 matches any color")
        void testColorMatching() {
            // Golem color 0 accepts task color 0 and task color 5
            GolemSealLogic.TaskCandidate taskColored = new GolemSealLogic.TaskCandidate(
                    1, 10.0, 0, (byte) 5, Set.of(), Set.of()
            );
            GolemSealLogic.TaskCandidate taskUncolored = new GolemSealLogic.TaskCandidate(
                    2, 10.0, 0, (byte) 0, Set.of(), Set.of()
            );

            // Golem 0 (uncolored) accepts both
            assertTrue(GolemSealLogic.isTaskValidForGolem(taskColored, (byte) 0, Set.of()));
            assertTrue(GolemSealLogic.isTaskValidForGolem(taskUncolored, (byte) 0, Set.of()));

            // Golem 5 (colored) accepts uncolored task and color 5 task
            assertTrue(GolemSealLogic.isTaskValidForGolem(taskUncolored, (byte) 5, Set.of()));
            assertTrue(GolemSealLogic.isTaskValidForGolem(taskColored, (byte) 5, Set.of()));

            // Golem 3 (different color) rejects color 5 task
            assertFalse(GolemSealLogic.isTaskValidForGolem(taskColored, (byte) 3, Set.of()));
        }

        @Test
        @DisplayName("Forbidden traits reject task immediately")
        void testForbiddenTraits() {
            GolemSealLogic.TaskCandidate delicateTask = new GolemSealLogic.TaskCandidate(
                    1, 10.0, 0, (byte) 0, Set.of(), Set.of("clumsy")
            );

            // Golem without clumsy is accepted
            assertTrue(GolemSealLogic.isTaskValidForGolem(delicateTask, (byte) 0, Set.of("smart", "deft")));

            // Golem with clumsy is rejected (case-insensitive)
            assertFalse(GolemSealLogic.isTaskValidForGolem(delicateTask, (byte) 0, Set.of("CLUMSY")));
            assertFalse(GolemSealLogic.isTaskValidForGolem(delicateTask, (byte) 0, Set.of("clumsy", "smart")));
        }

        @Test
        @DisplayName("Required traits gate task execution")
        void testRequiredTraits() {
            GolemSealLogic.TaskCandidate guardTask = new GolemSealLogic.TaskCandidate(
                    1, 10.0, 0, (byte) 0, Set.of("fighter"), Set.of()
            );

            // Golem without fighter is rejected
            assertFalse(GolemSealLogic.isTaskValidForGolem(guardTask, (byte) 0, Set.of("smart")));

            // Golem with fighter is accepted (case-insensitive)
            assertTrue(GolemSealLogic.isTaskValidForGolem(guardTask, (byte) 0, Set.of("FIGHTER")));
            assertTrue(GolemSealLogic.isTaskValidForGolem(guardTask, (byte) 0, Set.of("fighter", "armored")));
        }

        @Test
        @DisplayName("Null task candidate handling")
        void testNullTaskCandidate() {
            assertFalse(GolemSealLogic.isTaskValidForGolem(null, (byte) 0, Set.of()));
        }
    }

    // =========================================================================
    // 3. Task Lifespan & Expiration
    // =========================================================================

    @Nested
    @DisplayName("Task Lifespan & Expiration Tests")
    class LifespanTests {

        @Test
        @DisplayName("Task expires when currentTick - spawnTick >= lifespan")
        void testTickBasedExpiration() {
            int spawnTick = 100;
            int lifespan = 300;

            // Before expiration
            assertFalse(GolemSealLogic.isTaskExpired(100, spawnTick, lifespan));
            assertFalse(GolemSealLogic.isTaskExpired(399, spawnTick, lifespan));

            // Exactly at expiration
            assertTrue(GolemSealLogic.isTaskExpired(400, spawnTick, lifespan));

            // Past expiration
            assertTrue(GolemSealLogic.isTaskExpired(450, spawnTick, lifespan));

            // Non-positive lifespan expires immediately
            assertTrue(GolemSealLogic.isTaskExpired(100, spawnTick, 0));
            assertTrue(GolemSealLogic.isTaskExpired(100, spawnTick, -5));
        }

        @Test
        @DisplayName("Task expires when lifespan <= 0 or when suspended")
        void testLifespanAndSuspension() {
            assertFalse(GolemSealLogic.isTaskExpired((short) 10, false));
            assertTrue(GolemSealLogic.isTaskExpired((short) 0, false));
            assertTrue(GolemSealLogic.isTaskExpired((short) -1, false));
            assertTrue(GolemSealLogic.isTaskExpired((short) 10, true), "Suspended task must be considered expired");
        }

        @Test
        @DisplayName("decrementLifespan decrements down to 0 without underflow")
        void testDecrementLifespan() {
            assertEquals((short) 9, GolemSealLogic.decrementLifespan((short) 10));
            assertEquals((short) 0, GolemSealLogic.decrementLifespan((short) 1));
            assertEquals((short) 0, GolemSealLogic.decrementLifespan((short) 0));
        }
    }

    // =========================================================================
    // 4. Carrying Capacity & Item Logistics
    // =========================================================================

    @Nested
    @DisplayName("Carrying Capacity & Item Logistics Tests")
    class CapacityAndLogisticsTests {

        @Test
        @DisplayName("Capacity checking respects bounds and incoming amounts")
        void testCanGolemCarry() {
            int maxCap = 64;

            // Normal capacity space
            assertTrue(GolemSealLogic.canGolemCarry(0, maxCap, 32));
            assertTrue(GolemSealLogic.canGolemCarry(32, maxCap, 32));

            // Exceeding capacity
            assertFalse(GolemSealLogic.canGolemCarry(33, maxCap, 32));
            assertFalse(GolemSealLogic.canGolemCarry(64, maxCap, 1));

            // Incoming 0 or negative is always allowed
            assertTrue(GolemSealLogic.canGolemCarry(64, maxCap, 0));
            assertTrue(GolemSealLogic.canGolemCarry(64, maxCap, -5));

            // Invalid max capacity
            assertFalse(GolemSealLogic.canGolemCarry(0, 0, 1));
            assertFalse(GolemSealLogic.canGolemCarry(0, -10, 1));

            // Negative current items
            assertFalse(GolemSealLogic.canGolemCarry(-1, maxCap, 1));
        }

        @Test
        @DisplayName("Remaining capacity calculation")
        void testRemainingCapacity() {
            assertEquals(32, GolemSealLogic.remainingCapacity(32, 64));
            assertEquals(0, GolemSealLogic.remainingCapacity(64, 64));
            assertEquals(0, GolemSealLogic.remainingCapacity(70, 64));
            assertEquals(64, GolemSealLogic.remainingCapacity(0, 64));
            assertEquals(0, GolemSealLogic.remainingCapacity(0, 0));
        }

        @Test
        @DisplayName("Calculate deposit amount respecting container stack limits")
        void testCalculateDepositAmount() {
            // Unlimited container (stackLimit = 0)
            assertEquals(64, GolemSealLogic.calculateDepositAmount(100, 0, 64));
            assertEquals(32, GolemSealLogic.calculateDepositAmount(0, -1, 32));

            // Container limit 64, currently has 40 items, golem carrying 32 -> can only deposit 24
            assertEquals(24, GolemSealLogic.calculateDepositAmount(40, 64, 32));

            // Container limit 64, currently has 64 items -> deposit 0
            assertEquals(0, GolemSealLogic.calculateDepositAmount(64, 64, 32));

            // Container limit 64, currently has 10 items, golem carrying 16 -> can deposit all 16
            assertEquals(16, GolemSealLogic.calculateDepositAmount(10, 64, 16));

            // Golem carrying 0
            assertEquals(0, GolemSealLogic.calculateDepositAmount(10, 64, 0));
        }

        @Test
        @DisplayName("Hauler trait doubles max carry stacks")
        void testHaulerTrait() {
            assertEquals(1, GolemSealLogic.getMaxCarryStacks(null));
            assertEquals(1, GolemSealLogic.getMaxCarryStacks(Set.of("fighter", "smart")));
            assertEquals(2, GolemSealLogic.getMaxCarryStacks(Set.of("hauler")));
            assertEquals(2, GolemSealLogic.getMaxCarryStacks(Set.of("HAULER", "heavy")));
        }
    }

    // =========================================================================
    // 5. Whitelist / Blacklist Item Filtering
    // =========================================================================

    @Nested
    @DisplayName("Filter Matching Tests")
    class FilterMatchingTests {

        @Test
        @DisplayName("String filter: whitelist vs blacklist")
        void testStringFiltering() {
            List<String> filter = List.of("minecraft:iron_ingot", "thaumcraft:nugget#1");

            // Whitelist mode (isBlacklist = false)
            assertTrue(GolemSealLogic.matchesFilter("minecraft:iron_ingot", filter, false, false));
            assertFalse(GolemSealLogic.matchesFilter("minecraft:gold_ingot", filter, false, false));

            // Blacklist mode (isBlacklist = true)
            assertFalse(GolemSealLogic.matchesFilter("minecraft:iron_ingot", filter, true, false));
            assertTrue(GolemSealLogic.matchesFilter("minecraft:gold_ingot", filter, true, false));
        }

        @Test
        @DisplayName("String filter: ignoreMeta flag")
        void testIgnoreMetaFlag() {
            List<String> filter = List.of("thaumcraft:crystal#aer");

            // Without ignoreMeta: different metadata does not match
            assertFalse(GolemSealLogic.matchesFilter("thaumcraft:crystal#ignis", filter, false, false));
            assertTrue(GolemSealLogic.matchesFilter("thaumcraft:crystal#aer", filter, false, false));

            // With ignoreMeta: matching base ID matches regardless of metadata
            assertTrue(GolemSealLogic.matchesFilter("thaumcraft:crystal#ignis", filter, false, true));
            assertTrue(GolemSealLogic.matchesFilter("thaumcraft:crystal#terra", filter, false, true));
            assertFalse(GolemSealLogic.matchesFilter("minecraft:dirt#0", filter, false, true));
        }

        @Test
        @DisplayName("Empty filter behavior: whitelist matches nothing, blacklist matches everything")
        void testEmptyFilter() {
            assertFalse(GolemSealLogic.matchesFilter("minecraft:stone", List.of(), false, false));
            assertFalse(GolemSealLogic.matchesFilter("minecraft:stone", null, false, false));

            assertTrue(GolemSealLogic.matchesFilter("minecraft:stone", List.of(), true, false));
            assertTrue(GolemSealLogic.matchesFilter("minecraft:stone", null, true, false));
        }

        @Test
        @DisplayName("ItemSpec rich component filtering")
        void testItemSpecFiltering() {
            GolemSealLogic.ItemSpec candidate = new GolemSealLogic.ItemSpec("thaumcraft:focus", 1, "hash_xyz");
            GolemSealLogic.ItemSpec filterItemA = new GolemSealLogic.ItemSpec("thaumcraft:focus", 1, "hash_xyz");
            GolemSealLogic.ItemSpec filterItemB = new GolemSealLogic.ItemSpec("thaumcraft:focus", 1, "hash_other");

            // Whitelist matching with component check
            assertTrue(GolemSealLogic.matchesFilter(candidate, List.of(filterItemA), false, true));
            assertFalse(GolemSealLogic.matchesFilter(candidate, List.of(filterItemB), false, true));

            // Whitelist matching without component check (matches by ID only)
            assertTrue(GolemSealLogic.matchesFilter(candidate, List.of(filterItemB), false, false));

            // Blacklist matching
            assertFalse(GolemSealLogic.matchesFilter(candidate, List.of(filterItemA), true, true));
            assertTrue(GolemSealLogic.matchesFilter(candidate, List.of(filterItemB), true, true));
        }
    }

    // =========================================================================
    // 6. Trait Modifiers & Attribute Calculations
    // =========================================================================

    @Nested
    @DisplayName("Attribute Calculation Tests")
    class AttributeCalculationTests {

        @Test
        @DisplayName("Default base attributes without material or traits")
        void testDefaultAttributes() {
            GolemSealLogic.AttributeResult res = GolemSealLogic.calculateAttributes(null, Set.of());
            assertEquals(10.0, res.maxHealth(), 0.001);
            assertEquals(0.0, res.armor(), 0.001);
            assertEquals(0.3, res.speed(), 0.001);
            assertEquals(1.0, res.damage(), 0.001);
            assertEquals(0.0, res.knockbackRes(), 0.001);
        }

        @Test
        @DisplayName("Wood Material with Light and Fragile traits")
        void testWoodMaterialAttributes() {
            // Wood material: healthMod -2, armor 0, damage 0 -> base: HP 8, Armor 0, Dmg 1, Spd 0.3
            GolemSealLogic.MaterialStats wood = new GolemSealLogic.MaterialStats(-2, 0, 0);
            GolemSealLogic.AttributeResult res = GolemSealLogic.calculateAttributes(wood, Set.of("light", "fragile"));

            // Fragile: HP * 0.75 -> 8 * 0.75 = 6.0
            assertEquals(6.0, res.maxHealth(), 0.001);
            // Light: speed * 1.2 -> 0.3 * 1.2 = 0.36
            assertEquals(0.36, res.speed(), 0.001);
            assertEquals(0.0, res.armor(), 0.001);
            assertEquals(1.0, res.damage(), 0.001);
        }

        @Test
        @DisplayName("Iron Material with Armored, Heavy, Brutal, and Fighter traits")
        void testIronMaterialAttributes() {
            // Iron material: healthMod +2, armor +4, damage +2 -> base: HP 12, Armor 4, Dmg 3, Spd 0.3
            GolemSealLogic.MaterialStats iron = new GolemSealLogic.MaterialStats(2, 4, 2);
            GolemSealLogic.AttributeResult res = GolemSealLogic.calculateAttributes(iron, Set.of("armored", "heavy", "brutal", "fighter"));

            assertEquals(12.0, res.maxHealth(), 0.001);
            // Armored: armor + 4 -> 4 + 4 = 8.0
            assertEquals(8.0, res.armor(), 0.001);
            // Heavy: speed * 0.8 -> 0.3 * 0.8 = 0.24, knockbackRes + 0.5
            assertEquals(0.24, res.speed(), 0.001);
            assertEquals(0.5, res.knockbackRes(), 0.001);
            // Brutal (+2) + Fighter (+2): damage 3 + 2 + 2 = 7.0
            assertEquals(7.0, res.damage(), 0.001);
        }

        @Test
        @DisplayName("Wheeled and Breaker traits")
        void testWheeledAndBreakerTraits() {
            GolemSealLogic.AttributeResult res = GolemSealLogic.calculateAttributes(null, Set.of("wheeled", "breaker"));
            // Wheeled: speed * 1.25 -> 0.3 * 1.25 = 0.375
            assertEquals(0.375, res.speed(), 0.001);
            // Breaker: damage + 1.0 -> 1.0 + 1.0 = 2.0
            assertEquals(2.0, res.damage(), 0.001);
        }
    }

    // =========================================================================
    // 7. Area Bounding Box Geometry
    // =========================================================================

    @Nested
    @DisplayName("Area Bounding Box Geometry Tests")
    class AreaGeometryTests {

        @Test
        @DisplayName("Bounding box calculation with step and area spans")
        void testCalculateAreaBounds() {
            // Pos at (10, 64, 10), face pointing UP (stepY = 1), area (3, 5, 3)
            GolemSealLogic.AreaBox box = GolemSealLogic.calculateAreaBounds(10, 64, 10, 0, 1, 0, 3, 5, 3);

            // originY = 64 + 1 = 65. spanY = (5 - 1) * 1 = 4 -> maxY = 65 + 4 = 69
            assertEquals(65, box.minY());
            assertEquals(69, box.maxY());

            // radX = 3 - 1 = 2 -> originX = 10, minX = 10 - 2 = 8, maxX = 10 + 2 = 12
            assertEquals(8, box.minX());
            assertEquals(12, box.maxX());

            // radZ = 3 - 1 = 2 -> originZ = 10, minZ = 10 - 2 = 8, maxZ = 10 + 2 = 12
            assertEquals(8, box.minZ());
            assertEquals(12, box.maxZ());
        }

        @Test
        @DisplayName("isWithinSealArea coordinate containment check")
        void testIsWithinSealArea() {
            // Box: X [5, 15], Y [60, 70], Z [-10, 10]
            assertTrue(GolemSealLogic.isWithinSealArea(10, 65, 0, 5, 60, -10, 15, 70, 10));

            // Boundary points (inclusive)
            assertTrue(GolemSealLogic.isWithinSealArea(5, 60, -10, 5, 60, -10, 15, 70, 10));
            assertTrue(GolemSealLogic.isWithinSealArea(15, 70, 10, 5, 60, -10, 15, 70, 10));

            // Points outside each axis
            assertFalse(GolemSealLogic.isWithinSealArea(4, 65, 0, 5, 60, -10, 15, 70, 10));
            assertFalse(GolemSealLogic.isWithinSealArea(16, 65, 0, 5, 60, -10, 15, 70, 10));
            assertFalse(GolemSealLogic.isWithinSealArea(10, 59, 0, 5, 60, -10, 15, 70, 10));
            assertFalse(GolemSealLogic.isWithinSealArea(10, 71, 0, 5, 60, -10, 15, 70, 10));
            assertFalse(GolemSealLogic.isWithinSealArea(10, 65, -11, 5, 60, -10, 15, 70, 10));
            assertFalse(GolemSealLogic.isWithinSealArea(10, 65, 11, 5, 60, -10, 15, 70, 10));

            // Inverted bounds (min > max) should still be handled safely
            assertTrue(GolemSealLogic.isWithinSealArea(10, 65, 0, 15, 70, 10, 5, 60, -10));
        }
    }

    // =========================================================================
    // 8. Wander & Patrol Calculations
    // =========================================================================

    @Nested
    @DisplayName("Wander & Patrol Calculation Tests")
    class WanderPatrolTests {

        @Test
        @DisplayName("Tether leash checking: shouldReturnHome when distSq > 1024.0")
        void testShouldReturnHome() {
            assertFalse(GolemSealLogic.shouldReturnHome(500.0, 1024.0));
            assertFalse(GolemSealLogic.shouldReturnHome(1024.0, 1024.0));
            assertTrue(GolemSealLogic.shouldReturnHome(1024.01, 1024.0));
            assertTrue(GolemSealLogic.shouldReturnHome(2000.0, 1024.0));
        }

        @Test
        @DisplayName("Arrival check: isAtHome when distSq <= 5.0")
        void testIsAtHome() {
            assertTrue(GolemSealLogic.isAtHome(0.0, 5.0));
            assertTrue(GolemSealLogic.isAtHome(5.0, 5.0));
            assertFalse(GolemSealLogic.isAtHome(5.01, 5.0));
            assertFalse(GolemSealLogic.isAtHome(20.0, 5.0));
        }

        @Test
        @DisplayName("Patrol waypoint index advances cyclically")
        void testPatrolWaypointCycle() {
            int totalWaypoints = 4;
            assertEquals(1, GolemSealLogic.calculatePatrolWaypointIndex(0, totalWaypoints));
            assertEquals(2, GolemSealLogic.calculatePatrolWaypointIndex(1, totalWaypoints));
            assertEquals(3, GolemSealLogic.calculatePatrolWaypointIndex(2, totalWaypoints));
            assertEquals(0, GolemSealLogic.calculatePatrolWaypointIndex(3, totalWaypoints));

            // Total waypoints 0 or negative
            assertEquals(0, GolemSealLogic.calculatePatrolWaypointIndex(2, 0));
        }
    }
}
