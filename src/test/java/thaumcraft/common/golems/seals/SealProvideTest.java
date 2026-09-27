package thaumcraft.common.golems.seals;

import org.junit.jupiter.api.Test;
import thaumcraft.api.golems.EnumGolemTrait;

import static org.junit.jupiter.api.Assertions.*;

public class SealProvideTest {

    @Test
    public void testGetKey() {
        SealProvide seal = new SealProvide();
        assertEquals("thaumcraft:provide", seal.getKey());
    }

    @Test
    public void testHasStacksizeLimiters() {
        SealProvide seal = new SealProvide();
        assertFalse(seal.hasStacksizeLimiters(), "SealProvide should inherit hasStacksizeLimiters false from SealFiltered");
    }

    @Test
    public void testTags() {
        SealProvide seal = new SealProvide();
        assertNull(seal.getRequiredTags());
        assertNull(seal.getForbiddenTags());
    }
}
