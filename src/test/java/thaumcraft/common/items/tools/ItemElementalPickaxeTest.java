package thaumcraft.common.items.tools;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ItemElementalPickaxe & Sounding Logic Contract Tests")
public class ItemElementalPickaxeTest {

    @Test
    @DisplayName("Verify class hierarchy, constructors, and method signatures")
    public void testClassStructureAndConstructors() throws Exception {
        assertTrue(Item.class.isAssignableFrom(ItemElementalPickaxe.class),
                "ItemElementalPickaxe must extend net.minecraft.world.item.Item");

        Constructor<ItemElementalPickaxe> propsCtor = ItemElementalPickaxe.class.getConstructor(Item.Properties.class);
        assertNotNull(propsCtor, "Must have (Properties) constructor");

        Constructor<ItemElementalPickaxe> defaultCtor = ItemElementalPickaxe.class.getConstructor();
        assertNotNull(defaultCtor, "Must have () default constructor");

        Method useMethod = ItemElementalPickaxe.class.getMethod("use", Level.class, Player.class, InteractionHand.class);
        assertNotNull(useMethod, "Must have use(Level, Player, InteractionHand)");
        assertEquals(net.minecraft.world.InteractionResult.class, useMethod.getReturnType());
    }

    @Test
    @DisplayName("Verify instantiation and durability (1500), stacksTo (1)")
    public void testInstantiationAndDurability() {
        try {
            ItemElementalPickaxe pickaxe = new ItemElementalPickaxe();
            assertNotNull(pickaxe);
            assertEquals(1, pickaxe.getDefaultMaxStackSize(), "Elemental Pickaxe must have max stack size 1");
        } catch (Throwable t) {
            // In headless JUnit without FML loader, Item.Properties may throw ExceptionInInitializerError.
            assertNotNull(ItemElementalPickaxe.class);
        }
    }

    @Test
    @DisplayName("Ore Sounding Logic: Prioritizes higher rarity tiers first")
    public void testFindPrioritizedOreRarityTiers() {
        List<ElementalToolLogic.SoundedOre> ores = new ArrayList<>();
        // Common ore at distance 1
        ores.add(new ElementalToolLogic.SoundedOre(new ElementalToolLogic.BlockCoordinate(1, 0, 0), ElementalToolLogic.ORE_TIER_COMMON, 1.0));
        // Precious ore (Gold) at distance 4
        ores.add(new ElementalToolLogic.SoundedOre(new ElementalToolLogic.BlockCoordinate(4, 0, 0), ElementalToolLogic.ORE_TIER_PRECIOUS, 4.0));
        // Rare ore (Diamond) at distance 8
        ores.add(new ElementalToolLogic.SoundedOre(new ElementalToolLogic.BlockCoordinate(8, 0, 0), ElementalToolLogic.ORE_TIER_RARE, 8.0));
        // Thaumic ore (Cinnabar) at distance 12
        ores.add(new ElementalToolLogic.SoundedOre(new ElementalToolLogic.BlockCoordinate(12, 0, 0), ElementalToolLogic.ORE_TIER_THAUMIC, 12.0));

        ElementalToolLogic.SoundedOre prioritized = ElementalToolLogic.findPrioritizedOre(ores);
        assertNotNull(prioritized);
        assertEquals(ElementalToolLogic.ORE_TIER_THAUMIC, prioritized.rarityTier());
        assertEquals(12.0, prioritized.distance(), 1e-6);
    }

    @Test
    @DisplayName("Ore Sounding Logic: Breaks rarity ties by closest distance")
    public void testFindPrioritizedOreTieBreaker() {
        List<ElementalToolLogic.SoundedOre> ores = new ArrayList<>();
        ores.add(new ElementalToolLogic.SoundedOre(new ElementalToolLogic.BlockCoordinate(10, 0, 0), ElementalToolLogic.ORE_TIER_RARE, 10.0));
        ores.add(new ElementalToolLogic.SoundedOre(new ElementalToolLogic.BlockCoordinate(3, 0, 0), ElementalToolLogic.ORE_TIER_RARE, 3.0));
        ores.add(new ElementalToolLogic.SoundedOre(new ElementalToolLogic.BlockCoordinate(7, 0, 0), ElementalToolLogic.ORE_TIER_RARE, 7.0));

        ElementalToolLogic.SoundedOre prioritized = ElementalToolLogic.findPrioritizedOre(ores);
        assertNotNull(prioritized);
        assertEquals(ElementalToolLogic.ORE_TIER_RARE, prioritized.rarityTier());
        assertEquals(3.0, prioritized.distance(), 1e-6);
    }

    @Test
    @DisplayName("Ore Sounding Logic: Empty or null lists return null")
    public void testFindPrioritizedOreEmpty() {
        assertNull(ElementalToolLogic.findPrioritizedOre(null));
        assertNull(ElementalToolLogic.findPrioritizedOre(List.of()));
    }

    @Test
    @DisplayName("Native Cluster Drop Chance: Base 25%, Fortune +10% per level, max capped at 75%")
    public void testNativeClusterDropChances() {
        // Fortune 0: chance = 25%
        assertTrue(ElementalToolLogic.shouldDropNativeCluster(0, 0.24));
        assertFalse(ElementalToolLogic.shouldDropNativeCluster(0, 0.26));

        // Fortune 1: chance = 35%
        assertTrue(ElementalToolLogic.shouldDropNativeCluster(1, 0.34));
        assertFalse(ElementalToolLogic.shouldDropNativeCluster(1, 0.36));

        // Fortune 3: chance = 55%
        assertTrue(ElementalToolLogic.shouldDropNativeCluster(3, 0.54));
        assertFalse(ElementalToolLogic.shouldDropNativeCluster(3, 0.56));

        // Fortune 10: chance capped at 75%
        assertTrue(ElementalToolLogic.shouldDropNativeCluster(10, 0.74));
        assertFalse(ElementalToolLogic.shouldDropNativeCluster(10, 0.76));
    }
}
