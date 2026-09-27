package thaumcraft.common.tiles.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;
import thaumcraft.common.blocks.entities.ThaumcraftBlockEntities;
import thaumcraft.common.tiles.devices.VisRechargePedestalLogic;

public class RechargePedestalBlockEntity extends BlockEntity {

    private ItemStack item = ItemStack.EMPTY;
    private final int baseTransferRate = 5; // Default generic base transfer rate

    public RechargePedestalBlockEntity(BlockPos pos, BlockState state) {
        super(ThaumcraftBlockEntities.RECHARGE_PEDESTAL.get(), pos, state);
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
        setChanged();
    }

    public void tickRecharge(Level level, BlockPos pos) {
        if (level == null || level.isClientSide()) return;
        if (item.isEmpty() || !(item.getItem() instanceof IRechargable)) return;

        IRechargable rechargeable = (IRechargable) item.getItem();
        int maxCharge = rechargeable.getMaxCharge(item, null);
        int currentCharge = RechargeHelper.getCharge(item);
        int missingCharge = maxCharge - currentCharge;

        if (missingCharge <= 0) return;

        int auraAvailable = (int) AuraHelper.getVis(level, pos);
        int transferRate = VisRechargePedestalLogic.calculateTransferRate(auraAvailable, missingCharge, baseTransferRate);

        if (transferRate > 0) {
            float drained = AuraHelper.drainVis(level, pos, transferRate, false);
            if (drained > 0) {
                RechargeHelper.rechargeItemBlindly(item, null, (int) drained);
                setChanged();
            }
        }
    }
}
