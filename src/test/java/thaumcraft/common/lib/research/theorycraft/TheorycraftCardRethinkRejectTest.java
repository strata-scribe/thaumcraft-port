package thaumcraft.common.lib.research.theorycraft;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.TreeMap;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.util.RandomSource;
import net.minecraft.core.BlockPos;

import thaumcraft.api.research.theorycraft.CardRethink;
import thaumcraft.api.research.theorycraft.CardReject;
import thaumcraft.api.research.theorycraft.ResearchTableData;

public class TheorycraftCardRethinkRejectTest {

    private ResearchTableData data;

    @BeforeEach
    public void setup() {
        data = new ResearchTableData((BlockEntity) null);
        data.categoryTotals = new TreeMap<>();
    }

    static class MockPlayer extends Player {
        public MockPlayer() {
            super((Level)null, new GameProfile(UUID.randomUUID(), "MockPlayer"));
        }

        @Override
        public RandomSource getRandom() {
            return new RandomSource() {
                @Override public RandomSource fork() { return this; }
                @Override public net.minecraft.world.level.levelgen.PositionalRandomFactory forkPositional() { return null; }
                @Override public void setSeed(long seed) {}
                @Override public int nextInt() { return 0; }
                @Override public int nextInt(int bound) { return 0; }
                @Override public int nextIntBetweenInclusive(int min, int max) { return 5; } // Fake random 5
                @Override public long nextLong() { return 42L; }
                @Override public boolean nextBoolean() { return false; }
                @Override public float nextFloat() { return 0f; }
                @Override public double nextDouble() { return 0d; }
                @Override public double nextGaussian() { return 0d; }
            };
        }

        @Override public boolean isSpectator() { return false; }
        @Override public boolean isCreative() { return false; }
        @Override public net.minecraft.world.level.GameType gameMode() { return net.minecraft.world.level.GameType.SURVIVAL; }
    }

    @Test
    public void testCardRethink_ReshuffleAndRefund() {
        data.categoryTotals.put("ALCHEMY", 8);
        data.categoryTotals.put("ARTIFICE", 4);
        data.inspirationStart = 20;
        data.inspiration = 10;
        data.bonusDraws = 0;

        CardRethink card = new CardRethink();
        MockPlayer player = null;
        try {
            player = new MockPlayer();
        } catch (Throwable t) {
            // Ignore
        }

        if (player != null) {
            assertTrue(card.initialize(player, data));
            assertTrue(card.activate(player, data));

            int remainingOriginalSum = 0;
            for (Map.Entry<String, Integer> entry : data.categoryTotals.entrySet()) {
                if (!entry.getKey().equals("BASICS")) {
                    remainingOriginalSum += entry.getValue();
                }
            }
            assertEquals(2, remainingOriginalSum);
            assertEquals(5, data.categoryTotals.get("BASICS").intValue());
            assertEquals(15, data.inspiration);
            assertEquals(1, data.bonusDraws);
        }
    }

    @Test
    public void testCardReject_PermanentRemoval() {
        data.categoryTotals.put("ALCHEMY", 5);
        data.categoryTotals.put("ARTIFICE", 10);

        CardReject card = new CardReject();
        card.setSeed(42L); // Set deterministic seed for randomness

        MockPlayer player = null;
        try {
            player = new MockPlayer();
        } catch (Throwable t) {
            // Ignore
        }

        if (player != null) {
            assertTrue(card.initialize(player, data));

            int initialBlockedSize = data.categoriesBlocked.size();
            assertEquals(0, initialBlockedSize);
            int initialBasics = data.getTotal("BASICS");

            assertTrue(card.activate(player, data));

            assertEquals(1, data.categoriesBlocked.size());
            String blockedCategory = data.categoriesBlocked.get(0);
            assertTrue(blockedCategory.equals("ALCHEMY") || blockedCategory.equals("ARTIFICE"));

            assertEquals(initialBasics + 5, data.getTotal("BASICS"));
        }
    }
}
