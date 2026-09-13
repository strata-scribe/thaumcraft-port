package thaumcraft.common.entities.monster.tainted;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaintedAnimalMutationLogicTest {

    @Test
    void testIsMutationThresholdReached() {
        assertTrue(TaintedAnimalMutationLogic.isMutationThresholdReached(10.0f, 10.0f));
        assertTrue(TaintedAnimalMutationLogic.isMutationThresholdReached(11.0f, 10.0f));
        assertFalse(TaintedAnimalMutationLogic.isMutationThresholdReached(9.0f, 10.0f));
    }

    @Test
    void testCalculateExposureIncrement() {
        // High severity, 0 resistance
        assertEquals(5.0f, TaintedAnimalMutationLogic.calculateExposureIncrement(5.0f, 0.0f), 0.001f);

        // 50% resistance
        assertEquals(2.5f, TaintedAnimalMutationLogic.calculateExposureIncrement(5.0f, 0.5f), 0.001f);

        // 100% resistance
        assertEquals(0.0f, TaintedAnimalMutationLogic.calculateExposureIncrement(5.0f, 1.0f), 0.001f);

        // Over 100% resistance (clamped to 1.0)
        assertEquals(0.0f, TaintedAnimalMutationLogic.calculateExposureIncrement(5.0f, 1.5f), 0.001f);

        // Negative resistance (clamped to 0.0)
        assertEquals(5.0f, TaintedAnimalMutationLogic.calculateExposureIncrement(5.0f, -0.5f), 0.001f);

        // Negative severity (not mathematically clamped below 0 in theory, but maxed at 0.0)
        assertEquals(0.0f, TaintedAnimalMutationLogic.calculateExposureIncrement(-5.0f, 0.5f), 0.001f);
    }

    @Test
    void testComputeMutatedHealth() {
        // 1.5x severity multiplier, 0 flat bonus
        assertEquals(30.0f, TaintedAnimalMutationLogic.computeMutatedHealth(20.0f, 1.5f, 0.0f), 0.001f);

        // 1.0x severity multiplier, 10 flat bonus
        assertEquals(30.0f, TaintedAnimalMutationLogic.computeMutatedHealth(20.0f, 1.0f, 10.0f), 0.001f);

        // Severity multiplier below 1 (clamped to 1.0)
        assertEquals(30.0f, TaintedAnimalMutationLogic.computeMutatedHealth(20.0f, 0.5f, 10.0f), 0.001f);

        // Negative flat bonus (clamped to 0.0)
        assertEquals(30.0f, TaintedAnimalMutationLogic.computeMutatedHealth(20.0f, 1.5f, -10.0f), 0.001f);
    }

    @Test
    void testComputeMutatedAttackDamage() {
        // Passive mob with 0 base attack, mutant gets 5 base attack, 1.0x severity
        assertEquals(5.0f, TaintedAnimalMutationLogic.computeMutatedAttackDamage(0.0f, 5.0f, 1.0f), 0.001f);

        // Passive mob with 2 base attack, mutant gets 5 base attack, 1.5x severity
        assertEquals(7.5f, TaintedAnimalMutationLogic.computeMutatedAttackDamage(2.0f, 5.0f, 1.5f), 0.001f);

        // Neutral mob with 8 base attack, mutant gets 5 base attack, 1.5x severity
        assertEquals(12.0f, TaintedAnimalMutationLogic.computeMutatedAttackDamage(8.0f, 5.0f, 1.5f), 0.001f);

        // Severity multiplier below 1 (clamped to 1.0)
        assertEquals(8.0f, TaintedAnimalMutationLogic.computeMutatedAttackDamage(8.0f, 5.0f, 0.5f), 0.001f);
    }
}
