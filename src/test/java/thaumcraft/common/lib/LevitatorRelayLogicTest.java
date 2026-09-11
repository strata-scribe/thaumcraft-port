package thaumcraft.common.lib;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LevitatorRelayLogicTest {

    @Test
    public void testCalculatePropulsionUpward() {
        Vec3 currentMotion = new Vec3(0, 0, 0);
        Vec3 newMotion = LevitatorRelayLogic.calculatePropulsion(currentMotion, Direction.UP, false, false);
        assertEquals(0.05, newMotion.y, 0.01);
    }

    @Test
    public void testCalculatePropulsionDownward() {
        Vec3 currentMotion = new Vec3(0, 0, 0);
        Vec3 newMotion = LevitatorRelayLogic.calculatePropulsion(currentMotion, Direction.DOWN, false, false);
        assertEquals(-0.05, newMotion.y, 0.01);
    }

    @Test
    public void testCalculatePropulsionInvertedUpward() {
        Vec3 currentMotion = new Vec3(0, 0, 0);
        Vec3 newMotion = LevitatorRelayLogic.calculatePropulsion(currentMotion, Direction.UP, true, false);
        assertEquals(-0.05, newMotion.y, 0.01);
    }

    @Test
    public void testCalculatePropulsionInvertedDownward() {
        Vec3 currentMotion = new Vec3(0, 0, 0);
        Vec3 newMotion = LevitatorRelayLogic.calculatePropulsion(currentMotion, Direction.DOWN, true, false);
        assertEquals(0.05, newMotion.y, 0.01);
    }

    @Test
    public void testCalculatePropulsionSneaking() {
        Vec3 currentMotion = new Vec3(0, 0, 0);
        Vec3 newMotion = LevitatorRelayLogic.calculatePropulsion(currentMotion, Direction.UP, false, true);
        assertEquals(0.025, newMotion.y, 0.01);

        Vec3 newMotionDown = LevitatorRelayLogic.calculatePropulsion(currentMotion, Direction.DOWN, false, true);
        assertEquals(-0.025, newMotionDown.y, 0.01);
    }

    @Test
    public void testRedstoneSignalFromAura() {
        assertEquals(0, LevitatorRelayLogic.calculateRedstoneSignalFromAura(0, 100));
        assertEquals(15, LevitatorRelayLogic.calculateRedstoneSignalFromAura(100, 100));
        assertEquals(7, LevitatorRelayLogic.calculateRedstoneSignalFromAura(50, 100));
        assertEquals(15, LevitatorRelayLogic.calculateRedstoneSignalFromAura(200, 100));
        assertEquals(0, LevitatorRelayLogic.calculateRedstoneSignalFromAura(50, 0));
    }

    @Test
    public void testRedstoneSignalFromEssentia() {
        assertEquals(0, LevitatorRelayLogic.calculateRedstoneSignalFromEssentia(0, 250));
        assertEquals(15, LevitatorRelayLogic.calculateRedstoneSignalFromEssentia(250, 250));
        assertEquals(7, LevitatorRelayLogic.calculateRedstoneSignalFromEssentia(125, 250));
        assertEquals(15, LevitatorRelayLogic.calculateRedstoneSignalFromEssentia(300, 250));
        assertEquals(0, LevitatorRelayLogic.calculateRedstoneSignalFromEssentia(125, 0));
    }
}
