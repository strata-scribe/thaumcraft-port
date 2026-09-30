package thaumcraft.common.tiles.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;
import thaumcraft.common.blocks.entities.ThaumcraftBlockEntities;

public class RechargePedestalBlockEntity extends BlockEntity {

    private ItemStack item = ItemStack.EMPTY;
    private final int baseTransferRate; // Default generic base transfer rate

    public RechargePedestalBlockEntity(BlockPos pos, BlockState state) {
        this(pos, state, 5);
    }

    public RechargePedestalBlockEntity(BlockPos pos, BlockState state, int baseTransferRate) {
        super(ThaumcraftBlockEntities.RECHARGE_PEDESTAL.get(), pos, state);
        this.baseTransferRate = Math.max(0, baseTransferRate);
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item == null ? ItemStack.EMPTY : item;
        setChanged();
    }

    public boolean hasItem() {
        return !item.isEmpty();
    }

    public int getBaseTransferRate() {
        return baseTransferRate;
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

    public static void serverTick(Level level, BlockPos pos, BlockState state, RechargePedestalBlockEntity blockEntity) {
        if (blockEntity != null) {
            blockEntity.tickRecharge(level, pos);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("Item", ItemStack.OPTIONAL_CODEC, item);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        item = input.read("Item", ItemStack.OPTIONAL_CODEC)
                .or(() -> input.read("PedestalItem", ItemStack.OPTIONAL_CODEC))
                .orElse(ItemStack.EMPTY);
    }
}
