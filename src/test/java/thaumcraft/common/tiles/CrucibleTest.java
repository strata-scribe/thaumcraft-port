package thaumcraft.common.tiles;

import org.junit.jupiter.api.Test;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.tiles.crafting.CrucibleBlockEntity;

import static org.junit.jupiter.api.Assertions.*;

public class CrucibleTest {

    @Test
    public void testCrucibleConstants() {
        assertEquals(1000, CrucibleBlockEntity.TANK_CAPACITY, "Crucible tank capacity must be 1000 mB");
        assertEquals(500, CrucibleBlockEntity.MAX_ASPECTS, "Crucible maximum aspect storage must be 500");
    }

    @Test
    public void testAspectListOperations() {
        AspectList list = new AspectList();
        assertEquals(0, list.visSize());
        
        list.add(Aspect.WATER, 10);
        list.add(Aspect.FIRE, 5);
        assertEquals(15, list.visSize());
        assertEquals(10, list.getAmount(Aspect.WATER));
        assertEquals(5, list.getAmount(Aspect.FIRE));
        
        assertTrue(list.reduce(Aspect.WATER, 4));
        assertEquals(6, list.getAmount(Aspect.WATER));
        assertEquals(11, list.visSize());
        
        list.remove(Aspect.FIRE);
        assertEquals(0, list.getAmount(Aspect.FIRE));
        assertEquals(6, list.visSize());
    }
}

