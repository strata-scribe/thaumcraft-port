package thaumcraft.common.items.casters.foci;

import net.minecraft.world.phys.HitResult;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusMedium;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.common.casters.FocusLogic;

public class FocusMediumMine extends FocusMedium {

    public FocusMediumMine() {
        super();
    }

    @Override
    public String getResearch() {
        return "FOCUSMINE";
    }

    @Override
    public String getKey() {
        return "thaumcraft.MINE";
    }

    @Override
    public int getComplexity() {
        return FocusLogic.calculateMineComplexity();
    }

    @Override
    public Aspect getAspect() {
        return Aspect.TRAP;
    }

    @Override
    public EnumSupplyType[] willSupply() {
        return new EnumSupplyType[] { EnumSupplyType.TARGET, EnumSupplyType.TRAJECTORY };
    }

    @Override
    public boolean hasIntermediary() {
        return true;
    }

    @Override
    public NodeSetting[] createSettings() {
        int[] friend = { 0, 1 };
        String[] friendDesc = { "focus.common.enemy", "focus.common.friend" };
        return new NodeSetting[] {
                new NodeSetting("target", "focus.common.target", new NodeSetting.NodeSettingIntList(friend, friendDesc))
        };
    }

    @Override
    public boolean execute(Trajectory trajectory) {
        FocusPackage remaining = getRemainingPackage();
        if (remaining != null && trajectory != null) {
            // Direct detonation fallback when entity runtime is headless/test
            thaumcraft.api.casters.FocusEngine.runFocusPackage(
                    remaining,
                    new Trajectory[] { trajectory },
                    new HitResult[] { new net.minecraft.world.phys.BlockHitResult(trajectory.source, net.minecraft.core.Direction.UP, net.minecraft.core.BlockPos.containing(trajectory.source), false) }
            );
        }
        return true;
    }
}
