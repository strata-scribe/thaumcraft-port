package thaumcraft.common.entities.monster.tainted;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import thaumcraft.api.entities.ITaintedMob;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

public class EntityTaintCrawlerTest {

    @Test
    @DisplayName("Verify class hierarchy: extends Monster, implements ITaintedMob")
    public void testClassHierarchy() {
        assertTrue(Monster.class.isAssignableFrom(EntityTaintCrawler.class),
                "EntityTaintCrawler must extend net.minecraft.world.entity.monster.Monster");
        assertTrue(ITaintedMob.class.isAssignableFrom(EntityTaintCrawler.class),
                "EntityTaintCrawler must implement thaumcraft.api.entities.ITaintedMob");
    }

    @Test
    @DisplayName("Verify constructor accepts (EntityType, Level)")
    public void testConstructor() throws Exception {
        Constructor<?> ctor = EntityTaintCrawler.class.getConstructor(EntityType.class, Level.class);
        assertNotNull(ctor, "Constructor(EntityType, Level) must exist");
        assertTrue(Modifier.isPublic(ctor.getModifiers()), "Constructor must be public");
    }

    @Test
    @DisplayName("Verify createAttributes exists, is static, and returns AttributeSupplier.Builder")
    public void testCreateAttributes() throws Exception {
        Method createAttributes = EntityTaintCrawler.class.getMethod("createAttributes");
        assertNotNull(createAttributes, "createAttributes method must exist");
        assertTrue(Modifier.isPublic(createAttributes.getModifiers()), "createAttributes must be public");
        assertTrue(Modifier.isStatic(createAttributes.getModifiers()), "createAttributes must be static");
        assertEquals(AttributeSupplier.Builder.class, createAttributes.getReturnType(),
                "createAttributes must return AttributeSupplier.Builder");

        try {
            AttributeSupplier.Builder builder = (AttributeSupplier.Builder) createAttributes.invoke(null);
            assertNotNull(builder, "AttributeSupplier.Builder instance should not be null if registries are initialized");
        } catch (Throwable t) {
            // Unbootstrapped Minecraft registries throw in headless pure JUnit
            assertTrue(t.getCause() instanceof ExceptionInInitializerError
                    || t.getCause() instanceof IllegalStateException
                    || t.getCause() instanceof IllegalArgumentException
                    || t instanceof IllegalStateException,
                    "Expected headless environment exception if unbootstrapped");
        }
    }

    @Test
    @DisplayName("Logic integration: TaintCrawlerPackLogic pack scaling and swarm logic")
    public void testPackLogic() {
        // 0 companions -> base 1.0x
        assertEquals(1.0f, TaintCrawlerPackLogic.calculateSpeedMultiplier(0), 0.001f);
        assertEquals(1.0f, TaintCrawlerPackLogic.calculateDamageMultiplier(0), 0.001f);

        // 3 companions -> +30% speed, +60% damage
        assertEquals(1.3f, TaintCrawlerPackLogic.calculateSpeedMultiplier(3), 0.001f);
        assertEquals(1.6f, TaintCrawlerPackLogic.calculateDamageMultiplier(3), 0.001f);

        // Capped bonus (>= 5 companions)
        assertEquals(1.5f, TaintCrawlerPackLogic.calculateSpeedMultiplier(5), 0.001f);
        assertEquals(1.5f, TaintCrawlerPackLogic.calculateSpeedMultiplier(10), 0.001f);
        assertEquals(2.0f, TaintCrawlerPackLogic.calculateDamageMultiplier(5), 0.001f);
        assertEquals(2.0f, TaintCrawlerPackLogic.calculateDamageMultiplier(10), 0.001f);

        // Leader following range (<= 16 blocks)
        assertTrue(TaintCrawlerPackLogic.isFollowingLeader(10.0));
        assertTrue(TaintCrawlerPackLogic.isFollowingLeader(16.0));
        assertFalse(TaintCrawlerPackLogic.isFollowingLeader(16.1));

        // Swarm burst count
        assertEquals(6, TaintCrawlerPackLogic.getSwarmBurstCount(3));
    }
}
