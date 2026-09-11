package thaumcraft.common.entities.monster.tainted;

import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TaintMobLogicTest {

    @Test
    public void testIsWithinWhipReach() {
        Vec3 attackerPos = new Vec3(0, 0, 0);
        Vec3 targetPosInside = new Vec3(3, 0, 4); // Distance 5, within reach 6
        Vec3 targetPosOutside = new Vec3(6, 0, 8); // Distance 10, outside reach 6

        assertTrue(TaintMobLogic.isWithinWhipReach(attackerPos, targetPosInside, 6.0));
        assertFalse(TaintMobLogic.isWithinWhipReach(attackerPos, targetPosOutside, 6.0));
    }

    @Test
    public void testCalculateKnockbackImpulse() {
        Vec3 attackerPos = new Vec3(0, 0, 0);
        Vec3 targetPos = new Vec3(10, 0, 0); // Directly in +x

        Vec3 knockback = TaintMobLogic.calculateKnockbackImpulse(attackerPos, targetPos, 2.0);

        // Expected: direction is +x, so (2.0, 1.0, 0.0) based on calculation:
        // diff = (1, 0, 0)
        // x * strength = 1 * 2 = 2
        // y = strength * 0.5 = 1
        // z = 0 * 2 = 0
        assertEquals(2.0, knockback.x, 0.001);
        assertEquals(1.0, knockback.y, 0.001);
        assertEquals(0.0, knockback.z, 0.001);
    }

    @Test
    public void testCalculateTentacleDamage() {
        float baseDamage = 7.0f;

        // Distance 1.0 (<= 2.0, so no bonus)
        float damageClose = TaintMobLogic.calculateTentacleDamage(baseDamage, 1.0);
        assertEquals(7.0f, damageClose, 0.001f);

        // Distance 4.0 (bonus = (4.0 - 2.0) * 0.5 = 1.0)
        float damageFar = TaintMobLogic.calculateTentacleDamage(baseDamage, 4.0);
        assertEquals(8.0f, damageFar, 0.001f);
    }
}
