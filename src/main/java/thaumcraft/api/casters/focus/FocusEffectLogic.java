package thaumcraft.api.casters.focus;

import thaumcraft.api.casters.FocusEffect;

public class FocusEffectLogic {

    public static float calculateDamage(int powerSetting, float finalPower) {
        return (powerSetting * 3) * finalPower;
    }

    public static int calculateDuration(int durationSetting, float finalPower) {
        return (int)(durationSetting * 2 * finalPower);
    }

    public static float calculateHealing(int powerSetting, float finalPower) {
        return (powerSetting * 2.0f) * finalPower;
    }

    public static boolean canBreakBlock(int powerSetting, float finalPower) {
        // Simplified block break logic based on power
        return (powerSetting * finalPower) > 0.5f;
    }

    public static int getComplexity(FocusEffect effect) {
        int complexity = 2; // base

        if (effect.getSettingList() != null) {
            for (String key : effect.getSettingList()) {
                if (key.equals("power")) {
                    complexity += effect.getSettingValue("power");
                } else if (key.equals("duration")) {
                    complexity += effect.getSettingValue("duration") * 2;
                }
            }
        }
        return complexity;
    }

}
