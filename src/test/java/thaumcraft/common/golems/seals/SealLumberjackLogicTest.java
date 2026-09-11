package thaumcraft.common.golems.seals;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SealLumberjackLogicTest {

    @Test
    public void testTopDownPrioritization() {
        List<SealLumberjackLogic.Position> logs = new ArrayList<>();
        logs.add(new SealLumberjackLogic.Position(0, 10, 0));
        logs.add(new SealLumberjackLogic.Position(0, 5, 0));
        logs.add(new SealLumberjackLogic.Position(0, 15, 0));
        logs.add(new SealLumberjackLogic.Position(0, 12, 0));

        SealLumberjackLogic.prioritizeTopDown(logs);

        assertEquals(15, logs.get(0).y);
        assertEquals(12, logs.get(1).y);
        assertEquals(10, logs.get(2).y);
        assertEquals(5, logs.get(3).y);
    }

    @Test
    public void testScanForLogs() {
        SealLumberjackLogic.BlockValidator validator = new SealLumberjackLogic.BlockValidator() {
            @Override
            public boolean isLog(int x, int y, int z) {
                // simulate a tree at x=0, z=0 from y=0 to 5
                return x == 0 && z == 0 && y >= 0 && y <= 5;
            }
            @Override
            public boolean isSapling(int x, int y, int z) {
                return false;
            }
        };

        List<SealLumberjackLogic.Position> logs = SealLumberjackLogic.scanForLogs(-2, 0, -2, 2, 10, 2, validator);

        assertEquals(6, logs.size());

        for (SealLumberjackLogic.Position pos : logs) {
            assertEquals(0, pos.x);
            assertEquals(0, pos.z);
            assertTrue(pos.y >= 0 && pos.y <= 5);
        }
    }

    @Test
    public void testScanForSaplings() {
        SealLumberjackLogic.BlockValidator validator = new SealLumberjackLogic.BlockValidator() {
            @Override
            public boolean isLog(int x, int y, int z) {
                return false;
            }
            @Override
            public boolean isSapling(int x, int y, int z) {
                // simulate saplings around x=1, z=1
                return Math.abs(x) == 1 && Math.abs(z) == 1 && y == 0;
            }
        };

        List<SealLumberjackLogic.Position> saplings = SealLumberjackLogic.scanForSaplings(-2, 0, -2, 2, 10, 2, validator);

        assertEquals(4, saplings.size());

        for (SealLumberjackLogic.Position pos : saplings) {
            assertEquals(1, Math.abs(pos.x));
            assertEquals(1, Math.abs(pos.z));
            assertEquals(0, pos.y);
        }
    }
}
