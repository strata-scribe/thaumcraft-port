package thaumcraft.common.lib.research.theorycraft;

import org.junit.jupiter.api.Test;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import static org.junit.jupiter.api.Assertions.*;
import thaumcraft.api.aspects.Aspect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class TheorycraftCardSculptingSynthesisTest {

    @Test
    public void testCardSculptingCategoryProgressBonus() {
        CardSculpting card = new CardSculpting();
        ResearchTableData data = new ResearchTableData(null);

        boolean result = card.activate(null, data);

        assertTrue(result);
        assertEquals(15, data.getTotal("GOLEMANCY"));
    }

    @Test
    public void testCardSynthesisAlchemicalCombiningBonus() {
        CardSynthesis card = new CardSynthesis() {
            @Override
            protected float getRandomFloat(Player player) {
                return 0.1f; // Force a bonus inspiration roll
            }
            @Override
            protected void givePlayerItem(Player player) {
                // Do nothing to avoid Bootstrap exceptions with ThaumcraftApiHelper and Player
            }
        };

        ResearchTableData data = new ResearchTableData(null);
        data.inspirationStart = 10;
        data.inspiration = 5;
        card.aspect3 = Aspect.AIR;

        boolean result = card.activate(null, data);

        assertTrue(result);
        assertEquals(40, data.getTotal("ALCHEMY"));
        assertEquals(6, data.inspiration);
    }
}
