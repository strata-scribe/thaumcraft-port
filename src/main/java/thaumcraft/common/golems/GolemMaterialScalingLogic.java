package thaumcraft.common.golems;

import java.util.Set;

public class GolemMaterialScalingLogic {

    public static class CalculatedAttributes {
        public double maxHealth = 10.0D;
        public double armor = 0.0D;
        public double speed = 0.3D;
        public double damage = 1.0D;
        public double knockbackRes = 0.0D;
    }

    public static CalculatedAttributes calculateAttributes(String materialKey, int matHealthMod, int matArmorMod, int matDamageMod, Set<String> traits) {
        CalculatedAttributes attr = new CalculatedAttributes();

        attr.maxHealth += matHealthMod;
        attr.armor += matArmorMod;
        attr.damage += matDamageMod;

        if (traits != null) {
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
        }

        if (materialKey != null) {
            String key = materialKey.toUpperCase();
            switch (key) {
                case "STRAW":
                    attr.speed *= 1.2;
                    attr.maxHealth *= 0.8;
                    break;
                case "WOOD":
                    // balanced, no changes
                    break;
                case "CLAY":
                    attr.armor += 2.0;
                    attr.knockbackRes += 0.2;
                    break;
                case "IRON":
                    attr.armor += 3.0;
                    attr.speed *= 0.8;
                    attr.knockbackRes += 0.5;
                    break;
                case "BRASS":
                    attr.speed *= 1.1;
                    attr.damage += 1.0;
                    break;
                case "THAUMIUM":
                    attr.maxHealth *= 1.5;
                    attr.armor += 1.0;
                    break;
                case "VOID":
                    attr.maxHealth *= 1.2;
                    attr.damage += 1.0;
                    break;
                default:
                    break;
            }
        }

        return attr;
    }
}
