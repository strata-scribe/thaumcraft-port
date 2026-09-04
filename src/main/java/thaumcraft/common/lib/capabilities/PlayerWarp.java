package thaumcraft.common.lib.capabilities;

import javax.annotation.Nonnull;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketSyncWarp;

public class PlayerWarp implements IPlayerWarp {

    private int[] warp;
    private int counter;
    
    public PlayerWarp() {
        warp = new int[EnumWarpType.values().length];
    }
    
    @Override
    public void clear() {
        warp = new int[EnumWarpType.values().length];
        counter = 0;
    }
    
    @Override
    public int get(@Nonnull EnumWarpType type) {
        return warp[type.ordinal()];
    }
    
    @Override
    public void set(EnumWarpType type, int amount) {
        warp[type.ordinal()] = Mth.clamp(amount, 0, 500);
    }
    
    @Override
    public int add(@Nonnull EnumWarpType type, int amount) {
        return warp[type.ordinal()] = Mth.clamp(warp[type.ordinal()] + amount, 0, 500);
    }
    
    @Override
    public int reduce(@Nonnull EnumWarpType type, int amount) {
        return warp[type.ordinal()] = Mth.clamp(warp[type.ordinal()] - amount, 0, 500);
    }
    
    @Override
    public void sync(ServerPlayer player) {
        net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player, new PacketSyncWarp(this.serializeNBT()));
    }
    
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag properties = new CompoundTag();
        properties.putIntArray("warp", warp);
        properties.putInt("counter", getCounter());
        return properties;
    }
    
    @Override
    public void deserializeNBT(CompoundTag properties) {
        if (properties == null) {
            return;
        }
        clear();
        int[] ba = properties.getIntArray("warp").orElse(new int[0]);
        if (ba != null && ba.length > 0) {
            int l = EnumWarpType.values().length;
            if (ba.length < l) {
                l = ba.length;
            }
            for (int a = 0; a < l; ++a) {
                warp[a] = ba[a];
            }
        }
        setCounter(properties.getIntOr("counter", 0));
    }
    
    @Override
    public int getCounter() {
        return counter;
    }
    
    @Override
    public void setCounter(int amount) {
        counter = amount;
    }
}
