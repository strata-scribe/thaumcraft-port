package thaumcraft.common.tiles.devices;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FluxCondenserBlockEntityTest {

    @Test
    public void testFiltrationLogicIntegration() {
        FluxCondenserFiltrationLogic logic = new FluxCondenserFiltrationLogic(5, 0);
        assertEquals(5, logic.getCleanLattices());
        assertEquals(0, logic.getCloggedLattices());

        java.util.Random random = new java.util.Random(42);

        float extracted = logic.processFlux(1.0f, random);
        assertTrue(extracted > 0f);

        // Ensure vitium residue accumulated
        assertTrue(logic.getVitiumResidue() > 0f);

        // Try extracting vitium aspect
        logic.setVitiumResidue(1.5f);
        assertTrue(logic.extractVitiumAspect());
        assertEquals(0.5f, logic.getVitiumResidue(), 0.01f);

        assertFalse(logic.extractVitiumAspect());
    }
}
