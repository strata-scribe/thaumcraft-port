package thaumcraft.common.blocks.devices;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import thaumcraft.api.aura.AuraChunk;
import thaumcraft.common.tiles.devices.CondenserLogic;

public class FluxCondenserTest {

    @Test
    public void testFluxReductionAndCostAccumulation() {
        AuraChunk chunk = new AuraChunk((short) 100, 100f, 5f);
        CondenserLogic logic = new CondenserLogic();

        // Single lattice above the condenser
        CondenserLogic.BlockProvider provider = new CondenserLogic.BlockProvider() {
            @Override
            public boolean isLattice(int dx, int dy, int dz) {
                return dx == 0 && dy == 1 && dz == 0;
            }

            @Override
            public boolean isDirtyLattice(int dx, int dz) {
                return false;
            }

            @Override
            public void makeDirty(int dx, int dy, int dz) {
            }
        };

        boolean[] emitted = {false};

        logic.tick(chunk, provider, (success) -> {
            emitted[0] = true;
        });

        assertTrue(chunk.getFlux() < 5f, "Flux should be reduced");
        assertTrue(logic.getCost() > 0, "Cost should accumulate");

        // Tick until it emits
        for (int i = 0; i < 200; i++) {
            chunk.setFlux(10f); // keep feeding it flux
            logic.tick(chunk, provider, (success) -> {
                emitted[0] = true;
            });
        }

        assertTrue(emitted[0], "Should have emitted essentia or slag after accumulating enough cost");
    }

    @Test
    public void testLatticeClogging() {
        AuraChunk chunk = new AuraChunk((short) 100, 100f, 100f);
        CondenserLogic logic = new CondenserLogic();

        boolean[] clogged = {false};

        CondenserLogic.BlockProvider provider = new CondenserLogic.BlockProvider() {
            @Override
            public boolean isLattice(int dx, int dy, int dz) {
                return dy == 1; // 9 lattices above
            }

            @Override
            public boolean isDirtyLattice(int dx, int dz) {
                return false;
            }

            @Override
            public void makeDirty(int dx, int dy, int dz) {
                clogged[0] = true;
            }
        };

        // Tick many times to practically guarantee a clogging event
        for (int i = 0; i < 1000; i++) {
            chunk.setFlux(100f);
            logic.tick(chunk, provider, (success) -> {});
            if (clogged[0]) break;
        }

        assertTrue(clogged[0], "A lattice should eventually get clogged when drawing high amounts of flux");
    }
}
