package thaumcraft;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(Thaumcraft.MODID)
public class Thaumcraft {
    public static final String MODID = "thaumcraft";
    public static final Logger LOGGER = LogUtils.getLogger();

    // Deferred Registers
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    // Initial Registrations
    public static final DeferredBlock<Block> AMBER_BLOCK = BLOCKS.registerSimpleBlock("amber_block", p -> p.mapColor(MapColor.STONE));
    public static final DeferredItem<BlockItem> AMBER_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("amber_block", AMBER_BLOCK);
    
    public static final DeferredItem<Item> AMBER = thaumcraft.api.items.ThaumcraftItems.amber;
    public static final DeferredItem<Item> SALIS_MUNDUS = thaumcraft.api.items.ThaumcraftItems.salisMundus;
    public static final DeferredItem<Item> THAUMONOMICON = thaumcraft.api.items.ThaumcraftItems.thaumonomicon;


    public Thaumcraft(IEventBus modEventBus, ModContainer modContainer) {
        Dump.dump();
        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        thaumcraft.api.items.ThaumcraftItems.ITEMS.register(modEventBus);
        thaumcraft.common.lib.CreativeTabThaumcraft.CREATIVE_MODE_TABS.register(modEventBus);
        thaumcraft.common.lib.SoundsTC.SOUNDS.register(modEventBus);
        thaumcraft.api.capabilities.ThaumcraftCapabilities.ATTACHMENT_TYPES.register(modEventBus);
        modEventBus.addListener(thaumcraft.common.lib.network.PacketHandler::register);
        thaumcraft.api.blocks.ThaumcraftBlocks.BLOCKS.register(modEventBus);
        thaumcraft.api.golems.ThaumcraftGolemRegistries.register(modEventBus);
        thaumcraft.common.blocks.entities.ThaumcraftBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        thaumcraft.common.container.ThaumcraftMenus.MENU_TYPES.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Thaumcraft 6 port is initializing common setup!");
        event.enqueueWork(() -> {
            thaumcraft.api.items.ThaumcraftItems.populateItemsTC();
            thaumcraft.common.lib.SoundsTC.registerSoundTypes();
            thaumcraft.common.config.ConfigResearch.init();
            thaumcraft.common.config.ConfigResearch.postInit();
        });
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(AMBER);
            event.accept(SALIS_MUNDUS);
        }
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(AMBER_BLOCK_ITEM);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Thaumcraft server is starting...");
    }

    @SubscribeEvent
    public void onRegisterCommands(net.neoforged.neoforge.event.RegisterCommandsEvent event) {
        event.getDispatcher().register(
            net.minecraft.commands.Commands.literal("tc_research")
                .then(net.minecraft.commands.Commands.literal("give")
                    .then(net.minecraft.commands.Commands.argument("research", com.mojang.brigadier.arguments.StringArgumentType.string())
                        .executes(context -> {
                            net.minecraft.world.entity.player.Player player = context.getSource().getPlayerOrException();
                            String res = com.mojang.brigadier.arguments.StringArgumentType.getString(context, "research");
                            boolean result = thaumcraft.common.lib.research.ResearchManager.progressResearch(player, res);
                            thaumcraft.api.capabilities.IPlayerKnowledge k = thaumcraft.api.capabilities.ThaumcraftCapabilities.getKnowledge(player);
                            String rList = k.getResearchList().toString();
                            context.getSource().sendSuccess(() -> net.minecraft.network.chat.Component.literal("Granted research: " + res + " | Result: " + result + " | List: " + rList), false);
                            return 1;
                        })
                    )
                )
                .then(net.minecraft.commands.Commands.literal("check")
                    .then(net.minecraft.commands.Commands.argument("research", com.mojang.brigadier.arguments.StringArgumentType.string())
                        .executes(context -> {
                            net.minecraft.world.entity.player.Player player = context.getSource().getPlayerOrException();
                            String res = com.mojang.brigadier.arguments.StringArgumentType.getString(context, "research");
                            boolean knows = thaumcraft.api.capabilities.ThaumcraftCapabilities.knowsResearch(player, res);
                            thaumcraft.api.capabilities.IPlayerKnowledge k = thaumcraft.api.capabilities.ThaumcraftCapabilities.getKnowledge(player);
                            String rList = k.getResearchList().toString();
                            context.getSource().sendSuccess(() -> net.minecraft.network.chat.Component.literal("Knows " + res + "? " + knows + " | List: " + rList), false);
                            return 1;
                        })
                    )
                )
        );
    }

}
