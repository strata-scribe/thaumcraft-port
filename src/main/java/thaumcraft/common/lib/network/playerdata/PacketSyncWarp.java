package thaumcraft.common.lib.network.playerdata;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.Thaumcraft;

public record PacketSyncWarp(CompoundTag data) implements CustomPacketPayload {
    public static final Type<PacketSyncWarp> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Thaumcraft.MODID, "sync_warp"));
    public static final StreamCodec<FriendlyByteBuf, PacketSyncWarp> STREAM_CODEC = StreamCodec.of(
            (buf, packet) -> buf.writeNbt(packet.data()),
            buf -> new PacketSyncWarp(buf.readNbt())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player != null) {
                thaumcraft.api.capabilities.IPlayerWarp warp = thaumcraft.api.capabilities.ThaumcraftCapabilities.getWarp(player);
                if (warp != null) warp.deserializeNBT(data());
            }
        });
    }
}
