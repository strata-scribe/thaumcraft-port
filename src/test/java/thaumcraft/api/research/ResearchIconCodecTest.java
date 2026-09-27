package thaumcraft.api.research;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ResearchIconCodecTest {

    @Test
    public void testParseTexture() {
        ResearchIcon icon = ResearchIcon.parse("thaumcraft:textures/items/alumentum.png");
        assertTrue(icon.isTexture());
        assertEquals("thaumcraft:textures/items/alumentum.png", icon.rawText());
        assertNotNull(icon.textureLocation());
        assertEquals("thaumcraft", icon.textureLocation().getNamespace());
        assertEquals("textures/items/alumentum.png", icon.textureLocation().getPath());
    }

    @Test
    public void testParseItem() {
        ResearchIcon icon = ResearchIcon.parse("minecraft:stick");
        assertFalse(icon.isTexture());
        assertEquals("minecraft:stick", icon.rawText());
        assertNull(icon.textureLocation());
    }

    @Test
    public void testItemRequirementBasic() {
        ResearchItemRequirement req = ResearchItemRequirement.parse("minecraft:apple");
        assertEquals("minecraft:apple", req.itemOrTag());
        assertEquals(1, req.count());
        assertFalse(req.isTag());
    }

    @Test
    public void testItemRequirementCount() {
        ResearchItemRequirement req = ResearchItemRequirement.parse("minecraft:stick;4");
        assertEquals("minecraft:stick", req.itemOrTag());
        assertEquals(4, req.count());
        assertFalse(req.isTag());
    }

    @Test
    public void testItemRequirementTag() {
        ResearchItemRequirement req = ResearchItemRequirement.parse("oredict:plankWood");
        assertEquals("plankWood", req.itemOrTag());
        assertEquals(1, req.count());
        assertTrue(req.isTag());
    }

    @Test
    public void testItemRequirementTagCount() {
        ResearchItemRequirement req = ResearchItemRequirement.parse("oredict:plankWood;10");
        assertEquals("plankWood", req.itemOrTag());
        assertEquals(10, req.count());
        assertTrue(req.isTag());
    }
}
