package thaumcraft.common.casters;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FocusRiftLogicTest {

    @Test
    public void testCalculateRiftComplexity() {
        assertEquals(4, FocusRiftLogic.calculateRiftComplexity(0));
        assertEquals(7, FocusRiftLogic.calculateRiftComplexity(1));
        assertEquals(19, FocusRiftLogic.calculateRiftComplexity(5));
        assertEquals(4, FocusRiftLogic.calculateRiftComplexity(-2)); // Should treat negative as 0
    }

    @Test
    public void testCalculateGravitationalFalloff() {
        assertEquals(1.0, FocusRiftLogic.calculateGravitationalFalloff(0.0, 5.0), 0.001);
        assertEquals(0.5, FocusRiftLogic.calculateGravitationalFalloff(2.5, 5.0), 0.001);
        assertEquals(0.0, FocusRiftLogic.calculateGravitationalFalloff(5.0, 5.0), 0.001);
        assertEquals(0.0, FocusRiftLogic.calculateGravitationalFalloff(6.0, 5.0), 0.001); // Outside radius
    }

    @Test
    public void testCalculateAttractionVelocity() {
        // At center, no velocity applied (distance <= 0.1)
        assertEquals(0.0, FocusRiftLogic.calculateAttractionVelocity(0.0, 5.0), 0.001);

        // At max radius or further, no velocity applied
        assertEquals(0.0, FocusRiftLogic.calculateAttractionVelocity(5.0, 5.0), 0.001);
        assertEquals(0.0, FocusRiftLogic.calculateAttractionVelocity(6.0, 5.0), 0.001);

        // At mid radius (2.5), falloff is 0.5, so velocity is 0.5 * 0.15 = 0.075
        assertEquals(0.075, FocusRiftLogic.calculateAttractionVelocity(2.5, 5.0), 0.001);
    }

    @Test
    public void testCalculateRiftDurationTicks() {
        assertEquals(20, FocusRiftLogic.calculateRiftDurationTicks(0));
        assertEquals(40, FocusRiftLogic.calculateRiftDurationTicks(1));
        assertEquals(20, FocusRiftLogic.calculateRiftDurationTicks(-1));
    }

    @Test
    public void testCalculateRiftRadius() {
        assertEquals(4.0, FocusRiftLogic.calculateRiftRadius(0), 0.001);
        assertEquals(4.5, FocusRiftLogic.calculateRiftRadius(1), 0.001);
        assertEquals(9.0, FocusRiftLogic.calculateRiftRadius(10), 0.001);
    }
}
