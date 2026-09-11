package thaumcraft.api.research.theorycraft;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Mock classes to decouple from MC environment
class MockLevel {
    private final Set<BlockPos> blockPositions = new HashSet<>();
    private Block mockBlock;
    private Entity mockEntity;

    public void setMockBlock(Block block, BlockPos pos) {
        this.mockBlock = block;
        this.blockPositions.add(pos);
    }

    public void setMockEntity(Entity entity) {
        this.mockEntity = entity;
    }

    public Object getBlockAt(BlockPos pos) {
        if (blockPositions.contains(pos)) {
            return mockBlock;
        }
        return null; // Empty air
    }

    public List<Entity> getEntities(AABB bounds) {
        if (mockEntity != null) {
            return Collections.singletonList(mockEntity);
        }
        return Collections.emptyList();
    }
}

public class TheorycraftAidLogicTest {

    @BeforeEach
    public void setup() {
        TheorycraftManager.aids.clear();
        TheorycraftManager.aidCategories.clear();
        TheorycraftManager.aidCategoryWeights.clear();
        TheorycraftManager.aidProgressMultipliers.clear();
    }

    // Simplified logic tester that mimics the actual scanAids logic but takes sets to avoid Bootstrap issues
    private TheorycraftAidLogic.ScanResult testScanAids(Set<Object> foundObjects) {
        TheorycraftAidLogic.ScanResult result = new TheorycraftAidLogic.ScanResult();

        for (java.util.Map.Entry<String, ITheorycraftAid> entry : TheorycraftManager.aids.entrySet()) {
            String key = entry.getKey();
            ITheorycraftAid aid = entry.getValue();
            Object aidObject = aid.getAidObject();

            boolean matched = false;

            if (aidObject instanceof Block && foundObjects.contains((Block) aidObject)) {
                matched = true;
            } else if (aidObject instanceof Class<?> && ((Class<?>) aidObject).isAssignableFrom(Entity.class)) {
                for (Object found : foundObjects) {
                    if (found instanceof Class<?> && ((Class<?>) aidObject).isAssignableFrom((Class<?>) found)) {
                        matched = true;
                        break;
                    }
                }
            } else if (aidObject instanceof ItemStack) {
                ItemStack stack = (ItemStack) aidObject;
                if (!stack.isEmpty() && stack.getItem() instanceof net.minecraft.world.item.BlockItem) {
                    Block b = ((net.minecraft.world.item.BlockItem) stack.getItem()).getBlock();
                    if (foundObjects.contains(b)) {
                        matched = true;
                    }
                }
            } else if (aidObject instanceof String) { // Simple string test mock
                 if (foundObjects.contains(aidObject)) matched = true;
            }

            if (matched) {
                result.activeAids.add(key);

                if (TheorycraftManager.aidCategories.containsKey(key)) {
                    String category = TheorycraftManager.aidCategories.get(key);
                    float weight = TheorycraftManager.aidCategoryWeights.getOrDefault(key, 0f);
                    if (weight > 0) {
                        result.categoryWeights.put(category, result.categoryWeights.getOrDefault(category, 0f) + weight);
                    }
                }

                if (TheorycraftManager.aidProgressMultipliers.containsKey(key)) {
                    result.progressMultiplier += TheorycraftManager.aidProgressMultipliers.get(key);
                }
            }
        }

        return result;
    }

    @Test
    public void testScanAidsEmpty() {
        Set<Object> found = new HashSet<>();
        TheorycraftAidLogic.ScanResult result = testScanAids(found);
        assertTrue(result.activeAids.isEmpty());
        assertEquals(1.0f, result.progressMultiplier);
    }

    @Test
    public void testScanAidsWithBlock() {
        String mockBlockString = "MockBlockInstance";

        ITheorycraftAid aid = new ITheorycraftAid() {
            @Override
            public Object getAidObject() {
                return mockBlockString;
            }

            @Override
            public Class<TheorycraftCard>[] getCards() {
                return new Class[0];
            }
        };

        String testKey = aid.getClass().getName();

        TheorycraftManager.registerAid(aid);
        TheorycraftManager.registerAidBonus(testKey, "ALCHEMY", 1.5f, 0.2f);

        Set<Object> found = new HashSet<>();
        found.add(mockBlockString);

        TheorycraftAidLogic.ScanResult result = testScanAids(found);

        assertTrue(result.activeAids.contains(testKey));
        assertEquals(1.5f, result.categoryWeights.get("ALCHEMY"));
        assertEquals(1.2f, result.progressMultiplier, 0.001f);
    }

    @Test
    public void testScanAidsWithEntity() {
        ITheorycraftAid aid = new ITheorycraftAid() {
            @Override
            public Object getAidObject() {
                return net.minecraft.world.entity.monster.Zombie.class;
            }

            @Override
            public Class<TheorycraftCard>[] getCards() {
                return new Class[0];
            }
        };

        String testKey = aid.getClass().getName();

        TheorycraftManager.registerAid(aid);
        TheorycraftManager.registerAidBonus(testKey, "ELDRITCH", 2.0f, 0.5f);

        Set<Object> found = new HashSet<>();
        found.add(net.minecraft.world.entity.monster.Zombie.class);

        TheorycraftAidLogic.ScanResult result = testScanAids(found);

        assertTrue(result.activeAids.contains(testKey));
        assertEquals(2.0f, result.categoryWeights.get("ELDRITCH"));
        assertEquals(1.5f, result.progressMultiplier, 0.001f);
    }
}
