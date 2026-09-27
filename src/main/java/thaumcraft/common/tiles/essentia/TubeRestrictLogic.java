package thaumcraft.common.tiles.essentia;

import net.minecraft.core.Direction;

public class TubeRestrictLogic extends TubeLogic {

    public TubeRestrictLogic(Runnable setChangedCallback) {
        super(setChangedCallback);
    }

    @Override
    public int getSuctionAmount(Direction face) {
        int amount = super.getSuctionAmount(face);
        return Math.max(0, amount / 2); // 50% reduction
    }
}
