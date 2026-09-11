package thaumcraft.common.items.tools;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PrimalCrusherLogic.
 */
public class PrimalCrusherLogicTest {

    @Test
    public void testCalculate3x3GridUpFaceXZPlane() {
        PrimalCrusherLogic.BlockCoordinate center = new PrimalCrusherLogic.BlockCoordinate(5, 50, 10);
        List<PrimalCrusherLogic.BlockCoordinate> grid = PrimalCrusherLogic.calculate3x3Grid(center, PrimalCrusherLogic.BlockFace.UP);

        assertEquals(9, grid.size());
        for (PrimalCrusherLogic.BlockCoordinate pos : grid) {
            assertEquals(50, pos.y(), "Y level must remain identical on UP face click");
            assertTrue(Math.abs(pos.x() - 5) <= 1);
            assertTrue(Math.abs(pos.z() - 10) <= 1);
        }
    }

    @Test
    public void testCalculate3x3GridNorthFaceXYPlane() {
        PrimalCrusherLogic.BlockCoordinate center = new PrimalCrusherLogic.BlockCoordinate(5, 50, 10);
        List<PrimalCrusherLogic.BlockCoordinate> grid = PrimalCrusherLogic.calculate3x3Grid(center, PrimalCrusherLogic.BlockFace.NORTH);

        assertEquals(9, grid.size());
        for (PrimalCrusherLogic.BlockCoordinate pos : grid) {
            assertEquals(10, pos.z(), "Z level must remain identical on NORTH face click");
            assertTrue(Math.abs(pos.x() - 5) <= 1);
            assertTrue(Math.abs(pos.y() - 50) <= 1);
        }
    }

    @Test
    public void testCalculate3x3GridEastFaceYZPlane() {
        PrimalCrusherLogic.BlockCoordinate center = new PrimalCrusherLogic.BlockCoordinate(5, 50, 10);
        List<PrimalCrusherLogic.BlockCoordinate> grid = PrimalCrusherLogic.calculate3x3Grid(center, PrimalCrusherLogic.BlockFace.EAST);

        assertEquals(9, grid.size());
        for (PrimalCrusherLogic.BlockCoordinate pos : grid) {
            assertEquals(5, pos.x(), "X level must remain identical on EAST face click");
            assertTrue(Math.abs(pos.y() - 50) <= 1);
            assertTrue(Math.abs(pos.z() - 10) <= 1);
        }
    }

    @Test
    public void testIsEffectiveMaterial() {
        // Valid effective materials
        assertTrue(PrimalCrusherLogic.isEffectiveMaterial("minecraft:stone"));
        assertTrue(PrimalCrusherLogic.isEffectiveMaterial("minecraft:dirt"));
        assertTrue(PrimalCrusherLogic.isEffectiveMaterial("minecraft:sand"));
        assertTrue(PrimalCrusherLogic.isEffectiveMaterial("minecraft:gravel"));
        assertTrue(PrimalCrusherLogic.isEffectiveMaterial("thaumcraft:eldritch_stone"));

        // Case insensitivity
        assertTrue(PrimalCrusherLogic.isEffectiveMaterial("STONE"));

        // Invalid materials
        assertFalse(PrimalCrusherLogic.isEffectiveMaterial("minecraft:wood"));
        assertFalse(PrimalCrusherLogic.isEffectiveMaterial("minecraft:obsidian"));
        assertFalse(PrimalCrusherLogic.isEffectiveMaterial(null));
        assertFalse(PrimalCrusherLogic.isEffectiveMaterial(""));
    }

    @Test
    public void testCalculateDamageBonus() {
        float baseDamage = 10.0f;

        // Eldritch and Tainted entities get 1.5x damage
        assertEquals(15.0f, PrimalCrusherLogic.calculateDamageBonus("eldritch_guardian", baseDamage), 1e-5);
        assertEquals(15.0f, PrimalCrusherLogic.calculateDamageBonus("thaumcraft:tainted_crawler", baseDamage), 1e-5);
        assertEquals(15.0f, PrimalCrusherLogic.calculateDamageBonus("TaintSeed", baseDamage), 1e-5);

        // Normal entities get base damage
        assertEquals(10.0f, PrimalCrusherLogic.calculateDamageBonus("minecraft:zombie", baseDamage), 1e-5);
        assertEquals(10.0f, PrimalCrusherLogic.calculateDamageBonus("minecraft:skeleton", baseDamage), 1e-5);

        // Null/empty safety
        assertEquals(10.0f, PrimalCrusherLogic.calculateDamageBonus(null, baseDamage), 1e-5);
        assertEquals(10.0f, PrimalCrusherLogic.calculateDamageBonus("", baseDamage), 1e-5);
    }

}
