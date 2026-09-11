package thaumcraft.common.tiles.essentia;

import thaumcraft.api.aspects.Aspect;

public class VoidTubeLogic extends TubeLogic {

    private Runnable onVoidCallback;

    public VoidTubeLogic(Runnable setChangedCallback, Runnable onVoidCallback) {
        super(setChangedCallback);
        this.onVoidCallback = onVoidCallback;
    }

    public void setOnVoidCallback(Runnable onVoidCallback) {
        this.onVoidCallback = onVoidCallback;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, net.minecraft.core.Direction face) {
        if (!isOpen(face)) return 0;

        int added = super.addEssentia(aspect, amount, face);

        if (added < amount) {
            if (onVoidCallback != null) {
                onVoidCallback.run();
            }
            return amount;
        }

        return added;
    }
}
