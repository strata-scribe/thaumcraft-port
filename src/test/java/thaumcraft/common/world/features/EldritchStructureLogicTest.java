package thaumcraft.common.world.features;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EldritchStructureLogicTest {

    @Test
    public void testObeliskPlatform() {
        assertTrue(EldritchStructureLogic.isObeliskPlatformBlock(0, 0, 3));
        assertTrue(EldritchStructureLogic.isObeliskPlatformBlock(3, 3, 3));
        assertTrue(EldritchStructureLogic.isObeliskPlatformBlock(-3, -3, 3));
        assertFalse(EldritchStructureLogic.isObeliskPlatformBlock(4, 0, 3));
    }

    @Test
    public void testObeliskPedestal() {
        assertTrue(EldritchStructureLogic.isObeliskPedestal(3, 3, 3));
        assertTrue(EldritchStructureLogic.isObeliskPedestal(-3, 3, 3));
        assertFalse(EldritchStructureLogic.isObeliskPedestal(2, 3, 3));
        assertFalse(EldritchStructureLogic.isObeliskPedestal(0, 0, 3));
    }

    @Test
    public void testObeliskSpire() {
        assertTrue(EldritchStructureLogic.isObeliskSpireBlock(0, 0));
        assertFalse(EldritchStructureLogic.isObeliskSpireBlock(1, 0));
    }

    @Test
    public void testObeliskSpireHeight() {
        assertEquals(12, EldritchStructureLogic.getObeliskSpireHeight());
    }

    @Test
    public void testObeliskCapstone() {
        assertTrue(EldritchStructureLogic.isObeliskCapstone(0, 12, 0, 12));
        assertFalse(EldritchStructureLogic.isObeliskCapstone(0, 11, 0, 12));
        assertFalse(EldritchStructureLogic.isObeliskCapstone(1, 12, 0, 12));
    }

    @Test
    public void testSinisterMonolith() {
        assertTrue(EldritchStructureLogic.isSinisterMonolith(5, 0, 5));
        assertTrue(EldritchStructureLogic.isSinisterMonolith(-5, 0, 5));
        assertTrue(EldritchStructureLogic.isSinisterMonolith(0, 5, 5));
        assertTrue(EldritchStructureLogic.isSinisterMonolith(0, -5, 5));
        assertFalse(EldritchStructureLogic.isSinisterMonolith(5, 5, 5));
        assertFalse(EldritchStructureLogic.isSinisterMonolith(4, 0, 5));
    }

    @Test
    public void testCalculateMoundRadius() {
        assertEquals(6, EldritchStructureLogic.calculateMoundRadius(0));
        assertEquals(7, EldritchStructureLogic.calculateMoundRadius(1));
        assertEquals(8, EldritchStructureLogic.calculateMoundRadius(2));
    }

    @Test
    public void testInsideMoundChamber() {
        assertTrue(EldritchStructureLogic.isInsideMoundChamber(0, 0, 0, 6));
        assertTrue(EldritchStructureLogic.isInsideMoundChamber(5, 0, 0, 6));
        assertFalse(EldritchStructureLogic.isInsideMoundChamber(6, 6, 6, 6));
    }

    @Test
    public void testMoundLootChest() {
        assertTrue(EldritchStructureLogic.isMoundLootChest(0, 0, 0));
        assertFalse(EldritchStructureLogic.isMoundLootChest(1, 0, 0));
        assertFalse(EldritchStructureLogic.isMoundLootChest(0, 1, 0));
    }

    @Test
    public void testMoundSpawner() {
        assertTrue(EldritchStructureLogic.isMoundSpawner(3, 0, 0));
        assertTrue(EldritchStructureLogic.isMoundSpawner(-3, 0, 0));
        assertTrue(EldritchStructureLogic.isMoundSpawner(0, 0, 3));
        assertTrue(EldritchStructureLogic.isMoundSpawner(0, 0, -3));
        assertFalse(EldritchStructureLogic.isMoundSpawner(2, 0, 0));
        assertFalse(EldritchStructureLogic.isMoundSpawner(3, 1, 0));
        assertFalse(EldritchStructureLogic.isMoundSpawner(3, 0, 3));
    }
}
