package thaumcraft.common.tiles.essentia;

import net.minecraft.core.Direction;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;

public class TubeLogic implements IEssentiaTransport {
    private Aspect essentiaType = null;
    private int essentiaAmount = 0;
    private Aspect suctionType = null;
    private int suctionAmount = 0;

    private boolean[] openFaces = new boolean[]{true, true, true, true, true, true};

    private final Runnable setChangedCallback;

    public TubeLogic(Runnable setChangedCallback) {
        this.setChangedCallback = setChangedCallback;
    }

    @Override
    public boolean isConnectable(Direction face) { return isOpen(face); }

    @Override
    public boolean canInputFrom(Direction face) { return isOpen(face); }

    @Override
    public boolean canOutputTo(Direction face) { return isOpen(face); }

    @Override
    public void setSuction(Aspect aspect, int amount) {
        this.suctionType = aspect;
        this.suctionAmount = amount;
    }

    @Override
    public Aspect getSuctionType(Direction face) { return suctionType; }

    @Override
    public int getSuctionAmount(Direction face) { return suctionAmount; }

    @Override
    public int takeEssentia(Aspect aspect, int amount, Direction face) {
        if (!isOpen(face)) return 0;
        if (this.essentiaAmount > 0 && this.essentiaType == aspect) {
            int toTake = Math.min(amount, this.essentiaAmount);
            this.essentiaAmount -= toTake;
            if (this.essentiaAmount == 0) this.essentiaType = null;
            setChangedCallback.run();
            return toTake;
        }
        return 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, Direction face) {
        if (!isOpen(face)) return 0;
        if (this.essentiaAmount == 0) {
            this.essentiaType = aspect;
            this.essentiaAmount = Math.min(amount, 1);
            setChangedCallback.run();
            return this.essentiaAmount;
        } else if (this.essentiaType == aspect && this.essentiaAmount < 1) {
            this.essentiaAmount++;
            setChangedCallback.run();
            return 1;
        }
        return 0;
    }

    @Override
    public Aspect getEssentiaType(Direction face) { return essentiaType; }

    @Override
    public int getEssentiaAmount(Direction face) { return essentiaAmount; }

    @Override
    public int getMinimumSuction() { return 0; }

    public boolean isOpen(Direction face) {
        return openFaces[face.get3DDataValue()];
    }

    public void toggleOpenFace(Direction face) {
        openFaces[face.get3DDataValue()] = !openFaces[face.get3DDataValue()];
        setChangedCallback.run();
    }

    // For serialization
    public void setOpenFace(Direction face, boolean open) {
        openFaces[face.get3DDataValue()] = open;
    }

    public void setEssentia(Aspect type, int amount) {
        this.essentiaType = type;
        this.essentiaAmount = amount;
    }
}
