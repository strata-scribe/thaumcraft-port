package thaumcraft.common.tiles.devices.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class VisBatteryStorageLogicTest {

    @Test
    public void testInitialization() {
        VisBatteryStorageLogic battery = new VisBatteryStorageLogic(100.0f, 5.0f, 10.0f);

        assertEquals(100.0f, battery.getMaxCapacity());
        assertEquals(5.0f, battery.getSiphonRate());
        assertEquals(10.0f, battery.getDischargeRate());
        assertEquals(0.0f, battery.getStoredVis());
    }

    @Test
    public void testNegativeInitialization() {
        VisBatteryStorageLogic battery = new VisBatteryStorageLogic(-100.0f, -5.0f, -10.0f);

        assertEquals(0.0f, battery.getMaxCapacity());
        assertEquals(0.0f, battery.getSiphonRate());
        assertEquals(0.0f, battery.getDischargeRate());
        assertEquals(0.0f, battery.getStoredVis());
    }

    @Test
    public void testSetStoredVis() {
        VisBatteryStorageLogic battery = new VisBatteryStorageLogic(100.0f, 5.0f, 10.0f);

        battery.setStoredVis(50.0f);
        assertEquals(50.0f, battery.getStoredVis());

        battery.setStoredVis(150.0f);
        assertEquals(100.0f, battery.getStoredVis(), "Should not exceed max capacity");

        battery.setStoredVis(-10.0f);
        assertEquals(0.0f, battery.getStoredVis(), "Should not be negative");
    }

    @Test
    public void testSiphonFromAura() {
        VisBatteryStorageLogic battery = new VisBatteryStorageLogic(100.0f, 10.0f, 20.0f);

        // Standard siphon limited by rate
        float drawn = battery.siphonFromAura(50.0f);
        assertEquals(10.0f, drawn, "Should be limited by siphon rate");
        assertEquals(10.0f, battery.getStoredVis());

        // Siphon limited by available aura
        drawn = battery.siphonFromAura(5.0f);
        assertEquals(5.0f, drawn, "Should be limited by available aura vis");
        assertEquals(15.0f, battery.getStoredVis());

        // Siphon limited by capacity
        battery.setStoredVis(95.0f);
        drawn = battery.siphonFromAura(50.0f);
        assertEquals(5.0f, drawn, "Should be limited by available capacity");
        assertEquals(100.0f, battery.getStoredVis());

        // Siphon when full
        drawn = battery.siphonFromAura(50.0f);
        assertEquals(0.0f, drawn, "Should not siphon if full");
        assertEquals(100.0f, battery.getStoredVis());
    }

    @Test
    public void testSiphonFromAuraEdgeCases() {
        VisBatteryStorageLogic battery = new VisBatteryStorageLogic(100.0f, 10.0f, 20.0f);

        float drawn = battery.siphonFromAura(0.0f);
        assertEquals(0.0f, drawn);

        drawn = battery.siphonFromAura(-5.0f);
        assertEquals(0.0f, drawn);
    }

    @Test
    public void testDischargeToMachine() {
        VisBatteryStorageLogic battery = new VisBatteryStorageLogic(100.0f, 10.0f, 20.0f);
        battery.setStoredVis(50.0f);

        // Standard discharge limited by rate
        float discharged = battery.dischargeToMachine(50.0f);
        assertEquals(20.0f, discharged, "Should be limited by discharge rate");
        assertEquals(30.0f, battery.getStoredVis());

        // Discharge limited by requested vis
        discharged = battery.dischargeToMachine(15.0f);
        assertEquals(15.0f, discharged, "Should be limited by requested vis");
        assertEquals(15.0f, battery.getStoredVis());

        // Discharge limited by stored vis
        discharged = battery.dischargeToMachine(50.0f);
        assertEquals(15.0f, discharged, "Should be limited by available stored vis");
        assertEquals(0.0f, battery.getStoredVis());

        // Discharge when empty
        discharged = battery.dischargeToMachine(50.0f);
        assertEquals(0.0f, discharged, "Should not discharge if empty");
        assertEquals(0.0f, battery.getStoredVis());
    }

    @Test
    public void testDischargeToMachineEdgeCases() {
        VisBatteryStorageLogic battery = new VisBatteryStorageLogic(100.0f, 10.0f, 20.0f);
        battery.setStoredVis(50.0f);

        float discharged = battery.dischargeToMachine(0.0f);
        assertEquals(0.0f, discharged);

        discharged = battery.dischargeToMachine(-5.0f);
        assertEquals(0.0f, discharged);
    }

    @Test
    public void testCapacityAndFillStatus() {
        VisBatteryStorageLogic battery = new VisBatteryStorageLogic(100.0f, 10.0f, 20.0f);

        assertTrue(battery.isEmpty());
        assertFalse(battery.isFull());
        assertEquals(0.0f, battery.getFillRatio(), 1e-6);
        assertEquals(100.0f, battery.getRemainingCapacity(), 1e-6);

        battery.setStoredVis(50.0f);
        assertFalse(battery.isEmpty());
        assertFalse(battery.isFull());
        assertEquals(0.5f, battery.getFillRatio(), 1e-6);
        assertEquals(50.0f, battery.getRemainingCapacity(), 1e-6);

        battery.setStoredVis(100.0f);
        assertFalse(battery.isEmpty());
        assertTrue(battery.isFull());
        assertEquals(1.0f, battery.getFillRatio(), 1e-6);
        assertEquals(0.0f, battery.getRemainingCapacity(), 1e-6);
    }

    @Test
    public void testZeroCapacityFillRatio() {
        VisBatteryStorageLogic zeroBattery = new VisBatteryStorageLogic(0.0f, 5.0f, 5.0f);
        assertTrue(zeroBattery.isEmpty());
        assertTrue(zeroBattery.isFull());
        assertEquals(0.0f, zeroBattery.getFillRatio(), 1e-6);
        assertEquals(0.0f, zeroBattery.getRemainingCapacity(), 1e-6);
    }

    @Test
    public void testNaNAndInfinitySanitization() {
        VisBatteryStorageLogic nanBattery = new VisBatteryStorageLogic(Float.NaN, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY);
        assertEquals(0.0f, nanBattery.getMaxCapacity());
        assertEquals(0.0f, nanBattery.getSiphonRate());
        assertEquals(0.0f, nanBattery.getDischargeRate());

        VisBatteryStorageLogic normalBattery = new VisBatteryStorageLogic(100.0f, 10.0f, 20.0f);
        normalBattery.setStoredVis(50.0f);

        // NaN inputs must be rejected without mutating storedVis
        normalBattery.setStoredVis(Float.NaN);
        assertEquals(50.0f, normalBattery.getStoredVis());

        normalBattery.setStoredVis(Float.POSITIVE_INFINITY);
        assertEquals(50.0f, normalBattery.getStoredVis());

        assertEquals(0.0f, normalBattery.siphonFromAura(Float.NaN));
        assertEquals(50.0f, normalBattery.getStoredVis());

        assertEquals(0.0f, normalBattery.siphonFromAura(Float.POSITIVE_INFINITY));
        assertEquals(50.0f, normalBattery.getStoredVis());

        assertEquals(0.0f, normalBattery.dischargeToMachine(Float.NaN));
        assertEquals(50.0f, normalBattery.getStoredVis());

        assertEquals(0.0f, normalBattery.dischargeToMachine(Float.POSITIVE_INFINITY));
        assertEquals(50.0f, normalBattery.getStoredVis());
    }

    @Test
    public void testShouldAbsorbThreshold() {
        float baseAura = 100.0f;

        // Exactly at or below 95% threshold -> do not absorb
        assertFalse(VisBatteryStorageLogic.shouldAbsorb(95.0f, baseAura, false));
        assertFalse(VisBatteryStorageLogic.shouldAbsorb(90.0f, baseAura, false));

        // Above 95% threshold -> absorb
        assertTrue(VisBatteryStorageLogic.shouldAbsorb(96.0f, baseAura, false));
        assertTrue(VisBatteryStorageLogic.shouldAbsorb(120.0f, baseAura, false));

        // Redstone signal inhibits absorption regardless of aura
        assertFalse(VisBatteryStorageLogic.shouldAbsorb(120.0f, baseAura, true));

        // NaN safety
        assertFalse(VisBatteryStorageLogic.shouldAbsorb(Float.NaN, baseAura, false));
        assertFalse(VisBatteryStorageLogic.shouldAbsorb(120.0f, Float.NaN, false));
    }

    @Test
    public void testShouldDischargeThreshold() {
        float baseAura = 100.0f;

        // Empty battery never discharges
        assertFalse(VisBatteryStorageLogic.shouldDischarge(50.0f, baseAura, false, 0.0f));
        assertFalse(VisBatteryStorageLogic.shouldDischarge(50.0f, baseAura, true, 0.0f));

        // Redstone signal forces rapid discharge if battery contains vis
        assertTrue(VisBatteryStorageLogic.shouldDischarge(90.0f, baseAura, true, 10.0f));
        assertTrue(VisBatteryStorageLogic.shouldDischarge(50.0f, baseAura, true, 10.0f));

        // Unpowered: discharges only below 75% threshold
        assertFalse(VisBatteryStorageLogic.shouldDischarge(75.0f, baseAura, false, 10.0f));
        assertFalse(VisBatteryStorageLogic.shouldDischarge(80.0f, baseAura, false, 10.0f));
        assertTrue(VisBatteryStorageLogic.shouldDischarge(74.0f, baseAura, false, 10.0f));
        assertTrue(VisBatteryStorageLogic.shouldDischarge(10.0f, baseAura, false, 10.0f));

        // NaN safety
        assertFalse(VisBatteryStorageLogic.shouldDischarge(Float.NaN, baseAura, false, 10.0f));
        assertFalse(VisBatteryStorageLogic.shouldDischarge(50.0f, Float.NaN, false, 10.0f));
        assertFalse(VisBatteryStorageLogic.shouldDischarge(50.0f, baseAura, false, Float.NaN));
    }

    @Test
    public void testCalculateAbsorbAmount() {
        float baseAura = 100.0f; // 95% threshold is 95.0

        // Excess 10 (105 - 95), remaining capacity 50, siphon rate 5 -> limited by rate
        assertEquals(5.0f, VisBatteryStorageLogic.calculateAbsorbAmount(105.0f, baseAura, 50.0f, 5.0f), 1e-6);

        // Excess 3 (98 - 95), remaining capacity 50, siphon rate 5 -> limited by excess
        assertEquals(3.0f, VisBatteryStorageLogic.calculateAbsorbAmount(98.0f, baseAura, 50.0f, 5.0f), 1e-6);

        // Excess 10 (105 - 95), remaining capacity 2, siphon rate 5 -> limited by capacity
        assertEquals(2.0f, VisBatteryStorageLogic.calculateAbsorbAmount(105.0f, baseAura, 2.0f, 5.0f), 1e-6);

        // At or below threshold -> 0
        assertEquals(0.0f, VisBatteryStorageLogic.calculateAbsorbAmount(95.0f, baseAura, 50.0f, 5.0f), 1e-6);
        assertEquals(0.0f, VisBatteryStorageLogic.calculateAbsorbAmount(80.0f, baseAura, 50.0f, 5.0f), 1e-6);

        // Zero capacity or rate -> 0
        assertEquals(0.0f, VisBatteryStorageLogic.calculateAbsorbAmount(110.0f, baseAura, 0.0f, 5.0f), 1e-6);
        assertEquals(0.0f, VisBatteryStorageLogic.calculateAbsorbAmount(110.0f, baseAura, 50.0f, 0.0f), 1e-6);

        // NaN safety
        assertEquals(0.0f, VisBatteryStorageLogic.calculateAbsorbAmount(Float.NaN, baseAura, 50.0f, 5.0f), 1e-6);
    }

    @Test
    public void testCalculateDischargeAmount() {
        float baseAura = 100.0f; // 75% threshold is 75.0

        // Powered rapid discharge: 2x rate (10 * 2 = 20), stored 50 -> 20
        assertEquals(20.0f, VisBatteryStorageLogic.calculateDischargeAmount(80.0f, baseAura, 50.0f, 10.0f, true), 1e-6);

        // Powered rapid discharge: 2x rate (10 * 2 = 20), stored 15 -> limited by stored (15)
        assertEquals(15.0f, VisBatteryStorageLogic.calculateDischargeAmount(80.0f, baseAura, 15.0f, 10.0f, true), 1e-6);

        // Unpowered: deficit 25 (75 - 50), stored 50, rate 10 -> limited by rate (10)
        assertEquals(10.0f, VisBatteryStorageLogic.calculateDischargeAmount(50.0f, baseAura, 50.0f, 10.0f, false), 1e-6);

        // Unpowered: deficit 5 (75 - 70), stored 50, rate 10 -> limited by deficit (5)
        assertEquals(5.0f, VisBatteryStorageLogic.calculateDischargeAmount(70.0f, baseAura, 50.0f, 10.0f, false), 1e-6);

        // Unpowered: deficit 25 (75 - 50), stored 8, rate 10 -> limited by stored (8)
        assertEquals(8.0f, VisBatteryStorageLogic.calculateDischargeAmount(50.0f, baseAura, 8.0f, 10.0f, false), 1e-6);

        // Unpowered: at or above 75% threshold -> 0
        assertEquals(0.0f, VisBatteryStorageLogic.calculateDischargeAmount(75.0f, baseAura, 50.0f, 10.0f, false), 1e-6);
        assertEquals(0.0f, VisBatteryStorageLogic.calculateDischargeAmount(80.0f, baseAura, 50.0f, 10.0f, false), 1e-6);

        // Zero stored or rate -> 0
        assertEquals(0.0f, VisBatteryStorageLogic.calculateDischargeAmount(50.0f, baseAura, 0.0f, 10.0f, false), 1e-6);
        assertEquals(0.0f, VisBatteryStorageLogic.calculateDischargeAmount(50.0f, baseAura, 50.0f, 0.0f, false), 1e-6);

        // NaN safety
        assertEquals(0.0f, VisBatteryStorageLogic.calculateDischargeAmount(Float.NaN, baseAura, 50.0f, 10.0f, false), 1e-6);
    }
}
