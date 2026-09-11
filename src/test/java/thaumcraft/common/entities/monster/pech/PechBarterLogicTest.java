package thaumcraft.common.entities.monster.pech;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

// We decouple core logic to avoid hitting FML classloader in unit tests.
// The new logic takes primitive types instead of ItemStacks to make unit tests simple and accurate to the production method.
public class PechBarterLogicTest {

    @Test
    public void testEmptyItemValue() {
        assertEquals(0, PechBarterLogic.getBarterValue(false, false, false, false, 0));
    }

    @Test
    public void testGoldNuggetValue() {
        assertEquals(1, PechBarterLogic.getBarterValue(true, false, false, false, 0));
    }

    @Test
    public void testGoldIngotValue() {
        assertEquals(3, PechBarterLogic.getBarterValue(false, true, false, false, 0));
    }

    @Test
    public void testGoldBlockValue() {
        assertEquals(27, PechBarterLogic.getBarterValue(false, false, true, false, 0));
    }

    @Test
    public void testGemValue() {
        assertEquals(5, PechBarterLogic.getBarterValue(false, false, false, true, 0));
    }

    @Test
    public void testAspectValue() {
        // Gold ingot (3) + 10 vis (5) = 8
        assertEquals(8, PechBarterLogic.getBarterValue(false, true, false, false, 10));
    }
}
