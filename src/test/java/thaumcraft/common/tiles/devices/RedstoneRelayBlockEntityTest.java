package thaumcraft.common.tiles.devices;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thaumcraft.common.lib.RedstoneRelayLogic;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class RedstoneRelayBlockEntityTest {

    private MockEnvironment env;

    @BeforeEach
    public void setup() {
        env = new MockEnvironment();
    }

    private static class BlockData {
        boolean isRelay;
        int power;
        boolean isSolid;
    }

    private class MockEnvironment implements RedstoneRelayLogic.IEnvironment {
        Map<String, BlockData> blocks = new HashMap<>();

        private String key(int x, int y, int z) {
            return x + "," + y + "," + z;
        }

        public void setRelay(int x, int y, int z, int power) {
            BlockData data = new BlockData();
            data.isRelay = true;
            data.power = power;
            data.isSolid = false;
            blocks.put(key(x, y, z), data);
        }

        public void setSolid(int x, int y, int z) {
            BlockData data = new BlockData();
            data.isRelay = false;
            data.power = 0;
            data.isSolid = true;
            blocks.put(key(x, y, z), data);
        }

        @Override
        public boolean isRedstoneRelay(int x, int y, int z) {
            BlockData data = blocks.get(key(x, y, z));
            return data != null && data.isRelay;
        }

        @Override
        public int getPower(int x, int y, int z) {
            BlockData data = blocks.get(key(x, y, z));
            return data != null ? data.power : 0;
        }

        @Override
        public void setPower(int x, int y, int z, int power) {
            BlockData data = blocks.get(key(x, y, z));
            if (data != null && data.isRelay) {
                data.power = power;
            }
        }

        @Override
        public boolean isLineOfSightBlocked(int x, int y, int z) {
            BlockData data = blocks.get(key(x, y, z));
            return data != null && data.isSolid;
        }
    }

    @Test
    public void testTransmissionWithin16Blocks() {
        env.setRelay(0, 0, 0, 15);
        env.setRelay(0, 0, 5, 0); // Relay 5 blocks away
        env.setRelay(0, 0, -10, 0); // Relay 10 blocks away opposite direction
        env.setRelay(15, 0, 0, 0); // Relay 15 blocks away on X axis

        RedstoneRelayLogic.transmitPower(env, 0, 0, 0, 15);

        assertEquals(15, env.getPower(0, 0, 5), "Relay at Z=5 should receive power");
        assertEquals(15, env.getPower(0, 0, -10), "Relay at Z=-10 should receive power");
        assertEquals(15, env.getPower(15, 0, 0), "Relay at X=15 should receive power");
    }

    @Test
    public void testLineOfSightBlocked() {
        env.setRelay(0, 0, 0, 15);
        env.setSolid(0, 0, 3);
        env.setRelay(0, 0, 5, 0);

        RedstoneRelayLogic.transmitPower(env, 0, 0, 0, 15);

        assertEquals(0, env.getPower(0, 0, 5), "Relay at Z=5 should not receive power due to solid block");
    }

    @Test
    public void testTransmissionLimit16Blocks() {
        env.setRelay(0, 0, 0, 15);
        env.setRelay(0, 0, 16, 0);
        env.setRelay(0, 0, 17, 0);

        RedstoneRelayLogic.transmitPower(env, 0, 0, 0, 15);

        assertEquals(15, env.getPower(0, 0, 16), "Relay at Z=16 should receive power");
        assertEquals(0, env.getPower(0, 0, 17), "Relay at Z=17 should not receive power (out of range)");
    }
}
