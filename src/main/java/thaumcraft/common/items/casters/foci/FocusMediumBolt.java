package thaumcraft.common.items.casters.foci;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.common.casters.FocusLogic;

public class FocusMediumBolt extends FocusMediumTouch {

    public FocusMediumBolt() {
        super();
    }

    @Override
    public String getResearch() {
        return "FOCUSBOLT";
    }

    @Override
    public String getKey() {
        return "thaumcraft.BOLT";
    }

    @Override
    public int getComplexity() {
        return FocusLogic.calculateBoltComplexity();
    }

    @Override
    public Aspect getAspect() {
        return Aspect.ENERGY;
    }

    @Override
    protected double getReachDistance() {
        return 16.0;
    }

    @Override
    public boolean execute(Trajectory trajectory) {
        return true;
    }
}
