package thaumcraft.api.casters.focus;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SpectralArrowLogicTest {

    @Test
    public void testCalculateDamage() {
        assertEquals(3.0f, SpectralArrowLogic.calculateDamage(1, 1.0f), 0.01f);
        assertEquals(15.0f, SpectralArrowLogic.calculateDamage(5, 1.0f), 0.01f);
        assertEquals(7.5f, SpectralArrowLogic.calculateDamage(5, 0.5f), 0.01f);
    }

    @Test
    public void testCalculateArmorPenetration() {
        assertEquals(0.0f, SpectralArrowLogic.calculateArmorPenetration(0), 0.01f);
        assertEquals(0.3f, SpectralArrowLogic.calculateArmorPenetration(3), 0.01f);
        assertEquals(0.5f, SpectralArrowLogic.calculateArmorPenetration(5), 0.01f);
    }
}
