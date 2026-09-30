package thaumcraft.common.items.curios.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PrimordialPearlLogic Unit Tests")
public class PrimordialPearlLogicTest {

    @Test
    @DisplayName("Verify max durability")
    void testMaxDurability() {
        assertEquals(8, PrimordialPearlLogic.getMaxDurability());
    }

    @Test
    @DisplayName("Verify remaining uses across damage range, negative values, and overflow")
    void testRemainingUses() {
        assertEquals(8, PrimordialPearlLogic.getRemainingUses(0));
        assertEquals(7, PrimordialPearlLogic.getRemainingUses(1));
        assertEquals(4, PrimordialPearlLogic.getRemainingUses(4));
        assertEquals(1, PrimordialPearlLogic.getRemainingUses(7));
        assertEquals(0, PrimordialPearlLogic.getRemainingUses(8));
        assertEquals(0, PrimordialPearlLogic.getRemainingUses(9));
        assertEquals(0, PrimordialPearlLogic.getRemainingUses(100));

        // Negative damage handled safely
        assertEquals(9, PrimordialPearlLogic.getRemainingUses(-1));
        assertEquals(13, PrimordialPearlLogic.getRemainingUses(-5));
    }

    @Test
    @DisplayName("Verify isDepleted across damage range, negative values, and overflow")
    void testIsDepleted() {
        assertFalse(PrimordialPearlLogic.isDepleted(-5));
        assertFalse(PrimordialPearlLogic.isDepleted(-1));
        assertFalse(PrimordialPearlLogic.isDepleted(0));
        assertFalse(PrimordialPearlLogic.isDepleted(1));
        assertFalse(PrimordialPearlLogic.isDepleted(7));
        assertTrue(PrimordialPearlLogic.isDepleted(8));
        assertTrue(PrimordialPearlLogic.isDepleted(9));
        assertTrue(PrimordialPearlLogic.isDepleted(100));
    }

    @Test
    @DisplayName("Verify canCraft across damage range, negative values, and overflow")
    void testCanCraft() {
        assertTrue(PrimordialPearlLogic.canCraft(-5));
        assertTrue(PrimordialPearlLogic.canCraft(-1));
        assertTrue(PrimordialPearlLogic.canCraft(0));
        assertTrue(PrimordialPearlLogic.canCraft(1));
        assertTrue(PrimordialPearlLogic.canCraft(7));
        assertFalse(PrimordialPearlLogic.canCraft(8));
        assertFalse(PrimordialPearlLogic.canCraft(9));
        assertFalse(PrimordialPearlLogic.canCraft(100));
    }

    @Test
    @DisplayName("Verify applyCraftDamage clamps between 0 and 8")
    void testApplyCraftDamage() {
        assertEquals(1, PrimordialPearlLogic.applyCraftDamage(-5));
        assertEquals(1, PrimordialPearlLogic.applyCraftDamage(-1));
        assertEquals(1, PrimordialPearlLogic.applyCraftDamage(0));
        assertEquals(2, PrimordialPearlLogic.applyCraftDamage(1));
        assertEquals(5, PrimordialPearlLogic.applyCraftDamage(4));
        assertEquals(8, PrimordialPearlLogic.applyCraftDamage(7));
        assertEquals(8, PrimordialPearlLogic.applyCraftDamage(8));
        assertEquals(8, PrimordialPearlLogic.applyCraftDamage(9));
        assertEquals(8, PrimordialPearlLogic.applyCraftDamage(100));
    }

    @Test
    @DisplayName("Verify calculateAuraCharge calculation and zero clamping")
    void testCalculateAuraCharge() {
        assertEquals(200, PrimordialPearlLogic.calculateAuraCharge(0));
        assertEquals(175, PrimordialPearlLogic.calculateAuraCharge(1));
        assertEquals(100, PrimordialPearlLogic.calculateAuraCharge(4));
        assertEquals(25, PrimordialPearlLogic.calculateAuraCharge(7));
        assertEquals(0, PrimordialPearlLogic.calculateAuraCharge(8));
        assertEquals(0, PrimordialPearlLogic.calculateAuraCharge(9));
        assertEquals(0, PrimordialPearlLogic.calculateAuraCharge(100));

        // Negative damage handled safely
        assertEquals(225, PrimordialPearlLogic.calculateAuraCharge(-1));
    }
}
