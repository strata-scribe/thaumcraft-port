package thaumcraft.common.lib.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import thaumcraft.Thaumcraft;
import thaumcraft.common.lib.network.playerdata.PacketSyncKnowledge;
import thaumcraft.common.lib.network.playerdata.PacketSyncWarp;

public class PacketHandler {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Thaumcraft.MODID);
        
        // Player Data Sync
        registrar.playToClient(
                PacketSyncKnowledge.TYPE,
                PacketSyncKnowledge.STREAM_CODEC,
                PacketSyncKnowledge::handle
        );
        
        registrar.playToClient(
                PacketSyncWarp.TYPE,
                PacketSyncWarp.STREAM_CODEC,
                PacketSyncWarp::handle
        );
    }
}
