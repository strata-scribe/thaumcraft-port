package thaumcraft.common.items.tools;

import org.junit.jupiter.api.Test;
import thaumcraft.common.items.tools.logic.ThaumometerZoomLogic;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemThaumometerTest {

    @Test
    void testLogicIntegration() {
        double fov = ThaumometerZoomLogic.calculateFovScaling(0.5, 90.0);
        assertEquals(60.0, fov, 0.01);
    }
}
