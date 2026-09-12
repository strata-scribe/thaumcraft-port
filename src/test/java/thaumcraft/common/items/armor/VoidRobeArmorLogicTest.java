package thaumcraft.common.items.armor;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class VoidRobeArmorLogicTest {

    @Test
    public void testCalculatePassiveRepairTicks() {
        assertEquals(20, VoidRobeArmorLogic.calculatePassiveRepairTicks(0));
        assertEquals(20, VoidRobeArmorLogic.calculatePassiveRepairTicks(-10));
        assertEquals(19, VoidRobeArmorLogic.calculatePassiveRepairTicks(5));
        assertEquals(18, VoidRobeArmorLogic.calculatePassiveRepairTicks(10));
        assertEquals(17, VoidRobeArmorLogic.calculatePassiveRepairTicks(15));
        assertEquals(5, VoidRobeArmorLogic.calculatePassiveRepairTicks(100)); // (20 - 20) -> 5 minimum
        assertEquals(5, VoidRobeArmorLogic.calculatePassiveRepairTicks(200));
    }

    @Test
    public void testCalculateDamageResistance() {
        // Not a warp attack
        assertEquals(10.0f, VoidRobeArmorLogic.calculateDamageResistance(10.0f, false, 3), 0.0001f);

        // Zero pieces
        assertEquals(10.0f, VoidRobeArmorLogic.calculateDamageResistance(10.0f, true, 0), 0.0001f);

        // Negative damage
        assertEquals(0.0f, VoidRobeArmorLogic.calculateDamageResistance(-5.0f, true, 2), 0.0001f);

        // Warp attack with pieces
        assertEquals(8.0f, VoidRobeArmorLogic.calculateDamageResistance(10.0f, true, 1), 0.0001f);
        assertEquals(6.0f, VoidRobeArmorLogic.calculateDamageResistance(10.0f, true, 2), 0.0001f);
        assertEquals(4.0f, VoidRobeArmorLogic.calculateDamageResistance(10.0f, true, 3), 0.0001f);
        assertEquals(2.0f, VoidRobeArmorLogic.calculateDamageResistance(10.0f, true, 4), 0.0001f);
        assertEquals(0.0f, VoidRobeArmorLogic.calculateDamageResistance(10.0f, true, 5), 0.0001f);
        assertEquals(0.0f, VoidRobeArmorLogic.calculateDamageResistance(10.0f, true, 10), 0.0001f);
    }
}
