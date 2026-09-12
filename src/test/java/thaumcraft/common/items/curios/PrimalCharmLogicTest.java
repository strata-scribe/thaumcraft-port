package thaumcraft.common.items.curios;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class PrimalCharmLogicTest {

    @Test
    @DisplayName("Should crystallize if chance is hit")
    public void testShouldCrystallize() {
        Random fixedRandom = new Random(42); // 42 seed nextInt(10) first calls: 0, 5, ...
        // So first call with 10 might be 0. Let's see:
        // Actually to be robust, we'll mock or just do a statistical/deterministic test.
        // Let's use an anonymous subclass of Random for deterministic output.
        Random alwaysZeroRandom = new Random() {
            @Override
            public int nextInt(int bound) {
                return 0; // always hits 0
            }
        };

        assertTrue(PrimalCharmLogic.shouldCrystallize(100, alwaysZeroRandom));
    }

    @Test
    @DisplayName("Should not crystallize if chance is missed")
    public void testShouldNotCrystallize() {
        Random neverZeroRandom = new Random() {
            @Override
            public int nextInt(int bound) {
                return 1; // never hits 0
            }
        };

        assertFalse(PrimalCharmLogic.shouldCrystallize(100, neverZeroRandom));
    }

    @Test
    @DisplayName("Should handle 0 or negative chance gracefully")
    public void testShouldCrystallizeNegative() {
        Random random = new Random();
        assertTrue(PrimalCharmLogic.shouldCrystallize(0, random));
        assertTrue(PrimalCharmLogic.shouldCrystallize(-5, random));
    }

    @Test
    @DisplayName("Should only roll valid primal aspects")
    public void testRollPrimalAspect() {
        Random random = new Random();
        Set<String> validAspects = Set.of("aer", "terra", "ignis", "aqua", "ordo", "perditio");
        Set<String> rolledAspects = new HashSet<>();

        // Roll enough times to likely get all 6
        for (int i = 0; i < 1000; i++) {
            String rolled = PrimalCharmLogic.rollPrimalAspect(random);
            assertTrue(validAspects.contains(rolled), "Rolled invalid aspect: " + rolled);
            rolledAspects.add(rolled);
        }

        // Check that all 6 were eventually rolled
        assertEquals(6, rolledAspects.size(), "Should have rolled all 6 primal aspects over 1000 tries");
        assertTrue(rolledAspects.containsAll(validAspects));
    }
}
