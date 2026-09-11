package thaumcraft.common.tiles.essentia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thaumcraft.api.aspects.Aspect;

import static org.junit.jupiter.api.Assertions.*;

public class JarLogicTest {

    private JarLogic jarNormal;
    private JarLogic jarVoid;

    @BeforeEach
    void setUp() {
        jarNormal = new JarLogic(false);
        jarVoid = new JarLogic(true);
    }

    // -------------------------------------------------------------------------
    // 1. Constants
    // -------------------------------------------------------------------------

    @Test
    void testCapacityConstant() {
        assertEquals(250, JarLogic.CAPACITY, "CAPACITY must be exactly 250");
        assertEquals(10, JarLogic.PHIAL_AMOUNT, "PHIAL_AMOUNT must be exactly 10");
    }

    // -------------------------------------------------------------------------
    // 2. Acceptance rules
    // -------------------------------------------------------------------------

    @Test
    void testEmptyJarAcceptsAnyAspect() {
        assertTrue(jarNormal.doesContainerAccept(Aspect.FIRE), "Empty jar (no filter) should accept any aspect");
        assertTrue(jarNormal.doesContainerAccept(Aspect.WATER), "Empty jar (no filter) should accept any aspect");
    }

    @Test
    void testFilteredJarRejectsWrongAspect() {
        jarNormal.setAspectFilter(Aspect.FIRE);
        assertFalse(jarNormal.doesContainerAccept(Aspect.WATER), "Jar with FIRE filter should reject WATER");
        assertTrue(jarNormal.doesContainerAccept(Aspect.FIRE), "Jar with FIRE filter should accept FIRE");
    }

    @Test
    void testJarRejectsDifferentAspectWhenFilled() {
        jarNormal.addToContainer(Aspect.FIRE, 10);
        assertEquals(10, jarNormal.getAmount());

        int leftover = jarNormal.addToContainer(Aspect.WATER, 5);
        assertEquals(5, leftover, "Adding WATER to a FIRE jar should return full leftover");
        assertEquals(10, jarNormal.getAmount(), "Amount must not change when wrong aspect added");
        assertEquals(Aspect.FIRE, jarNormal.getAspect(), "Stored aspect must remain FIRE");
    }

    // -------------------------------------------------------------------------
    // 3. addToContainer (Normal & Void Jars)
    // -------------------------------------------------------------------------

    @Test
    void testAddSetsAspectAndAmount() {
        int leftover = jarNormal.addToContainer(Aspect.EARTH, 30);
        assertEquals(0, leftover, "No leftover for an amount well below CAPACITY");
        assertEquals(Aspect.EARTH, jarNormal.getAspect());
        assertEquals(30, jarNormal.getAmount());
    }

    @Test
    void testAddUpToCapacityExactly() {
        int leftover = jarNormal.addToContainer(Aspect.AIR, JarLogic.CAPACITY);
        assertEquals(0, leftover);
        assertEquals(JarLogic.CAPACITY, jarNormal.getAmount());
    }

    @Test
    void testAddBeyondCapacityReturnsLeftover() {
        jarNormal.addToContainer(Aspect.ORDER, 200);
        int leftover = jarNormal.addToContainer(Aspect.ORDER, 100);
        assertEquals(50, leftover, "Overflow of 50 should be returned as leftover");
        assertEquals(JarLogic.CAPACITY, jarNormal.getAmount(), "Amount must be capped at CAPACITY");
    }

    @Test
    void testAddZeroIsNoop() {
        int leftover = jarNormal.addToContainer(Aspect.FIRE, 0);
        assertEquals(0, leftover);
        assertNull(jarNormal.getAspect(), "Aspect must remain null after adding 0");
        assertEquals(0, jarNormal.getAmount());
    }

    @Test
    void testVoidJarInfiniteSink() {
        jarVoid.addToContainer(Aspect.MAGIC, 200);
        int leftover = jarVoid.addToContainer(Aspect.MAGIC, 100);
        assertEquals(0, leftover, "Void Jar should consume all overflow of the accepted aspect, returning 0 leftover");
        assertEquals(JarLogic.CAPACITY, jarVoid.getAmount(), "Amount must be capped at CAPACITY");
    }

    // -------------------------------------------------------------------------
    // 4. takeFromContainer
    // -------------------------------------------------------------------------

    @Test
    void testTakeSuccessfully() {
        jarNormal.addToContainer(Aspect.FIRE, 50);
        assertTrue(jarNormal.takeFromContainer(Aspect.FIRE, 20));
        assertEquals(30, jarNormal.getAmount());
        assertEquals(Aspect.FIRE, jarNormal.getAspect());
    }

    @Test
    void testTakeExactlyAll() {
        jarNormal.addToContainer(Aspect.WATER, 100);
        assertTrue(jarNormal.takeFromContainer(Aspect.WATER, 100));
        assertEquals(0, jarNormal.getAmount());
        assertNull(jarNormal.getAspect(), "Aspect must be cleared when amount reaches 0");
    }

    @Test
    void testTakeMoreThanAvailableReturnsFalse() {
        jarNormal.addToContainer(Aspect.EARTH, 10);
        assertFalse(jarNormal.takeFromContainer(Aspect.EARTH, 11), "Cannot take more than stored");
        assertEquals(10, jarNormal.getAmount(), "Amount must not change on failed take");
    }

    @Test
    void testTakeWrongAspectReturnsFalse() {
        jarNormal.addToContainer(Aspect.FIRE, 50);
        assertFalse(jarNormal.takeFromContainer(Aspect.WATER, 10), "Cannot take WATER from a FIRE jar");
    }

    // -------------------------------------------------------------------------
    // 5. Phial Interaction
    // -------------------------------------------------------------------------

    @Test
    void testPhialFillEmptyJar() {
        JarLogic.InteractionResult result = jarNormal.tryFillFromPhial(Aspect.MAGIC, 10);
        assertEquals(JarLogic.InteractionResult.SUCCESS, result);
        assertEquals(10, jarNormal.getAmount());
        assertEquals(Aspect.MAGIC, jarNormal.getAspect());
    }

    @Test
    void testPhialDrainSucceeds() {
        jarNormal.addToContainer(Aspect.MAGIC, 20);
        JarLogic.InteractionResult result = jarNormal.tryDrainToPhial();
        assertEquals(JarLogic.InteractionResult.SUCCESS, result);
        assertEquals(10, jarNormal.getAmount());
    }

    @Test
    void testPhialDrainFailsNotEnough() {
        jarNormal.addToContainer(Aspect.MAGIC, 5);
        JarLogic.InteractionResult result = jarNormal.tryDrainToPhial();
        assertEquals(JarLogic.InteractionResult.PASS, result);
        assertEquals(5, jarNormal.getAmount()); // Untouched
    }

    @Test
    void testPhialFillVoidJarAtCapacity() {
        jarVoid.addToContainer(Aspect.MAGIC, JarLogic.CAPACITY);
        JarLogic.InteractionResult result = jarVoid.tryFillFromPhial(Aspect.MAGIC, 10);
        assertEquals(JarLogic.InteractionResult.SUCCESS, result);
        assertEquals(JarLogic.CAPACITY, jarVoid.getAmount()); // Consumes phial without increasing amount
    }

    @Test
    void testPhialFillNormalJarAtCapacity() {
        jarNormal.addToContainer(Aspect.MAGIC, JarLogic.CAPACITY);
        JarLogic.InteractionResult result = jarNormal.tryFillFromPhial(Aspect.MAGIC, 10);
        assertEquals(JarLogic.InteractionResult.PASS, result);
        assertEquals(JarLogic.CAPACITY, jarNormal.getAmount());
    }

    @Test
    void testPhialFillNormalJarPartialCapacity() {
        jarNormal.addToContainer(Aspect.MAGIC, JarLogic.CAPACITY - 5);
        JarLogic.InteractionResult result = jarNormal.tryFillFromPhial(Aspect.MAGIC, 10);
        // Because of "amount + phialAmount <= CAPACITY" check for normal jars, it should pass (fail to fill).
        assertEquals(JarLogic.InteractionResult.PASS, result);
        assertEquals(JarLogic.CAPACITY - 5, jarNormal.getAmount());
    }

    // -------------------------------------------------------------------------
    // 6. Label locking and unlocking
    // -------------------------------------------------------------------------

    @Test
    void testApplyLabelToEmptyJar() {
        JarLogic.InteractionResult result = jarNormal.applyLabel(Aspect.AURA);
        assertEquals(JarLogic.InteractionResult.SUCCESS, result);
        assertEquals(Aspect.AURA, jarNormal.getAspectFilter());
        assertEquals(Aspect.AURA, jarNormal.getAspect(), "Applying label to empty jar should set its aspect");
        assertEquals(0, jarNormal.getAmount());
    }

    @Test
    void testApplyLabelToMatchingFilledJar() {
        jarNormal.addToContainer(Aspect.EARTH, 10);
        JarLogic.InteractionResult result = jarNormal.applyLabel(Aspect.EARTH);
        assertEquals(JarLogic.InteractionResult.SUCCESS, result);
        assertEquals(Aspect.EARTH, jarNormal.getAspectFilter());
    }

    @Test
    void testApplyLabelToMismatchedFilledJar() {
        jarNormal.addToContainer(Aspect.EARTH, 10);
        JarLogic.InteractionResult result = jarNormal.applyLabel(Aspect.AURA);
        assertEquals(JarLogic.InteractionResult.PASS, result);
        assertNull(jarNormal.getAspectFilter());
    }

    @Test
    void testRemoveLabel() {
        jarNormal.applyLabel(Aspect.AURA);
        JarLogic.InteractionResult result = jarNormal.removeLabel();
        assertEquals(JarLogic.InteractionResult.SUCCESS, result);
        assertNull(jarNormal.getAspectFilter());
    }

    @Test
    void testRemoveLabelWhenNoLabel() {
        JarLogic.InteractionResult result = jarNormal.removeLabel();
        assertEquals(JarLogic.InteractionResult.PASS, result);
    }
}
