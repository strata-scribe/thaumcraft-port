package thaumcraft.common.lib.network.playerdata;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.Thaumcraft;

public record PacketSyncKnowledge(CompoundTag data) implements CustomPacketPayload {
    public static final Type<PacketSyncKnowledge> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Thaumcraft.MODID, "sync_knowledge"));
    public static final StreamCodec<FriendlyByteBuf, PacketSyncKnowledge> STREAM_CODEC = StreamCodec.of(
            (buf, packet) -> buf.writeNbt(packet.data()),
            buf -> new PacketSyncKnowledge(buf.readNbt())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player != null) {
                thaumcraft.api.capabilities.IPlayerKnowledge knowledge = thaumcraft.api.capabilities.ThaumcraftCapabilities.getKnowledge(player);
                if (knowledge != null) knowledge.deserializeNBT(data());
            }
        });
    }
}
