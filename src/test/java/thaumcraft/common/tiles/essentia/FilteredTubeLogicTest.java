package thaumcraft.common.tiles.essentia;

import net.minecraft.core.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thaumcraft.api.aspects.Aspect;

import static org.junit.jupiter.api.Assertions.*;

public class FilteredTubeLogicTest {

    private FilteredTubeLogic logic;
    private boolean changed;

    @BeforeEach
    public void setup() {
        changed = false;
        logic = new FilteredTubeLogic(() -> changed = true);
    }

    @Test
    public void testNoFilterBehavesLikeNormalTube() {
        assertNull(logic.getAspectFilter());

        logic.setSuction(Aspect.FIRE, 32);
        assertEquals(Aspect.FIRE, logic.getSuctionType(Direction.NORTH));
        assertEquals(32, logic.getSuctionAmount(Direction.NORTH));

        assertEquals(1, logic.addEssentia(Aspect.WATER, 1, Direction.SOUTH));
        assertEquals(Aspect.WATER, logic.getEssentiaType(Direction.SOUTH));
        assertEquals(1, logic.getEssentiaAmount(Direction.SOUTH));
    }

    @Test
    public void testFilterBlocksNonMatchingSuction() {
        logic.setAspectFilter(Aspect.WATER);

        logic.setSuction(Aspect.FIRE, 32);

        // Since the filter blocks non-matching, it shouldn't apply FIRE suction
        // The TubeLogic initializes suction as null/0 by default
        assertNull(logic.getSuctionType(Direction.NORTH));
        assertEquals(0, logic.getSuctionAmount(Direction.NORTH));
    }

    @Test
    public void testFilterAllowsMatchingSuction() {
        logic.setAspectFilter(Aspect.WATER);

        logic.setSuction(Aspect.WATER, 32);

        assertEquals(Aspect.WATER, logic.getSuctionType(Direction.NORTH));
        assertEquals(32, logic.getSuctionAmount(Direction.NORTH));
    }

    @Test
    public void testFilterBlocksNonMatchingEssentiaFlow() {
        logic.setAspectFilter(Aspect.WATER);

        int added = logic.addEssentia(Aspect.FIRE, 1, Direction.SOUTH);

        assertEquals(0, added);
        assertNull(logic.getEssentiaType(Direction.SOUTH));
        assertEquals(0, logic.getEssentiaAmount(Direction.SOUTH));
    }

    @Test
    public void testFilterAllowsMatchingEssentiaFlow() {
        logic.setAspectFilter(Aspect.WATER);

        int added = logic.addEssentia(Aspect.WATER, 1, Direction.SOUTH);

        assertEquals(1, added);
        assertEquals(Aspect.WATER, logic.getEssentiaType(Direction.SOUTH));
        assertEquals(1, logic.getEssentiaAmount(Direction.SOUTH));
    }
}
