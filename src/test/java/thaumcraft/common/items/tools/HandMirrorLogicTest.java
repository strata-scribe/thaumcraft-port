package thaumcraft.common.items.tools;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HandMirrorLogicTest {

    static class MockSlot implements HandMirrorLogic.IItemSlot {
        String id;
        int count;
        int max;

        MockSlot(String id, int count, int max) {
            this.id = id;
            this.count = count;
            this.max = max;
        }

        @Override
        public String getItemId() { return id; }

        @Override
        public int getCount() { return count; }

        @Override
        public int getMaxStackSize() { return max; }

        @Override
        public void setItem(String id, int count) {
            this.id = id;
            this.count = count;
        }

        @Override
        public void addCount(int amount) {
            this.count += amount;
        }
    }

    @Test
    void testDepositDifferentDimension() {
        MockSlot[] inv = new MockSlot[]{ new MockSlot(null, 0, 64) };
        int deposited = HandMirrorLogic.tryDeposit(
            "overworld", 0, 0, 0,
            "nether", 0, 0, 0,
            100, inv, "itemA", 10
        );
        assertEquals(0, deposited);
    }

    @Test
    void testDepositOutOfRange() {
        MockSlot[] inv = new MockSlot[]{ new MockSlot(null, 0, 64) };
        int deposited = HandMirrorLogic.tryDeposit(
            "overworld", 0, 0, 0,
            "overworld", 101, 0, 0,
            100, inv, "itemA", 10
        );
        assertEquals(0, deposited);
    }

    @Test
    void testDepositIntoEmptyInventory() {
        MockSlot[] inv = new MockSlot[]{
            new MockSlot(null, 0, 64),
            new MockSlot(null, 0, 64)
        };
        int deposited = HandMirrorLogic.tryDeposit(
            "overworld", 0, 0, 0,
            "overworld", 10, 0, 0,
            100, inv, "itemA", 70
        );
        assertEquals(70, deposited);
        assertEquals("itemA", inv[0].getItemId());
        assertEquals(64, inv[0].getCount());
        assertEquals("itemA", inv[1].getItemId());
        assertEquals(6, inv[1].getCount());
    }

    @Test
    void testDepositIntoExistingStack() {
        MockSlot[] inv = new MockSlot[]{
            new MockSlot("itemA", 60, 64),
            new MockSlot(null, 0, 64)
        };
        int deposited = HandMirrorLogic.tryDeposit(
            "overworld", 0, 0, 0,
            "overworld", 10, 0, 0,
            100, inv, "itemA", 10
        );
        assertEquals(10, deposited);
        assertEquals(64, inv[0].getCount());
        assertEquals(6, inv[1].getCount());
        assertEquals("itemA", inv[1].getItemId());
    }

    @Test
    void testDepositInventoryFull() {
        MockSlot[] inv = new MockSlot[]{
            new MockSlot("itemA", 64, 64),
            new MockSlot("itemB", 64, 64)
        };
        int deposited = HandMirrorLogic.tryDeposit(
            "overworld", 0, 0, 0,
            "overworld", 10, 0, 0,
            100, inv, "itemA", 10
        );
        assertEquals(0, deposited);
    }
}
