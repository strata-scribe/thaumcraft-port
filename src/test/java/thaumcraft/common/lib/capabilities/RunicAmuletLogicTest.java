package thaumcraft.common.lib.capabilities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RunicAmuletLogicTest {

    @Test
    public void testCalculateOverloadBurst_ZeroShield() {
        RunicAmuletLogic.BurstResult result = RunicAmuletLogic.calculateOverloadBurst(0);
        assertEquals(2.0f, result.radius, 0.001f);
        assertEquals(0.5f, result.knockback, 0.001f);
    }

    @Test
    public void testCalculateOverloadBurst_SmallShield() {
        RunicAmuletLogic.BurstResult result = RunicAmuletLogic.calculateOverloadBurst(10);
        assertEquals(7.0f, result.radius, 0.001f);
        assertEquals(1.5f, result.knockback, 0.001f);
    }

    @Test
    public void testCalculateOverloadBurst_LargeShield() {
        RunicAmuletLogic.BurstResult result = RunicAmuletLogic.calculateOverloadBurst(50);
        assertEquals(27.0f, result.radius, 0.001f);
        assertEquals(5.5f, result.knockback, 0.001f);
    }
}
