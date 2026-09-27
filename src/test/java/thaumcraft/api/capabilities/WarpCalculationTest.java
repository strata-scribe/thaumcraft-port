package thaumcraft.api.capabilities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import thaumcraft.common.lib.capabilities.PlayerWarp;
import thaumcraft.api.capabilities.IPlayerWarp.EnumWarpType;

public class WarpCalculationTest {

    @Test
    public void testPermanentWarpNeverDecays() {
        PlayerWarp warp = new PlayerWarp();
        warp.set(EnumWarpType.PERMANENT, 50);

        // Permanent warp cannot be removed by normal means
        // We simulate a cleansing action that affects temporary and normal warp
        warp.set(EnumWarpType.TEMPORARY, 0);
        warp.reduce(EnumWarpType.NORMAL, 1);

        assertEquals(50, warp.get(EnumWarpType.PERMANENT), "Permanent warp should not decay");
    }

    @Test
    public void testStickyWarpDecreasesWithSoap() {
        PlayerWarp warp = new PlayerWarp();
        warp.set(EnumWarpType.NORMAL, 10);

        // Simulating soap usage, which typically reduces normal (sticky) warp by 1
        warp.reduce(EnumWarpType.NORMAL, 1);

        assertEquals(9, warp.get(EnumWarpType.NORMAL), "Sticky (Normal) warp should decrease by 1 when using soap");
    }

    @Test
    public void testTemporaryWarpDecaysOverTime() {
        PlayerWarp warp = new PlayerWarp();
        warp.set(EnumWarpType.TEMPORARY, 15);

        // Simulating periodic decay of temporary warp
        warp.reduce(EnumWarpType.TEMPORARY, 1);

        assertEquals(14, warp.get(EnumWarpType.TEMPORARY), "Temporary warp should decay periodically over time");
    }

    @Test
    public void testTotalWarpScoreComputation() {
        PlayerWarp warp = new PlayerWarp();
        warp.set(EnumWarpType.PERMANENT, 10);
        warp.set(EnumWarpType.NORMAL, 5);
        warp.set(EnumWarpType.TEMPORARY, 3);

        int totalWarp = warp.get(EnumWarpType.PERMANENT)
                      + warp.get(EnumWarpType.NORMAL)
                      + warp.get(EnumWarpType.TEMPORARY);

        assertEquals(18, totalWarp, "Total warp should be the sum of all warp types");
    }
}
