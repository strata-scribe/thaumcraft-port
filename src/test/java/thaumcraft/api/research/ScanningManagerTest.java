package thaumcraft.api.research;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thaumcraft.common.lib.research.ScanEnchantment;
import thaumcraft.common.lib.research.ScanGeneric;
import thaumcraft.common.lib.research.ScanPotion;
import thaumcraft.common.lib.research.ScanSky;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ScanningManagerTest {

    @BeforeEach
    @AfterEach
    public void cleanup() {
        ScanningManager.clear();
    }

    @Test
    public void testScanningManagerRegistry() {
        // Clear and verify empty list
        ScanningManager.clear();
        assertTrue(ScanningManager.getScannableThings().isEmpty());

        IScanThing thing1 = new ScanGeneric("RESEARCH_1");
        IScanThing thing2 = new ScanGeneric("RESEARCH_2");

        // Add things and verify retrieval
        ScanningManager.addScannableThing(thing1);
        ScanningManager.addScannableThing(thing2);
        List<IScanThing> things = ScanningManager.getScannableThings();
        assertEquals(2, things.size());
        assertTrue(things.contains(thing1));
        assertTrue(things.contains(thing2));

        // Verify unmodifiable list
        assertThrows(UnsupportedOperationException.class, () -> things.add(new ScanGeneric("FAIL")));

        // Verify duplicates are not added
        ScanningManager.addScannableThing(thing1);
        assertEquals(2, ScanningManager.getScannableThings().size());

        // Verify nulls are ignored
        ScanningManager.addScannableThing(null);
        assertEquals(2, ScanningManager.getScannableThings().size());

        // Clear again
        ScanningManager.clear();
        assertTrue(ScanningManager.getScannableThings().isEmpty());
    }

    @Test
    public void testScanGeneric() {
        // Custom predicate matching / non-matching
        ScanGeneric magicScan = new ScanGeneric("MAGIC_THING", obj -> obj instanceof String str && str.startsWith("thaum:"));
        assertEquals("MAGIC_THING", magicScan.getResearchKey(null, "thaum:jar"));
        assertTrue(magicScan.checkThing(null, "thaum:jar"));
        assertFalse(magicScan.checkThing(null, "minecraft:stone"));
        assertFalse(magicScan.checkThing(null, 123));
        assertFalse(magicScan.checkThing(null, null));

        // Default non-null predicate
        ScanGeneric defaultScan = new ScanGeneric("DEFAULT_THING");
        assertEquals("DEFAULT_THING", defaultScan.getResearchKey(null, null));
        assertTrue(defaultScan.checkThing(null, "anything"));
        assertTrue(defaultScan.checkThing(null, 42));
        assertFalse(defaultScan.checkThing(null, null));

        // Null predicate in 2-arg constructor defaults to Objects::nonNull
        ScanGeneric nullPredScan = new ScanGeneric("NULL_PRED", null);
        assertTrue(nullPredScan.checkThing(null, "hello"));
        assertFalse(nullPredScan.checkThing(null, null));
    }

    @Test
    public void testScanSky() {
        ScanSky scanSky = new ScanSky("SKY_RESEARCH");

        // Correct research key returned
        assertEquals("SKY_RESEARCH", scanSky.getResearchKey(null, null));
        assertEquals("SKY_RESEARCH", scanSky.getResearchKey(null, "foo"));

        // Non-null obj returns false
        assertFalse(scanSky.checkThing(null, "some_block"));
        assertFalse(scanSky.checkThing(null, 123));
        assertFalse(scanSky.checkThing(null, new Object()));

        // Null player returns false
        assertFalse(scanSky.checkThing(null, null));

        // Look vector pitch / upward view returns true when y > 0.5, false when y <= 0.5
        assertTrue(ScanSky.isUpwardView(0.51), "y = 0.51 must be upward view (> 0.5)");
        assertTrue(ScanSky.isUpwardView(0.8), "y = 0.8 must be upward view (> 0.5)");
        assertTrue(ScanSky.isUpwardView(1.0), "y = 1.0 (straight up) must be upward view (> 0.5)");

        assertFalse(ScanSky.isUpwardView(0.50), "y = 0.50 must not be upward view (<= 0.5)");
        assertFalse(ScanSky.isUpwardView(0.49), "y = 0.49 must not be upward view (<= 0.5)");
        assertFalse(ScanSky.isUpwardView(0.0), "y = 0.0 (horizontal) must not be upward view (<= 0.5)");
        assertFalse(ScanSky.isUpwardView(-0.5), "y = -0.5 (downward) must not be upward view (<= 0.5)");
        assertFalse(ScanSky.isUpwardView(-1.0), "y = -1.0 (straight down) must not be upward view (<= 0.5)");

        // View vector pitch conversion: viewVector.y = -sin(pitch)
        // Looking up at sky (-60 degrees pitch): -sin(-60 deg) = sin(60 deg) = 0.866 > 0.5
        double pitchUp60 = -Math.sin(Math.toRadians(-60.0));
        assertTrue(ScanSky.isUpwardView(pitchUp60), "Pitch -60 deg must be upward view (> 0.5)");

        // Looking straight up (-90 degrees pitch): -sin(-90 deg) = 1.0 > 0.5
        double pitchUp90 = -Math.sin(Math.toRadians(-90.0));
        assertTrue(ScanSky.isUpwardView(pitchUp90), "Pitch -90 deg must be upward view (> 0.5)");

        // Looking moderately up (-35 degrees pitch): -sin(-35 deg) = sin(35 deg) = 0.5735 > 0.5
        double pitchUp35 = -Math.sin(Math.toRadians(-35.0));
        assertTrue(ScanSky.isUpwardView(pitchUp35), "Pitch -35 deg must be upward view (> 0.5)");

        // Looking slightly up (-20 degrees pitch): -sin(-20 deg) = sin(20 deg) = 0.342 <= 0.5
        double pitchUp20 = -Math.sin(Math.toRadians(-20.0));
        assertFalse(ScanSky.isUpwardView(pitchUp20), "Pitch -20 deg must not be upward view (<= 0.5)");

        // Horizontal (0 degrees pitch): -sin(0) = 0.0 <= 0.5
        double pitchHorizontal = -Math.sin(Math.toRadians(0.0));
        assertFalse(ScanSky.isUpwardView(pitchHorizontal), "Pitch 0 deg must not be upward view (<= 0.5)");

        // Downward (+45 degrees pitch): -sin(45 deg) = -0.707 <= 0.5
        double pitchDown45 = -Math.sin(Math.toRadians(45.0));
        assertFalse(ScanSky.isUpwardView(pitchDown45), "Pitch +45 deg must not be upward view (<= 0.5)");

        // Subclass logic verification: when isLookingAtSky returns true/false
        ScanSky skyCustom = new ScanSky("SKY_CUSTOM") {
            private boolean lookingUp = true;
            @Override
            protected boolean isLookingAtSky(Player player) {
                return lookingUp;
            }
        };

        // When looking up is true, non-null obj still returns false
        assertFalse(skyCustom.checkThing(null, "some_block"));
        // When looking up is true, null player still returns false
        assertFalse(skyCustom.checkThing(null, null));
    }

    @Test
    public void testScanEnchantment() {
        ScanEnchantment scan = new ScanEnchantment("ENCHANT_RESEARCH");
        assertEquals("ENCHANT_RESEARCH", scan.getResearchKey(null, null));
        assertEquals("ENCHANT_RESEARCH", scan.getResearchKey(null, "some_item"));

        // Non-ItemStack returns false
        assertFalse(scan.checkThing(null, null));
        assertFalse(scan.checkThing(null, "not an itemstack"));
        assertFalse(scan.checkThing(null, 100));
        assertFalse(scan.checkThing(null, new Object()));

        // Empty stack returns false
        assertFalse(scan.checkThing(null, ItemStack.EMPTY));
    }

    @Test
    public void testScanPotion() {
        ScanPotion scan = new ScanPotion("POTION_RESEARCH");
        assertEquals("POTION_RESEARCH", scan.getResearchKey(null, null));
        assertEquals("POTION_RESEARCH", scan.getResearchKey(null, "some_item"));

        // Non-ItemStack returns false
        assertFalse(scan.checkThing(null, null));
        assertFalse(scan.checkThing(null, "not an itemstack"));
        assertFalse(scan.checkThing(null, 100));
        assertFalse(scan.checkThing(null, new Object()));

        // Empty stack returns false
        assertFalse(scan.checkThing(null, ItemStack.EMPTY));
    }

    @Test
    public void testScanningManagerFindMatchingScanAndIsThingScannable() {
        ScanGeneric scanStr = new ScanGeneric("STRING_RESEARCH", obj -> obj instanceof String);
        ScanGeneric scanInt = new ScanGeneric("INT_RESEARCH", obj -> obj instanceof Integer);

        ScanningManager.addScannableThing(scanStr);
        ScanningManager.addScannableThing(scanInt);

        // Find matching scan
        IScanThing matched1 = ScanningManager.findMatchingScan(null, "hello");
        assertNotNull(matched1);
        assertSame(scanStr, matched1);
        assertEquals("STRING_RESEARCH", matched1.getResearchKey(null, "hello"));

        IScanThing matched2 = ScanningManager.findMatchingScan(null, 42);
        assertNotNull(matched2);
        assertSame(scanInt, matched2);
        assertEquals("INT_RESEARCH", matched2.getResearchKey(null, 42));

        // Unmatched object
        assertNull(ScanningManager.findMatchingScan(null, 3.14));
        assertNull(ScanningManager.findMatchingScan(null, null));

        // isThingScannable
        assertTrue(ScanningManager.isThingScannable(null, "world"));
        assertTrue(ScanningManager.isThingScannable(null, 100));
        assertFalse(ScanningManager.isThingScannable(null, 3.14));
        assertFalse(ScanningManager.isThingScannable(null, null));
    }
}
