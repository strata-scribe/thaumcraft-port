package thaumcraft.common.golems.logic;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class GolemTraitMatrixLogicTest {

    @Test
    public void testResolveOpposingTraits() {
        // Test Deft vs Clumsy
        Set<String> traits1 = new HashSet<>(Arrays.asList("deft", "clumsy", "smart"));
        Set<String> resolved1 = GolemTraitMatrixLogic.resolveOpposingTraits(traits1);
        assertFalse(resolved1.contains("deft"));
        assertFalse(resolved1.contains("clumsy"));
        assertTrue(resolved1.contains("smart"));

        // Test Heavy vs Light
        Set<String> traits2 = new HashSet<>(Arrays.asList("heavy", "light", "fighter"));
        Set<String> resolved2 = GolemTraitMatrixLogic.resolveOpposingTraits(traits2);
        assertFalse(resolved2.contains("heavy"));
        assertFalse(resolved2.contains("light"));
        assertTrue(resolved2.contains("fighter"));

        // Test Fragile vs Armored
        Set<String> traits3 = new HashSet<>(Arrays.asList("fragile", "armored", "brutal"));
        Set<String> resolved3 = GolemTraitMatrixLogic.resolveOpposingTraits(traits3);
        assertFalse(resolved3.contains("fragile"));
        assertFalse(resolved3.contains("armored"));
        assertTrue(resolved3.contains("brutal"));

        // Test All Together
        Set<String> traits4 = new HashSet<>(Arrays.asList("fragile", "armored", "heavy", "light", "deft", "clumsy", "repair"));
        Set<String> resolved4 = GolemTraitMatrixLogic.resolveOpposingTraits(traits4);
        assertEquals(1, resolved4.size());
        assertTrue(resolved4.contains("repair"));

        // Test Empty / Null
        assertTrue(GolemTraitMatrixLogic.resolveOpposingTraits(null).isEmpty());
        assertTrue(GolemTraitMatrixLogic.resolveOpposingTraits(new HashSet<>()).isEmpty());
    }

    @Test
    public void testCalculateAttributes() {
        double baseHealth = 10.0;
        double baseArmor = 0.0;
        double baseSpeed = 0.3;
        double baseDamage = 1.0;
        double baseKnockbackRes = 0.0;

        // Test No Traits
        GolemTraitMatrixLogic.GolemAttributes attr1 = GolemTraitMatrixLogic.calculateAttributes(
                baseHealth, baseArmor, baseSpeed, baseDamage, baseKnockbackRes, new HashSet<>()
        );
        assertEquals(baseHealth, attr1.health, 0.01);
        assertEquals(baseArmor, attr1.armor, 0.01);
        assertEquals(baseSpeed, attr1.speed, 0.01);
        assertEquals(baseDamage, attr1.damage, 0.01);
        assertEquals(baseKnockbackRes, attr1.knockbackRes, 0.01);

        // Test Light & Fragile
        GolemTraitMatrixLogic.GolemAttributes attr2 = GolemTraitMatrixLogic.calculateAttributes(
                baseHealth, baseArmor, baseSpeed, baseDamage, baseKnockbackRes, new HashSet<>(Arrays.asList("light", "fragile"))
        );
        assertEquals(7.5, attr2.health, 0.01); // 10 * 0.75
        assertEquals(0.36, attr2.speed, 0.01); // 0.3 * 1.2
        assertEquals(baseArmor, attr2.armor, 0.01);
        assertEquals(baseDamage, attr2.damage, 0.01);
        assertEquals(baseKnockbackRes, attr2.knockbackRes, 0.01);

        // Test Heavy, Armored, Brutal
        GolemTraitMatrixLogic.GolemAttributes attr3 = GolemTraitMatrixLogic.calculateAttributes(
                baseHealth, baseArmor, baseSpeed, baseDamage, baseKnockbackRes, new HashSet<>(Arrays.asList("heavy", "armored", "brutal"))
        );
        assertEquals(baseHealth, attr3.health, 0.01);
        assertEquals(4.0, attr3.armor, 0.01); // 0 + 4
        assertEquals(0.24, attr3.speed, 0.01); // 0.3 * 0.8
        assertEquals(3.0, attr3.damage, 0.01); // 1.0 + 2
        assertEquals(0.5, attr3.knockbackRes, 0.01); // 0 + 0.5

        // Test Opposing Traits Cancellation
        // heavy + light = speed unchanged, knockback unchanged
        // fragile + armored = health unchanged, armor unchanged
        GolemTraitMatrixLogic.GolemAttributes attr4 = GolemTraitMatrixLogic.calculateAttributes(
                baseHealth, baseArmor, baseSpeed, baseDamage, baseKnockbackRes, new HashSet<>(Arrays.asList("heavy", "light", "fragile", "armored"))
        );
        assertEquals(baseHealth, attr4.health, 0.01);
        assertEquals(baseArmor, attr4.armor, 0.01);
        assertEquals(baseSpeed, attr4.speed, 0.01);
        assertEquals(baseDamage, attr4.damage, 0.01);
        assertEquals(baseKnockbackRes, attr4.knockbackRes, 0.01);
    }
}
