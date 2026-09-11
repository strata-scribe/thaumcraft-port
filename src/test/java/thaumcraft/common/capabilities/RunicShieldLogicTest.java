package thaumcraft.common.capabilities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import thaumcraft.common.lib.capabilities.RunicShieldLogic;

public class RunicShieldLogicTest {

    @Test
    public void testAbsorptionMathComplete() {
        int currentShield = 10;
        float damage = 5.0f;

        RunicShieldLogic.DamageResult result = RunicShieldLogic.calculateDamageRemaining(currentShield, damage);

        assertEquals(5, result.newShield);
        assertEquals(0.0f, result.remainingDamage, 0.01f);
    }

    @Test
    public void testAbsorptionMathPartial() {
        int currentShield = 5;
        float damage = 12.0f;

        RunicShieldLogic.DamageResult result = RunicShieldLogic.calculateDamageRemaining(currentShield, damage);

        assertEquals(0, result.newShield);
        assertEquals(7.0f, result.remainingDamage, 0.01f);
    }

    @Test
    public void testTickRechargeDelayReduction() {
        int currentShield = 5;
        int maxShield = 10;
        int rechargeDelay = 5;

        // Reduce delay by 1
        RunicShieldLogic.TickResult result = RunicShieldLogic.processTick(currentShield, maxShield, rechargeDelay, 1);

        assertEquals(4, result.newRechargeDelay);
        assertFalse(result.wantsToRecharge);
    }

    @Test
    public void testTickRechargeRequestsVis() {
        int currentShield = 5;
        int maxShield = 10;
        int rechargeDelay = 0;

        // Tick is multiple of 20, should request vis
        RunicShieldLogic.TickResult result = RunicShieldLogic.processTick(currentShield, maxShield, rechargeDelay, 20);

        assertEquals(0, result.newRechargeDelay);
        assertTrue(result.wantsToRecharge);
    }

    @Test
    public void testTickRechargeWait() {
        int currentShield = 5;
        int maxShield = 10;
        int rechargeDelay = 0;

        // Tick is NOT multiple of 20, should wait
        RunicShieldLogic.TickResult result = RunicShieldLogic.processTick(currentShield, maxShield, rechargeDelay, 19);

        assertEquals(0, result.newRechargeDelay);
        assertFalse(result.wantsToRecharge);
    }

    @Test
    public void testTickFullShield() {
        int currentShield = 10;
        int maxShield = 10;
        int rechargeDelay = 0;

        // Tick is multiple of 20, but shield is full
        RunicShieldLogic.TickResult result = RunicShieldLogic.processTick(currentShield, maxShield, rechargeDelay, 20);

        assertEquals(0, result.newRechargeDelay);
        assertFalse(result.wantsToRecharge);
    }
}
