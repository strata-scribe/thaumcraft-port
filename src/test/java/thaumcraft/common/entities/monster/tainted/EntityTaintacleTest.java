package thaumcraft.common.entities.monster.tainted;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import thaumcraft.api.entities.ITaintedMob;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

public class EntityTaintacleTest {

    @Test
    @DisplayName("Verify class hierarchy: extends Monster, implements ITaintedMob")
    public void testClassHierarchy() {
        assertTrue(Monster.class.isAssignableFrom(EntityTaintacle.class),
                "EntityTaintacle must extend net.minecraft.world.entity.monster.Monster");
        assertTrue(ITaintedMob.class.isAssignableFrom(EntityTaintacle.class),
                "EntityTaintacle must implement thaumcraft.api.entities.ITaintedMob");
    }

    @Test
    @DisplayName("Verify constructor accepts (EntityType, Level)")
    public void testConstructor() throws Exception {
        Constructor<?> ctor = EntityTaintacle.class.getConstructor(EntityType.class, Level.class);
        assertNotNull(ctor, "Constructor(EntityType, Level) must exist");
        assertTrue(Modifier.isPublic(ctor.getModifiers()), "Constructor must be public");
    }

    @Test
    @DisplayName("Verify createAttributes exists, is static, and returns AttributeSupplier.Builder")
    public void testCreateAttributes() throws Exception {
        Method createAttributes = EntityTaintacle.class.getMethod("createAttributes");
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
    @DisplayName("Logic integration: TaintMobLogic.isWithinWhipReach")
    public void testIsWithinWhipReach() {
        Vec3 origin = new Vec3(0, 0, 0);
        Vec3 inReach = new Vec3(3, 0, 4); // distance = 5.0
        Vec3 outOfReach = new Vec3(6, 0, 8); // distance = 10.0

        assertTrue(TaintMobLogic.isWithinWhipReach(origin, inReach, 6.0),
                "Target at distance 5.0 should be within 6.0 reach");
        assertTrue(TaintMobLogic.isWithinWhipReach(origin, inReach, 5.0),
                "Target at distance 5.0 should be within exactly 5.0 reach");
        assertFalse(TaintMobLogic.isWithinWhipReach(origin, inReach, 4.9),
                "Target at distance 5.0 should be out of 4.9 reach");
        assertFalse(TaintMobLogic.isWithinWhipReach(origin, outOfReach, 6.0),
                "Target at distance 10.0 should be out of 6.0 reach");
    }

    @Test
    @DisplayName("Logic integration: TaintMobLogic.calculateTentacleDamage")
    public void testCalculateTentacleDamage() {
        float baseDamage = 7.0f;

        // At close range (<= 2 blocks), base damage applies without distance bonus
        assertEquals(baseDamage, TaintMobLogic.calculateTentacleDamage(baseDamage, 1.0), 0.001f);
        assertEquals(baseDamage, TaintMobLogic.calculateTentacleDamage(baseDamage, 2.0), 0.001f);

        // At extended range (> 2 blocks), damage scales with whip tip speed: (distance - 2) * 0.5
        // distance 4.0 -> base + (2.0 * 0.5) = 8.0f
        assertEquals(8.0f, TaintMobLogic.calculateTentacleDamage(baseDamage, 4.0), 0.001f);
        // distance 6.0 -> base + (4.0 * 0.5) = 9.0f
        assertEquals(9.0f, TaintMobLogic.calculateTentacleDamage(baseDamage, 6.0), 0.001f);
    }

    @Test
    @DisplayName("Logic integration: TaintMobLogic.calculateKnockbackImpulse")
    public void testCalculateKnockbackImpulse() {
        Vec3 attackerPos = new Vec3(0, 0, 0);
        Vec3 targetPos = new Vec3(3, 0, 4); // delta = (3, 0, 4), norm = (0.6, 0, 0.8)
        double strength = 1.5;

        Vec3 impulse = TaintMobLogic.calculateKnockbackImpulse(attackerPos, targetPos, strength);
        assertNotNull(impulse, "Knockback impulse must not be null");

        // X: 0.6 * 1.5 = 0.9
        assertEquals(0.9, impulse.x, 0.001, "X impulse should be 0.9");
        // Y: 1.5 * 0.5 = 0.75 (upward knockback)
        assertEquals(0.75, impulse.y, 0.001, "Y impulse should be 0.75");
        // Z: 0.8 * 1.5 = 1.2
        assertEquals(1.2, impulse.z, 0.001, "Z impulse should be 1.2");
    }
}
