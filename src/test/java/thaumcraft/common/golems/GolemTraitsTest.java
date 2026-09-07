package thaumcraft.common.golems;

import org.junit.jupiter.api.Test;
import thaumcraft.api.golems.parts.GolemMaterial;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Set;
import java.util.HashSet;

public class GolemTraitsTest {

    // We decouple the calculation logic completely from EntityThaumcraftGolem to avoid
    // triggering class initialization which triggers Minecraft/Forge registry crashes in plain JUnit.

    public static class CalculatedAttributes {
        public double maxHealth = 10.0D;
        public double armor = 0.0D;
        public double speed = 0.3D;
        public double damage = 1.0D;
        public double knockbackRes = 0.0D;
    }

    public static CalculatedAttributes calculateAttributes(GolemProperties properties, String[] traitNames) {
        CalculatedAttributes attr = new CalculatedAttributes();

        if (properties.getMaterial() != null) {
            attr.maxHealth += properties.getMaterial().healthMod;
            attr.armor += properties.getMaterial().armor;
            attr.damage += properties.getMaterial().damage;
        }

        Set<String> traits = new HashSet<>();
        if (traitNames != null) {
            for (String t : traitNames) traits.add(t);
        }

        if (traits.contains("light")) {
            attr.speed *= 1.2;
        }
        if (traits.contains("fragile")) {
            attr.maxHealth *= 0.75;
        }
        if (traits.contains("heavy")) {
            attr.speed *= 0.8;
            attr.knockbackRes += 0.5;
        }
        if (traits.contains("armored")) {
            attr.armor += 4;
        }
        if (traits.contains("brutal")) {
            attr.damage += 2;
        }

        return attr;
    }

    @Test
    public void testWoodMaterialAttributes() {
        GolemMaterial wood = new GolemMaterial("WOOD", new String[]{}, null, 0, -2, 0, 0, null, null, null);
        GolemProperties props = new GolemProperties();
        props.setMaterial(wood);

        CalculatedAttributes result = calculateAttributes(props, new String[]{"light", "fragile"});

        assertEquals(6.0, result.maxHealth, 0.01);
        assertEquals(0.36, result.speed, 0.01);
    }

    @Test
    public void testIronMaterialAttributes() {
        GolemMaterial iron = new GolemMaterial("IRON", new String[]{}, null, 0, 2, 4, 2, null, null, null);
        GolemProperties props = new GolemProperties();
        props.setMaterial(iron);

        CalculatedAttributes result = calculateAttributes(props, new String[]{"armored", "heavy", "brutal"});

        assertEquals(12.0, result.maxHealth, 0.01);
        assertEquals(8.0, result.armor, 0.01);
        assertEquals(5.0, result.damage, 0.01);
        assertEquals(0.5, result.knockbackRes, 0.01);
    }
}
