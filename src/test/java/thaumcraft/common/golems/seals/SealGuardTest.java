package thaumcraft.common.golems.seals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import net.minecraft.world.entity.LivingEntity;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;

class SealGuardTest {

    // We override isValidTarget in a TestableSealGuard to mock the internal categorization
    // without invoking target.getType().getTags() which requires Bootstrapping and throws exceptions.
    private static class TestableSealGuard extends SealGuard {
        private final SealGuardTargetLogic.EntityCategory forcedCategory;
        private final Set<String> mockTags;
        private final Set<String> mockWhitelist;

        public TestableSealGuard(SealGuardTargetLogic.EntityCategory forcedCategory, Set<String> mockTags, Set<String> mockWhitelist) {
            super();
            this.forcedCategory = forcedCategory;
            this.mockTags = mockTags;
            this.mockWhitelist = mockWhitelist;
        }

        public void setProps(boolean pmob, boolean panimal, boolean pplayer) {
            this.props[0].setValue(pmob);
            this.props[1].setValue(panimal);
            this.props[2].setValue(pplayer);
        }

        @Override
        public boolean isValidTarget(LivingEntity target) {
            SealGuardTargetLogic.EntityInfo info = new SealGuardTargetLogic.EntityInfo(
                    "mock:entity",
                    forcedCategory,
                    mockTags
            );

            return SealGuardTargetLogic.isValidTarget(
                    info,
                    props[0].getValue(),
                    props[1].getValue(),
                    props[2].getValue(),
                    mockWhitelist
            );
        }
    }

    @Test
    @DisplayName("Test target evaluation for hostile mobs")
    void testTargetMonster() {
        TestableSealGuard seal = new TestableSealGuard(SealGuardTargetLogic.EntityCategory.MONSTER, Collections.emptySet(), Collections.emptySet());

        seal.setProps(true, false, false);
        assertTrue(seal.isValidTarget(null));

        seal.setProps(false, false, false);
        assertFalse(seal.isValidTarget(null));
    }

    @Test
    @DisplayName("Test target evaluation for animals")
    void testTargetAnimal() {
        TestableSealGuard seal = new TestableSealGuard(SealGuardTargetLogic.EntityCategory.ANIMAL, Collections.emptySet(), Collections.emptySet());

        seal.setProps(false, true, false);
        assertTrue(seal.isValidTarget(null));

        seal.setProps(false, false, false);
        assertFalse(seal.isValidTarget(null));
    }

    @Test
    @DisplayName("Test target evaluation for players")
    void testTargetPlayer() {
        TestableSealGuard seal = new TestableSealGuard(SealGuardTargetLogic.EntityCategory.PLAYER, Collections.emptySet(), Collections.emptySet());

        seal.setProps(false, false, true);
        assertTrue(seal.isValidTarget(null));

        seal.setProps(false, false, false);
        assertFalse(seal.isValidTarget(null));
    }

    @Test
    @DisplayName("Test whitelist/blacklist entity filter matching")
    void testWhitelistFilter() {
        Set<String> tags = new HashSet<>();
        tags.add("minecraft:raider");

        Set<String> whitelist = new HashSet<>();
        whitelist.add("minecraft:raider");

        TestableSealGuard seal = new TestableSealGuard(SealGuardTargetLogic.EntityCategory.OTHER, tags, whitelist);

        seal.setProps(false, false, false); // No categories enabled

        // Entity should still be targeted because it matches the whitelist
        assertTrue(seal.isValidTarget(null));

        // Let's test a non-matching entity
        TestableSealGuard sealNoMatch = new TestableSealGuard(SealGuardTargetLogic.EntityCategory.OTHER, Collections.emptySet(), whitelist);
        sealNoMatch.setProps(false, false, false);
        assertFalse(sealNoMatch.isValidTarget(null));
    }

    @Test
    @DisplayName("Test redstone deactivation logic")
    void testRedstoneDeactivation() {
        TestableSealGuard seal = new TestableSealGuard(SealGuardTargetLogic.EntityCategory.OTHER, Collections.emptySet(), Collections.emptySet());

        // Create a mock ISealEntity that is stopped by redstone
        thaumcraft.api.golems.seals.ISealEntity sealEntityStopped = new thaumcraft.api.golems.seals.ISealEntity() {
            @Override public void tickSealEntity(net.minecraft.world.level.Level world) {}
            @Override public thaumcraft.api.golems.seals.ISeal getSeal() { return null; }
            @Override public thaumcraft.api.golems.seals.SealPos getSealPos() { return null; }
            @Override public byte getPriority() { return 0; }
            @Override public void setPriority(byte priority) {}
            @Override public void readNBT(net.minecraft.nbt.CompoundTag nbt) {}
            @Override public net.minecraft.nbt.CompoundTag writeNBT() { return null; }
            @Override public void syncToClient(net.minecraft.world.level.Level world) {}
            @Override public net.minecraft.core.BlockPos getArea() { return null; }
            @Override public void setArea(net.minecraft.core.BlockPos v) {}
            @Override public boolean isLocked() { return false; }
            @Override public void setLocked(boolean locked) {}
            @Override public boolean isRedstoneSensitive() { return true; }
            @Override public void setRedstoneSensitive(boolean redstone) {}
            @Override public String getOwner() { return null; }
            @Override public void setOwner(String owner) {}
            @Override public byte getColor() { return 0; }
            @Override public void setColor(byte color) {}
            @Override public boolean isStoppedByRedstone(net.minecraft.world.level.Level world) {
                return true;
            }
        };

        // If the redstone deactivation works, tickSeal will return early without trying to access world or create tasks
        // passing null as world. If it doesn't return early, it will throw a NullPointerException.
        assertDoesNotThrow(() -> seal.tickSeal(null, sealEntityStopped));
    }
}
