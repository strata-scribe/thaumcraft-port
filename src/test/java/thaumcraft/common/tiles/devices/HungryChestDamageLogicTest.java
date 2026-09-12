package thaumcraft.common.tiles.devices;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HungryChestDamageLogicTest {

    @Test
    @DisplayName("Should return correct base bite damage")
    public void testCalculateBiteDamage() {
        HungryChestDamageLogic logic = new HungryChestDamageLogic();
        assertEquals(2.0f, logic.calculateBiteDamage(), 0.001f);
    }

    @Test
    @DisplayName("Should be ready to bite initially")
    public void testInitialState() {
        HungryChestDamageLogic logic = new HungryChestDamageLogic(10);
        assertTrue(logic.canBite(), "Should be able to bite initially");
    }

    @Test
    @DisplayName("Cooldown should prevent biting until enough ticks pass")
    public void testCooldownLogic() {
        HungryChestDamageLogic logic = new HungryChestDamageLogic(5);

        assertTrue(logic.canBite(), "Should be ready initially");

        logic.resetCooldown();
        assertFalse(logic.canBite(), "Should not be able to bite immediately after reset");

        logic.tick();
        logic.tick();
        logic.tick();
        logic.tick();
        assertFalse(logic.canBite(), "Should not be able to bite after 4 ticks (cooldown is 5)");

        logic.tick();
        assertTrue(logic.canBite(), "Should be able to bite after 5 ticks");

        // Ticking past cooldown should not break anything
        logic.tick();
        assertTrue(logic.canBite(), "Should still be able to bite after 6 ticks");
    }
}
