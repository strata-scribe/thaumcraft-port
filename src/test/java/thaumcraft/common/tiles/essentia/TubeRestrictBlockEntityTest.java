package thaumcraft.common.tiles.essentia;

import net.minecraft.core.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thaumcraft.api.aspects.Aspect;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TubeRestrictBlockEntityTest {

    private TubeRestrictLogic logic;

    @BeforeEach
    public void setUp() {
        logic = new TubeRestrictLogic(() -> {});
    }

    @Test
    public void testSuctionReduction() {
        logic.setSuction(Aspect.AIR, 64);

        // Suction should be reduced by 50%
        // TubePhysicsLogic.calculateSuctionDrop drops 64 to 63.
        // Then we divide by 2: 63 / 2 = 31.

        int suction = logic.getSuctionAmount(Direction.NORTH);
        assertEquals(31, suction, "Suction should be halved after normal drop");
    }

    @Test
    public void testZeroSuction() {
        logic.setSuction(Aspect.AIR, 0);
        int suction = logic.getSuctionAmount(Direction.NORTH);
        assertEquals(0, suction, "Zero suction should remain zero");
    }

    @Test
    public void testFixedPenaltyFallback() {
        logic.setSuction(Aspect.AIR, 10);

        // normal drop: 10 - 1 = 9.
        // halving: 9 / 2 = 4.

        int suction = logic.getSuctionAmount(Direction.NORTH);
        assertEquals(4, suction, "Suction should be halved after normal drop");
    }
}
