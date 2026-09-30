package thaumcraft.common.entities.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.level.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import thaumcraft.common.entities.EldritchMobLogic;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

public class EntityInhabitedZombieTest {

    @Test
    @DisplayName("Verify class hierarchy: EntityInhabitedZombie extends Zombie")
    public void testClassHierarchy() {
        assertTrue(Zombie.class.isAssignableFrom(EntityInhabitedZombie.class),
                "EntityInhabitedZombie must extend net.minecraft.world.entity.monster.zombie.Zombie");
    }

    @Test
    @DisplayName("Verify constructor accepts (EntityType, Level)")
    public void testConstructor() throws Exception {
        Constructor<?> ctor = EntityInhabitedZombie.class.getConstructor(EntityType.class, Level.class);
        assertNotNull(ctor, "Constructor(EntityType, Level) must exist");
        assertTrue(Modifier.isPublic(ctor.getModifiers()), "Constructor must be public");
    }

    @Test
    @DisplayName("Verify createAttributes exists, is static, and returns AttributeSupplier.Builder")
    public void testCreateAttributes() throws Exception {
        Method createAttributes = EntityInhabitedZombie.class.getMethod("createAttributes");
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
    @DisplayName("Logic integration: EldritchMobLogic.calculateAugmentedSpeed")
    public void testCalculateAugmentedSpeed() {
        double baseSpeed = 0.23D;
        double augmentedSpeed = EldritchMobLogic.calculateAugmentedSpeed(baseSpeed);

        assertTrue(augmentedSpeed > baseSpeed,
                "Inhabited zombie augmented speed must be strictly greater than base zombie speed (0.23D)");
        assertEquals(baseSpeed * 1.5D, augmentedSpeed, 0.0001D,
                "Augmented speed should apply a 1.5x multiplier to base speed");
        assertEquals(0.0D, EldritchMobLogic.calculateAugmentedSpeed(0.0D), 0.0001D);
    }

    @Test
    @DisplayName("Logic integration: EldritchMobLogic.calculateAugmentedArmor")
    public void testCalculateAugmentedArmor() {
        double baseArmor = 2.0D;
        double augmentedArmor = EldritchMobLogic.calculateAugmentedArmor(baseArmor);

        assertTrue(augmentedArmor > baseArmor,
                "Inhabited zombie augmented armor must be strictly greater than base zombie armor (2.0D)");
        assertEquals(baseArmor + 6.0D, augmentedArmor, 0.0001D,
                "Augmented armor should add +6.0 to base armor");
        assertEquals(6.0D, EldritchMobLogic.calculateAugmentedArmor(0.0D), 0.0001D);
    }
}
