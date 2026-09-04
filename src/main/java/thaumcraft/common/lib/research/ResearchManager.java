package thaumcraft.common.lib.research;
import com.mojang.serialization.JsonOps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.minecraft.resources.Identifier;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.Event;
// import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thaumcraft.Thaumcraft;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.internal.CommonInternals;
import thaumcraft.api.research.ResearchAddendum;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.api.research.ResearchEvent;
import thaumcraft.api.research.ResearchStage;
// import thaumcraft.common.config.ModConfig;
import thaumcraft.common.lib.network.PacketHandler;
// import thaumcraft.common.lib.network.misc.PacketKnowledgeGain;


public class ResearchManager
{
    public static ConcurrentHashMap<String, Boolean> syncList;
    public static boolean noFlags;
    public static LinkedHashSet<Integer> craftingReferences;
    
    public static boolean addKnowledge(Player player, IPlayerKnowledge.EnumKnowledgeType type, ResearchCategory category, int amount) {
        IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);
        if (!type.hasFields()) {
            category = null;
        }
        if (false) {
            return false;
        }
        int kp = knowledge.getKnowledge(type, category);
        knowledge.addKnowledge(type, category, amount);
        int kr = knowledge.getKnowledge(type, category) - kp;
        if (amount > 0) {
            for (int a = 0; a < kr; ++a) {
                // PacketHandler.INSTANCE.sendTo(...);
            }
        }
        ResearchManager.syncList.put(player.getName().getString(), true);
        return true;
    }
    
    public static boolean completeResearch(Player player, String researchkey, boolean sync) {
        boolean b = false;
        while (progressResearch(player, researchkey, sync)) {
            b = true;
        }
        return b;
    }
    
    public static boolean completeResearch(Player player, String researchkey) {
        boolean b = false;
        while (progressResearch(player, researchkey, true)) {
            b = true;
        }
        return b;
    }
    
    public static boolean startResearchWithPopup(Player player, String researchkey) {
        boolean b = progressResearch(player, researchkey, true);
        if (b) {
            IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);
            knowledge.setResearchFlag(researchkey, IPlayerKnowledge.EnumResearchFlag.POPUP);
            knowledge.setResearchFlag(researchkey, IPlayerKnowledge.EnumResearchFlag.RESEARCH);
        }
        return b;
    }
    
    public static boolean progressResearch(Player player, String researchkey) {
        return progressResearch(player, researchkey, true);
    }
    
    public static boolean progressResearch(Player player, String researchkey, boolean sync) {
        IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);
        if (knowledge.isResearchComplete(researchkey) || !doesPlayerHaveRequisites(player, researchkey)) {
            return false;
        }
        if (false) { // TODO: Post Research event
            return false;
        }
        if (!knowledge.isResearchKnown(researchkey)) {
            knowledge.addResearch(researchkey);
        }
        ResearchEntry re = ResearchCategories.getResearch(researchkey);
        if (re != null) {
            boolean popups = true;
            if (re.getStages() != null) {
                int cs = knowledge.getResearchStage(researchkey);
                ResearchStage currentStage = null;
                if (cs > 0) {
                    cs = Math.min(cs, re.getStages().length);
                    currentStage = re.getStages()[cs - 1];
                }
                if (re.getStages().length == 1 && cs == 0 && re.getStages()[0].getCraft() == null && re.getStages()[0].getObtain() == null && re.getStages()[0].getKnow() == null && re.getStages()[0].getResearch() == null) {
                    ++cs;
                }
                else if (re.getStages().length > 1 && re.getStages().length <= cs + 1 && cs < re.getStages().length && re.getStages()[cs].getCraft() == null && re.getStages()[cs].getObtain() == null && re.getStages()[cs].getKnow() == null && re.getStages()[cs].getResearch() == null) {
                    ++cs;
                }
                knowledge.setResearchStage(researchkey, Math.min(re.getStages().length + 1, cs + 1));
                popups = (cs >= re.getStages().length);
                int warp = 0;
                if (currentStage != null) {
                    warp = currentStage.getWarp();
                }
                if (popups) {
                    cs = Math.min(cs, re.getStages().length);
                    currentStage = re.getStages()[cs - 1];
                }
                if (currentStage != null) {
                    warp += currentStage.getWarp();
                    if (warp > 0 && true && !player.level().isClientSide()) {
                        if (warp > 1) {
                            IPlayerWarp pw = ThaumcraftCapabilities.getWarp(player);
                            int w2 = warp / 2;
                            if (warp - w2 > 0) {
                                ThaumcraftApi.internalMethods.addWarpToPlayer(player, warp - w2, IPlayerWarp.EnumWarpType.PERMANENT);
                            }
                            if (w2 > 0) {
                                ThaumcraftApi.internalMethods.addWarpToPlayer(player, w2, IPlayerWarp.EnumWarpType.NORMAL);
                            }
                        }
                        else {
                            ThaumcraftApi.internalMethods.addWarpToPlayer(player, warp, IPlayerWarp.EnumWarpType.PERMANENT);
                        }
                    }
                }
            }
            if (popups) {
                if (sync) {
                    knowledge.setResearchFlag(researchkey, IPlayerKnowledge.EnumResearchFlag.POPUP);
                    if (!ResearchManager.noFlags) {
                        knowledge.setResearchFlag(researchkey, IPlayerKnowledge.EnumResearchFlag.RESEARCH);
                    }
                    else {
                        ResearchManager.noFlags = false;
                    }
                    if (re.getRewardItem() != null) {
                        for (ItemStack rs : re.getRewardItem()) {
                            if (!player.getInventory().add(rs.copy())) {
                                player.drop(rs.copy(), true);
                            }
                        }
                    }
                    if (re.getRewardKnow() != null) {
                        for (ResearchStage.Knowledge rk : re.getRewardKnow()) {
                            addKnowledge(player, rk.type, rk.category, rk.type.getProgression() * rk.amount);
                        }
                    }
                }
                for (String rc : ResearchCategories.researchCategories.keySet()) {
                    for (ResearchEntry ri : ResearchCategories.getResearchCategory(rc).research.values()) {
                        if (ri != null && ri.getAddenda() != null) {
                            if (!knowledge.isResearchComplete(ri.getKey())) {
                                continue;
                            }
                            for (ResearchAddendum addendum : ri.getAddenda()) {
                                if (addendum.getResearch() != null && Arrays.asList(addendum.getResearch()).contains(researchkey)) {
                                    Component text = Component.translatable("tc.addaddendum", ri.getLocalizedName());
                                    player.sendSystemMessage(text);
                                    knowledge.setResearchFlag(ri.getKey(), IPlayerKnowledge.EnumResearchFlag.PAGE);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (re != null && re.getSiblings() != null) {
            for (String sibling : re.getSiblings()) {
                if (!knowledge.isResearchComplete(sibling) && doesPlayerHaveRequisites(player, sibling)) {
                    completeResearch(player, sibling, sync);
                }
            }
        }
        if (sync) {
            ResearchManager.syncList.put(player.getName().getString(), true);
            if (re != null) {
                player.giveExperiencePoints(5);
            }
        }
        return true;
    }
    
    public static boolean doesPlayerHaveRequisites(Player player, String key) {
        ResearchEntry ri = ResearchCategories.getResearch(key);
        if (ri == null) {
            return true;
        }
        String[] parents = ri.getParentsStripped();
        return parents == null || ThaumcraftCapabilities.knowsResearchStrict(player, parents);
    }
    
    public static Aspect getCombinationResult(Aspect aspect1, Aspect aspect2) {
        Collection<Aspect> aspects = Aspect.aspects.values();
        for (Aspect aspect3 : aspects) {
            if (aspect3.getComponents() != null && ((aspect3.getComponents()[0] == aspect1 && aspect3.getComponents()[1] == aspect2) || (aspect3.getComponents()[0] == aspect2 && aspect3.getComponents()[1] == aspect1))) {
                return aspect3;
            }
        }
        return null;
    }
    
    public static void parseAllResearch() {
        JsonParser parser = new JsonParser();
        for (Identifier loc : CommonInternals.jsonLocs.values()) {
            String s = "/assets/" + loc.getNamespace() + "/" + loc.getPath();
            if (!s.endsWith(".json")) {
                s += ".json";
            }
            InputStream stream = ResearchManager.class.getResourceAsStream(s);
            if (stream != null) {
                try {
                    System.out.println("PARSING RESEARCH FILE: " + s);
                    InputStreamReader reader = new InputStreamReader(stream);
                    JsonObject obj = parser.parse(reader).getAsJsonObject();
                    JsonArray entries = obj.get("entries").getAsJsonArray();
                    int a = 0;
                    for (JsonElement element : entries) {
                        ++a;
                        try {
                            JsonObject entry = element.getAsJsonObject();
                            ResearchEntry researchEntry = ResearchEntry.CODEC.parse(JsonOps.INSTANCE, entry).getOrThrow(err -> new RuntimeException(err));
                            addResearchToCategory(researchEntry);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            Thaumcraft.LOGGER.warn("Invalid research entry [" + a + "] found in " + loc.toString());
                            --a;
                        }
                    }
                    Thaumcraft.LOGGER.info("Loaded " + a + " research entries from " + loc.toString());
                }
                catch (Exception e2) {
                    Thaumcraft.LOGGER.warn("Invalid research file: " + loc.toString());
                }
            } else {
                System.out.println("RESEARCH FILE NOT FOUND: " + s);
            }
        }
    }
    
    private static void addResearchToCategory(ResearchEntry ri) {
        ResearchCategory rl = ResearchCategories.getResearchCategory(ri.getCategory());
        if (rl != null && !rl.research.containsKey(ri.getKey())) {
            for (ResearchEntry rr : rl.research.values()) {
                if (rr.getDisplayColumn() == ri.getDisplayColumn() && rr.getDisplayRow() == ri.getDisplayRow()) {
                    Thaumcraft.LOGGER.warn("Research [" + ri.getKey() + "] not added as it overlaps with existing research [" + rr.getKey() + "] at " + ri.getDisplayColumn() + "," + rr.getDisplayRow());
                    return;
                }
            }
            rl.research.put(ri.getKey(), ri);
            if (ri.getDisplayColumn() < rl.minDisplayColumn) {
                rl.minDisplayColumn = ri.getDisplayColumn();
            }
            if (ri.getDisplayRow() < rl.minDisplayRow) {
                rl.minDisplayRow = ri.getDisplayRow();
            }
            if (ri.getDisplayColumn() > rl.maxDisplayColumn) {
                rl.maxDisplayColumn = ri.getDisplayColumn();
            }
            if (ri.getDisplayRow() > rl.maxDisplayRow) {
                rl.maxDisplayRow = ri.getDisplayRow();
            }
        }
        else {
            Thaumcraft.LOGGER.warn("Could not add invalid research entry " + ri.getKey());
        }
    }
    
    static {
        ResearchManager.syncList = new ConcurrentHashMap<String, Boolean>();
        ResearchManager.noFlags = false;
        ResearchManager.craftingReferences = new LinkedHashSet<Integer>();
    }
}
