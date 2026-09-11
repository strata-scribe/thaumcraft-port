package thaumcraft.common.items.casters.foci;

import thaumcraft.api.casters.FocusModSplit;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.common.casters.FocusLogic;
import thaumcraft.common.casters.logic.FocusSplitLogic;
import net.minecraft.world.phys.Vec3;
import java.util.ArrayList;
import java.util.List;

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
    public NodeSetting[] createSettings() {
        int[] angles = { 10, 30, 60, 90, 180, 270, 360 };
        String[] anglesDesc = { "10", "30", "60", "90", "180", "270", "360" };
        return new NodeSetting[] {
                new NodeSetting("angle", "focus.scatter.cone", new NodeSetting.NodeSettingIntList(angles, anglesDesc))
        };
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
        if (getParent() == null || getParent().supplyTrajectories() == null) {
            return new Trajectory[0];
        }

        Trajectory[] parentTrajectories = getParent().supplyTrajectories();
        int totalForks = getSplitPackages().size();

        if (getPackage() == null) {
            return parentTrajectories;
        }

        int index = getSplitPackages().indexOf(getPackage());

        if (index == -1 || totalForks <= 1) {
            return parentTrajectories;
        }

        int angle = getSettingValue("angle");
        List<Trajectory> out = new ArrayList<>();

        for (Trajectory sT : parentTrajectories) {
            if (sT == null || sT.direction == null || sT.source == null) continue;

            double[] newDir = FocusSplitLogic.calculateSplitVector(
                    sT.direction.x, sT.direction.y, sT.direction.z,
                    angle, index, totalForks
            );

            out.add(new Trajectory(sT.source, new Vec3(newDir[0], newDir[1], newDir[2])));
        }

        return out.toArray(new Trajectory[0]);
    }

    @Override
    public boolean execute() {
        return true;
    }
}
