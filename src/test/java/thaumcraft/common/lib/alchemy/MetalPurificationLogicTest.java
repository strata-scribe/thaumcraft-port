package thaumcraft.common.lib.alchemy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MetalPurificationLogicTest {

    @Test
    void canPurify_validInputAndAspects_returnsTrue() {
        assertTrue(MetalPurificationLogic.canPurify(true, 5, 5));
        assertTrue(MetalPurificationLogic.canPurify(true, 10, 5));
        assertTrue(MetalPurificationLogic.canPurify(true, 5, 10));
        assertTrue(MetalPurificationLogic.canPurify(true, 10, 10));
    }

    @Test
    void canPurify_invalidInput_returnsFalse() {
        assertFalse(MetalPurificationLogic.canPurify(false, 5, 5));
        assertFalse(MetalPurificationLogic.canPurify(false, 10, 10));
    }

    @Test
    void canPurify_insufficientAspects_returnsFalse() {
        assertFalse(MetalPurificationLogic.canPurify(true, 4, 5));
        assertFalse(MetalPurificationLogic.canPurify(true, 5, 4));
        assertFalse(MetalPurificationLogic.canPurify(true, 0, 0));
    }

    @Test
    void canPurify_customRequirements_validAspects_returnsTrue() {
        assertTrue(MetalPurificationLogic.canPurify(true, 10, 10, 10, 10));
        assertTrue(MetalPurificationLogic.canPurify(true, 15, 10, 15, 10));
    }

    @Test
    void canPurify_customRequirements_insufficientAspects_returnsFalse() {
        assertFalse(MetalPurificationLogic.canPurify(true, 5, 10, 10, 10));
        assertFalse(MetalPurificationLogic.canPurify(true, 10, 10, 5, 10));
    }

    @Test
    void getSmeltingYield_nativeCluster_returnsDoubleBaseYield() {
        assertEquals(2, MetalPurificationLogic.getSmeltingYield(true, 1));
        assertEquals(4, MetalPurificationLogic.getSmeltingYield(true, 2));
        assertEquals(10, MetalPurificationLogic.getSmeltingYield(true, 5));
    }

    @Test
    void getSmeltingYield_notNativeCluster_returnsBaseYield() {
        assertEquals(1, MetalPurificationLogic.getSmeltingYield(false, 1));
        assertEquals(2, MetalPurificationLogic.getSmeltingYield(false, 2));
        assertEquals(5, MetalPurificationLogic.getSmeltingYield(false, 5));
    }
}
