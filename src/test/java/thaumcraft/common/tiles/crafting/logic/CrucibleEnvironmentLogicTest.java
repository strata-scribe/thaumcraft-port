package thaumcraft.common.tiles.crafting.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CrucibleEnvironmentLogic Tests")
class CrucibleEnvironmentLogicTest {

    // =========================================================================
    // Heat Source Tests
    // =========================================================================

    @Test
    @DisplayName("Always-active heat sources provide heat regardless of isLit flag")
    void testAlwaysActiveHeatSources() {
        String[] sources = {
            "minecraft:fire",
            "minecraft:soul_fire",
            "minecraft:lava",
            "minecraft:magma_block",
            "thaumcraft:nitor"
        };

        for (String blockId : sources) {
            assertTrue(CrucibleEnvironmentLogic.isHeatSource(blockId, true),
                    blockId + " should provide heat when lit");
            assertTrue(CrucibleEnvironmentLogic.isHeatSource(blockId, false),
                    blockId + " should provide heat even if lit flag is false");
            assertTrue(CrucibleEnvironmentLogic.isHeatSource(blockId),
                    blockId + " should provide heat with single-argument overload");
        }
    }

    @Test
    @DisplayName("Campfires only provide heat when lit")
    void testCampfireHeatSources() {
        String[] campfires = {
            "minecraft:campfire",
            "minecraft:soul_campfire"
        };

        for (String blockId : campfires) {
            assertTrue(CrucibleEnvironmentLogic.isHeatSource(blockId, true),
                    blockId + " should provide heat when lit");
            assertFalse(CrucibleEnvironmentLogic.isHeatSource(blockId, false),
                    blockId + " must not provide heat when unlit");
            assertTrue(CrucibleEnvironmentLogic.isHeatSource(blockId),
                    blockId + " single-argument overload defaults to lit=true");
        }
    }

    @Test
    @DisplayName("Non-heat blocks return false")
    void testNonHeatSources() {
        String[] nonHeat = {
            "minecraft:dirt",
            "minecraft:stone",
            "minecraft:water",
            "minecraft:air",
            "thaumcraft:crucible"
        };

        for (String blockId : nonHeat) {
            assertFalse(CrucibleEnvironmentLogic.isHeatSource(blockId, true));
            assertFalse(CrucibleEnvironmentLogic.isHeatSource(blockId, false));
            assertFalse(CrucibleEnvironmentLogic.isHeatSource(blockId));
        }
    }

    @Test
    @DisplayName("Null blockId returns false safely without throwing")
    void testNullBlockId() {
        assertFalse(CrucibleEnvironmentLogic.isHeatSource(null, true));
        assertFalse(CrucibleEnvironmentLogic.isHeatSource(null, false));
        assertFalse(CrucibleEnvironmentLogic.isHeatSource(null));
    }

    // =========================================================================
    // Bucket Fill and Drain Tests
    // =========================================================================

    @Test
    @DisplayName("Bucket fill: empty crucible can be filled to capacity")
    void testBucketFillEmpty() {
        assertTrue(CrucibleEnvironmentLogic.canFillWithBucket(0));
        assertEquals(1000, CrucibleEnvironmentLogic.fillWithBucket(0));
    }

    @Test
    @DisplayName("Bucket fill: partially full crucible can be filled and clamps to capacity")
    void testBucketFillPartial() {
        assertTrue(CrucibleEnvironmentLogic.canFillWithBucket(500));
        assertEquals(1000, CrucibleEnvironmentLogic.fillWithBucket(500));

        assertTrue(CrucibleEnvironmentLogic.canFillWithBucket(999));
        assertEquals(1000, CrucibleEnvironmentLogic.fillWithBucket(999));
    }

    @Test
    @DisplayName("Bucket fill: completely full crucible cannot be filled further")
    void testBucketFillFull() {
        assertFalse(CrucibleEnvironmentLogic.canFillWithBucket(1000));
        assertEquals(1000, CrucibleEnvironmentLogic.fillWithBucket(1000));

        assertFalse(CrucibleEnvironmentLogic.canFillWithBucket(1200));
        assertEquals(1000, CrucibleEnvironmentLogic.fillWithBucket(1200));
    }

    @Test
    @DisplayName("Bucket drain: full crucible with zero aspects drains successfully")
    void testBucketDrainFullNoAspects() {
        assertTrue(CrucibleEnvironmentLogic.canDrainWithBucket(1000, 0));
        assertEquals(0, CrucibleEnvironmentLogic.drainWithBucket(1000));
    }

    @Test
    @DisplayName("Bucket drain: crucible with aspects cannot be drained with bucket")
    void testBucketDrainWithAspects() {
        assertFalse(CrucibleEnvironmentLogic.canDrainWithBucket(1000, 1),
                "Crucible containing aspects must not allow draining clean water");
        assertFalse(CrucibleEnvironmentLogic.canDrainWithBucket(1000, 50));
    }

    @Test
    @DisplayName("Bucket drain: crucible with less than 1000 mB cannot be drained with bucket")
    void testBucketDrainInsufficientWater() {
        assertFalse(CrucibleEnvironmentLogic.canDrainWithBucket(0, 0));
        assertFalse(CrucibleEnvironmentLogic.canDrainWithBucket(333, 0));
        assertFalse(CrucibleEnvironmentLogic.canDrainWithBucket(999, 0));
        assertEquals(0, CrucibleEnvironmentLogic.drainWithBucket(500), "Draining below 1000 clamps to 0");
    }

    // =========================================================================
    // Bottle Fill and Drain Tests
    // =========================================================================

    @Test
    @DisplayName("Bottle fill: can fill at 0, 333, 666, but not when exceeding 1000")
    void testBottleFillProgression() {
        // At 0 mB
        assertTrue(CrucibleEnvironmentLogic.canFillWithBottle(0));
        assertEquals(333, CrucibleEnvironmentLogic.fillWithBottle(0));

        // At 333 mB
        assertTrue(CrucibleEnvironmentLogic.canFillWithBottle(333));
        assertEquals(666, CrucibleEnvironmentLogic.fillWithBottle(333));

        // At 666 mB
        assertTrue(CrucibleEnvironmentLogic.canFillWithBottle(666));
        assertEquals(999, CrucibleEnvironmentLogic.fillWithBottle(666));

        // At 667 mB (667 + 333 = 1000)
        assertTrue(CrucibleEnvironmentLogic.canFillWithBottle(667));
        assertEquals(1000, CrucibleEnvironmentLogic.fillWithBottle(667));

        // At 999 mB -> cannot fit another bottle (999 + 333 > 1000)
        assertFalse(CrucibleEnvironmentLogic.canFillWithBottle(999));

        // At 1000 mB -> cannot fit another bottle
        assertFalse(CrucibleEnvironmentLogic.canFillWithBottle(1000));
    }

    @Test
    @DisplayName("Bottle drain: respects 333 mB threshold and aspect presence")
    void testBottleDrain() {
        // Without aspects
        assertFalse(CrucibleEnvironmentLogic.canDrainWithBottle(0, 0));
        assertFalse(CrucibleEnvironmentLogic.canDrainWithBottle(332, 0));
        assertTrue(CrucibleEnvironmentLogic.canDrainWithBottle(333, 0));
        assertEquals(0, CrucibleEnvironmentLogic.drainWithBottle(333));

        assertTrue(CrucibleEnvironmentLogic.canDrainWithBottle(666, 0));
        assertEquals(333, CrucibleEnvironmentLogic.drainWithBottle(666));

        assertTrue(CrucibleEnvironmentLogic.canDrainWithBottle(999, 0));
        assertEquals(666, CrucibleEnvironmentLogic.drainWithBottle(999));

        assertTrue(CrucibleEnvironmentLogic.canDrainWithBottle(1000, 0));
        assertEquals(667, CrucibleEnvironmentLogic.drainWithBottle(1000));

        // With aspects present -> draining is blocked
        assertFalse(CrucibleEnvironmentLogic.canDrainWithBottle(333, 1));
        assertFalse(CrucibleEnvironmentLogic.canDrainWithBottle(666, 10));
        assertFalse(CrucibleEnvironmentLogic.canDrainWithBottle(1000, 500));
    }

    // =========================================================================
    // Boiling and Smelting Threshold Tests
    // =========================================================================

    @Test
    @DisplayName("Boiling threshold edge cases (heat 150 vs 151, water 0 vs >0)")
    void testBoilingConditions() {
        // Heat 0
        assertFalse(CrucibleEnvironmentLogic.isBoiling((short) 0, 0));
        assertFalse(CrucibleEnvironmentLogic.isBoiling((short) 0, 1000));
        assertFalse(CrucibleEnvironmentLogic.canSmelt((short) 0, 1000));

        // Heat 150 (one below threshold)
        assertFalse(CrucibleEnvironmentLogic.isBoiling((short) 150, 1000));
        assertFalse(CrucibleEnvironmentLogic.isBoiling((short) 150, 0));
        assertFalse(CrucibleEnvironmentLogic.canSmelt((short) 150, 1000));

        // Heat 151 with 0 water
        assertFalse(CrucibleEnvironmentLogic.isBoiling((short) 151, 0));
        assertFalse(CrucibleEnvironmentLogic.canSmelt((short) 151, 0));

        // Heat 151 with >0 water (threshold met!)
        assertTrue(CrucibleEnvironmentLogic.isBoiling((short) 151, 1));
        assertTrue(CrucibleEnvironmentLogic.isBoiling((short) 151, 1000));
        assertTrue(CrucibleEnvironmentLogic.canSmelt((short) 151, 1));
        assertTrue(CrucibleEnvironmentLogic.canSmelt((short) 151, 1000));

        // Higher heat levels
        assertTrue(CrucibleEnvironmentLogic.isBoiling((short) 200, 1000));
        assertTrue(CrucibleEnvironmentLogic.isBoiling((short) 225, 500));
        assertTrue(CrucibleEnvironmentLogic.canSmelt((short) 200, 1000));
        assertTrue(CrucibleEnvironmentLogic.canSmelt((short) 225, 500));
    }

    // =========================================================================
    // Living Entity Damage Tests
    // =========================================================================

    @Test
    @DisplayName("Living entity hurt condition: non-fire-immune takes damage only when boiling with water")
    void testLivingEntityHurtConditions() {
        // Cold or no water -> no hurt
        assertFalse(CrucibleEnvironmentLogic.shouldHurtLivingEntity((short) 100, 1000, false));
        assertFalse(CrucibleEnvironmentLogic.shouldHurtLivingEntity((short) 150, 1000, false));
        assertFalse(CrucibleEnvironmentLogic.shouldHurtLivingEntity((short) 151, 0, false));
        assertFalse(CrucibleEnvironmentLogic.shouldHurtLivingEntity((short) 200, 0, false));

        // Boiling with water, non-immune -> hurt
        assertTrue(CrucibleEnvironmentLogic.shouldHurtLivingEntity((short) 151, 1, false));
        assertTrue(CrucibleEnvironmentLogic.shouldHurtLivingEntity((short) 151, 1000, false));
        assertTrue(CrucibleEnvironmentLogic.shouldHurtLivingEntity((short) 200, 1000, false));

        // Boiling with water, fire-immune -> safe (no hurt)
        assertFalse(CrucibleEnvironmentLogic.shouldHurtLivingEntity((short) 151, 1000, true));
        assertFalse(CrucibleEnvironmentLogic.shouldHurtLivingEntity((short) 200, 1000, true));
    }
}
