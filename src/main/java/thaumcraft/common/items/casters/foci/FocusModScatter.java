package thaumcraft.common.items.casters.foci;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.phys.Vec3;
import thaumcraft.api.casters.FocusMod;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.common.casters.FocusLogic;

public class FocusModScatter extends FocusMod {

    public FocusModScatter() {
        super();
    }

    @Override
    public String getResearch() {
        return "FOCUSSCATTER";
    }

    @Override
    public String getKey() {
        return "thaumcraft.SCATTER";
    }

    @Override
    public int getComplexity() {
        return FocusLogic.calculateScatterComplexity(getSettingValue("forks"), getSettingValue("cone"));
    }

    @Override
    public NodeSetting[] createSettings() {
        int[] angles = { 10, 30, 60, 90, 180, 270, 360 };
        String[] anglesDesc = { "10", "30", "60", "90", "180", "270", "360" };
        return new NodeSetting[] {
                new NodeSetting("forks", "focus.scatter.forks", new NodeSetting.NodeSettingIntRange(2, 10)),
                new NodeSetting("cone", "focus.scatter.cone", new NodeSetting.NodeSettingIntList(angles, anglesDesc))
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
        List<Trajectory> out = new ArrayList<>();
        int forks = getSettingValue("forks");
        int angle = getSettingValue("cone");
        java.util.Random rand = (getPackage() != null && getPackage().world != null)
                ? new java.util.Random(getPackage().world.getGameTime())
                : new java.util.Random();

        for (Trajectory sT : getParent().supplyTrajectories()) {
            if (sT == null || sT.direction == null || sT.source == null) continue;
            for (int a = 0; a < forks; a++) {
                double rx = rand.nextGaussian();
                double ry = rand.nextGaussian();
                double rz = rand.nextGaussian();
                double[] dir = FocusLogic.calculateScatterVector(sT.direction.x, sT.direction.y, sT.direction.z, rx, ry, rz, angle);
                out.add(new Trajectory(sT.source, new Vec3(dir[0], dir[1], dir[2])));
            }
        }
        return out.toArray(new Trajectory[0]);
    }

    @Override
    public float getPowerMultiplier() {
        return FocusLogic.calculateScatterPowerMultiplier(getSettingValue("forks"));
    }

    @Override
    public boolean execute() {
        return true;
    }

    @Override
    public boolean isExclusive() {
        return true;
    }
}
