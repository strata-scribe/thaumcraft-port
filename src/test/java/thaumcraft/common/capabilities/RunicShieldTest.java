package thaumcraft.common.capabilities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import thaumcraft.common.lib.capabilities.RunicShieldAttachment;

public class RunicShieldTest {

    @Test
    public void testAbsorptionMathComplete() {
        RunicShieldAttachment shield = new RunicShieldAttachment(10, 10, 0);
        float damage = 5.0f;
        int currentShield = shield.getCurrentShield();

        // Simulating the math in RunicShieldEvents
        float newAmount = damage;
        if (currentShield >= damage) {
            shield.setCurrentShield(currentShield - (int) Math.ceil(damage));
            newAmount = 0;
        } else {
            shield.setCurrentShield(0);
            newAmount = damage - currentShield;
        }

        assertEquals(5, shield.getCurrentShield());
        assertEquals(0.0f, newAmount, 0.01f);
    }

    @Test
    public void testAbsorptionMathPartial() {
        RunicShieldAttachment shield = new RunicShieldAttachment(5, 10, 0);
        float damage = 12.0f;
        int currentShield = shield.getCurrentShield();

        // Simulating the math in RunicShieldEvents
        float newAmount = damage;
        if (currentShield >= damage) {
            shield.setCurrentShield(currentShield - (int) Math.ceil(damage));
            newAmount = 0;
        } else {
            shield.setCurrentShield(0);
            newAmount = damage - currentShield;
        }

        assertEquals(0, shield.getCurrentShield());
        assertEquals(7.0f, newAmount, 0.01f);
    }

    @Test
    public void testRegenerationMath() {
        RunicShieldAttachment shield = new RunicShieldAttachment(5, 10, 5);

        // Simulating ticks in RunicShieldEvents
        int ticksElapsed = 0;

        // Wait for recharge delay (5 ticks)
        for (int i = 0; i < 5; i++) {
            ticksElapsed++;
            if (shield.getRechargeDelay() > 0) {
                shield.setRechargeDelay(shield.getRechargeDelay() - 1);
            }
        }

        assertEquals(0, shield.getRechargeDelay());
        assertEquals(5, shield.getCurrentShield());

        // Regenerate 1 shield (simulate 20 ticks where the tick count is divisible by 20 on the 20th tick)
        for (int i = 0; i < 20; i++) {
            ticksElapsed++;
            if (shield.getRechargeDelay() > 0) {
                shield.setRechargeDelay(shield.getRechargeDelay() - 1);
            } else if (shield.getCurrentShield() < shield.getMaxShield()) {
                if (ticksElapsed % 20 == 0) {
                    shield.setCurrentShield(Math.min(shield.getCurrentShield() + 1, shield.getMaxShield()));
                }
            }
        }

        assertEquals(6, shield.getCurrentShield());
    }
}
