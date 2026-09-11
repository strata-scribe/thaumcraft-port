package thaumcraft.common.lib;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MagicMirrorLogicTest {

    @Test
    public void testCanLink_ValidLink() {
        MagicMirrorLogic.MirrorPosition source = new MagicMirrorLogic.MirrorPosition(0, 64, 0, "minecraft:overworld");
        MagicMirrorLogic.MirrorPosition target = new MagicMirrorLogic.MirrorPosition(10, 64, 10, "minecraft:overworld");
        assertTrue(MagicMirrorLogic.canLink(source, target));
    }

    @Test
    public void testCanLink_SelfLink() {
        MagicMirrorLogic.MirrorPosition source = new MagicMirrorLogic.MirrorPosition(0, 64, 0, "minecraft:overworld");
        MagicMirrorLogic.MirrorPosition target = new MagicMirrorLogic.MirrorPosition(0, 64, 0, "minecraft:overworld");
        assertFalse(MagicMirrorLogic.canLink(source, target));
    }

    @Test
    public void testCanLink_DifferentDimensions() {
        MagicMirrorLogic.MirrorPosition source = new MagicMirrorLogic.MirrorPosition(0, 64, 0, "minecraft:overworld");
        MagicMirrorLogic.MirrorPosition target = new MagicMirrorLogic.MirrorPosition(0, 64, 0, "minecraft:the_nether");
        assertTrue(MagicMirrorLogic.canLink(source, target));
    }

    @Test
    public void testCalculateDropCoordinates_Up() {
        MagicMirrorLogic.MirrorPosition targetPos = new MagicMirrorLogic.MirrorPosition(10, 64, 10, "minecraft:overworld");
        MagicMirrorLogic.DropCoordinates drop = MagicMirrorLogic.calculateDropCoordinates(targetPos, 0, 1, 0);
        assertEquals(new MagicMirrorLogic.DropCoordinates(10.5, 65.2, 10.5), drop);
    }

    @Test
    public void testCalculateDropCoordinates_North() {
        MagicMirrorLogic.MirrorPosition targetPos = new MagicMirrorLogic.MirrorPosition(10, 64, 10, "minecraft:overworld");
        MagicMirrorLogic.DropCoordinates drop = MagicMirrorLogic.calculateDropCoordinates(targetPos, 0, 0, -1);
        assertEquals(new MagicMirrorLogic.DropCoordinates(10.5, 64.5, 9.8), drop);
    }

    @Test
    public void testCanSendItem_Valid() {
        MagicMirrorLogic.MirrorPosition link = new MagicMirrorLogic.MirrorPosition(10, 64, 10, "minecraft:overworld");
        MagicMirrorLogic.MirrorStateProvider provider = new MagicMirrorLogic.MirrorStateProvider() {
            @Override
            public boolean isDimensionLoaded(String dimensionId) {
                return true;
            }

            @Override
            public boolean isMirrorAt(int x, int y, int z, String dimensionId) {
                return true;
            }
        };
        assertTrue(MagicMirrorLogic.canSendItem(link, provider));
    }

    @Test
    public void testCanSendItem_DimensionNotLoaded() {
        MagicMirrorLogic.MirrorPosition link = new MagicMirrorLogic.MirrorPosition(10, 64, 10, "minecraft:overworld");
        MagicMirrorLogic.MirrorStateProvider provider = new MagicMirrorLogic.MirrorStateProvider() {
            @Override
            public boolean isDimensionLoaded(String dimensionId) {
                return false;
            }

            @Override
            public boolean isMirrorAt(int x, int y, int z, String dimensionId) {
                return true;
            }
        };
        assertFalse(MagicMirrorLogic.canSendItem(link, provider));
    }

    @Test
    public void testCanSendItem_NoMirror() {
        MagicMirrorLogic.MirrorPosition link = new MagicMirrorLogic.MirrorPosition(10, 64, 10, "minecraft:overworld");
        MagicMirrorLogic.MirrorStateProvider provider = new MagicMirrorLogic.MirrorStateProvider() {
            @Override
            public boolean isDimensionLoaded(String dimensionId) {
                return true;
            }

            @Override
            public boolean isMirrorAt(int x, int y, int z, String dimensionId) {
                return false;
            }
        };
        assertFalse(MagicMirrorLogic.canSendItem(link, provider));
    }
}
