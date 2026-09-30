package thaumcraft.api.research;

import net.minecraft.world.entity.player.Player;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ScanningManager {
    private static final List<IScanThing> things = new CopyOnWriteArrayList<>();

    public static void addScannableThing(IScanThing thing) {
        if (thing != null && !things.contains(thing)) {
            things.add(thing);
        }
    }

    public static List<IScanThing> getScannableThings() {
        return Collections.unmodifiableList(things);
    }

    public static IScanThing findMatchingScan(Player player, Object obj) {
        for (IScanThing thing : things) {
            if (thing.checkThing(player, obj)) {
                return thing;
            }
        }
        return null;
    }

    public static boolean isThingScannable(Player player, Object obj) {
        return findMatchingScan(player, obj) != null;
    }

    public static void clear() {
        things.clear();
    }
}
