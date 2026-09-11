package thaumcraft.common.golems;

import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

public class GolemArmLogicTest {

    @Test
    public void testClawsDamageBonus() {
        assertEquals(2.0, GolemArmLogic.getMeleeDamageBonus(Set.of("claws")));
        assertEquals(0.0, GolemArmLogic.getMeleeDamageBonus(Set.of("basic")));
        assertEquals(0.0, GolemArmLogic.getMeleeDamageBonus(null));
    }

    @Test
    public void testHasDartLauncher() {
        assertTrue(GolemArmLogic.hasDartLauncher(Set.of("darts")));
        assertFalse(GolemArmLogic.hasDartLauncher(Set.of("claws")));
        assertFalse(GolemArmLogic.hasDartLauncher(null));
    }

    @Test
    public void testFineManipulatorsCarryBonus() {
        assertEquals(1, GolemArmLogic.getCarryCapacityBonus(Set.of("fine")));
        assertEquals(0, GolemArmLogic.getCarryCapacityBonus(Set.of("basic")));
        assertEquals(0, GolemArmLogic.getCarryCapacityBonus(null));
    }

    @Test
    public void testBreakersBlockBreakingMultiplier() {
        assertEquals(2.0, GolemArmLogic.getBlockBreakingMultiplier(Set.of("breakers")));
        assertEquals(1.0, GolemArmLogic.getBlockBreakingMultiplier(Set.of("fine")));
        assertEquals(1.0, GolemArmLogic.getBlockBreakingMultiplier(null));
    }
}
