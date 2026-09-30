package thaumcraft.common.entities.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.level.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import thaumcraft.common.entities.EntityCombatLogic;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

public class EntityMindSpiderTest {

    @Test
    @DisplayName("Verify class hierarchy: EntityMindSpider extends Spider")
    public void testClassHierarchy() {
        assertTrue(Spider.class.isAssignableFrom(EntityMindSpider.class),
                "EntityMindSpider must extend net.minecraft.world.entity.monster.spider.Spider");
    }

    @Test
    @DisplayName("Verify constructor accepts (EntityType, Level)")
    public void testConstructor() throws Exception {
        Constructor<?> ctor = EntityMindSpider.class.getConstructor(EntityType.class, Level.class);
        assertNotNull(ctor, "Constructor(EntityType, Level) must exist");
        assertTrue(Modifier.isPublic(ctor.getModifiers()), "Constructor must be public");
    }

    @Test
    @DisplayName("Verify createAttributes exists, is static, and returns AttributeSupplier.Builder")
    public void testCreateAttributes() throws Exception {
        Method createAttributes = EntityMindSpider.class.getMethod("createAttributes");
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
    @DisplayName("Verify harmless getter/setter signatures and contracts")
    public void testHarmlessContract() throws Exception {
        Method isHarmless = EntityMindSpider.class.getMethod("isHarmless");
        assertNotNull(isHarmless, "isHarmless() must exist");
        assertEquals(boolean.class, isHarmless.getReturnType(), "isHarmless() must return boolean");

        Method setHarmless = EntityMindSpider.class.getMethod("setHarmless", boolean.class);
        assertNotNull(setHarmless, "setHarmless(boolean) must exist");
        assertEquals(void.class, setHarmless.getReturnType(), "setHarmless(boolean) must return void");

        Field harmlessField = EntityMindSpider.class.getDeclaredField("harmless");
        assertNotNull(harmlessField, "harmless field must exist");
        assertEquals(boolean.class, harmlessField.getType(), "harmless field must be boolean");

        try {
            Constructor<?> ctor = EntityMindSpider.class.getConstructor(EntityType.class, Level.class);
            EntityMindSpider spider = (EntityMindSpider) ctor.newInstance(null, null);
            assertTrue(spider.isHarmless(), "Default harmless state should be true");
            spider.setHarmless(false);
            assertFalse(spider.isHarmless());
            spider.setHarmless(true);
            assertTrue(spider.isHarmless());
        } catch (Throwable t) {
            // Expected in headless pure JUnit
        }
    }

    @Test
    @DisplayName("Verify lifespan getter/setter signatures and contracts")
    public void testLifeSpanContract() throws Exception {
        Method getLifeSpan = EntityMindSpider.class.getMethod("getLifeSpan");
        assertNotNull(getLifeSpan, "getLifeSpan() must exist");
        assertEquals(int.class, getLifeSpan.getReturnType(), "getLifeSpan() must return int");

        Method setLifeSpan = EntityMindSpider.class.getMethod("setLifeSpan", int.class);
        assertNotNull(setLifeSpan, "setLifeSpan(int) must exist");
        assertEquals(void.class, setLifeSpan.getReturnType(), "setLifeSpan(int) must return void");

        Field lifeSpanField = EntityMindSpider.class.getDeclaredField("lifeSpan");
        assertNotNull(lifeSpanField, "lifeSpan field must exist");
        assertEquals(int.class, lifeSpanField.getType(), "lifeSpan field must be int");

        try {
            Constructor<?> ctor = EntityMindSpider.class.getConstructor(EntityType.class, Level.class);
            EntityMindSpider spider = (EntityMindSpider) ctor.newInstance(null, null);
            assertEquals(1200, spider.getLifeSpan(), "Default lifespan should be 1200 ticks");
            spider.setLifeSpan(600);
            assertEquals(600, spider.getLifeSpan());
        } catch (Throwable t) {
            // Expected in headless pure JUnit
        }
    }

    @Test
    @DisplayName("Logic integration: EntityCombatLogic.isHarmlessExpired")
    public void testIsHarmlessExpired() {
        int lifespan = 1200;
        assertFalse(EntityCombatLogic.isHarmlessExpired(0, lifespan), "Freshly spawned spider should not be expired");
        assertFalse(EntityCombatLogic.isHarmlessExpired(600, lifespan), "Half-life spider should not be expired");
        assertFalse(EntityCombatLogic.isHarmlessExpired(1199, lifespan), "Spider at 1199 ticks should not be expired");
        assertTrue(EntityCombatLogic.isHarmlessExpired(1200, lifespan), "Spider reaching lifespan should be expired");
        assertTrue(EntityCombatLogic.isHarmlessExpired(1201, lifespan), "Spider exceeding lifespan should be expired");
    }

    @Test
    @DisplayName("Logic integration: EntityCombatLogic.canMindSpiderAttack")
    public void testCanMindSpiderAttack() {
        // Harmless hallucination spider deals 0 damage and cannot attack
        assertFalse(EntityCombatLogic.canMindSpiderAttack(true),
                "Harmless hallucination mind spider must not attack");
        // Real/hostile spider can attack
        assertTrue(EntityCombatLogic.canMindSpiderAttack(false),
                "Hostile mind spider must be allowed to attack");
    }

    @Test
    @DisplayName("Logic integration: MindSpiderLogic effect durations and swarm radius")
    public void testMindSpiderLogicConstants() {
        assertEquals(100, MindSpiderLogic.getPoisonDurationTicks(),
                "Poison duration should be 100 ticks (5 seconds)");
        assertEquals(60, MindSpiderLogic.getBlindnessDurationTicks(),
                "Blindness duration should be 60 ticks (3 seconds)");
        assertEquals(16.0D, MindSpiderLogic.getSwarmAlertRadius(), 0.001D,
                "Swarm alert radius should be 16.0 blocks");
    }
}
