package thaumcraft.common.tiles.devices;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class FluxCondenserFiltrationLogicTest {

    private FluxCondenserFiltrationLogic logic;

    @BeforeEach
    void setUp() {
        logic = new FluxCondenserFiltrationLogic(4, 0);
    }

    @Test
    void testInitialState() {
        assertEquals(4, logic.getCleanLattices());
        assertEquals(0, logic.getCloggedLattices());
        assertEquals(0.0f, logic.getVitiumResidue());
        assertEquals(2.0f, logic.calculateExtractionRate()); // 4 * 0.5f
    }

    @Test
    void testProcessFluxNoCleanLattices() {
        logic.setCleanLattices(0);
        float extracted = logic.processFlux(10.0f, new Random());
        assertEquals(0.0f, extracted);
        assertEquals(0.0f, logic.getVitiumResidue());
    }

    @Test
    void testProcessFluxLimitedByExtractionRate() {
        Random dummyRandom = new Random() {
            @Override
            public float nextFloat() {
                return 1.0f; // Prevent any clogs
            }
        };

        // Available flux > extraction rate
        float extracted = logic.processFlux(5.0f, dummyRandom);

        assertEquals(2.0f, extracted); // Max extraction rate is 4 * 0.5 = 2.0
        assertEquals(2.0f * 0.8f, logic.getVitiumResidue()); // 1.6f
        assertEquals(4, logic.getCleanLattices());
    }

    @Test
    void testProcessFluxLimitedByAvailableFlux() {
        Random dummyRandom = new Random() {
            @Override
            public float nextFloat() {
                return 1.0f; // Prevent any clogs
            }
        };

        // Available flux < extraction rate
        float extracted = logic.processFlux(1.5f, dummyRandom);

        assertEquals(1.5f, extracted);
        assertEquals(1.5f * 0.8f, logic.getVitiumResidue()); // 1.2f
        assertEquals(4, logic.getCleanLattices());
    }

    @Test
    void testLatticeClogging() {
        Random dummyRandom = new Random() {
            @Override
            public float nextFloat() {
                return 0.0f; // Always trigger clogs
            }
        };

        // Process exactly 1 unit of flux, which triggers 1 whole roll and 0 fractional
        // So 1 clog should occur
        float extracted = logic.processFlux(1.0f, dummyRandom);

        assertEquals(1.0f, extracted);
        assertEquals(3, logic.getCleanLattices());
        assertEquals(1, logic.getCloggedLattices());
    }

    @Test
    void testFractionalLatticeClogging() {
        Random dummyRandom = new Random() {
            @Override
            public float nextFloat() {
                return 0.0f; // Always trigger clogs for both whole and fractional
            }
        };

        // Extract 1.5 units, triggers 1 whole roll + 1 fractional roll
        // Both hit, so 2 clogs
        logic.processFlux(1.5f, dummyRandom);

        assertEquals(2, logic.getCleanLattices());
        assertEquals(2, logic.getCloggedLattices());
    }

    @Test
    void testVitiumAspectExtraction() {
        logic.setVitiumResidue(1.5f);

        assertTrue(logic.extractVitiumAspect());
        assertEquals(0.5f, logic.getVitiumResidue(), 0.001f);

        assertFalse(logic.extractVitiumAspect());
        assertEquals(0.5f, logic.getVitiumResidue(), 0.001f);
    }
}
