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
}
