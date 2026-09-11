package thaumcraft.common.items.casters.foci;

import thaumcraft.api.casters.FocusModSplit;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.common.casters.FocusLogic;

public class FocusModSplitTrajectory extends FocusModSplit {

    public FocusModSplitTrajectory() {
        super();
    }

    @Override
    public String getResearch() {
        return "FOCUSSPLIT";
    }

    @Override
    public String getKey() {
        return "thaumcraft.SPLITTRAJECTORY";
    }

    @Override
    public int getComplexity() {
        return FocusLogic.calculateSplitTrajectoryComplexity();
    }

    @Override
    public EnumSupplyType[] mustBeSupplied() {
        return new EnumSupplyType[] { EnumSupplyType.TRAJECTORY };
    }

    @Override
    public EnumSupplyType[] willSupply() {
        return new EnumSupplyType[] { EnumSupplyType.TRAJECTORY };
    }

    @Override
    public Trajectory[] supplyTrajectories() {
        return getParent() != null ? getParent().supplyTrajectories() : new Trajectory[0];
    }

    @Override
    public boolean execute() {
        return true;
    }
}
