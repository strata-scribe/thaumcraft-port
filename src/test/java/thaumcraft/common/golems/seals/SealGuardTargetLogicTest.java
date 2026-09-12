package thaumcraft.common.golems.seals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SealGuardTargetLogicTest {

    @Test
    @DisplayName("Test target monsters")
    void testTargetMonsters() {
        SealGuardTargetLogic.EntityInfo monster = new SealGuardTargetLogic.EntityInfo("zombie", SealGuardTargetLogic.EntityCategory.MONSTER, Collections.emptySet());
        assertTrue(SealGuardTargetLogic.isValidTarget(monster, true, false, false, Collections.emptySet()));
        assertFalse(SealGuardTargetLogic.isValidTarget(monster, false, false, false, Collections.emptySet()));
    }

    @Test
    @DisplayName("Test target animals")
    void testTargetAnimals() {
        SealGuardTargetLogic.EntityInfo animal = new SealGuardTargetLogic.EntityInfo("cow", SealGuardTargetLogic.EntityCategory.ANIMAL, Collections.emptySet());
        assertTrue(SealGuardTargetLogic.isValidTarget(animal, false, true, false, Collections.emptySet()));
        assertFalse(SealGuardTargetLogic.isValidTarget(animal, false, false, false, Collections.emptySet()));
    }

    @Test
    @DisplayName("Test target players")
    void testTargetPlayers() {
        SealGuardTargetLogic.EntityInfo player = new SealGuardTargetLogic.EntityInfo("steve", SealGuardTargetLogic.EntityCategory.PLAYER, Collections.emptySet());
        assertTrue(SealGuardTargetLogic.isValidTarget(player, false, false, true, Collections.emptySet()));
        assertFalse(SealGuardTargetLogic.isValidTarget(player, false, false, false, Collections.emptySet()));
    }

    @Test
    @DisplayName("Test target OTHER category")
    void testTargetOther() {
        SealGuardTargetLogic.EntityInfo other = new SealGuardTargetLogic.EntityInfo("armor_stand", SealGuardTargetLogic.EntityCategory.OTHER, Collections.emptySet());
        assertFalse(SealGuardTargetLogic.isValidTarget(other, true, true, true, Collections.emptySet()));
    }

    @Test
    @DisplayName("Test target whitelisted tags")
    void testTargetWhitelistedTags() {
        SealGuardTargetLogic.EntityInfo entityWithTag = new SealGuardTargetLogic.EntityInfo("custom_entity", SealGuardTargetLogic.EntityCategory.OTHER, Set.of("targetable"));
        assertTrue(SealGuardTargetLogic.isValidTarget(entityWithTag, false, false, false, Set.of("targetable")));
        assertFalse(SealGuardTargetLogic.isValidTarget(entityWithTag, false, false, false, Set.of("ignored_tag")));
    }

    @Test
    @DisplayName("Test null entity and null tags")
    void testNullInputs() {
        assertFalse(SealGuardTargetLogic.isValidTarget(null, true, true, true, Collections.emptySet()));

        SealGuardTargetLogic.EntityInfo noCategoryNoTags = new SealGuardTargetLogic.EntityInfo("test", null, null);
        assertFalse(SealGuardTargetLogic.isValidTarget(noCategoryNoTags, true, true, true, Collections.emptySet()));

        SealGuardTargetLogic.EntityInfo validCategoryNoTags = new SealGuardTargetLogic.EntityInfo("zombie", SealGuardTargetLogic.EntityCategory.MONSTER, null);
        assertTrue(SealGuardTargetLogic.isValidTarget(validCategoryNoTags, true, false, false, Set.of("targetable")));

        SealGuardTargetLogic.EntityInfo noCategoryWithTags = new SealGuardTargetLogic.EntityInfo("test", null, Set.of("targetable"));
        assertTrue(SealGuardTargetLogic.isValidTarget(noCategoryWithTags, false, false, false, Set.of("targetable")));
    }
}
