package thaumcraft.common.entities;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.common.entities.monster.tainted.EntityTaintCrawler;
import thaumcraft.common.entities.monster.tainted.EntityTaintacle;

public class ThaumcraftEntityEvents {

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ThaumcraftEntities.TAINTACLE.get(), EntityTaintacle.createAttributes().build());
        event.put(ThaumcraftEntities.TAINT_CRAWLER.get(), EntityTaintCrawler.createAttributes().build());
    }
}
