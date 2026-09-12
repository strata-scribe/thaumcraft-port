package thaumcraft.common.golems.seals;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class SealStockLogicTest {

    @Test
    void testAllItemsMissing() {
        SealStockLogic.Pos pos = new SealStockLogic.Pos(0, 0, 0);
        List<SealStockLogic.ItemStack> inventory = new ArrayList<>();
        List<SealStockLogic.ItemQuota> quotas = Arrays.asList(
            new SealStockLogic.ItemQuota("minecraft:stone", 64),
            new SealStockLogic.ItemQuota("minecraft:dirt", 32)
        );

        List<SealStockLogic.FetchTask> tasks = SealStockLogic.evaluateQuotasAndDispatchTasks(pos, inventory, quotas);

        assertEquals(2, tasks.size());
        assertEquals("minecraft:dirt", tasks.get(0).itemId());
        assertEquals(32, tasks.get(0).fetchAmount());
        assertEquals("minecraft:stone", tasks.get(1).itemId());
        assertEquals(64, tasks.get(1).fetchAmount());
    }

    @Test
    void testPartiallyMissing() {
        SealStockLogic.Pos pos = new SealStockLogic.Pos(0, 0, 0);
        List<SealStockLogic.ItemStack> inventory = Arrays.asList(
            new SealStockLogic.ItemStack("minecraft:stone", 20),
            new SealStockLogic.ItemStack("minecraft:dirt", 40)
        );
        List<SealStockLogic.ItemQuota> quotas = Arrays.asList(
            new SealStockLogic.ItemQuota("minecraft:stone", 64),
            new SealStockLogic.ItemQuota("minecraft:dirt", 32)
        );

        List<SealStockLogic.FetchTask> tasks = SealStockLogic.evaluateQuotasAndDispatchTasks(pos, inventory, quotas);

        assertEquals(1, tasks.size());
        assertEquals("minecraft:stone", tasks.get(0).itemId());
        assertEquals(44, tasks.get(0).fetchAmount());
    }

    @Test
    void testFullStock() {
        SealStockLogic.Pos pos = new SealStockLogic.Pos(0, 0, 0);
        List<SealStockLogic.ItemStack> inventory = Arrays.asList(
            new SealStockLogic.ItemStack("minecraft:stone", 64),
            new SealStockLogic.ItemStack("minecraft:dirt", 32)
        );
        List<SealStockLogic.ItemQuota> quotas = Arrays.asList(
            new SealStockLogic.ItemQuota("minecraft:stone", 64),
            new SealStockLogic.ItemQuota("minecraft:dirt", 32)
        );

        List<SealStockLogic.FetchTask> tasks = SealStockLogic.evaluateQuotasAndDispatchTasks(pos, inventory, quotas);

        assertEquals(0, tasks.size());
    }

    @Test
    void testNullHandling() {
        SealStockLogic.Pos pos = new SealStockLogic.Pos(0, 0, 0);
        List<SealStockLogic.FetchTask> tasks = SealStockLogic.evaluateQuotasAndDispatchTasks(pos, null, null);
        assertEquals(0, tasks.size());

        List<SealStockLogic.ItemQuota> quotasWithNull = Arrays.asList(
            null,
            new SealStockLogic.ItemQuota("minecraft:stone", 64)
        );
        tasks = SealStockLogic.evaluateQuotasAndDispatchTasks(pos, null, quotasWithNull);
        assertEquals(1, tasks.size());
        assertEquals("minecraft:stone", tasks.get(0).itemId());
        assertEquals(64, tasks.get(0).fetchAmount());
    }

    @Test
    void testMultipleStacksSameItem() {
        SealStockLogic.Pos pos = new SealStockLogic.Pos(0, 0, 0);
        List<SealStockLogic.ItemStack> inventory = Arrays.asList(
            new SealStockLogic.ItemStack("minecraft:stone", 20),
            new SealStockLogic.ItemStack("minecraft:stone", 30)
        );
        List<SealStockLogic.ItemQuota> quotas = Arrays.asList(
            new SealStockLogic.ItemQuota("minecraft:stone", 64)
        );

        List<SealStockLogic.FetchTask> tasks = SealStockLogic.evaluateQuotasAndDispatchTasks(pos, inventory, quotas);

        assertEquals(1, tasks.size());
        assertEquals("minecraft:stone", tasks.get(0).itemId());
        assertEquals(14, tasks.get(0).fetchAmount());
    }
}
