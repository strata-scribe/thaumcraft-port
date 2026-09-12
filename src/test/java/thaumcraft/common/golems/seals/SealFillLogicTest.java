package thaumcraft.common.golems.seals;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SealFillLogicTest {

    @Test
    public void testCanAcceptFluid_EmptyContainer() {
        SealFillLogic.ContainerState container = new SealFillLogic.ContainerState("", 0, 1000);
        assertTrue(SealFillLogic.canAcceptFluid(container, "water"));
    }

    @Test
    public void testCanAcceptFluid_SameFluid() {
        SealFillLogic.ContainerState container = new SealFillLogic.ContainerState("water", 500, 1000);
        assertTrue(SealFillLogic.canAcceptFluid(container, "water"));
    }

    @Test
    public void testCanAcceptFluid_DifferentFluid() {
        SealFillLogic.ContainerState container = new SealFillLogic.ContainerState("lava", 500, 1000);
        assertFalse(SealFillLogic.canAcceptFluid(container, "water"));
    }

    @Test
    public void testCanAcceptFluid_FullContainer() {
        SealFillLogic.ContainerState container = new SealFillLogic.ContainerState("water", 1000, 1000);
        assertFalse(SealFillLogic.canAcceptFluid(container, "water"));
    }

    @Test
    public void testCalculateFillUnits_PartialFill() {
        SealFillLogic.ContainerState container = new SealFillLogic.ContainerState("water", 500, 1000);
        SealFillLogic.GolemState golem = new SealFillLogic.GolemState("water", 200);

        int fillUnits = SealFillLogic.calculateFillUnits(container, golem);
        assertEquals(200, fillUnits);
    }

    @Test
    public void testCalculateFillUnits_CapacityLimit() {
        SealFillLogic.ContainerState container = new SealFillLogic.ContainerState("water", 900, 1000);
        SealFillLogic.GolemState golem = new SealFillLogic.GolemState("water", 200);

        int fillUnits = SealFillLogic.calculateFillUnits(container, golem);
        assertEquals(100, fillUnits); // Limited by container capacity
    }

    @Test
    public void testCalculateFillUnits_EmptyContainer() {
        SealFillLogic.ContainerState container = new SealFillLogic.ContainerState(null, 0, 1000);
        SealFillLogic.GolemState golem = new SealFillLogic.GolemState("lava", 300);

        int fillUnits = SealFillLogic.calculateFillUnits(container, golem);
        assertEquals(300, fillUnits);
    }

    @Test
    public void testCalculateFillUnits_Incompatible() {
        SealFillLogic.ContainerState container = new SealFillLogic.ContainerState("water", 500, 1000);
        SealFillLogic.GolemState golem = new SealFillLogic.GolemState("lava", 200);

        int fillUnits = SealFillLogic.calculateFillUnits(container, golem);
        assertEquals(0, fillUnits);
    }

    @Test
    public void testCalculateFillUnits_NullInputs() {
        assertEquals(0, SealFillLogic.calculateFillUnits(null, new SealFillLogic.GolemState("water", 200)));
        assertEquals(0, SealFillLogic.calculateFillUnits(new SealFillLogic.ContainerState("water", 500, 1000), null));
    }
}
