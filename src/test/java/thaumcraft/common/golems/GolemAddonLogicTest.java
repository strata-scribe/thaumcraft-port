package thaumcraft.common.golems;

import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class GolemAddonLogicTest {

    @Test
    public void testNullAddons() {
        assertEquals(0.0, GolemAddonLogic.getArmorBonus(null), "Null addons should give 0 armor bonus");
        assertFalse(GolemAddonLogic.hasVisReactor(null), "Null addons should not have Vis Reactor");
        assertEquals(0, GolemAddonLogic.getInventorySlotBonus(null), "Null addons should give 0 inventory slots");
        assertFalse(GolemAddonLogic.hasFez(null), "Null addons should not have Fez");
        assertFalse(GolemAddonLogic.hasTopHat(null), "Null addons should not have Top Hat");
    }

    @Test
    public void testEmptyAddons() {
        Set<String> addons = Collections.emptySet();
        assertEquals(0.0, GolemAddonLogic.getArmorBonus(addons), "Empty addons should give 0 armor bonus");
        assertFalse(GolemAddonLogic.hasVisReactor(addons), "Empty addons should not have Vis Reactor");
        assertEquals(0, GolemAddonLogic.getInventorySlotBonus(addons), "Empty addons should give 0 inventory slots");
        assertFalse(GolemAddonLogic.hasFez(addons), "Empty addons should not have Fez");
        assertFalse(GolemAddonLogic.hasTopHat(addons), "Empty addons should not have Top Hat");
    }

    @Test
    public void testArmorBonus() {
        Set<String> addons = new HashSet<>();
        addons.add("armor");
        assertEquals(4.0, GolemAddonLogic.getArmorBonus(addons), "Armor addon should give +4 armor");
        assertEquals(0, GolemAddonLogic.getInventorySlotBonus(addons), "Armor addon should not give inventory slots");
    }

    @Test
    public void testVisReactor() {
        Set<String> addons = new HashSet<>();
        addons.add("vis_reactor");
        assertTrue(GolemAddonLogic.hasVisReactor(addons), "vis_reactor addon should provide Vis Reactor");
        assertFalse(GolemAddonLogic.hasFez(addons), "vis_reactor addon should not provide Fez");
    }

    @Test
    public void testBackpack() {
        Set<String> addons = new HashSet<>();
        addons.add("backpack");
        assertEquals(1, GolemAddonLogic.getInventorySlotBonus(addons), "Backpack addon should give +1 inventory slot");
        assertEquals(0.0, GolemAddonLogic.getArmorBonus(addons), "Backpack addon should not give armor");
    }

    @Test
    public void testFez() {
        Set<String> addons = new HashSet<>();
        addons.add("fez");
        assertTrue(GolemAddonLogic.hasFez(addons), "fez addon should provide Fez");
        assertFalse(GolemAddonLogic.hasTopHat(addons), "fez addon should not provide Top Hat");
    }

    @Test
    public void testTopHat() {
        Set<String> addons = new HashSet<>();
        addons.add("top_hat");
        assertTrue(GolemAddonLogic.hasTopHat(addons), "top_hat addon should provide Top Hat");
        assertFalse(GolemAddonLogic.hasFez(addons), "top_hat addon should not provide Fez");
    }

    @Test
    public void testMultipleAddons() {
        Set<String> addons = new HashSet<>();
        addons.add("armor");
        addons.add("backpack");
        addons.add("fez");

        assertEquals(4.0, GolemAddonLogic.getArmorBonus(addons), "Multiple addons should evaluate armor correctly");
        assertEquals(1, GolemAddonLogic.getInventorySlotBonus(addons), "Multiple addons should evaluate inventory slots correctly");
        assertTrue(GolemAddonLogic.hasFez(addons), "Multiple addons should evaluate fez correctly");
        assertFalse(GolemAddonLogic.hasVisReactor(addons), "Multiple addons should not contain missing addons");
        assertFalse(GolemAddonLogic.hasTopHat(addons), "Multiple addons should not contain missing addons");
    }
}
