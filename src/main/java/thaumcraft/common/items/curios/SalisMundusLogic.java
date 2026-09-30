package thaumcraft.common.items.curios;

/**
 * Decoupled transformation logic for Salis Mundus.
 * Pure Java logic with zero Minecraft / NeoForge registry runtime dependencies.
 */
public class SalisMundusLogic {

    public enum TransformationTarget {
        NONE,
        TRANSFORM_WORKBENCH,
        TRANSFORM_CRUCIBLE,
        TRANSFORM_THAUMONOMICON
    }

    public static final String CRAFTING_TABLE = "minecraft:crafting_table";
    public static final String CAULDRON = "minecraft:cauldron";
    public static final String BOOKSHELF = "minecraft:bookshelf";

    public static TransformationTarget getTarget(String blockId) {
        if (blockId == null) {
            return TransformationTarget.NONE;
        }

        return switch (blockId) {
            case CRAFTING_TABLE, "crafting_table" -> TransformationTarget.TRANSFORM_WORKBENCH;
            case CAULDRON, "cauldron" -> TransformationTarget.TRANSFORM_CRUCIBLE;
            case BOOKSHELF, "bookshelf" -> TransformationTarget.TRANSFORM_THAUMONOMICON;
            default -> TransformationTarget.NONE;
        };
    }
}
