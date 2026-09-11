package thaumcraft.common.entities.monster;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EldritchCrabLogicTest {

    @Test
    public void testCalculateLeapingTrajectory() {
        // Crab at (0, 0, 0), Target at (10, 0, 0) with eye height 2
        EldritchCrabLogic.LeapVector jump = EldritchCrabLogic.calculateLeapingTrajectory(0, 0, 0, 10, 0, 0, 2);

        // Distance = sqrt(10^2 + 0) = 10
        // scale = 0.8 / 10 = 0.08
        // dx = 10, leapX = 10 * 0.08 = 0.8
        assertEquals(0.8, jump.x, 0.01);

        // dy = 2
        // leapY = (2/10) + 0.4 = 0.6
        assertEquals(0.6, jump.y, 0.01);

        // dz = 0, leapZ = 0
        assertEquals(0.0, jump.z, 0.01);

        // Same position
        EldritchCrabLogic.LeapVector jumpSamePos = EldritchCrabLogic.calculateLeapingTrajectory(0, 0, 0, 0, -2, 0, 2);
        assertEquals(0.0, jumpSamePos.x, 0.01);
        assertEquals(0.5, jumpSamePos.y, 0.01);
        assertEquals(0.0, jumpSamePos.z, 0.01);
    }

    @Test
    public void testIsWithinLatchDistance() {
        // Crab at (0, 1.9, 0), Target head at (0, 0+2, 0) => distance 0.1
        assertTrue(EldritchCrabLogic.isWithinLatchDistance(0, 1.9, 0, 0, 0, 0, 2, 1.5));

        // Crab at (5, 0, 5), Target head at (0, 2, 0) => distance sqrt(25+4+25) = sqrt(54) ~ 7.3
        assertFalse(EldritchCrabLogic.isWithinLatchDistance(5, 0, 5, 0, 0, 0, 2, 1.5));
    }

    @Test
    public void testGetInhabitedHostStats() {
        EldritchCrabLogic.HostStats stats = EldritchCrabLogic.getInhabitedHostStats(20.0, 0.23);

        assertEquals(40.0, stats.maxHealth, 0.001); // 2x health
        assertEquals(0.345, stats.speed, 0.001); // 1.5x speed
        assertTrue(stats.hasCrabCrown); // has crown
    }
}
