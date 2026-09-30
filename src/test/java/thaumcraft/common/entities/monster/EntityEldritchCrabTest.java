package thaumcraft.common.entities.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import thaumcraft.common.entities.EldritchMobLogic;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

public class EntityEldritchCrabTest {

    @Test
    @DisplayName("Verify class hierarchy: EntityEldritchCrab extends Monster")
    public void testClassHierarchy() {
        assertTrue(Monster.class.isAssignableFrom(EntityEldritchCrab.class),
                "EntityEldritchCrab must extend net.minecraft.world.entity.monster.Monster");
    }

    @Test
    @DisplayName("Verify constructor accepts (EntityType, Level)")
    public void testConstructor() throws Exception {
        Constructor<?> ctor = EntityEldritchCrab.class.getConstructor(EntityType.class, Level.class);
        assertNotNull(ctor, "Constructor(EntityType, Level) must exist");
        assertTrue(Modifier.isPublic(ctor.getModifiers()), "Constructor must be public");
    }

    @Test
    @DisplayName("Verify createAttributes exists, is static, and returns AttributeSupplier.Builder")
    public void testCreateAttributes() throws Exception {
        Method createAttributes = EntityEldritchCrab.class.getMethod("createAttributes");
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
    @DisplayName("Logic integration: EldritchMobLogic.canPossess validation")
    public void testCanPossess() {
        // Valid case: humanoid, unpossessed, distance within range
        assertTrue(EldritchMobLogic.canPossess(true, false, 1.5, 2.0),
                "Should allow possession when target is humanoid, unpossessed, and within range");

        // Boundary case: exactly at possession range
        assertTrue(EldritchMobLogic.canPossess(true, false, 2.0, 2.0),
                "Should allow possession when distance equals possession range");

        // Invalid: target is not humanoid (e.g. animal)
        assertFalse(EldritchMobLogic.canPossess(false, false, 1.5, 2.0),
                "Should reject possession if target is not humanoid");

        // Invalid: target already possessed
        assertFalse(EldritchMobLogic.canPossess(true, true, 1.5, 2.0),
                "Should reject possession if target is already possessed");

        // Invalid: target out of reach
        assertFalse(EldritchMobLogic.canPossess(true, false, 2.1, 2.0),
                "Should reject possession if distance exceeds range");
        assertFalse(EldritchMobLogic.canPossess(true, false, 5.0, 2.0),
                "Should reject possession if target is far away");
    }

    @Test
    @DisplayName("Logic integration: EldritchMobLogic.calculateCrabLeapTrajectory")
    public void testCalculateCrabLeapTrajectory() {
        double dx = 3.0;
        double dy = 0.0;
        double dz = 4.0;
        double distance = 5.0; // sqrt(3^2 + 4^2) = 5
        double speed = 1.0;

        double[] trajectory = EldritchMobLogic.calculateCrabLeapTrajectory(dx, dy, dz, distance, speed);
        assertNotNull(trajectory, "Trajectory array must not be null");
        assertEquals(3, trajectory.length, "Trajectory array must contain 3 elements [vx, vy, vz]");

        // vx = (3 / 5) * 1.0 = 0.6
        assertEquals(0.6, trajectory[0], 0.001, "X motion vector should be normalized and scaled by speed");
        // vy = speed * 0.5 = 0.5 (upward arc)
        assertEquals(0.5, trajectory[1], 0.001, "Y motion vector should provide upward arc");
        // vz = (4 / 5) * 1.0 = 0.8
        assertEquals(0.8, trajectory[2], 0.001, "Z motion vector should be normalized and scaled by speed");

        // Zero distance fallback
        double[] zeroTrajectory = EldritchMobLogic.calculateCrabLeapTrajectory(0, 0, 0, 0, speed);
        assertNotNull(zeroTrajectory);
        assertArrayEquals(new double[]{0.0, 0.0, 0.0}, zeroTrajectory, 0.001,
                "Trajectory should be zeroed when distance is zero or negative");
    }
}
