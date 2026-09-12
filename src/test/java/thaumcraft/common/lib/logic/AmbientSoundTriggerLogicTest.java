package thaumcraft.common.lib.logic;

import org.junit.jupiter.api.Test;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AmbientSoundTriggerLogicTest {

    @Test
    public void testGetInfusionAltarIntervalBounds() {
        Random random = new Random(12345L);
        for (int i = 0; i < 1000; i++) {
            int interval = AmbientSoundTriggerLogic.getInfusionAltarInterval(random);
            assertTrue(interval >= 80 && interval <= 120, "Interval should be between 80 and 120, but was " + interval);
        }
    }

    @Test
    public void testGetEldritchPortalIntervalBounds() {
        Random random = new Random(54321L);
        for (int i = 0; i < 1000; i++) {
            int interval = AmbientSoundTriggerLogic.getEldritchPortalInterval(random);
            assertTrue(interval >= 100 && interval <= 300, "Interval should be between 100 and 300, but was " + interval);
        }
    }
}
