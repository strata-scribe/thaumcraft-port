package thaumcraft.api.research.theorycraft;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TheorycraftAidLogic {

    public static class ScanResult {
        public final Set<String> activeAids = new HashSet<>();
        public final Map<String, Float> categoryWeights = new HashMap<>();
        public float progressMultiplier = 1.0f;
    }

    /**
     * Scans a 9x9x3 area around the given position (radius of 4 horizontally, 1 vertically)
     * to find active theorycraft aids.
     */
    public static ScanResult scanAids(Level level, BlockPos pos) {
        ScanResult result = new ScanResult();

        Set<Object> foundObjects = new HashSet<>();

        // Scan blocks in radius
        for (int x = -4; x <= 4; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -4; z <= 4; z++) {
                    BlockPos currentPos = pos.offset(x, y, z);
                    Block block = level.getBlockState(currentPos).getBlock();
                    foundObjects.add(block);
                }
            }
        }

        // Scan entities in radius
        AABB bounds = new AABB(
                pos.getX() - 4.5, pos.getY() - 1.5, pos.getZ() - 4.5,
                pos.getX() + 5.5, pos.getY() + 2.5, pos.getZ() + 5.5
        );
        List<Entity> entities = level.getEntities(null, bounds);
        for (Entity entity : entities) {
            foundObjects.add(entity.getClass());
        }

        for (Map.Entry<String, ITheorycraftAid> entry : TheorycraftManager.aids.entrySet()) {
            String key = entry.getKey();
            ITheorycraftAid aid = entry.getValue();
            Object aidObject = aid.getAidObject();

            boolean matched = false;

            if (aidObject instanceof Block && foundObjects.contains((Block) aidObject)) {
                matched = true;
            } else if (aidObject instanceof Class<?> && Entity.class.isAssignableFrom((Class<?>) aidObject)) {
                for (Object found : foundObjects) {
                    if (found instanceof Class<?> && ((Class<?>) aidObject).isAssignableFrom((Class<?>) found)) {
                        matched = true;
                        break;
                    }
                }
            } else if (aidObject instanceof ItemStack) {
                // Not ideal to test blocks against ItemStacks natively,
                // but in context this typically means we are looking for a specific block that drops this,
                // or we simulate checking block drops.
                // Assuming it's typically an item that is represented by a block.
                ItemStack stack = (ItemStack) aidObject;
                if (!stack.isEmpty() && stack.getItem() instanceof net.minecraft.world.item.BlockItem) {
                    Block b = ((net.minecraft.world.item.BlockItem) stack.getItem()).getBlock();
                    if (foundObjects.contains(b)) {
                        matched = true;
                    }
                }
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
}
