package thaumcraft.common.items.casters.foci;

import net.minecraft.world.phys.HitResult;
import thaumcraft.api.casters.FocusModSplit;
import thaumcraft.common.casters.FocusLogic;

public class FocusModSplitTarget extends FocusModSplit {

    public FocusModSplitTarget() {
        super();
    }

    @Override
    public String getResearch() {
        return "FOCUSSPLIT";
    }

    @Override
    public String getKey() {
        return "thaumcraft.SPLITTARGET";
    }

    @Override
    public int getComplexity() {
        return FocusLogic.calculateSplitTargetComplexity();
    }

    @Override
    public EnumSupplyType[] mustBeSupplied() {
        return new EnumSupplyType[] { EnumSupplyType.TARGET };
    }

    @Override
    public EnumSupplyType[] willSupply() {
        return new EnumSupplyType[] { EnumSupplyType.TARGET };
    }

    @Override
    public HitResult[] supplyTargets() {
        return getParent() != null ? getParent().supplyTargets() : new HitResult[0];
    }

    @Override
    public boolean execute() {
        return true;
    }
}
