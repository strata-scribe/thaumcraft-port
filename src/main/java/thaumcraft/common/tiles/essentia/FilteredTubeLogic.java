package thaumcraft.common.tiles.essentia;

import net.minecraft.core.Direction;
import thaumcraft.api.aspects.Aspect;

public class FilteredTubeLogic extends TubeLogic {
    private Aspect aspectFilter = null;

    public FilteredTubeLogic(Runnable setChangedCallback) {
        super(setChangedCallback);
    }

    public void setAspectFilter(Aspect filter) {
        this.aspectFilter = filter;
        // Trigger save if filter changed? Since it's logic, caller must handle it or callback.
    }

    public Aspect getAspectFilter() {
        return aspectFilter;
    }

    @Override
    public void setSuction(Aspect aspect, int amount) {
        if (aspectFilter != null) {
            if (aspect != null && aspect != aspectFilter) {
                // Ignore non-matching suction
                return;
            }
            // If suction is untyped, we type it to our filter.
            super.setSuction(aspectFilter, amount);
        } else {
            super.setSuction(aspect, amount);
        }
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, Direction face) {
        if (aspectFilter != null && aspect != null && aspect != aspectFilter) {
            return 0; // Blocks non-matching aspect flow
        }
        return super.addEssentia(aspect, amount, face);
    }
}
