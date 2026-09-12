package thaumcraft.data.tags;

import java.util.ArrayList;
import java.util.List;

public class ThaumcraftBlockTagProviderLogic {

    public static boolean isOre(String blockName) {
        return blockName != null && blockName.toLowerCase().contains("ore");
    }

    public static boolean isLog(String blockName) {
        if (blockName == null) return false;
        String lower = blockName.toLowerCase();
        return (lower.contains("log") || lower.contains("wood")) && !isLeaves(lower);
    }

    public static boolean isLeaves(String blockName) {
        return blockName != null && blockName.toLowerCase().contains("leaves");
    }

    public static boolean isStone(String blockName) {
        if (blockName == null) return false;
        String lower = blockName.toLowerCase();
        return lower.contains("stone") || lower.contains("rock");
    }

    public static boolean isWarded(String blockName) {
        return blockName != null && blockName.toLowerCase().contains("warded");
    }

    public static List<String> generateTagsForBlock(String blockName) {
        List<String> tags = new ArrayList<>();
        if (blockName == null) return tags;

        if (isOre(blockName)) tags.add("forge:ores");
        if (isLog(blockName)) tags.add("minecraft:logs");
        if (isLeaves(blockName)) tags.add("minecraft:leaves");
        if (isStone(blockName)) tags.add("forge:stone");
        if (isWarded(blockName)) tags.add("thaumcraft:warded");

        return tags;
    }
}
