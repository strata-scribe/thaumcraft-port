package thaumcraft.common.golems;

import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GolemMaterialScalingLogicTest {

    @Test
    public void testStrawScaling() {
        Set<String> traits = new HashSet<>(Arrays.asList("light", "fragile")); // standard straw traits usually
        GolemMaterialScalingLogic.CalculatedAttributes calc = GolemMaterialScalingLogic.calculateAttributes("STRAW", -4, 0, 0, traits);

        // baseHealth=10, matHealth=-4 -> 6. fragile: 6*0.75=4.5. straw: 4.5*0.8=3.6
        assertEquals(3.6, calc.maxHealth, 0.001);

        // baseSpeed=0.3. light: 0.3*1.2=0.36. straw: 0.36*1.2=0.432
        assertEquals(0.432, calc.speed, 0.001);
    }

    @Test
    public void testWoodScaling() {
        Set<String> traits = new HashSet<>(Arrays.asList("light", "fragile")); // Wood traits from ThaumcraftGolemMaterials
        GolemMaterialScalingLogic.CalculatedAttributes calc = GolemMaterialScalingLogic.calculateAttributes("WOOD", -2, 0, 0, traits);

        // baseHealth=10, matHealth=-2 -> 8. fragile: 8*0.75=6. wood: no change
        assertEquals(6.0, calc.maxHealth, 0.001);

        // baseSpeed=0.3. light: 0.3*1.2=0.36. wood: no change
        assertEquals(0.36, calc.speed, 0.001);
    }

    @Test
    public void testClayScaling() {
        Set<String> traits = new HashSet<>(Arrays.asList("fireproof", "heavy"));
        GolemMaterialScalingLogic.CalculatedAttributes calc = GolemMaterialScalingLogic.calculateAttributes("CLAY", 0, 0, 0, traits);

        assertEquals(10.0, calc.maxHealth, 0.001);

        // baseArmor=0, matArmor=0. clay: armor+2.0 = 2.0
        assertEquals(2.0, calc.armor, 0.001);

        // baseSpeed=0.3, heavy: 0.3*0.8=0.24
        assertEquals(0.24, calc.speed, 0.001);

        // knockbackRes=0. heavy: +0.5. clay: +0.2 -> 0.7
        assertEquals(0.7, calc.knockbackRes, 0.001);
    }

    @Test
    public void testIronScaling() {
        Set<String> traits = new HashSet<>(Arrays.asList("armored", "heavy", "brutal"));
        GolemMaterialScalingLogic.CalculatedAttributes calc = GolemMaterialScalingLogic.calculateAttributes("IRON", 2, 4, 2, traits);

        assertEquals(12.0, calc.maxHealth, 0.001);

        // armor=0, matArmor=4 -> 4. armored -> 8. iron -> 11
        assertEquals(11.0, calc.armor, 0.001);

        // speed=0.3. heavy -> 0.24. iron -> 0.192
        assertEquals(0.192, calc.speed, 0.001);

        // knockbackRes=0. heavy -> +0.5. iron -> +0.5 -> 1.0
        assertEquals(1.0, calc.knockbackRes, 0.001);

        // damage=1, matDamage=2 -> 3. brutal -> 5
        assertEquals(5.0, calc.damage, 0.001);
    }

    @Test
    public void testBrassScaling() {
        Set<String> traits = new HashSet<>(Arrays.asList("smart"));
        GolemMaterialScalingLogic.CalculatedAttributes calc = GolemMaterialScalingLogic.calculateAttributes("BRASS", 0, 1, 1, traits);

        assertEquals(10.0, calc.maxHealth, 0.001);
        assertEquals(1.0, calc.armor, 0.001);

        // speed=0.3. brass -> 0.33
        assertEquals(0.33, calc.speed, 0.001);

        // damage=1, matDamage=1 -> 2. brass -> +1 -> 3
        assertEquals(3.0, calc.damage, 0.001);
    }

    @Test
    public void testThaumiumScaling() {
        Set<String> traits = new HashSet<>(Arrays.asList("repair", "armored"));
        GolemMaterialScalingLogic.CalculatedAttributes calc = GolemMaterialScalingLogic.calculateAttributes("THAUMIUM", 6, 2, 3, traits);

        // health=10, matHealth=6 -> 16. thaumium: 16*1.5 = 24
        assertEquals(24.0, calc.maxHealth, 0.001);

        // armor=0, matArmor=2 -> 2. armored -> 6. thaumium -> 7
        assertEquals(7.0, calc.armor, 0.001);

        assertEquals(4.0, calc.damage, 0.001);
    }

    @Test
    public void testVoidScaling() {
        Set<String> traits = new HashSet<>(Arrays.asList("repair"));
        GolemMaterialScalingLogic.CalculatedAttributes calc = GolemMaterialScalingLogic.calculateAttributes("VOID", 4, 1, 4, traits);

        // health=10, matHealth=4 -> 14. void -> 14*1.2 = 16.8
        assertEquals(16.8, calc.maxHealth, 0.001);

        assertEquals(1.0, calc.armor, 0.001);

        // damage=1, matDamage=4 -> 5. void -> +1 -> 6
        assertEquals(6.0, calc.damage, 0.001);
    }

    @Test
    public void testNullInputs() {
        GolemMaterialScalingLogic.CalculatedAttributes calc = GolemMaterialScalingLogic.calculateAttributes(null, 0, 0, 0, null);
        assertEquals(10.0, calc.maxHealth, 0.001);
        assertEquals(0.0, calc.armor, 0.001);
        assertEquals(0.3, calc.speed, 0.001);
        assertEquals(1.0, calc.damage, 0.001);
        assertEquals(0.0, calc.knockbackRes, 0.001);
    }
}
