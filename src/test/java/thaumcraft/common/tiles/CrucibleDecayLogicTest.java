package thaumcraft.common.tiles;

import org.junit.jupiter.api.Test;
import thaumcraft.common.tiles.crafting.CrucibleDecayLogic;

import static org.junit.jupiter.api.Assertions.*;

public class CrucibleDecayLogicTest {

    @Test
    public void testIsDecayActive() {
        assertFalse(CrucibleDecayLogic.isDecayActive(0));
        assertFalse(CrucibleDecayLogic.isDecayActive(400));
        assertTrue(CrucibleDecayLogic.isDecayActive(401));
        assertTrue(CrucibleDecayLogic.isDecayActive(1000));
    }

    @Test
    public void testShouldDecayThisTick() {
        assertFalse(CrucibleDecayLogic.shouldDecayThisTick(0));
        assertFalse(CrucibleDecayLogic.shouldDecayThisTick(400));
        assertTrue(CrucibleDecayLogic.shouldDecayThisTick(500));
        assertFalse(CrucibleDecayLogic.shouldDecayThisTick(550));
        assertTrue(CrucibleDecayLogic.shouldDecayThisTick(600));
    }

    @Test
    public void testGetAuraFluxPollution() {
        assertEquals(0.25f, CrucibleDecayLogic.getAuraFluxPollution(), 0.001f);
    }

    @Test
    public void testShouldSpillAsGoo() {
        assertTrue(CrucibleDecayLogic.shouldSpillAsGoo(0.0f));
        assertTrue(CrucibleDecayLogic.shouldSpillAsGoo(0.24f));
        assertFalse(CrucibleDecayLogic.shouldSpillAsGoo(0.25f));
        assertFalse(CrucibleDecayLogic.shouldSpillAsGoo(0.99f));
    }
}
