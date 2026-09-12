package thaumcraft.common.items.tools;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PrimalCrusherYieldLogic.
 */
public class PrimalCrusherYieldLogicTest {

    // Custom Stub for java.util.Random to avoid Mockito
    private static class SequentialRandom extends Random {
        private final float[] values;
        private int index = 0;

        public SequentialRandom(float... values) {
            this.values = values;
        }

        @Override
        public float nextFloat() {
            if (values.length == 0) return 0.0f;
            float val = values[index];
            index = (index + 1) % values.length;
            return val;
        }
    }

    @Test
    public void testCalculatePulverizedDustBonus_NotMetalOre() {
        Random random = new SequentialRandom(0.1f);
        int bonus = PrimalCrusherYieldLogic.calculatePulverizedDustBonus(false, 3, random);
        assertEquals(0, bonus, "Should not drop bonus dust if not a metal ore.");
    }

    @Test
    public void testCalculatePulverizedDustBonus_NoFortune_Success() {
        // nextFloat < 0.5f triggers the base chance
        Random random = new SequentialRandom(0.4f);
        int bonus = PrimalCrusherYieldLogic.calculatePulverizedDustBonus(true, 0, random);
        assertEquals(1, bonus, "Should drop 1 base dust when rng < 0.5f.");
    }

    @Test
    public void testCalculatePulverizedDustBonus_NoFortune_Fail() {
        // nextFloat >= 0.5f fails the base chance
        Random random = new SequentialRandom(0.6f);
        int bonus = PrimalCrusherYieldLogic.calculatePulverizedDustBonus(true, 0, random);
        assertEquals(0, bonus, "Should not drop dust when rng >= 0.5f.");
    }

    @Test
    public void testCalculatePulverizedDustBonus_WithFortune_AllSuccess() {
        // For Fortune 3:
        // 1 call for base chance: needs < 0.5f
        // 3 calls for fortune: needs < 0.2f
        Random random = new SequentialRandom(0.4f, 0.1f, 0.1f, 0.1f);
        int bonus = PrimalCrusherYieldLogic.calculatePulverizedDustBonus(true, 3, random);
        assertEquals(4, bonus, "Should drop 1 base dust and 3 fortune dusts.");
    }

    @Test
    public void testCalculatePulverizedDustBonus_WithFortune_PartialSuccess() {
        // For Fortune 3:
        // base chance: 0.6f (fails)
        // fortune 1: 0.1f (succeeds)
        // fortune 2: 0.3f (fails)
        // fortune 3: 0.15f (succeeds)
        Random random = new SequentialRandom(0.6f, 0.1f, 0.3f, 0.15f);
        int bonus = PrimalCrusherYieldLogic.calculatePulverizedDustBonus(true, 3, random);
        assertEquals(2, bonus, "Should drop 2 fortune dusts.");
    }


    @Test
    public void testCalculateGemShardChance_NotGemOre() {
        Random random = new SequentialRandom(0.1f);
        int shards = PrimalCrusherYieldLogic.calculateGemShardChance(false, 3, random);
        assertEquals(0, shards, "Should not drop gem shards if not a gem ore.");
    }

    @Test
    public void testCalculateGemShardChance_NoFortune_Success() {
        // nextFloat < 0.25f triggers the base chance
        Random random = new SequentialRandom(0.2f);
        int shards = PrimalCrusherYieldLogic.calculateGemShardChance(true, 0, random);
        assertEquals(1, shards, "Should drop 1 base shard when rng < 0.25f.");
    }

    @Test
    public void testCalculateGemShardChance_NoFortune_Fail() {
        // nextFloat >= 0.25f fails the base chance
        Random random = new SequentialRandom(0.3f);
        int shards = PrimalCrusherYieldLogic.calculateGemShardChance(true, 0, random);
        assertEquals(0, shards, "Should not drop shard when rng >= 0.25f.");
    }

    @Test
    public void testCalculateGemShardChance_WithFortune_AllSuccess() {
        // For Fortune 2:
        // 1 call for base chance: needs < 0.25f (e.g. 0.1f)
        // 1 call for fortune chance (2 * 0.15f = 0.3f): needs < 0.3f (e.g. 0.2f)
        Random random = new SequentialRandom(0.1f, 0.2f);
        int shards = PrimalCrusherYieldLogic.calculateGemShardChance(true, 2, random);
        assertEquals(2, shards, "Should drop 1 base shard and 1 fortune shard.");
    }

    @Test
    public void testCalculateGemShardChance_WithFortune_OnlyFortuneSuccess() {
        // For Fortune 3:
        // 1 call for base chance: needs < 0.25f (fails with 0.3f)
        // 1 call for fortune chance (3 * 0.15f = 0.45f): needs < 0.45f (succeeds with 0.4f)
        Random random = new SequentialRandom(0.3f, 0.4f);
        int shards = PrimalCrusherYieldLogic.calculateGemShardChance(true, 3, random);
        assertEquals(1, shards, "Should drop 1 fortune shard.");
    }

    @Test
    public void testCalculateGemShardChance_WithFortune_NoneSuccess() {
        // For Fortune 2:
        // base chance: needs < 0.25f (fails with 0.5f)
        // fortune chance: needs < 0.30f (fails with 0.5f)
        Random random = new SequentialRandom(0.5f, 0.5f);
        int shards = PrimalCrusherYieldLogic.calculateGemShardChance(true, 2, random);
        assertEquals(0, shards, "Should drop 0 shards.");
    }
}
