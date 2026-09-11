package thaumcraft.common.tiles.crafting;

import org.junit.jupiter.api.Test;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

import static org.junit.jupiter.api.Assertions.*;

public class TransmutationLogicTest {

    @Test
    public void testThaumiumTransmutation() {
        AspectList available = new AspectList();
        available.add(Aspect.TOOL, 1);
        available.add(Aspect.MAGIC, 1);

        assertTrue(TransmutationLogic.canTransmute("THAUMIUM", available));

        AspectList required = TransmutationLogic.RECIPES.get("THAUMIUM");
        assertEquals(1.0, TransmutationLogic.calculatePurity(available, required), 0.001);
    }

    @Test
    public void testThaumiumTransmutationWithExtraAspects() {
        AspectList available = new AspectList();
        available.add(Aspect.TOOL, 2);
        available.add(Aspect.MAGIC, 1);
        available.add(Aspect.WATER, 1); // Extra aspect

        assertTrue(TransmutationLogic.canTransmute("THAUMIUM", available));

        AspectList required = TransmutationLogic.RECIPES.get("THAUMIUM");
        // Required is 2 total. Available is 4 total. Matched is 2. Purity is 2/4 = 0.5.
        assertEquals(0.5, TransmutationLogic.calculatePurity(available, required), 0.001);
    }

    @Test
    public void testThaumiumTransmutationMissingAspect() {
        AspectList available = new AspectList();
        available.add(Aspect.TOOL, 1);
        // Missing MAGIC

        assertFalse(TransmutationLogic.canTransmute("THAUMIUM", available));

        AspectList required = TransmutationLogic.RECIPES.get("THAUMIUM");
        assertEquals(0.0, TransmutationLogic.calculatePurity(available, required), 0.001);
    }

    @Test
    public void testAlchemicalBrassTransmutation() {
        AspectList available = new AspectList();
        available.add(Aspect.TOOL, 1);

        assertTrue(TransmutationLogic.canTransmute("ALCHEMICAL_BRASS", available));

        AspectList required = TransmutationLogic.RECIPES.get("ALCHEMICAL_BRASS");
        assertEquals(1.0, TransmutationLogic.calculatePurity(available, required), 0.001);
    }

    @Test
    public void testInvalidItemTransmutation() {
        AspectList available = new AspectList();
        available.add(Aspect.TOOL, 1);

        assertFalse(TransmutationLogic.canTransmute("NONEXISTENT_ITEM", available));
    }

    @Test
    public void testEmptyCrucible() {
        AspectList available = new AspectList();
        AspectList required = TransmutationLogic.RECIPES.get("THAUMIUM");

        assertFalse(TransmutationLogic.canTransmute("THAUMIUM", available));
        assertEquals(0.0, TransmutationLogic.calculatePurity(available, required), 0.001);
    }
}
