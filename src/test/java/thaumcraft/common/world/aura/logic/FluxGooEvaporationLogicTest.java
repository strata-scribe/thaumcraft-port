package thaumcraft.common.world.aura.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FluxGooEvaporationLogicTest {

    @Test
    public void testCalculateEvaporationAmount() {
        // Normal case: 10 ticks, 0.05 per tick = 0.5 evaporated
        float currentVolume = 10.0f;
        int ticksElapsed = 10;

        float evaporated = FluxGooEvaporationLogic.calculateEvaporationAmount(currentVolume, ticksElapsed);

        assertEquals(0.5f, evaporated, 0.0001f);
    }

    @Test
    public void testCalculateEvaporationAmountCap() {
        // Cap case: 100 ticks, 0.05 per tick = 5.0 expected, but current volume is 2.0
        float currentVolume = 2.0f;
        int ticksElapsed = 100;

        float evaporated = FluxGooEvaporationLogic.calculateEvaporationAmount(currentVolume, ticksElapsed);

        assertEquals(2.0f, evaporated, 0.0001f);
    }

    @Test
    public void testCalculateEvaporationAmountZeroCases() {
        assertEquals(0.0f, FluxGooEvaporationLogic.calculateEvaporationAmount(0.0f, 10));
        assertEquals(0.0f, FluxGooEvaporationLogic.calculateEvaporationAmount(-5.0f, 10));
        assertEquals(0.0f, FluxGooEvaporationLogic.calculateEvaporationAmount(10.0f, 0));
        assertEquals(0.0f, FluxGooEvaporationLogic.calculateEvaporationAmount(10.0f, -5));
    }

    @Test
    public void testCalculateFluxReleased() {
        // Normal case: 0.5 evaporated, 2.0 flux per unit = 1.0 flux
        float evaporatedVolume = 0.5f;

        float flux = FluxGooEvaporationLogic.calculateFluxReleased(evaporatedVolume);

        assertEquals(1.0f, flux, 0.0001f);
    }

    @Test
    public void testCalculateFluxReleasedZeroCases() {
        assertEquals(0.0f, FluxGooEvaporationLogic.calculateFluxReleased(0.0f));
        assertEquals(0.0f, FluxGooEvaporationLogic.calculateFluxReleased(-1.0f));
    }
}
