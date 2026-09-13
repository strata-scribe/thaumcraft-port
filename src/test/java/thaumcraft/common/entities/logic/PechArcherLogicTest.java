package thaumcraft.common.entities.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PechArcherLogicTest {

    @Test
    public void testComputeDrawInterval_BaseIntervals() {
        assertEquals(60, PechArcherLogic.computeDrawInterval(10.0, 0));
        assertEquals(50, PechArcherLogic.computeDrawInterval(10.0, 1));
        assertEquals(40, PechArcherLogic.computeDrawInterval(10.0, 2));
        assertEquals(30, PechArcherLogic.computeDrawInterval(10.0, 3));
    }

    @Test
    public void testComputeDrawInterval_CloseDistance() {
        // Distance <= 5.0 decreases interval by 15, minimum 10
        assertEquals(45, PechArcherLogic.computeDrawInterval(4.0, 0));
        assertEquals(35, PechArcherLogic.computeDrawInterval(3.0, 1));
        assertEquals(25, PechArcherLogic.computeDrawInterval(2.0, 2));
        assertEquals(15, PechArcherLogic.computeDrawInterval(5.0, 3));

        // At difficulty 5, base is still 30, so close distance makes it 15
        assertEquals(15, PechArcherLogic.computeDrawInterval(5.0, 5));
    }

    @Test
    public void testComputeDrawInterval_FarDistance() {
        // Distance >= 20.0 increases interval by 20
        assertEquals(80, PechArcherLogic.computeDrawInterval(25.0, 0));
        assertEquals(70, PechArcherLogic.computeDrawInterval(20.0, 1));
        assertEquals(60, PechArcherLogic.computeDrawInterval(30.0, 2));
        assertEquals(50, PechArcherLogic.computeDrawInterval(100.0, 3));
    }

    @Test
    public void testGetPoisonArrowChance() {
        assertEquals(0.0, PechArcherLogic.getPoisonArrowChance(0), 0.001);
        assertEquals(0.10, PechArcherLogic.getPoisonArrowChance(1), 0.001);
        assertEquals(0.20, PechArcherLogic.getPoisonArrowChance(2), 0.001);
        assertEquals(0.35, PechArcherLogic.getPoisonArrowChance(3), 0.001);
    }

    @Test
    public void testGetSpectralArrowChance() {
        assertEquals(0.0, PechArcherLogic.getSpectralArrowChance(0), 0.001);
        assertEquals(0.05, PechArcherLogic.getSpectralArrowChance(1), 0.001);
        assertEquals(0.10, PechArcherLogic.getSpectralArrowChance(2), 0.001);
        assertEquals(0.20, PechArcherLogic.getSpectralArrowChance(3), 0.001);
    }

    @Test
    public void testDetermineArrowType() {
        // Peaceful (diff=0): Spectral=0, Poison=0
        assertEquals(PechArcherLogic.ArrowType.REGULAR, PechArcherLogic.determineArrowType(0.0, 0));

        // Easy (diff=1): Spectral=0.05, Poison=0.10
        assertEquals(PechArcherLogic.ArrowType.SPECTRAL, PechArcherLogic.determineArrowType(0.04, 1));
        assertEquals(PechArcherLogic.ArrowType.POISON, PechArcherLogic.determineArrowType(0.06, 1));
        assertEquals(PechArcherLogic.ArrowType.POISON, PechArcherLogic.determineArrowType(0.14, 1));
        assertEquals(PechArcherLogic.ArrowType.REGULAR, PechArcherLogic.determineArrowType(0.16, 1));

        // Normal (diff=2): Spectral=0.10, Poison=0.20
        assertEquals(PechArcherLogic.ArrowType.SPECTRAL, PechArcherLogic.determineArrowType(0.09, 2));
        assertEquals(PechArcherLogic.ArrowType.POISON, PechArcherLogic.determineArrowType(0.11, 2));
        assertEquals(PechArcherLogic.ArrowType.POISON, PechArcherLogic.determineArrowType(0.29, 2));
        assertEquals(PechArcherLogic.ArrowType.REGULAR, PechArcherLogic.determineArrowType(0.31, 2));

        // Hard (diff=3): Spectral=0.20, Poison=0.35
        assertEquals(PechArcherLogic.ArrowType.SPECTRAL, PechArcherLogic.determineArrowType(0.19, 3));
        assertEquals(PechArcherLogic.ArrowType.POISON, PechArcherLogic.determineArrowType(0.21, 3));
        assertEquals(PechArcherLogic.ArrowType.POISON, PechArcherLogic.determineArrowType(0.54, 3));
        assertEquals(PechArcherLogic.ArrowType.REGULAR, PechArcherLogic.determineArrowType(0.56, 3));
    }
}
