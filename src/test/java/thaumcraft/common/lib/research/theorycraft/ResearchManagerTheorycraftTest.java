package thaumcraft.common.lib.research.theorycraft;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;
import thaumcraft.api.research.theorycraft.TheorycraftManager;
import thaumcraft.common.config.ConfigResearch;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.research.theorycraft.logic.TheorycraftCardBonusLogic;

import static org.junit.jupiter.api.Assertions.*;

public class ResearchManagerTheorycraftTest {

    @BeforeEach
    public void setUp() {
        ResearchManager.clearTheorycraft();
        ResearchManager.initTheorycraft();
    }

    @Test
    public void testTheorycraftInitializationAndCounts() {
        assertTrue(TheorycraftManager.cards.size() >= 26, "Expected at least 26 theorycraft cards registered");
        assertTrue(TheorycraftManager.aids.size() >= 13, "Expected at least 13 theorycraft aids registered");
    }

    @Test
    public void testSpecificCardsRegistered() {
        Class<?>[] expectedCards = new Class<?>[] {
            CardAwareness.class,
            CardBeacon.class,
            CardCalibrate.class,
            CardCelestial.class,
            CardChannel.class,
            CardConcentrate.class,
            CardCurio.class,
            CardDarkWhispers.class,
            CardDragonEgg.class,
            CardEnchantment.class,
            CardFocus.class,
            CardGlyphs.class,
            CardInfuse.class,
            CardMeasure.class,
            CardMindOverMatter.class,
            CardPortal.class,
            CardReactions.class,
            CardRealization.class,
            CardRevelation.class,
            CardScripting.class,
            CardSculpting.class,
            CardSpellbinding.class,
            CardSynergy.class,
            CardSynthesis.class,
            CardTinker.class,
            CardTruth.class
        };

        for (Class<?> cardClass : expectedCards) {
            assertTrue(TheorycraftManager.cards.containsKey(cardClass.getName()),
                "Card not registered: " + cardClass.getSimpleName());
            assertEquals(cardClass, TheorycraftManager.cards.get(cardClass.getName()));
        }
    }

    @Test
    public void testSpecificAidsRegistered() {
        Class<?>[] expectedAids = new Class<?>[] {
            AidBasicAlchemy.class,
            AidBasicArtifice.class,
            AidBasicAuromancy.class,
            AidBasicEldritch.class,
            AidBasicGolemancy.class,
            AidBasicInfusion.class,
            AidBeacon.class,
            AidBrainInAJar.class,
            AidDragonEgg.class,
            AidEnchantmentTable.class,
            AidGlyphedStone.class,
            AidPortal.AidPortalEnd.class,
            AidPortal.AidPortalNether.class
        };

        for (Class<?> aidClass : expectedAids) {
            assertTrue(TheorycraftManager.aids.containsKey(aidClass.getName()),
                "Aid not registered: " + aidClass.getSimpleName());
        }
    }

    @Test
    public void testCardPropertiesWithoutPlayer() {
        // CardFocus
        CardFocus cardFocus = new CardFocus();
        assertEquals(1, cardFocus.getInspirationCost());
        assertEquals("AUROMANCY", cardFocus.getResearchCategory());
        assertFalse(cardFocus.isAidOnly());

        // CardBeacon
        CardBeacon cardBeacon = new CardBeacon();
        assertEquals(-2, cardBeacon.getInspirationCost());
        assertNull(cardBeacon.getResearchCategory());
        assertTrue(cardBeacon.isAidOnly());

        // CardPortal
        CardPortal cardPortal = new CardPortal();
        assertEquals(-1, cardPortal.getInspirationCost());
        assertEquals("ELDRITCH", cardPortal.getResearchCategory());
        assertTrue(cardPortal.isAidOnly());

        // CardMeasure
        CardMeasure cardMeasure = new CardMeasure();
        assertEquals(1, cardMeasure.getInspirationCost());
        assertEquals("INFUSION", cardMeasure.getResearchCategory());
        assertFalse(cardMeasure.isAidOnly());

        // CardSculpting
        CardSculpting cardSculpting = new CardSculpting();
        assertEquals(1, cardSculpting.getInspirationCost());
        assertEquals("GOLEMANCY", cardSculpting.getResearchCategory());
        assertFalse(cardSculpting.isAidOnly());

        // CardCalibrate
        CardCalibrate cardCalibrate = new CardCalibrate();
        assertEquals(1, cardCalibrate.getInspirationCost());
        assertEquals("ARTIFICE", cardCalibrate.getResearchCategory());
        assertFalse(cardCalibrate.isAidOnly());

        // CardTruth
        CardTruth cardTruth = new CardTruth();
        assertEquals(1, cardTruth.getInspirationCost());
        assertEquals("ELDRITCH", cardTruth.getResearchCategory());
        assertFalse(cardTruth.isAidOnly());
    }

    @Test
    public void testResearchTableDataCardInteractions() {
        ResearchTableData data = new ResearchTableData(null);

        // Verify initial state
        assertEquals(0, data.bonusDraws);
        assertEquals(0, data.penaltyStart);
        assertEquals(0, data.getTotal("AUROMANCY"));
        assertEquals(0, data.getTotal("ARTIFICE"));
        assertEquals(0, data.getTotal("INFUSION"));
        assertEquals(0, data.getTotal("GOLEMANCY"));
        assertEquals(0, data.getTotal("ELDRITCH"));

        // Activate CardFocus
        CardFocus cardFocus = new CardFocus();
        assertTrue(cardFocus.activate(null, data));
        assertEquals(1, data.bonusDraws);
        assertEquals(15, data.getTotal("AUROMANCY"));

        // Activate CardCalibrate
        CardCalibrate cardCalibrate = new CardCalibrate();
        assertTrue(cardCalibrate.activate(null, data));
        assertEquals(2, data.bonusDraws);
        assertEquals(15, data.getTotal("ARTIFICE"));

        // Activate CardMeasure
        CardMeasure cardMeasure = new CardMeasure();
        assertTrue(cardMeasure.activate(null, data));
        assertEquals(3, data.bonusDraws);
        assertEquals(15, data.getTotal("INFUSION"));

        // Activate CardSculpting
        CardSculpting cardSculpting = new CardSculpting();
        assertTrue(cardSculpting.activate(null, data));
        assertEquals(4, data.bonusDraws);
        assertEquals(15, data.getTotal("GOLEMANCY"));

        // Activate CardBeacon
        CardBeacon cardBeacon = new CardBeacon();
        assertTrue(cardBeacon.activate(null, data));
        assertEquals(5, data.bonusDraws);
        assertEquals(1, data.penaltyStart);

        // Test bonus logic methods directly on data state
        data.bonusDraws = TheorycraftCardBonusLogic.calculatePortalBonusDraws(data.bonusDraws);
        assertEquals(7, data.bonusDraws);

        int truthBonus = TheorycraftCardBonusLogic.calculateTruthBonus(data.getTotal("ELDRITCH"), 18);
        data.addTotal("ELDRITCH", truthBonus);
        assertEquals(18, data.getTotal("ELDRITCH"));

        int portalEldritch = TheorycraftCardBonusLogic.calculatePortalEldritchBonus(data.getTotal("ELDRITCH"), 10);
        data.addTotal("ELDRITCH", portalEldritch - data.getTotal("ELDRITCH"));
        assertEquals(28, data.getTotal("ELDRITCH"));

        // Test beacon penalty logic
        data.penaltyStart = TheorycraftCardBonusLogic.calculateBeaconPenalty(data.penaltyStart);
        assertEquals(2, data.penaltyStart);
    }

    @Test
    public void testClearTheorycraft() {
        assertFalse(TheorycraftManager.cards.isEmpty());
        assertFalse(TheorycraftManager.aids.isEmpty());

        ResearchManager.clearTheorycraft();

        assertTrue(TheorycraftManager.cards.isEmpty());
        assertTrue(TheorycraftManager.aids.isEmpty());

        // Re-initialize for subsequent tests
        ResearchManager.initTheorycraft();
        assertEquals(26, TheorycraftManager.cards.size());
        assertEquals(13, TheorycraftManager.aids.size());
    }

    @Test
    public void testConfigResearchInitRegistersTheorycraft() {
        ResearchManager.clearTheorycraft();
        assertTrue(TheorycraftManager.cards.isEmpty());
        assertTrue(TheorycraftManager.aids.isEmpty());

        ConfigResearch.init();

        assertTrue(TheorycraftManager.cards.size() >= 26);
        assertTrue(TheorycraftManager.aids.size() >= 13);
    }
}
