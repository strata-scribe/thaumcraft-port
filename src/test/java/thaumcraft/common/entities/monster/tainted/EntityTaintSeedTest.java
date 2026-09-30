package thaumcraft.common.entities.monster.tainted;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

public class EntityTaintSeedTest {

    @Test
    @DisplayName("Verify class hierarchy: EntityTaintSeed extends Entity")
    public void testClassHierarchy() {
        assertTrue(Entity.class.isAssignableFrom(EntityTaintSeed.class),
                "EntityTaintSeed must extend net.minecraft.world.entity.Entity");
    }

    @Test
    @DisplayName("Verify constructor accepts (EntityType, Level)")
    public void testConstructor() throws Exception {
        Constructor<?> ctor = EntityTaintSeed.class.getConstructor(EntityType.class, Level.class);
        assertNotNull(ctor, "Constructor(EntityType, Level) must exist");
        assertTrue(Modifier.isPublic(ctor.getModifiers()), "Constructor must be public");
    }

    @Test
    @DisplayName("Logic integration: TaintSeedGrowthFootprintLogic maturity and footprint calculations")
    public void testGrowthFootprintLogic() {
        // Maturity stages 0..4
        assertEquals(0, TaintSeedGrowthFootprintLogic.calculateMaturityStage(0, 1000));
        assertEquals(1, TaintSeedGrowthFootprintLogic.calculateMaturityStage(250, 1000));
        assertEquals(2, TaintSeedGrowthFootprintLogic.calculateMaturityStage(500, 1000));
        assertEquals(3, TaintSeedGrowthFootprintLogic.calculateMaturityStage(750, 1000));
        assertEquals(4, TaintSeedGrowthFootprintLogic.calculateMaturityStage(1000, 1000));
        assertEquals(4, TaintSeedGrowthFootprintLogic.calculateMaturityStage(1200, 1000));

        // Footprint radius based on stage (0..4) and maxRadius
        double maxRadius = 12.0;
        assertEquals(0.0, TaintSeedGrowthFootprintLogic.calculateFootprintRadius(0, maxRadius), 0.001);
        assertEquals(3.0, TaintSeedGrowthFootprintLogic.calculateFootprintRadius(1, maxRadius), 0.001);
        assertEquals(6.0, TaintSeedGrowthFootprintLogic.calculateFootprintRadius(2, maxRadius), 0.001);
        assertEquals(9.0, TaintSeedGrowthFootprintLogic.calculateFootprintRadius(3, maxRadius), 0.001);
        assertEquals(12.0, TaintSeedGrowthFootprintLogic.calculateFootprintRadius(4, maxRadius), 0.001);

        // Circular footprint bounds checks
        assertTrue(TaintSeedGrowthFootprintLogic.isBlockInFootprint(0, 0, 0, 0, 5.0),
                "Center must be inside footprint");
        assertTrue(TaintSeedGrowthFootprintLogic.isBlockInFootprint(0, 0, 3, 4, 5.0),
                "Point at exact boundary (3, 4 with radius 5) must be inside footprint");
        assertFalse(TaintSeedGrowthFootprintLogic.isBlockInFootprint(0, 0, 3.1, 4.0, 5.0),
                "Point outside boundary must not be inside footprint");
    }
}
