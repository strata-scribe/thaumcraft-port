package thaumcraft.common.blocks.essentia.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SmelterAttachmentLogic Unit Tests")
class SmelterAttachmentLogicTest {

    @Nested
    @DisplayName("canAttachToSmelter")
    class CanAttachToSmelterTests {

        @Test
        @DisplayName("Valid side faces attach successfully when smelter is present")
        void validSideFaces() {
            // Smelter facing NORTH: attachment can be from NORTH (attaching to south side),
            // EAST (attaching to west side), WEST (attaching to east side), but NOT SOUTH (smelter's front face)
            assertTrue(SmelterAttachmentLogic.canAttachToSmelter("north", "south", true));
            assertTrue(SmelterAttachmentLogic.canAttachToSmelter("east", "north", true));
            assertTrue(SmelterAttachmentLogic.canAttachToSmelter("west", "north", true));
            assertTrue(SmelterAttachmentLogic.canAttachToSmelter("south", "east", true));
            // Case insensitivity
            assertTrue(SmelterAttachmentLogic.canAttachToSmelter("NORTH", "south", true));
            assertTrue(SmelterAttachmentLogic.canAttachToSmelter("East", "North", true));
        }

        @Test
        @DisplayName("Cannot attach to smelter front face")
        void frontFaceFails() {
            // Attachment face matches smelter facing -> attempting to attach to front face
            assertFalse(SmelterAttachmentLogic.canAttachToSmelter("north", "north", true));
            assertFalse(SmelterAttachmentLogic.canAttachToSmelter("south", "south", true));
            assertFalse(SmelterAttachmentLogic.canAttachToSmelter("east", "east", true));
            assertFalse(SmelterAttachmentLogic.canAttachToSmelter("west", "west", true));
            // Case insensitivity
            assertFalse(SmelterAttachmentLogic.canAttachToSmelter("NORTH", "north", true));
        }

        @Test
        @DisplayName("Non-smelter blocks reject attachment")
        void nonSmelterFails() {
            assertFalse(SmelterAttachmentLogic.canAttachToSmelter("north", "south", false));
            assertFalse(SmelterAttachmentLogic.canAttachToSmelter("east", "west", false));
        }

        @Test
        @DisplayName("Null parameters reject attachment")
        void nullParametersFail() {
            assertFalse(SmelterAttachmentLogic.canAttachToSmelter(null, "north", true));
            assertFalse(SmelterAttachmentLogic.canAttachToSmelter("north", null, true));
            assertFalse(SmelterAttachmentLogic.canAttachToSmelter(null, null, true));
            assertFalse(SmelterAttachmentLogic.canAttachToSmelter(null, null, false));
        }
    }

    @Nested
    @DisplayName("isValidAttachmentFace")
    class IsValidAttachmentFaceTests {

        @Test
        @DisplayName("Horizontal faces are valid attachment faces")
        void horizontalFacesValid() {
            assertTrue(SmelterAttachmentLogic.isValidAttachmentFace("north"));
            assertTrue(SmelterAttachmentLogic.isValidAttachmentFace("south"));
            assertTrue(SmelterAttachmentLogic.isValidAttachmentFace("east"));
            assertTrue(SmelterAttachmentLogic.isValidAttachmentFace("west"));
            // Case insensitivity
            assertTrue(SmelterAttachmentLogic.isValidAttachmentFace("NORTH"));
            assertTrue(SmelterAttachmentLogic.isValidAttachmentFace("South"));
        }

        @Test
        @DisplayName("Vertical faces are invalid attachment faces")
        void verticalFacesInvalid() {
            assertFalse(SmelterAttachmentLogic.isValidAttachmentFace("up"));
            assertFalse(SmelterAttachmentLogic.isValidAttachmentFace("down"));
            assertFalse(SmelterAttachmentLogic.isValidAttachmentFace("UP"));
            assertFalse(SmelterAttachmentLogic.isValidAttachmentFace("Down"));
        }

        @Test
        @DisplayName("Invalid or null names return false")
        void invalidNamesReturnFalse() {
            assertFalse(SmelterAttachmentLogic.isValidAttachmentFace("top"));
            assertFalse(SmelterAttachmentLogic.isValidAttachmentFace("bottom"));
            assertFalse(SmelterAttachmentLogic.isValidAttachmentFace(""));
            assertFalse(SmelterAttachmentLogic.isValidAttachmentFace(null));
        }
    }
}
