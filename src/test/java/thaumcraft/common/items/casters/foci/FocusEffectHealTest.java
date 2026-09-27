package thaumcraft.common.items.casters.foci;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.common.casters.FocusLogic;
import net.minecraft.world.phys.HitResult;

public class FocusEffectHealTest {

    @Test
    public void testComplexityAndFormulas() {
        FocusEffectHeal heal = new FocusEffectHeal();

        NodeSetting powerSetting = heal.getSetting("power");
        assertNotNull(powerSetting);

        // Test complexity calculation delegation
        powerSetting.setValue(1);
        assertEquals(FocusLogic.calculateHealComplexity(1), heal.getComplexity());
        assertEquals(4, heal.getComplexity());
        assertEquals(-FocusLogic.calculateHealAmount(1, 1.0f), heal.getDamageForDisplay(1.0f));
        assertEquals(-1.0f, heal.getDamageForDisplay(1.0f));

        powerSetting.setValue(5);
        assertEquals(FocusLogic.calculateHealComplexity(5), heal.getComplexity());
        assertEquals(20, heal.getComplexity());
        assertEquals(-FocusLogic.calculateHealAmount(5, 0.5f), heal.getDamageForDisplay(0.5f));
        assertEquals(-2.5f, heal.getDamageForDisplay(0.5f));
    }

    @Test
    public void testFocusLogicHealFormulas() {
        // Direct testing of FocusLogic methods used by FocusEffectHeal (pure Java)

        // Complexity: max(0, power) * 4
        assertEquals(0, FocusLogic.calculateHealComplexity(-1));
        assertEquals(0, FocusLogic.calculateHealComplexity(0));
        assertEquals(4, FocusLogic.calculateHealComplexity(1));
        assertEquals(20, FocusLogic.calculateHealComplexity(5));

        // Heal Amount: max(0, power) * max(0.0f, finalPower)
        assertEquals(0.0f, FocusLogic.calculateHealAmount(1, -1.0f));
        assertEquals(0.0f, FocusLogic.calculateHealAmount(0, 1.0f));
        assertEquals(1.0f, FocusLogic.calculateHealAmount(1, 1.0f));
        assertEquals(2.5f, FocusLogic.calculateHealAmount(5, 0.5f));

        // Undead Damage: max(0, power) * max(0.0f, finalPower) * 1.5f
        assertEquals(0.0f, FocusLogic.calculateHealUndeadDamage(1, -1.0f));
        assertEquals(0.0f, FocusLogic.calculateHealUndeadDamage(0, 1.0f));
        assertEquals(1.5f, FocusLogic.calculateHealUndeadDamage(1, 1.0f));
        assertEquals(3.75f, FocusLogic.calculateHealUndeadDamage(5, 0.5f));
    }

    @Test
    public void testExecuteSafeguards() {
        FocusEffectHeal heal = new FocusEffectHeal();

        // Target null
        assertFalse(heal.execute(null, null, 1.0f, 0));

        // Target non-null, but package is null (from default state)
        HitResult mockTarget = new HitResult(new net.minecraft.world.phys.Vec3(0,0,0)) {
            @Override
            public Type getType() {
                return Type.MISS;
            }
        };
        assertFalse(heal.execute(mockTarget, null, 1.0f, 0));

        // Target non-null, package non-null, but world is null
        FocusPackage pkg = new FocusPackage();
        heal.setPackage(pkg);
        assertFalse(heal.execute(mockTarget, null, 1.0f, 0));
    }
}
