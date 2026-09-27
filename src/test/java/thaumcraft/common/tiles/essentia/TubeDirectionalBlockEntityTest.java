package thaumcraft.common.tiles.essentia;

import net.minecraft.core.Direction;
import org.junit.jupiter.api.Test;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.tiles.essentia.logic.TubeDirectionalLogic;

import static org.junit.jupiter.api.Assertions.*;

public class TubeDirectionalBlockEntityTest {

    @Test
    public void testTubeDirectionalBlockEntityLogic() {
        TubeDirectionalLogic logic = new TubeDirectionalLogic(() -> {});

        logic.setFacing(Direction.NORTH);

        Aspect mockAspect = null; // Use null to avoid bootstrapping aspects

        // Input should only be allowed from SOUTH (opposite of facing NORTH)
        assertTrue(logic.canInputFrom(Direction.SOUTH));
        assertFalse(logic.canInputFrom(Direction.NORTH));
        assertFalse(logic.canInputFrom(Direction.EAST));

        // Output should only be allowed to NORTH (facing direction)
        assertTrue(logic.canOutputTo(Direction.NORTH));
        assertFalse(logic.canOutputTo(Direction.SOUTH));
        assertFalse(logic.canOutputTo(Direction.WEST));

        // Suction amount test
        logic.setSuction(mockAspect, 32);

        // Suction should only be readable from SOUTH (opposite of facing)
        assertEquals(31, logic.getSuctionAmount(Direction.SOUTH)); // 32 - 1 drop from tube
        assertEquals(0, logic.getSuctionAmount(Direction.NORTH));
        assertEquals(0, logic.getSuctionAmount(Direction.EAST));

        // Essentia adding test
        assertEquals(1, logic.addEssentia(mockAspect, 1, Direction.SOUTH)); // Should allow input from SOUTH
        assertEquals(0, logic.addEssentia(mockAspect, 1, Direction.NORTH)); // Should block input from NORTH

        // Essentia taking test
        assertEquals(1, logic.takeEssentia(mockAspect, 1, Direction.NORTH)); // Should allow taking to NORTH
        assertEquals(0, logic.takeEssentia(mockAspect, 1, Direction.SOUTH)); // Should block taking from SOUTH
    }
}
