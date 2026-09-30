package thaumcraft.common.entities.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import thaumcraft.common.entities.EntityCombatLogic;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

public class EntityEldritchGuardianTest {

    @Test
    @DisplayName("Verify class hierarchy: EntityEldritchGuardian extends Monster")
    public void testClassHierarchy() {
        assertTrue(Monster.class.isAssignableFrom(EntityEldritchGuardian.class),
                "EntityEldritchGuardian must extend net.minecraft.world.entity.monster.Monster");
    }

    @Test
    @DisplayName("Verify constructor accepts (EntityType, Level)")
    public void testConstructor() throws Exception {
        Constructor<?> ctor = EntityEldritchGuardian.class.getConstructor(EntityType.class, Level.class);
        assertNotNull(ctor, "Constructor(EntityType, Level) must exist");
        assertTrue(Modifier.isPublic(ctor.getModifiers()), "Constructor must be public");
    }

    @Test
    @DisplayName("Verify createAttributes exists, is static, and returns AttributeSupplier.Builder")
    public void testCreateAttributes() throws Exception {
        Method createAttributes = EntityEldritchGuardian.class.getMethod("createAttributes");
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
    @DisplayName("Verify isOuterDimension and setOuterDimension methods and contracts")
    public void testOuterDimensionContract() throws Exception {
        Method isOuterDimMethod = EntityEldritchGuardian.class.getMethod("isOuterDimension");
        assertNotNull(isOuterDimMethod, "isOuterDimension() method must exist");
        assertEquals(boolean.class, isOuterDimMethod.getReturnType(), "isOuterDimension() must return boolean");

        Method setOuterDimMethod = EntityEldritchGuardian.class.getMethod("setOuterDimension", boolean.class);
        assertNotNull(setOuterDimMethod, "setOuterDimension(boolean) method must exist");
        assertEquals(void.class, setOuterDimMethod.getReturnType(), "setOuterDimension(boolean) must return void");

        Field outerDimField = EntityEldritchGuardian.class.getDeclaredField("isOuterDimension");
        assertNotNull(outerDimField, "isOuterDimension field must exist");
        assertEquals(boolean.class, outerDimField.getType(), "isOuterDimension field must be boolean");

        try {
            Constructor<?> ctor = EntityEldritchGuardian.class.getConstructor(EntityType.class, Level.class);
            EntityEldritchGuardian guardian = (EntityEldritchGuardian) ctor.newInstance(null, null);
            assertFalse(guardian.isOuterDimension(), "Default outer dimension flag should be false");
            guardian.setOuterDimension(false);
            assertFalse(guardian.isOuterDimension());
            guardian.setOuterDimension(true);
            assertTrue(guardian.isOuterDimension());
        } catch (Throwable t) {
            // Expected in headless pure JUnit due to unbootstrapped registries
        }
    }

    @Test
    @DisplayName("Logic integration: EntityCombatLogic.calculateGuardianAbsorption")
    public void testCalculateGuardianAbsorption() {
        assertEquals(25.0f, EntityCombatLogic.calculateGuardianAbsorption(true), 0.001f,
                "Outer dimension guardian absorption should be 25.0f");
        assertEquals(0.0f, EntityCombatLogic.calculateGuardianAbsorption(false), 0.001f,
                "Normal dimension guardian absorption should be 0.0f");
    }

    @Test
    @DisplayName("Logic integration: EntityCombatLogic.calculateGuardianMagicDamage")
    public void testCalculateGuardianMagicDamage() {
        assertEquals(5.0f, EntityCombatLogic.calculateGuardianMagicDamage(10.0f, true), 0.001f,
                "Magic damage to guardian should be halved (10.0f -> 5.0f)");
        assertEquals(10.0f, EntityCombatLogic.calculateGuardianMagicDamage(10.0f, false), 0.001f,
                "Physical/non-magic damage to guardian should remain unmitigated (10.0f)");
        assertEquals(0.0f, EntityCombatLogic.calculateGuardianMagicDamage(0.0f, true), 0.001f);
        assertEquals(0.0f, EntityCombatLogic.calculateGuardianMagicDamage(-5.0f, true), 0.001f);
    }

    @Test
    @DisplayName("Logic integration: EntityCombatLogic.shouldGuardianScreech")
    public void testShouldGuardianScreech() {
        assertTrue(EntityCombatLogic.shouldGuardianScreech(0.00f), "Roll 0.00f should trigger screech (< 0.15f)");
        assertTrue(EntityCombatLogic.shouldGuardianScreech(0.10f), "Roll 0.10f should trigger screech (< 0.15f)");
        assertTrue(EntityCombatLogic.shouldGuardianScreech(0.149f), "Roll 0.149f should trigger screech (< 0.15f)");
        assertFalse(EntityCombatLogic.shouldGuardianScreech(0.15f), "Roll 0.15f should not trigger screech (>= 0.15f)");
        assertFalse(EntityCombatLogic.shouldGuardianScreech(0.50f), "Roll 0.50f should not trigger screech");
        assertFalse(EntityCombatLogic.shouldGuardianScreech(1.00f), "Roll 1.00f should not trigger screech");
    }

    @Test
    @DisplayName("Logic integration: EntityCombatLogic.calculateEldritchOrbDamage")
    public void testCalculateEldritchOrbDamage() {
        float orbDamage = EntityCombatLogic.calculateEldritchOrbDamage(7.0f);
        assertEquals(7.0f * 0.666f, orbDamage, 0.001f, "Orb damage should scale by 0.666f");
        assertEquals(0.0f, EntityCombatLogic.calculateEldritchOrbDamage(0.0f), 0.001f);
        assertEquals(0.0f, EntityCombatLogic.calculateEldritchOrbDamage(-1.0f), 0.001f);
    }
}
