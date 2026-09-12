package thaumcraft.common.lib.research.theorycraft;

import org.junit.jupiter.api.Test;
import thaumcraft.common.lib.research.theorycraft.logic.CardSynthesisLogic;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class CardSynthesisLogicTest {

    @Test
    public void testInitializeSynthesis() {
        List<String[]> compounds = new ArrayList<>();
        compounds.add(new String[] {"vacuos", "aer", "perditio"});
        compounds.add(new String[] {"lux", "aer", "ignis"});
        compounds.add(new String[] {"motus", "aer", "ordo"});

        CardSynthesisLogic.SynthesisResult result = CardSynthesisLogic.initializeSynthesis(12345L, compounds);
        assertNotNull(result);

        boolean valid = false;
        for (String[] comp : compounds) {
            if (comp[0].equals(result.aspect3) && comp[1].equals(result.aspect1) && comp[2].equals(result.aspect2)) {
                valid = true;
                break;
            }
        }
        assertTrue(valid, "Result should be one of the provided compound aspects.");
    }

    @Test
    public void testInitializeSynthesisEmpty() {
        List<String[]> compounds = new ArrayList<>();
        CardSynthesisLogic.SynthesisResult result = CardSynthesisLogic.initializeSynthesis(12345L, compounds);
        assertNull(result);
    }

    @Test
    public void testCalculateActivation_InspirationGained() {
        CardSynthesisLogic.ActivationResult result = CardSynthesisLogic.calculateActivation(0.1f);
        assertEquals(40, result.bonusProgress);
        assertEquals(1, result.bonusInspiration);
    }

    @Test
    public void testCalculateActivation_NoInspiration() {
        CardSynthesisLogic.ActivationResult result = CardSynthesisLogic.calculateActivation(0.4f);
        assertEquals(40, result.bonusProgress);
        assertEquals(0, result.bonusInspiration);
    }
}
