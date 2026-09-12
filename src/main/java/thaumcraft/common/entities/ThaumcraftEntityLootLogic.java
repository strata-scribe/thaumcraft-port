package thaumcraft.common.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Pure Java logic helper for entity loot mechanics, such as drop tables.
 * Strict mathematical decoupling: contains zero Minecraft or NeoForge classloader imports.
 */
public class ThaumcraftEntityLootLogic {

    /**
     * Calculates the drops for a Pech entity.
     * Always drops 1 mana_bean. Drops 1 pech_wand if randomValue > 0.5.
     *
     * @param randomValue The RNG value (0.0 to 1.0).
     * @param lootingLevel The looting enchantment level.
     * @return A list of strings representing the item drops.
     */
    public static List<String> calculatePechDrops(double randomValue, int lootingLevel) {
        List<String> drops = new ArrayList<>();
        drops.add("mana_bean");
        if (randomValue > 0.5) {
            drops.add("pech_wand");
        }
        return drops;
    }

    /**
     * Calculates the drops for a Crimson Cultist entity.
     * Drops 1 crimson_rites if randomValue > 0.8.
     * Drops iron_ingot equal to 1 + lootingLevel.
     *
     * @param randomValue The RNG value (0.0 to 1.0).
     * @param lootingLevel The looting enchantment level.
     * @return A list of strings representing the item drops.
     */
    public static List<String> calculateCrimsonCultistDrops(double randomValue, int lootingLevel) {
        List<String> drops = new ArrayList<>();
        if (randomValue > 0.8) {
            drops.add("crimson_rites");
        }
        int ingotCount = 1 + lootingLevel;
        for (int i = 0; i < ingotCount; i++) {
            drops.add("iron_ingot");
        }
        return drops;
    }

    /**
     * Calculates the drops for a Wisp entity.
     * Drops 1 auram_vis_crystal if randomValue > 0.4.
     *
     * @param randomValue The RNG value (0.0 to 1.0).
     * @param lootingLevel The looting enchantment level.
     * @return A list of strings representing the item drops.
     */
    public static List<String> calculateWispDrops(double randomValue, int lootingLevel) {
        List<String> drops = new ArrayList<>();
        if (randomValue > 0.4) {
            drops.add("auram_vis_crystal");
        }
        return drops;
    }

    /**
     * Calculates the drops for an Eldritch Guardian entity.
     * Drops 1 eldritch_eye if randomValue > 0.3.
     * Always drops 1 void_seed.
     *
     * @param randomValue The RNG value (0.0 to 1.0).
     * @param lootingLevel The looting enchantment level.
     * @return A list of strings representing the item drops.
     */
    public static List<String> calculateEldritchGuardianDrops(double randomValue, int lootingLevel) {
        List<String> drops = new ArrayList<>();
        if (randomValue > 0.3) {
            drops.add("eldritch_eye");
        }
        drops.add("void_seed");
        return drops;
    }
}
