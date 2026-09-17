package thaumcraft.common.lib.research.theorycraft.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CardInspiredLogicTest {

    @Test
    public void testCalculateBonusCardDraws() {
        // Base case, low warp, no aids
        assertEquals(1, CardInspiredLogic.calculateBonusCardDraws(0, 0));
        assertEquals(1, CardInspiredLogic.calculateBonusCardDraws(10, 0));

        // High warp, no aids
        assertEquals(2, CardInspiredLogic.calculateBonusCardDraws(25, 0));
        assertEquals(2, CardInspiredLogic.calculateBonusCardDraws(50, 0));

        // Aids bridging the gap
        assertEquals(1, CardInspiredLogic.calculateBonusCardDraws(10, 2)); // 10 + 10 = 20 < 25
        assertEquals(2, CardInspiredLogic.calculateBonusCardDraws(10, 3)); // 10 + 15 = 25 >= 25

        // Zero warp, many aids
        assertEquals(1, CardInspiredLogic.calculateBonusCardDraws(0, 4)); // 20 < 25
        assertEquals(2, CardInspiredLogic.calculateBonusCardDraws(0, 5)); // 25 >= 25
    }

    @Test
    public void testCalculateInspirationRefundChance() {
        // Zero values
        assertEquals(0.0, CardInspiredLogic.calculateInspirationRefundChance(0, 0), 0.001);

        // Only warp
        assertEquals(0.10, CardInspiredLogic.calculateInspirationRefundChance(10, 0), 0.001);
        assertEquals(0.50, CardInspiredLogic.calculateInspirationRefundChance(50, 0), 0.001);

        // Only aids
        assertEquals(0.10, CardInspiredLogic.calculateInspirationRefundChance(0, 2), 0.001);
        assertEquals(0.25, CardInspiredLogic.calculateInspirationRefundChance(0, 5), 0.001);

        // Both warp and aids
        assertEquals(0.35, CardInspiredLogic.calculateInspirationRefundChance(20, 3), 0.001); // 0.20 + 0.15 = 0.35

        // Max cap
        assertEquals(1.0, CardInspiredLogic.calculateInspirationRefundChance(150, 10), 0.001);

        // Min cap
        assertEquals(0.0, CardInspiredLogic.calculateInspirationRefundChance(-10, -5), 0.001);
    }
}
