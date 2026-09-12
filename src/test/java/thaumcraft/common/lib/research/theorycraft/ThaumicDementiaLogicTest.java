package thaumcraft.common.lib.research.theorycraft;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ThaumicDementiaLogicTest {

    @Test
    public void testCalculateDistortionChance_ZeroWarp() {
        assertEquals(0.0, ThaumicDementiaLogic.calculateDistortionChance(0), 0.001);
    }

    @Test
    public void testCalculateDistortionChance_LowWarp() {
        assertEquals(0.05, ThaumicDementiaLogic.calculateDistortionChance(10), 0.001);
    }

    @Test
    public void testCalculateDistortionChance_HighWarp() {
        assertEquals(0.5, ThaumicDementiaLogic.calculateDistortionChance(150), 0.001); // 150 * 0.005 = 0.75, but capped at 0.5
    }

    @Test
    public void testCalculateDistortionChance_MaxCap() {
        assertEquals(0.5, ThaumicDementiaLogic.calculateDistortionChance(100), 0.001); // 100 * 0.005 = 0.5
        assertEquals(0.5, ThaumicDementiaLogic.calculateDistortionChance(200), 0.001);
    }

    @Test
    public void testCalculateInspirationLoss_ZeroWarp() {
        assertEquals(0, ThaumicDementiaLogic.calculateInspirationLoss(0));
    }

    @Test
    public void testCalculateInspirationLoss_LowWarp() {
        assertEquals(0, ThaumicDementiaLogic.calculateInspirationLoss(19));
        assertEquals(1, ThaumicDementiaLogic.calculateInspirationLoss(20));
        assertEquals(1, ThaumicDementiaLogic.calculateInspirationLoss(49));
    }

    @Test
    public void testCalculateInspirationLoss_MediumWarp() {
        assertEquals(2, ThaumicDementiaLogic.calculateInspirationLoss(50));
        assertEquals(2, ThaumicDementiaLogic.calculateInspirationLoss(99));
    }

    @Test
    public void testCalculateInspirationLoss_HighWarp() {
        assertEquals(3, ThaumicDementiaLogic.calculateInspirationLoss(100));
        assertEquals(3, ThaumicDementiaLogic.calculateInspirationLoss(200));
    }
}
