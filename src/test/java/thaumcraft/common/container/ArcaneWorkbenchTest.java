package thaumcraft.common.container;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ArcaneWorkbenchTest {

    @Test
    public void testSlotConstants() {
        assertEquals(15, thaumcraft.common.tiles.ArcaneWorkbenchBlockEntity.SLOT_COUNT, "Total slots must equal 15 (9 craft + 6 crystals)");
        assertEquals(9, thaumcraft.common.tiles.ArcaneWorkbenchBlockEntity.CRAFT_SLOTS, "Crafting matrix must occupy 9 slots");
        assertEquals(6, thaumcraft.common.tiles.ArcaneWorkbenchBlockEntity.CRYSTAL_SLOTS, "Primal crystals must occupy 6 slots");
    }
}
