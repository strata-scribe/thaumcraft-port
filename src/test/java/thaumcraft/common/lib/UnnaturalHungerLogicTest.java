package thaumcraft.common.lib;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UnnaturalHungerLogicTest {

    @Test
    public void testCalculateExhaustionPerTick() {
        // Level 0 or below should return 0
        assertEquals(0.0f, UnnaturalHungerLogic.calculateExhaustionPerTick(0), 0.001f);
        assertEquals(0.0f, UnnaturalHungerLogic.calculateExhaustionPerTick(-1), 0.001f);

        // Level 1
        assertEquals(0.025f, UnnaturalHungerLogic.calculateExhaustionPerTick(1), 0.001f);

        // Level 2
        assertEquals(0.050f, UnnaturalHungerLogic.calculateExhaustionPerTick(2), 0.001f);

        // Level 5
        assertEquals(0.125f, UnnaturalHungerLogic.calculateExhaustionPerTick(5), 0.001f);
    }

    @Test
    public void testCalculateRottenFleshFood() {
        int baseFood = 4;

        // Level 0 or below should return base food
        assertEquals(baseFood, UnnaturalHungerLogic.calculateRottenFleshFood(0, baseFood));
        assertEquals(baseFood, UnnaturalHungerLogic.calculateRottenFleshFood(-2, baseFood));

        // Level 1: 4 + (4 * 0.5 * 1) = 6
        assertEquals(6, UnnaturalHungerLogic.calculateRottenFleshFood(1, baseFood));

        // Level 2: 4 + (4 * 0.5 * 2) = 8
        assertEquals(8, UnnaturalHungerLogic.calculateRottenFleshFood(2, baseFood));

        // Level 3: 4 + (4 * 0.5 * 3) = 10
        assertEquals(10, UnnaturalHungerLogic.calculateRottenFleshFood(3, baseFood));
    }

    @Test
    public void testCalculateRottenFleshSaturation() {
        float baseSat = 0.8f;

        // Level 0 or below should return base saturation
        assertEquals(baseSat, UnnaturalHungerLogic.calculateRottenFleshSaturation(0, baseSat), 0.001f);
        assertEquals(baseSat, UnnaturalHungerLogic.calculateRottenFleshSaturation(-1, baseSat), 0.001f);

        // Level 1: 0.8 * 1.5 = 1.2
        assertEquals(1.2f, UnnaturalHungerLogic.calculateRottenFleshSaturation(1, baseSat), 0.001f);

        // Level 2: 0.8 * 2.0 = 1.6
        assertEquals(1.6f, UnnaturalHungerLogic.calculateRottenFleshSaturation(2, baseSat), 0.001f);

        // Level 3: 0.8 * 2.5 = 2.0
        assertEquals(2.0f, UnnaturalHungerLogic.calculateRottenFleshSaturation(3, baseSat), 0.001f);
    }
}
