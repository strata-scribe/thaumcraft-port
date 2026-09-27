package thaumcraft.common.tiles.essentia.logic;

import net.minecraft.core.Direction;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.tiles.essentia.TubeLogic;

public class TubeDirectionalLogic extends TubeLogic {

    private Direction facing = Direction.NORTH;

    public TubeDirectionalLogic(Runnable setChangedCallback) {
        super(setChangedCallback);
    }

    public void setFacing(Direction facing) {
        this.facing = facing;
    }

    public Direction getFacing() {
        return facing;
    }

    @Override
    public boolean canInputFrom(Direction face) {
        return super.canInputFrom(face) && face == facing.getOpposite();
    }

    @Override
    public boolean canOutputTo(Direction face) {
        return super.canOutputTo(face) && face == facing;
    }

    @Override
    public int getSuctionAmount(Direction face) {
        if (face != facing.getOpposite()) return 0;
        return super.getSuctionAmount(face);
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, Direction face) {
        if (face != facing) return 0;
        return super.takeEssentia(aspect, amount, face);
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, Direction face) {
        if (face != facing.getOpposite()) return 0;
        return super.addEssentia(aspect, amount, face);
    }
}
