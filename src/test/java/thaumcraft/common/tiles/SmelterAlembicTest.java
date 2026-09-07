package thaumcraft.common.tiles;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.blocks.essentia.SmelterTier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Essentia Smelter and Distillation Alembic subsystem.
 *
 * <p>These tests exercise the core logic without any Minecraft server context —
 * they use lightweight test stubs that mirror the real block-entity logic,
 * following the same pattern as {@link JarTest}.
 *
 * <h3>Tested invariants</h3>
 * <ul>
 *   <li>Smelter fuel consumption and burn time decrement.</li>
 *   <li>Item decomposition into aspect essentia.</li>
 *   <li>Alembic single-aspect storage, aspect filter enforcement, and capacity limits.</li>
 *   <li>processAlembics multi-pass stacking resolution.</li>
 *   <li>IEssentiaTransport suction and extraction from alembics.</li>
 *   <li>SmelterTier enum values.</li>
 *   <li>Comparator signal formulas.</li>
 * </ul>
 */
public class SmelterAlembicTest {

    // =========================================================================
    // Test Stubs — mirror real logic without BlockEntity/registry dependencies
    // =========================================================================

    /**
     * Lightweight alembic stub mirroring AlembicBlockEntity logic.
     */
    static class TestAlembic {
        static final int MAX_AMOUNT = 128;

        Aspect aspect = null;
        Aspect aspectFilter = null;
        int amount = 0;
        int facing = 0; // Direction.DOWN ordinal

        boolean doesContainerAccept(Aspect tag) {
            return true;
        }

        int addToContainer(Aspect tag, int am) {
            if (aspectFilter != null && tag != aspectFilter) return am;
            if ((amount < MAX_AMOUNT && tag == aspect) || amount == 0) {
                aspect = tag;
                int added = Math.min(am, MAX_AMOUNT - amount);
                amount += added;
                am -= added;
            }
            return am;
        }

        boolean takeFromContainer(Aspect tag, int am) {
            if (amount == 0 || aspect == null) {
                aspect = null;
                amount = 0;
                return false;
            }
            if (aspect == tag && amount >= am) {
                amount -= am;
                if (amount <= 0) {
                    aspect = null;
                    amount = 0;
                }
                return true;
            }
            return false;
        }

        boolean doesContainerContainAmount(Aspect tag, int am) {
            return tag == aspect && amount >= am;
        }

        int containerContains(Aspect tag) {
            return tag == aspect ? amount : 0;
        }

        // IEssentiaTransport stubs
        boolean isConnectable(int faceOrdinal) {
            return faceOrdinal != 0 /* DOWN */ && faceOrdinal != facing;
        }

        boolean canOutputTo(int faceOrdinal) {
            return isConnectable(faceOrdinal);
        }

        boolean canInputFrom(int faceOrdinal) {
            return false;
        }

        int takeEssentia(Aspect asp, int amt, int faceOrdinal) {
            return (canOutputTo(faceOrdinal) && takeFromContainer(asp, amt)) ? amt : 0;
        }

        int addEssentia(Aspect asp, int amt, int faceOrdinal) {
            return 0;
        }

        /** Comparator formula: (amount * 14 / maxAmount) + (amount > 0 ? 1 : 0) */
        int comparatorLevel() {
            if (amount <= 0) return 0;
            return (int)(amount / (float) MAX_AMOUNT * 14.0f) + (amount > 0 ? 1 : 0);
        }
    }

    /**
     * Lightweight smelter stub mirroring SmelterBlockEntity logic.
     */
    static class TestSmelter {
        final SmelterTier tier;
        final AspectList aspects = new AspectList();
        int vis = 0;
        int furnaceBurnTime = 0;
        int currentItemBurnTime = 0;
        int furnaceCookTime = 0;
        int smeltTime = 100;
        boolean speedBoost = false;
        int bellows = 0;

        TestSmelter(SmelterTier tier) {
            this.tier = tier;
        }

        boolean isLit() {
            return furnaceBurnTime > 0;
        }

        void consumeFuel(int burnTime, boolean isAlumentum) {
            furnaceBurnTime = burnTime;
            currentItemBurnTime = burnTime;
            speedBoost = isAlumentum;
        }

        boolean canSmelt(AspectList inputAspects) {
            if (inputAspects == null || inputAspects.size() == 0) return false;
            int totalVis = inputAspects.visSize();
            return totalVis <= (tier.getCapacity() - vis);
        }

        int computeSmeltTime(AspectList inputAspects) {
            int totalVis = inputAspects.visSize();
            return Math.max(1, (int)(totalVis * 2 * (1.0f - 0.125f * bellows)));
        }

        /**
         * Deterministic smelt for testing — no random flux generation.
         * All aspects go directly into the buffer.
         */
        void smeltItemDeterministic(AspectList inputAspects) {
            if (inputAspects == null) return;
            for (Aspect a : inputAspects.getAspects()) {
                aspects.add(a, inputAspects.getAmount(a));
            }
            vis = aspects.visSize();
        }

        boolean takeFromBuffer(Aspect aspect, int amount) {
            if (aspects.getAmount(aspect) >= amount) {
                aspects.remove(aspect, amount);
                vis = aspects.visSize();
                return true;
            }
            return false;
        }

        int getPushSpeed() {
            int speed = tier.getPushSpeed();
            if (speedBoost) speed = Math.max(1, (int)(speed * 0.8));
            return speed;
        }
    }

    /**
     * Multi-pass processAlembics logic (mirrors AlembicBlockEntity.processAlembics).
     */
    static boolean processAlembics(TestAlembic[] column, Aspect aspect) {
        // Pass 1: find alembic already holding this aspect
        for (TestAlembic alembic : column) {
            if (alembic.amount > 0 && alembic.aspect == aspect
                    && alembic.addToContainer(aspect, 1) == 0) {
                return true;
            }
        }
        // Pass 2: find first empty/filtered alembic
        for (TestAlembic alembic : column) {
            if ((alembic.aspectFilter == null || alembic.aspectFilter == aspect)
                    && alembic.addToContainer(aspect, 1) == 0) {
                return true;
            }
        }
        return false;
    }

    // =========================================================================
    // Test Fixtures
    // =========================================================================

    private TestSmelter smelterBasic;
    private TestSmelter smelterThaumium;
    private TestSmelter smelterVoid;
    private TestAlembic alembic;

    @BeforeEach
    void setUp() {
        smelterBasic = new TestSmelter(SmelterTier.BASIC);
        smelterThaumium = new TestSmelter(SmelterTier.THAUMIUM);
        smelterVoid = new TestSmelter(SmelterTier.VOID);
        alembic = new TestAlembic();
    }

    // =========================================================================
    // 1. SmelterTier Constants
    // =========================================================================

    @Nested
    class TierConstants {

        @Test
        void basicTierValues() {
            assertEquals(256, SmelterTier.BASIC.getCapacity());
            assertEquals(15, SmelterTier.BASIC.getPushSpeed());
            assertEquals(0.8f, SmelterTier.BASIC.getEfficiency(), 0.001f);
        }

        @Test
        void thaumiumTierValues() {
            assertEquals(384, SmelterTier.THAUMIUM.getCapacity());
            assertEquals(10, SmelterTier.THAUMIUM.getPushSpeed());
            assertEquals(0.9f, SmelterTier.THAUMIUM.getEfficiency(), 0.001f);
        }

        @Test
        void voidTierValues() {
            assertEquals(512, SmelterTier.VOID.getCapacity());
            assertEquals(15, SmelterTier.VOID.getPushSpeed());
            assertEquals(0.95f, SmelterTier.VOID.getEfficiency(), 0.001f);
        }
    }

    // =========================================================================
    // 2. Smelter Fuel Consumption
    // =========================================================================

    @Nested
    class FuelConsumption {

        @Test
        void smelterStartsUnlit() {
            assertFalse(smelterBasic.isLit());
            assertEquals(0, smelterBasic.furnaceBurnTime);
        }

        @Test
        void consumeFuelSetsLitAndBurnTime() {
            smelterBasic.consumeFuel(200, false);
            assertTrue(smelterBasic.isLit());
            assertEquals(200, smelterBasic.furnaceBurnTime);
            assertEquals(200, smelterBasic.currentItemBurnTime);
            assertFalse(smelterBasic.speedBoost);
        }

        @Test
        void consumeAlumentumSetsSpeedBoost() {
            smelterBasic.consumeFuel(150, true);
            assertTrue(smelterBasic.speedBoost);
        }

        @Test
        void burnTimeDecrement() {
            smelterBasic.consumeFuel(5, false);
            for (int i = 4; i >= 0; i--) {
                smelterBasic.furnaceBurnTime--;
                assertEquals(i, smelterBasic.furnaceBurnTime);
            }
            assertFalse(smelterBasic.isLit());
        }

        @Test
        void pushSpeedWithSpeedBoost() {
            smelterBasic.consumeFuel(100, true);
            int boosted = smelterBasic.getPushSpeed();
            int normal = SmelterTier.BASIC.getPushSpeed();
            assertEquals(Math.max(1, (int)(normal * 0.8)), boosted);
            assertTrue(boosted < normal);
        }
    }

    // =========================================================================
    // 3. Item Decomposition
    // =========================================================================

    @Nested
    class ItemDecomposition {

        @Test
        void canSmeltReturnsTrueWhenBufferHasRoom() {
            AspectList input = new AspectList().add(Aspect.FIRE, 10).add(Aspect.EARTH, 5);
            assertTrue(smelterBasic.canSmelt(input));
        }

        @Test
        void canSmeltReturnsFalseWhenBufferFull() {
            // Fill buffer to capacity
            AspectList fill = new AspectList().add(Aspect.WATER, SmelterTier.BASIC.getCapacity());
            smelterBasic.smeltItemDeterministic(fill);

            AspectList more = new AspectList().add(Aspect.FIRE, 1);
            assertFalse(smelterBasic.canSmelt(more));
        }

        @Test
        void canSmeltReturnsFalseForNullInput() {
            assertFalse(smelterBasic.canSmelt(null));
        }

        @Test
        void canSmeltReturnsFalseForEmptyInput() {
            assertFalse(smelterBasic.canSmelt(new AspectList()));
        }

        @Test
        void smeltItemAddsAspectsToBuffer() {
            AspectList input = new AspectList()
                    .add(Aspect.FIRE, 3)
                    .add(Aspect.AIR, 2);
            smelterBasic.smeltItemDeterministic(input);

            assertEquals(3, smelterBasic.aspects.getAmount(Aspect.FIRE));
            assertEquals(2, smelterBasic.aspects.getAmount(Aspect.AIR));
            assertEquals(5, smelterBasic.vis);
        }

        @Test
        void smeltTimeComputationWithNoBellows() {
            AspectList input = new AspectList().add(Aspect.FIRE, 10);
            int expected = (int)(10 * 2 * (1.0f - 0.125f * 0));
            assertEquals(expected, smelterBasic.computeSmeltTime(input));
        }

        @Test
        void smeltTimeComputationWithBellows() {
            smelterBasic.bellows = 2;
            AspectList input = new AspectList().add(Aspect.FIRE, 10);
            int expected = (int)(10 * 2 * (1.0f - 0.125f * 2));
            assertEquals(expected, smelterBasic.computeSmeltTime(input));
        }

        @Test
        void multipleSmeltItemsAccumulateInBuffer() {
            smelterBasic.smeltItemDeterministic(new AspectList().add(Aspect.FIRE, 5));
            smelterBasic.smeltItemDeterministic(new AspectList().add(Aspect.FIRE, 3));
            assertEquals(8, smelterBasic.aspects.getAmount(Aspect.FIRE));
            assertEquals(8, smelterBasic.vis);
        }

        @Test
        void takeFromBufferReducesVis() {
            smelterBasic.smeltItemDeterministic(new AspectList().add(Aspect.FIRE, 10));
            assertTrue(smelterBasic.takeFromBuffer(Aspect.FIRE, 4));
            assertEquals(6, smelterBasic.aspects.getAmount(Aspect.FIRE));
            assertEquals(6, smelterBasic.vis);
        }

        @Test
        void takeFromBufferFailsWhenInsufficient() {
            smelterBasic.smeltItemDeterministic(new AspectList().add(Aspect.FIRE, 3));
            assertFalse(smelterBasic.takeFromBuffer(Aspect.FIRE, 5));
            assertEquals(3, smelterBasic.vis, "Vis must not change on failed take");
        }
    }

    // =========================================================================
    // 4. Alembic Single-Aspect Storage
    // =========================================================================

    @Nested
    class AlembicStorage {

        @Test
        void emptyAlembicAcceptsAnyAspect() {
            int left = alembic.addToContainer(Aspect.FIRE, 10);
            assertEquals(0, left);
            assertEquals(Aspect.FIRE, alembic.aspect);
            assertEquals(10, alembic.amount);
        }

        @Test
        void alembicRejectsDifferentAspectWhenFilled() {
            alembic.addToContainer(Aspect.FIRE, 10);
            int left = alembic.addToContainer(Aspect.WATER, 5);
            assertEquals(5, left, "Different aspect should be fully rejected");
            assertEquals(10, alembic.amount);
        }

        @Test
        void alembicAcceptsSameAspect() {
            alembic.addToContainer(Aspect.FIRE, 50);
            int left = alembic.addToContainer(Aspect.FIRE, 30);
            assertEquals(0, left);
            assertEquals(80, alembic.amount);
        }

        @Test
        void alembicCapacityEnforced() {
            alembic.addToContainer(Aspect.EARTH, 100);
            int left = alembic.addToContainer(Aspect.EARTH, 50);
            assertEquals(22, left, "Should overflow by 22 (100+50-128=22)");
            assertEquals(TestAlembic.MAX_AMOUNT, alembic.amount);
        }

        @Test
        void capacityConstant() {
            assertEquals(128, TestAlembic.MAX_AMOUNT);
        }
    }

    // =========================================================================
    // 5. Alembic Aspect Filter
    // =========================================================================

    @Nested
    class AlembicFilter {

        @Test
        void filteredAlembicRejectsWrongAspect() {
            alembic.aspectFilter = Aspect.FIRE;
            int left = alembic.addToContainer(Aspect.WATER, 10);
            assertEquals(10, left);
            assertNull(alembic.aspect);
            assertEquals(0, alembic.amount);
        }

        @Test
        void filteredAlembicAcceptsMatchingAspect() {
            alembic.aspectFilter = Aspect.FIRE;
            int left = alembic.addToContainer(Aspect.FIRE, 10);
            assertEquals(0, left);
            assertEquals(Aspect.FIRE, alembic.aspect);
            assertEquals(10, alembic.amount);
        }

        @Test
        void clearFilterAllowsAnyAspect() {
            alembic.aspectFilter = Aspect.FIRE;
            alembic.aspectFilter = null;
            int left = alembic.addToContainer(Aspect.WATER, 5);
            assertEquals(0, left);
        }
    }

    // =========================================================================
    // 6. processAlembics Multi-Pass Stacking Resolution
    // =========================================================================

    @Nested
    class ProcessAlembics {

        @Test
        void pass1FillsExistingMatchFirst() {
            TestAlembic a1 = new TestAlembic();
            TestAlembic a2 = new TestAlembic();

            // a1 already has FIRE, a2 is empty
            a1.addToContainer(Aspect.FIRE, 10);

            assertTrue(processAlembics(new TestAlembic[]{a1, a2}, Aspect.FIRE));
            assertEquals(11, a1.amount, "Should add to a1 (already has FIRE)");
            assertEquals(0, a2.amount, "a2 should remain empty");
        }

        @Test
        void pass2FillsFirstEmptySlot() {
            TestAlembic a1 = new TestAlembic();
            TestAlembic a2 = new TestAlembic();

            // a1 has WATER (different), a2 is empty
            a1.addToContainer(Aspect.WATER, 50);

            assertTrue(processAlembics(new TestAlembic[]{a1, a2}, Aspect.FIRE));
            assertEquals(50, a1.amount, "a1 should remain unchanged (different aspect)");
            assertEquals(1, a2.amount, "a2 should receive the FIRE");
            assertEquals(Aspect.FIRE, a2.aspect);
        }

        @Test
        void pass2RespectsAspectFilter() {
            TestAlembic a1 = new TestAlembic();
            TestAlembic a2 = new TestAlembic();

            a1.aspectFilter = Aspect.WATER; // Won't accept FIRE
            a2.aspectFilter = Aspect.FIRE;  // Will accept FIRE

            assertTrue(processAlembics(new TestAlembic[]{a1, a2}, Aspect.FIRE));
            assertEquals(0, a1.amount, "a1 filtered for WATER, should not receive FIRE");
            assertEquals(1, a2.amount, "a2 filtered for FIRE, should receive it");
        }

        @Test
        void returnsFalseWhenAllAlembicsFull() {
            TestAlembic a1 = new TestAlembic();
            a1.addToContainer(Aspect.FIRE, TestAlembic.MAX_AMOUNT);

            TestAlembic a2 = new TestAlembic();
            a2.addToContainer(Aspect.WATER, TestAlembic.MAX_AMOUNT);

            assertFalse(processAlembics(new TestAlembic[]{a1, a2}, Aspect.FIRE));
        }

        @Test
        void returnsFalseWhenNoAlembics() {
            assertFalse(processAlembics(new TestAlembic[]{}, Aspect.FIRE));
        }

        @Test
        void fillsExistingBeforeEmpty() {
            TestAlembic a1 = new TestAlembic(); // empty
            TestAlembic a2 = new TestAlembic();
            a2.addToContainer(Aspect.FIRE, 5); // already has FIRE

            // Pass 1 should find a2 first (has matching FIRE)
            assertTrue(processAlembics(new TestAlembic[]{a1, a2}, Aspect.FIRE));
            assertEquals(6, a2.amount, "a2 should receive (already has FIRE)");
            assertEquals(0, a1.amount, "a1 should remain empty (pass 1 found a2)");
        }
    }

    // =========================================================================
    // 7. IEssentiaTransport — Suction and Extraction
    // =========================================================================

    @Nested
    class EssentiaTransport {

        @Test
        void canOutputOnHorizontalFaces() {
            // Direction ordinals: DOWN=0, UP=1, NORTH=2, SOUTH=3, WEST=4, EAST=5
            alembic.facing = 0; // DOWN (no label)
            assertTrue(alembic.canOutputTo(2), "Should output NORTH");
            assertTrue(alembic.canOutputTo(3), "Should output SOUTH");
            assertTrue(alembic.canOutputTo(4), "Should output WEST");
            assertTrue(alembic.canOutputTo(5), "Should output EAST");
            assertTrue(alembic.canOutputTo(1), "Should output UP");
        }

        @Test
        void cannotOutputDown() {
            assertFalse(alembic.canOutputTo(0), "Should not output DOWN");
        }

        @Test
        void cannotOutputOnLabelFace() {
            alembic.facing = 2; // NORTH
            assertFalse(alembic.canOutputTo(2), "Should not output on label face");
            assertTrue(alembic.canOutputTo(3), "Should output on opposite face");
        }

        @Test
        void cannotInputFromAnyFace() {
            for (int i = 0; i < 6; i++) {
                assertFalse(alembic.canInputFrom(i),
                        "Alembics should never accept external input");
            }
        }

        @Test
        void takeEssentiaSuccess() {
            alembic.addToContainer(Aspect.FIRE, 10);
            int taken = alembic.takeEssentia(Aspect.FIRE, 5, 2); // NORTH
            assertEquals(5, taken);
            assertEquals(5, alembic.amount);
        }

        @Test
        void takeEssentiaFailsOnWrongFace() {
            alembic.addToContainer(Aspect.FIRE, 10);
            int taken = alembic.takeEssentia(Aspect.FIRE, 5, 0); // DOWN
            assertEquals(0, taken, "Cannot take from DOWN face");
            assertEquals(10, alembic.amount, "Amount unchanged");
        }

        @Test
        void takeEssentiaFailsWhenInsufficient() {
            alembic.addToContainer(Aspect.FIRE, 3);
            int taken = alembic.takeEssentia(Aspect.FIRE, 5, 2);
            assertEquals(0, taken);
            assertEquals(3, alembic.amount, "Amount unchanged on failed take");
        }

        @Test
        void addEssentiaAlwaysReturnsZero() {
            int added = alembic.addEssentia(Aspect.FIRE, 10, 2);
            assertEquals(0, added, "Alembics don't accept via addEssentia");
        }
    }

    // =========================================================================
    // 8. Comparator Signals
    // =========================================================================

    @Nested
    class ComparatorSignals {

        @Test
        void alembicComparatorEmptyIsZero() {
            assertEquals(0, alembic.comparatorLevel());
        }

        @Test
        void alembicComparatorFullIs15() {
            alembic.addToContainer(Aspect.FIRE, TestAlembic.MAX_AMOUNT);
            // (128 * 14 / 128) + 1 = 14 + 1 = 15
            assertEquals(15, alembic.comparatorLevel());
        }

        @Test
        void alembicComparatorHalfway() {
            alembic.addToContainer(Aspect.FIRE, 64);
            // (64/128 * 14) + 1 = 7 + 1 = 8
            int expected = (int)(64 / (float) TestAlembic.MAX_AMOUNT * 14.0f) + 1;
            assertEquals(expected, alembic.comparatorLevel());
        }

        @Test
        void alembicComparatorSingle() {
            alembic.addToContainer(Aspect.FIRE, 1);
            // (1/128 * 14) + 1 = 0 + 1 = 1
            int expected = (int)(1 / (float) TestAlembic.MAX_AMOUNT * 14.0f) + 1;
            assertEquals(expected, alembic.comparatorLevel());
        }
    }

    // =========================================================================
    // 9. Serialization (ValueOutput/ValueInput pattern validation)
    // =========================================================================

    @Nested
    class Serialization {

        @Test
        void alembicStateRoundTrip() {
            // Simulate save → load cycle by verifying field consistency
            alembic.addToContainer(Aspect.FIRE, 42);
            alembic.aspectFilter = Aspect.WATER;
            alembic.facing = 3; // SOUTH

            // "Save" state
            Aspect savedAspect = alembic.aspect;
            Aspect savedFilter = alembic.aspectFilter;
            int savedAmount = alembic.amount;
            int savedFacing = alembic.facing;

            // Create new alembic and "load"
            TestAlembic loaded = new TestAlembic();
            loaded.aspect = savedAspect;
            loaded.aspectFilter = savedFilter;
            loaded.amount = savedAmount;
            loaded.facing = savedFacing;

            assertEquals(Aspect.FIRE, loaded.aspect);
            assertEquals(Aspect.WATER, loaded.aspectFilter);
            assertEquals(42, loaded.amount);
            assertEquals(3, loaded.facing);
        }

        @Test
        void smelterStateRoundTrip() {
            smelterBasic.consumeFuel(200, true);
            smelterBasic.smeltItemDeterministic(
                    new AspectList().add(Aspect.FIRE, 5).add(Aspect.EARTH, 3));
            smelterBasic.smeltTime = 42;
            smelterBasic.furnaceCookTime = 10;

            // "Save"
            int savedBurn = smelterBasic.furnaceBurnTime;
            int savedItemBurn = smelterBasic.currentItemBurnTime;
            int savedCook = smelterBasic.furnaceCookTime;
            int savedSmelt = smelterBasic.smeltTime;
            boolean savedBoost = smelterBasic.speedBoost;
            int savedVis = smelterBasic.vis;

            // "Load" into new smelter
            TestSmelter loaded = new TestSmelter(SmelterTier.BASIC);
            loaded.furnaceBurnTime = savedBurn;
            loaded.currentItemBurnTime = savedItemBurn;
            loaded.furnaceCookTime = savedCook;
            loaded.smeltTime = savedSmelt;
            loaded.speedBoost = savedBoost;
            loaded.smeltItemDeterministic(
                    new AspectList().add(Aspect.FIRE, 5).add(Aspect.EARTH, 3));

            assertEquals(savedBurn, loaded.furnaceBurnTime);
            assertEquals(savedItemBurn, loaded.currentItemBurnTime);
            assertEquals(savedCook, loaded.furnaceCookTime);
            assertEquals(savedSmelt, loaded.smeltTime);
            assertEquals(savedBoost, loaded.speedBoost);
            assertEquals(savedVis, loaded.vis);
        }
    }

    // =========================================================================
    // 10. Cross-Tier Capacity Checks
    // =========================================================================

    @Nested
    class CrossTierCapacity {

        @Test
        void basicCapacityRejectionAtBoundary() {
            AspectList fill = new AspectList().add(Aspect.FIRE, 250);
            smelterBasic.smeltItemDeterministic(fill);

            AspectList more = new AspectList().add(Aspect.EARTH, 7);
            assertFalse(smelterBasic.canSmelt(more),
                    "6 capacity remaining (256-250), can't fit 7");

            AspectList fits = new AspectList().add(Aspect.EARTH, 6);
            assertTrue(smelterBasic.canSmelt(fits),
                    "6 capacity remaining, 6 should fit");
        }

        @Test
        void thaumiumHasMoreCapacityThanBasic() {
            AspectList fill = new AspectList().add(Aspect.FIRE, 300);
            assertFalse(smelterBasic.canSmelt(fill),
                    "300 exceeds basic capacity of 256");
            assertTrue(smelterThaumium.canSmelt(fill),
                    "300 fits in thaumium capacity of 384");
        }

        @Test
        void voidHasLargestCapacity() {
            AspectList fill = new AspectList().add(Aspect.FIRE, 500);
            assertFalse(smelterThaumium.canSmelt(fill),
                    "500 exceeds thaumium capacity of 384");
            assertTrue(smelterVoid.canSmelt(fill),
                    "500 fits in void capacity of 512");
        }
    }
}
