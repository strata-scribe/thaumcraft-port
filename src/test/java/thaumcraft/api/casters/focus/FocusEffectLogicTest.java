package thaumcraft.api.casters.focus;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FocusEffectLogicTest {

    @Test
    public void testCalculateDamage() {
        assertEquals(15.0f, FocusEffectLogic.calculateDamage(5, 1.0f), 0.01f);
        assertEquals(7.5f, FocusEffectLogic.calculateDamage(5, 0.5f), 0.01f);
        assertEquals(3.0f, FocusEffectLogic.calculateDamage(1, 1.0f), 0.01f);
    }

    @Test
    public void testCalculateDuration() {
        assertEquals(10, FocusEffectLogic.calculateDuration(5, 1.0f));
        assertEquals(5, FocusEffectLogic.calculateDuration(5, 0.5f));
        assertEquals(2, FocusEffectLogic.calculateDuration(1, 1.0f));
    }

    @Test
    public void testCalculateHealing() {
        assertEquals(10.0f, FocusEffectLogic.calculateHealing(5, 1.0f), 0.01f);
        assertEquals(5.0f, FocusEffectLogic.calculateHealing(5, 0.5f), 0.01f);
        assertEquals(2.0f, FocusEffectLogic.calculateHealing(1, 1.0f), 0.01f);
    }

    @Test
    public void testCanBreakBlock() {
        assertTrue(FocusEffectLogic.canBreakBlock(1, 1.0f));
        assertTrue(FocusEffectLogic.canBreakBlock(5, 1.0f));
        assertFalse(FocusEffectLogic.canBreakBlock(1, 0.4f));
}
    @Test
    public void testGetComplexity() {
        thaumcraft.api.casters.FocusEffect dummyEffect = new thaumcraft.api.casters.FocusEffect() {
            @Override public String getKey() { return "test"; }
            @Override public thaumcraft.api.aspects.Aspect getAspect() { return thaumcraft.api.aspects.Aspect.FIRE; }
            @Override public String getResearch() { return null; }
            @Override public int getComplexity() { return 0; }
            @Override public thaumcraft.api.casters.NodeSetting[] createSettings() {
                return new thaumcraft.api.casters.NodeSetting[] {
                    new thaumcraft.api.casters.NodeSetting("power", "focus.common.power", new thaumcraft.api.casters.NodeSetting.NodeSettingIntRange(1, 5)),
                    new thaumcraft.api.casters.NodeSetting("duration", "focus.common.duration", new thaumcraft.api.casters.NodeSetting.NodeSettingIntRange(0, 5))
                };
            }
            @Override public boolean execute(net.minecraft.world.phys.HitResult target, @javax.annotation.Nullable thaumcraft.api.casters.Trajectory trajectory, float finalPower, int num) { return false; }
            @Override public void renderParticleFX(net.minecraft.world.level.Level world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {}
        };
        // By default, power is set to its min (1), duration to its min (0) in NodeSetting creation.
        // Base is 2. power (1) adds 1. duration (0) adds 0. Total = 3.
        assertEquals(3, FocusEffectLogic.getComplexity(dummyEffect));

        dummyEffect.getSetting("power").setValue(3);
        dummyEffect.getSetting("duration").setValue(2);
        // Base 2 + power(3) + duration(2 * 2 = 4) = 9
        assertEquals(9, FocusEffectLogic.getComplexity(dummyEffect));
    }
}
