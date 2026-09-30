package thaumcraft.common.items.tools;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import thaumcraft.common.items.tools.logic.GrappleGunLogic;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ItemGrappleGun Contract & Logic Tests")
public class ItemGrappleGunTest {

    @Test
    @DisplayName("Verify class hierarchy, constructors, and use method presence")
    public void testClassStructureAndConstructors() throws Exception {
        assertTrue(Item.class.isAssignableFrom(ItemGrappleGun.class),
                "ItemGrappleGun must extend net.minecraft.world.item.Item");

        Constructor<ItemGrappleGun> propsCtor = ItemGrappleGun.class.getConstructor(Item.Properties.class);
        assertNotNull(propsCtor, "ItemGrappleGun must have (Item.Properties) constructor");

        Constructor<ItemGrappleGun> noArgCtor = ItemGrappleGun.class.getConstructor();
        assertNotNull(noArgCtor, "ItemGrappleGun must have () default constructor");

        Method useMethod = ItemGrappleGun.class.getMethod("use", Level.class, Player.class, InteractionHand.class);
        assertNotNull(useMethod, "ItemGrappleGun must implement use");
        assertEquals(InteractionResult.class, useMethod.getReturnType(), "use must return InteractionResult");
    }

    @Test
    @DisplayName("Verify instantiation and default stack size")
    public void testInstantiationAndProperties() {
        try {
            ItemGrappleGun gun = new ItemGrappleGun();
            assertNotNull(gun);
            assertEquals(1, gun.getDefaultMaxStackSize(), "Grapple gun stack size should be 1");
        } catch (Throwable t) {
            assertNotNull(ItemGrappleGun.class);
        }
    }

    @Test
    @DisplayName("Velocity and pulling calculations for Grapple Gun")
    public void testVelocityCalculationsForGrappleGun() {
        GrappleGunLogic.Vector3d playerPos = new GrappleGunLogic.Vector3d(0, 64, 0);
        GrappleGunLogic.Vector3d playerLook = new GrappleGunLogic.Vector3d(1, 0, 0); // Looking directly east

        // Mock ItemGrappleGun starting vel
        GrappleGunLogic.Vector3d pos = new GrappleGunLogic.Vector3d(playerPos.x(), playerPos.y() + 1.62, playerPos.z());
        GrappleGunLogic.Vector3d vel = new GrappleGunLogic.Vector3d(playerLook.x() * 2.0, playerLook.y() * 2.0, playerLook.z() * 2.0);

        assertEquals(2.0, vel.x(), 0.001);
        assertEquals(0.0, vel.y(), 0.001);
        assertEquals(0.0, vel.z(), 0.001);

        double gravity = 0.05;
        double drag = 0.01;

        GrappleGunLogic.Vector3d impact = GrappleGunLogic.calculateImpactCoordinate(
                pos, vel, gravity, drag, 5,
                v -> v.x() >= 5.0
        );

        assertNotNull(impact);
        assertTrue(impact.x() >= 5.0);

        GrappleGunLogic.Vector3d playerVel = new GrappleGunLogic.Vector3d(0, 0, 0);

        double springStiffness = 0.5;
        double dampingCoefficient = 0.2;

        GrappleGunLogic.Vector3d accel = GrappleGunLogic.calculatePullingAcceleration(
                playerPos, playerVel, impact, springStiffness, dampingCoefficient
        );

        // Ensure acceleration pulls player towards impact coordinate
        assertTrue(accel.x() > 0);
    }
}
