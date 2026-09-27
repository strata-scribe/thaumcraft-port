package thaumcraft.common.golems.seals;

import net.minecraft.resources.Identifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thaumcraft.api.golems.EnumGolemTrait;

import static org.junit.jupiter.api.Assertions.*;

public class SealStockTest {

    private SealStock sealStock;

    @BeforeEach
    public void setUp() {
        sealStock = new SealStock();
    }

    @Test
    public void testGetKey() {
        assertEquals("thaumcraft:stock", sealStock.getKey());
    }

    @Test
    public void testGetSealIcon() {
        Identifier expectedIcon = Identifier.fromNamespaceAndPath("thaumcraft", "items/seals/seal_stock");
        assertEquals(expectedIcon, sealStock.getSealIcon());
    }

    @Test
    public void testGetRequiredTags() {
        assertNull(sealStock.getRequiredTags());
    }

    @Test
    public void testGetGuiCategories() {
        int[] categories = sealStock.getGuiCategories();
        assertNotNull(categories);
        assertEquals(3, categories.length);
        assertEquals(1, categories[0]);
        assertEquals(0, categories[1]);
        assertEquals(4, categories[2]);
    }

    @Test
    public void testCanGolemPerformTask() {
        assertFalse(sealStock.canGolemPerformTask(null, null));
    }
}
