package thaumcraft.common.lib.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MirrorTransportStabilityLogicTest {

    @Test
    public void testCalculateVisCost_SameDimension_ShortDistance() {
        double cost = MirrorTransportStabilityLogic.calculateVisCost("overworld", 0, 0, 0, "overworld", 10, 0, 0, 1);
        // Distance is 10. Cost = 0.1 + (10 * 0.01) = 0.2
        assertEquals(0.2, cost, 0.001);
    }

    @Test
    public void testCalculateVisCost_SameDimension_LongDistance() {
        double cost = MirrorTransportStabilityLogic.calculateVisCost("overworld", 0, 0, 0, "overworld", 1000, 0, 0, 10);
        // Distance is 1000. Cost = 0.1 + (1000 * 0.01) = 10.1
        assertEquals(10.1, cost, 0.001);
    }

    @Test
    public void testCalculateVisCost_Interdimensional() {
        double cost = MirrorTransportStabilityLogic.calculateVisCost("overworld", 0, 0, 0, "nether", 10, 0, 0, 5);
        // Cost = 5.0 + (5 * 0.5) = 7.5
        assertEquals(7.5, cost, 0.001);
    }

    @Test
    public void testCalculateVisCost_NullDimension() {
        double cost = MirrorTransportStabilityLogic.calculateVisCost(null, 0, 0, 0, "nether", 10, 0, 0, 5);
        assertEquals(0.0, cost, 0.001);
    }

    @Test
    public void testCalculateInstabilityRisk_SameDimension_ShortDistance() {
        float risk = MirrorTransportStabilityLogic.calculateInstabilityRisk("overworld", 0, 0, 0, "overworld", 10, 0, 0, 1);
        // Distance is 10. Risk = 10 * 0.0001 * 1 = 0.001
        assertEquals(0.001f, risk, 0.00001f);
    }

    @Test
    public void testCalculateInstabilityRisk_SameDimension_LongDistance_HighAmount() {
        float risk = MirrorTransportStabilityLogic.calculateInstabilityRisk("overworld", 0, 0, 0, "overworld", 10000, 0, 0, 1000);
        // Distance is 10000. Risk = 10000 * 0.0001 * 1000 = 1000. Capped at 1.0.
        assertEquals(1.0f, risk, 0.00001f);
    }

    @Test
    public void testCalculateInstabilityRisk_Interdimensional() {
        float risk = MirrorTransportStabilityLogic.calculateInstabilityRisk("overworld", 0, 0, 0, "nether", 10, 0, 0, 10);
        // Risk = 0.05 + (10 * 0.01) = 0.15
        assertEquals(0.15f, risk, 0.00001f);
    }

    @Test
    public void testCalculateInstabilityRisk_NullDimension() {
        float risk = MirrorTransportStabilityLogic.calculateInstabilityRisk("overworld", 0, 0, 0, null, 10, 0, 0, 10);
        assertEquals(0.0f, risk, 0.00001f);
    }
}
