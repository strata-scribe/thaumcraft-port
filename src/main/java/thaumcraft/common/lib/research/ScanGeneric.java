package thaumcraft.common.lib.research;

import net.minecraft.world.entity.player.Player;
import thaumcraft.api.research.IScanThing;

import java.util.Objects;
import java.util.function.Predicate;

public class ScanGeneric implements IScanThing {
    private final String research;
    private final Predicate<Object> predicate;

    public ScanGeneric(String research, Predicate<Object> predicate) {
        this.research = research;
        this.predicate = predicate != null ? predicate : Objects::nonNull;
    }

    public ScanGeneric(String research) {
        this(research, Objects::nonNull);
    }

    @Override
    public boolean checkThing(Player player, Object obj) {
        return predicate.test(obj);
    }

    @Override
    public String getResearchKey(Player player, Object object) {
        return research;
    }
}
