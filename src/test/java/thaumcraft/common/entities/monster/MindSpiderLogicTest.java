package thaumcraft.common.entities.monster;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MindSpiderLogicTest {

    @Test
    @DisplayName("Swarm Alert Radius should be 16.0")
    public void testSwarmAlertRadius() {
        assertEquals(16.0, MindSpiderLogic.getSwarmAlertRadius(), 0.001);
    }

    @Test
    @DisplayName("Poison Duration Ticks should be 100")
    public void testPoisonDurationTicks() {
        assertEquals(100, MindSpiderLogic.getPoisonDurationTicks());
    }

    @Test
    @DisplayName("Blindness Duration Ticks should be 60")
    public void testBlindnessDurationTicks() {
        assertEquals(60, MindSpiderLogic.getBlindnessDurationTicks());
    }
}
