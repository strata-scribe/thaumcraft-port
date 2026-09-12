package thaumcraft.common.golems.seals;

import java.util.Set;

/**
 * Pure Java logic for evaluating targets for the Guard Seal.
 * Decoupled from Minecraft and Forge imports for unit testing.
 */
public final class SealGuardTargetLogic {

    private SealGuardTargetLogic() {}

    public enum EntityCategory {
        MONSTER,
        ANIMAL,
        PLAYER,
        OTHER
    }

    public record EntityInfo(String id, EntityCategory category, Set<String> tags) {}

    /**
     * Evaluates if a given entity is a valid target based on hostility, mob type, and whitelisted tags.
     *
     * @param entity          The entity information to check.
     * @param targetMonsters  Whether monsters (hostiles) are targeted.
     * @param targetAnimals   Whether animals are targeted.
     * @param targetPlayers   Whether players are targeted.
     * @param whitelistedTags A set of entity tags that are always targeted, regardless of category.
     * @return true if the entity should be targeted, false otherwise.
     */
    public static boolean isValidTarget(
            EntityInfo entity,
            boolean targetMonsters,
            boolean targetAnimals,
            boolean targetPlayers,
            Set<String> whitelistedTags) {

        if (entity == null) {
            return false;
        }

        // Check if the entity has any of the whitelisted tags
        if (whitelistedTags != null && entity.tags() != null) {
            for (String tag : whitelistedTags) {
                if (entity.tags().contains(tag)) {
                    return true;
                }
            }
        }

        // Fallback to category checking
        if (entity.category() != null) {
            return switch (entity.category()) {
                case MONSTER -> targetMonsters;
                case ANIMAL -> targetAnimals;
                case PLAYER -> targetPlayers;
                case OTHER -> false;
            };
        }

        return false;
    }
}
