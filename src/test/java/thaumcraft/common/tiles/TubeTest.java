package thaumcraft.common.tiles;

import net.minecraft.core.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thaumcraft.common.tiles.essentia.TubeLogic;

import static org.junit.jupiter.api.Assertions.*;

public class TubeTest {

    private TubeLogic tube;
    private boolean changed;

    @BeforeEach
    public void setup() {
        changed = false;
        tube = new TubeLogic(() -> changed = true);
    }

    @Test
    public void testSuctionPropagation() {
        tube.setSuction(null, 64);

        for (Direction dir : Direction.values()) {
            assertNull(tube.getSuctionType(dir));
            assertEquals(64, tube.getSuctionAmount(dir));
        }
    }

    @Test
    public void testEssentiaTransfer() {
        assertNull(tube.getEssentiaType(Direction.NORTH));
        assertEquals(0, tube.getEssentiaAmount(Direction.NORTH));

        int added = tube.addEssentia(null, 1, Direction.NORTH);
        assertEquals(1, added);
        assertTrue(changed);
        assertNull(tube.getEssentiaType(Direction.NORTH));
        assertEquals(1, tube.getEssentiaAmount(Direction.NORTH));

        changed = false;
        int addedMore = tube.addEssentia(null, 1, Direction.NORTH);
        assertEquals(0, addedMore);
        assertFalse(changed);
        assertEquals(1, tube.getEssentiaAmount(Direction.NORTH));

        int taken = tube.takeEssentia(null, 1, Direction.SOUTH);
        assertEquals(1, taken);
        assertNull(tube.getEssentiaType(Direction.SOUTH));
        assertEquals(0, tube.getEssentiaAmount(Direction.SOUTH));

        int takenEmpty = tube.takeEssentia(null, 1, Direction.SOUTH);
        assertEquals(0, takenEmpty);
    }

    @Test
    public void testValveInteraction() {
        Direction face = Direction.NORTH;

        assertTrue(tube.isOpen(face));
        assertTrue(tube.canInputFrom(face));
        assertTrue(tube.canOutputTo(face));
        assertTrue(tube.isConnectable(face));

        tube.toggleOpenFace(face);

        assertFalse(tube.isOpen(face));
        assertFalse(tube.canInputFrom(face));
        assertFalse(tube.canOutputTo(face));
        assertFalse(tube.isConnectable(face));

        int added = tube.addEssentia(null, 1, face);
        assertEquals(0, added);

        int addedOther = tube.addEssentia(null, 1, Direction.SOUTH);
        assertEquals(1, addedOther);

        int taken = tube.takeEssentia(null, 1, face);
        assertEquals(0, taken);

        int takenOther = tube.takeEssentia(null, 1, Direction.SOUTH);
        assertEquals(1, takenOther);
    }
}
