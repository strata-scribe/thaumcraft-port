package thaumcraft.common.tiles.essentia.logic;

import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SmelterAuxLogicTest {

    @Test
    void testCalculateSmeltTime_noPumps() {
        assertEquals(100, SmelterAuxLogic.calculateSmeltTime(100, 0));
    }

    @Test
    void testCalculateSmeltTime_onePump() {
        assertEquals(80, SmelterAuxLogic.calculateSmeltTime(100, 1));
    }

    @Test
    void testCalculateSmeltTime_twoPumps() {
        assertEquals(60, SmelterAuxLogic.calculateSmeltTime(100, 2));
    }

    @Test
    void testCalculateSmeltTime_threePumps() {
        // Capped at 2 pumps
        assertEquals(60, SmelterAuxLogic.calculateSmeltTime(100, 3));
    }

    @Test
    void testCalculateSmeltTime_minimum() {
        assertEquals(1, SmelterAuxLogic.calculateSmeltTime(1, 2));
    }

    @Test
    void testGetVentedFlux_noVents() {
        Random random = new Random(0);
        assertEquals(0, SmelterAuxLogic.getVentedFlux(10, 0, random));
    }

    @Test
    void testGetVentedFlux_noFlux() {
        Random random = new Random(0);
        assertEquals(0, SmelterAuxLogic.getVentedFlux(0, 5, random));
    }

    @Test
    void testGetVentedFlux_predictable() {
        // We use a custom random that returns specific values to test the 33.3% chance
        Random dummyRandom = new Random() {
            int counter = 0;
            @Override
            public float nextFloat() {
                // Return sequence: 0.1, 0.4, 0.2, 0.5, 0.1...
                counter++;
                return (counter % 2 != 0) ? 0.1f : 0.5f;
            }
        };

        // For 1 vent, with 4 flux points:
        // Flux 1: 0.1f (< 0.333f) -> absorbed
        // Flux 2: 0.5f (>= 0.333f) -> not absorbed
        // Flux 3: 0.1f (< 0.333f) -> absorbed
        // Flux 4: 0.5f (>= 0.333f) -> not absorbed
        int absorbed = SmelterAuxLogic.getVentedFlux(4, 1, dummyRandom);
        assertEquals(2, absorbed);
    }

    @Test
    void testGetVentedFlux_multipleVents() {
        Random dummyRandom = new Random() {
            int counter = 0;
            @Override
            public float nextFloat() {
                // Return sequence: 0.5, 0.1, 0.5, 0.5...
                counter++;
                if (counter == 1) return 0.5f;
                if (counter == 2) return 0.1f;
                return 0.5f;
            }
        };

        // For 2 vents, with 2 flux points:
        // Flux 1:
        //   Vent 1: 0.5f -> no
        //   Vent 2: 0.1f -> absorbed
        // Flux 2:
        //   Vent 1: 0.5f -> no
        //   Vent 2: 0.5f -> no
        int absorbed = SmelterAuxLogic.getVentedFlux(2, 2, dummyRandom);
        assertEquals(1, absorbed);
    }
}
