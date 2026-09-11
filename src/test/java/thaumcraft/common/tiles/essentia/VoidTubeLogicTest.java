package thaumcraft.common.tiles.essentia;

import net.minecraft.core.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thaumcraft.api.aspects.Aspect;

import static org.junit.jupiter.api.Assertions.*;

public class VoidTubeLogicTest {

    private VoidTubeLogic logic;
    private boolean changed;
    private boolean voided;

    @BeforeEach
    public void setup() {
        changed = false;
        voided = false;
        logic = new VoidTubeLogic(() -> changed = true, () -> voided = true);
    }

    @Test
    public void testVoidExcessEssentia() {
        // Initial state: empty
        assertNull(logic.getEssentiaType(Direction.NORTH));
        assertEquals(0, logic.getEssentiaAmount(Direction.NORTH));

        // Add 1 Aer. Should be accepted and not voided.
        Aspect aer = Aspect.getAspect("aer");
        int added = logic.addEssentia(aer, 1, Direction.NORTH);
        assertEquals(1, added);
        assertTrue(changed);
        assertFalse(voided);
        assertEquals(1, logic.getEssentiaAmount(Direction.NORTH));
        assertEquals(aer, logic.getEssentiaType(Direction.NORTH));

        // Add another 1 Aer. Since tube max capacity is 1, it will try to add, fail, and then void it.
        changed = false;
        voided = false;
        int addedMore = logic.addEssentia(aer, 1, Direction.NORTH);

        // VoidTubeLogic pretends to accept it by returning the requested amount
        assertEquals(1, addedMore);

        // It should have voided the excess
        assertTrue(voided);

        // The storage should remain at 1
        assertEquals(1, logic.getEssentiaAmount(Direction.NORTH));
    }

    @Test
    public void testVoidMismatchEssentia() {
        // Add 1 Aer
        Aspect aer = Aspect.getAspect("aer");
        logic.addEssentia(aer, 1, Direction.NORTH);
        assertEquals(1, logic.getEssentiaAmount(Direction.NORTH));

        // Try to add 1 Terra. The regular tube logic would reject it (return 0).
        // VoidTubeLogic should void it (return 1, call onVoidCallback).
        voided = false;
        Aspect terra = Aspect.getAspect("terra");
        int addedMismatch = logic.addEssentia(terra, 1, Direction.NORTH);

        // Pretends to accept it
        assertEquals(1, addedMismatch);

        // Voids it
        assertTrue(voided);

        // Keeps original aspect and amount
        assertEquals(1, logic.getEssentiaAmount(Direction.NORTH));
        assertEquals(aer, logic.getEssentiaType(Direction.NORTH));
    }

    @Test
    public void testClosedFace() {
        logic.toggleOpenFace(Direction.NORTH);

        voided = false;
        Aspect aer = Aspect.getAspect("aer");
        int added = logic.addEssentia(aer, 1, Direction.NORTH);

        // If face is closed, it should act normally and return 0 (rejected entirely, NOT voided)
        assertEquals(0, added);
        assertFalse(voided);
        assertEquals(0, logic.getEssentiaAmount(Direction.NORTH));
    }
}
