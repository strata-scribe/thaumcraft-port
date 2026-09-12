package thaumcraft.common.world.aura.logic;

import org.junit.jupiter.api.Test;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FluxSaturationLogicTest {

    @Test
    public void testTaintFibersTippingPoint() {
        assertEquals(75.0f, FluxSaturationLogic.getTaintFibersTippingPoint(100.0f), 0.001f);
        assertEquals(37.5f, FluxSaturationLogic.getTaintFibersTippingPoint(50.0f), 0.001f);
        assertEquals(0.0f, FluxSaturationLogic.getTaintFibersTippingPoint(0.0f), 0.001f);
    }

    @Test
    public void testFluxRiftTippingPoint() {
        assertEquals(150.0f, FluxSaturationLogic.getFluxRiftTippingPoint(100.0f), 0.001f);
        assertEquals(75.0f, FluxSaturationLogic.getFluxRiftTippingPoint(50.0f), 0.001f);
        assertEquals(0.0f, FluxSaturationLogic.getFluxRiftTippingPoint(0.0f), 0.001f);
    }

    // A simple mock for Random since Mockito isn't available
    private static class FixedRandom extends Random {
        private float nextFloatValue;

        public void setNextFloat(float value) {
            this.nextFloatValue = value;
        }

        @Override
        public float nextFloat() {
            return nextFloatValue;
        }
    }

    @Test
    public void testEvaluateCondensationEvent() {
        FixedRandom mockRandom = new FixedRandom();

        // Below taint tipping point
        assertEquals(FluxSaturationLogic.FluxCondensationEvent.NONE, FluxSaturationLogic.evaluateCondensationEvent(50.0f, 100.0f, mockRandom));

        // At taint tipping point, but random chance misses (0.0 < 0.0 is false)
        mockRandom.setNextFloat(0.5f);
        assertEquals(FluxSaturationLogic.FluxCondensationEvent.NONE, FluxSaturationLogic.evaluateCondensationEvent(75.0f, 100.0f, mockRandom));

        // Above taint tipping point, hit chance (flux=105, threshold=75. chance = 30/75 = 0.4. roll=0.3 -> hits)
        mockRandom.setNextFloat(0.3f);
        assertEquals(FluxSaturationLogic.FluxCondensationEvent.TAINT_FIBERS, FluxSaturationLogic.evaluateCondensationEvent(105.0f, 100.0f, mockRandom));

        // Above taint tipping point, miss chance (roll=0.5 > 0.4)
        mockRandom.setNextFloat(0.5f);
        assertEquals(FluxSaturationLogic.FluxCondensationEvent.NONE, FluxSaturationLogic.evaluateCondensationEvent(105.0f, 100.0f, mockRandom));

        // Above rift tipping point, hit chance (flux=225, threshold=150. chance = 75/150 = 0.5. roll=0.4 -> hits rift)
        mockRandom.setNextFloat(0.4f);
        assertEquals(FluxSaturationLogic.FluxCondensationEvent.FLUX_RIFT, FluxSaturationLogic.evaluateCondensationEvent(225.0f, 100.0f, mockRandom));

        // Above rift tipping point, miss rift chance (roll=0.6), but hits taint chance
        // For flux=225, taint threshold=75. chance = 150/75 = 2.0 -> capped at 0.8
        // Roll=0.6 -> hits taint.
        mockRandom.setNextFloat(0.6f);
        assertEquals(FluxSaturationLogic.FluxCondensationEvent.TAINT_FIBERS, FluxSaturationLogic.evaluateCondensationEvent(225.0f, 100.0f, mockRandom));

        // Above rift tipping point, miss both chances (roll=0.9 > max capped chances 0.5 for rift, 0.8 for taint)
        mockRandom.setNextFloat(0.9f);
        assertEquals(FluxSaturationLogic.FluxCondensationEvent.NONE, FluxSaturationLogic.evaluateCondensationEvent(225.0f, 100.0f, mockRandom));
    }
}
