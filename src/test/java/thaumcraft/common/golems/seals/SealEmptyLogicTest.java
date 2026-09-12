package thaumcraft.common.golems.seals;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import thaumcraft.common.golems.seals.SealEmptyLogic.*;

public class SealEmptyLogicTest {

    @Test
    public void testSortSourcesByPriority() {
        FluidSource s1 = new FluidSource(1, "water", 100);
        FluidSource s2 = new FluidSource(2, "water", 500);
        FluidSource s3 = new FluidSource(3, "lava", 300);

        List<FluidSource> sources = Arrays.asList(s1, s2, s3);
        List<FluidSource> sorted = SealEmptyLogic.sortSourcesByPriority(sources);

        assertEquals(3, sorted.size());
        assertEquals(2, sorted.get(0).id); // 500
        assertEquals(3, sorted.get(1).id); // 300
        assertEquals(1, sorted.get(2).id); // 100
    }

    @Test
    public void testMatchAndSortDestinations() {
        FluidSource source = new FluidSource(1, "water", 1000);

        // Matching type, small remaining cap
        FluidDestination d1 = new FluidDestination(1, "water", 900, 1000); // 100 remaining

        // Matching type, large remaining cap
        FluidDestination d2 = new FluidDestination(2, "water", 0, 1000); // 1000 remaining

        // Wrong type
        FluidDestination d3 = new FluidDestination(3, "lava", 0, 1000); // 1000 remaining

        // Null type (accepts anything)
        FluidDestination d4 = new FluidDestination(4, null, 500, 1000); // 500 remaining

        // Full
        FluidDestination d5 = new FluidDestination(5, "water", 1000, 1000); // 0 remaining

        List<FluidDestination> destinations = Arrays.asList(d1, d2, d3, d4, d5);
        List<FluidDestination> sorted = SealEmptyLogic.matchAndSortDestinations(source, destinations);

        assertEquals(3, sorted.size(), "Should only include valid matching destinations with capacity");

        assertEquals(1, sorted.get(0).id, "Smallest remaining capacity should be first");
        assertEquals(4, sorted.get(1).id);
        assertEquals(2, sorted.get(2).id);
    }

    @Test
    public void testAllocateFluids() {
        FluidSource s1 = new FluidSource(1, "water", 600); // High priority
        FluidSource s2 = new FluidSource(2, "water", 200); // Low priority
        FluidSource s3 = new FluidSource(3, "lava", 500); // Lava

        FluidDestination d1 = new FluidDestination(1, "water", 900, 1000); // needs 100
        FluidDestination d2 = new FluidDestination(2, "water", 500, 1000); // needs 500
        FluidDestination d3 = new FluidDestination(3, "water", 0, 1000);   // needs 1000
        FluidDestination d4 = new FluidDestination(4, "lava", 900, 1000); // needs 100
        FluidDestination d5 = new FluidDestination(5, null, 800, 1000); // needs 200

        List<FluidSource> sources = Arrays.asList(s1, s2, s3);
        List<FluidDestination> destinations = Arrays.asList(d1, d2, d3, d4, d5);

        List<Allocation> allocations = SealEmptyLogic.allocateFluids(sources, destinations);

        // Sources sorted: s1(600), s3(500), s2(200)
        // For s1(600 water):
        //   dests: d1(100), d5(200), d2(500), d3(1000)
        //   -> alloc s1 to d1: 100 (s1=500, d1 cap=0)
        //   -> alloc s1 to d5: 200 (s1=300, d5 cap=0)
        //   -> alloc s1 to d2: 300 (s1=0, d2 cap=200)

        // For s3(500 lava):
        //   dests: d4(100) (d5 is full)
        //   -> alloc s3 to d4: 100 (s3=400, d4 cap=0)

        // For s2(200 water):
        //   dests: d2(200), d3(1000)
        //   -> alloc s2 to d2: 200 (s2=0, d2 cap=0)

        assertEquals(5, allocations.size());

        assertTrue(allocations.contains(new Allocation(1, 1, 100)));
        assertTrue(allocations.contains(new Allocation(1, 5, 200)));
        assertTrue(allocations.contains(new Allocation(1, 2, 300)));
        assertTrue(allocations.contains(new Allocation(3, 4, 100)));
        assertTrue(allocations.contains(new Allocation(2, 2, 200)));

        assertEquals(0, s1.amount);
        assertEquals(0, s2.amount);
        assertEquals(400, s3.amount);

        assertEquals(1000, d1.currentAmount);
        assertEquals(1000, d2.currentAmount);
        assertEquals(0, d3.currentAmount);
        assertEquals(1000, d4.currentAmount);
        assertEquals(1000, d5.currentAmount);
    }
}
