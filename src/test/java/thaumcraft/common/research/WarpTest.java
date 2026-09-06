package thaumcraft.common.research;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.common.lib.capabilities.PlayerWarp;

public class WarpTest {

    @Test
    public void testTotalWarpCalculation() {
        PlayerWarp warp = new PlayerWarp();

        warp.set(IPlayerWarp.EnumWarpType.PERMANENT, 10);
        warp.set(IPlayerWarp.EnumWarpType.NORMAL, 10);
        warp.set(IPlayerWarp.EnumWarpType.TEMPORARY, 4);

        int totalWarp = warp.get(IPlayerWarp.EnumWarpType.PERMANENT) +
                        warp.get(IPlayerWarp.EnumWarpType.NORMAL) +
                        warp.get(IPlayerWarp.EnumWarpType.TEMPORARY);

        assertEquals(24, totalWarp, "Minor warp tier boundary calculation failed.");
        assertTrue(totalWarp < 25);

        warp.add(IPlayerWarp.EnumWarpType.TEMPORARY, 1);

        totalWarp = warp.get(IPlayerWarp.EnumWarpType.PERMANENT) +
                    warp.get(IPlayerWarp.EnumWarpType.NORMAL) +
                    warp.get(IPlayerWarp.EnumWarpType.TEMPORARY);

        assertEquals(25, totalWarp, "Medium warp tier boundary calculation failed.");
        assertTrue(totalWarp >= 25 && totalWarp <= 50);

        warp.add(IPlayerWarp.EnumWarpType.PERMANENT, 26);
        totalWarp = warp.get(IPlayerWarp.EnumWarpType.PERMANENT) +
                    warp.get(IPlayerWarp.EnumWarpType.NORMAL) +
                    warp.get(IPlayerWarp.EnumWarpType.TEMPORARY);

        assertEquals(51, totalWarp, "Major warp tier boundary calculation failed.");
        assertTrue(totalWarp > 50);
    }

    @Test
    public void testSoapReduction() {
        PlayerWarp warp = new PlayerWarp();
        warp.set(IPlayerWarp.EnumWarpType.TEMPORARY, 10);
        warp.set(IPlayerWarp.EnumWarpType.NORMAL, 5);

        warp.set(IPlayerWarp.EnumWarpType.TEMPORARY, 0);
        warp.reduce(IPlayerWarp.EnumWarpType.NORMAL, 1);

        assertEquals(0, warp.get(IPlayerWarp.EnumWarpType.TEMPORARY), "Soap failed to clear temporary warp.");
        assertEquals(4, warp.get(IPlayerWarp.EnumWarpType.NORMAL), "Soap failed to reduce normal warp by 1.");
    }
}
