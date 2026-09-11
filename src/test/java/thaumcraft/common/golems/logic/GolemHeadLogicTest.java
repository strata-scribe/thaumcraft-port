package thaumcraft.common.golems.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

public class GolemHeadLogicTest {

    @Test
    @DisplayName("Smart trait enables exact NBT filtering")
    void testExactFiltering() {
        assertTrue(GolemHeadLogic.hasExactFiltering(Set.of("smart", "fighter")));
        assertTrue(GolemHeadLogic.hasExactFiltering(Set.of("SMART")));
        assertFalse(GolemHeadLogic.hasExactFiltering(Set.of("clueless", "scout")));
        assertFalse(GolemHeadLogic.hasExactFiltering(Set.of()));
        assertFalse(GolemHeadLogic.hasExactFiltering(null));
    }

    @Test
    @DisplayName("Clueless trait enforces simple matching")
    void testSimpleFiltering() {
        assertTrue(GolemHeadLogic.hasSimpleFiltering(Set.of("clueless", "heavy")));
        assertTrue(GolemHeadLogic.hasSimpleFiltering(Set.of("CLUELESS")));
        assertFalse(GolemHeadLogic.hasSimpleFiltering(Set.of("smart", "scout")));
        assertFalse(GolemHeadLogic.hasSimpleFiltering(Set.of()));
        assertFalse(GolemHeadLogic.hasSimpleFiltering(null));
    }

    @Test
    @DisplayName("Scout trait doubles perception radius")
    void testCalculatePerceptionRadius() {
        int baseRadius = 16;

        // Base radius is doubled
        assertEquals(32, GolemHeadLogic.calculatePerceptionRadius(baseRadius, Set.of("scout", "light")));
        assertEquals(32, GolemHeadLogic.calculatePerceptionRadius(baseRadius, Set.of("SCOUT")));

        // Base radius remains unchanged
        assertEquals(16, GolemHeadLogic.calculatePerceptionRadius(baseRadius, Set.of("smart", "armored")));
        assertEquals(16, GolemHeadLogic.calculatePerceptionRadius(baseRadius, Set.of()));
        assertEquals(16, GolemHeadLogic.calculatePerceptionRadius(baseRadius, null));
    }

    @Test
    @DisplayName("Aggressive trait enables hostile targeting")
    void testAggressiveTargeting() {
        assertTrue(GolemHeadLogic.isAggressive(Set.of("aggressive", "brutal")));
        assertTrue(GolemHeadLogic.isAggressive(Set.of("AGGRESSIVE")));
        assertFalse(GolemHeadLogic.isAggressive(Set.of("scout", "fragile")));
        assertFalse(GolemHeadLogic.isAggressive(Set.of()));
        assertFalse(GolemHeadLogic.isAggressive(null));
    }
}
