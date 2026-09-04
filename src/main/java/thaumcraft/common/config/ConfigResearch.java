package thaumcraft.common.config;

import net.minecraft.resources.Identifier;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.common.lib.research.ResearchManager;

public class ConfigResearch {
    public static String[] TCCategories = new String[] { "BASICS", "ALCHEMY", "AUROMANCY", "ARTIFICE", "INFUSION", "GOLEMANCY", "ELDRITCH" };
    private static Identifier BACK_OVER = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_research_back_over.png");
    
    public static void init() {
        initCategories();
        for (String cat : ConfigResearch.TCCategories) {
            ThaumcraftApi.registerResearchLocation(Identifier.fromNamespaceAndPath("thaumcraft", "research/" + cat.toLowerCase()));
        }
        ThaumcraftApi.registerResearchLocation(Identifier.fromNamespaceAndPath("thaumcraft", "research/scans"));
    }
    
    public static void postInit() {
        ResearchManager.parseAllResearch();
    }
    
    private static void initCategories() {
        ResearchCategories.registerCategory("BASICS", null, new AspectList().add(Aspect.PLANT, 5).add(Aspect.ORDER, 5).add(Aspect.ENTROPY, 5).add(Aspect.AIR, 5).add(Aspect.FIRE, 5).add(Aspect.EARTH, 3).add(Aspect.WATER, 5), Identifier.fromNamespaceAndPath("thaumcraft", "textures/items/thaumonomicon_cheat.png"), Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_research_back_1.png"), ConfigResearch.BACK_OVER);
        ResearchCategories.registerCategory("AUROMANCY", "UNLOCKAUROMANCY", new AspectList().add(Aspect.AURA, 20).add(Aspect.MAGIC, 20).add(Aspect.FLUX, 15).add(Aspect.CRYSTAL, 5).add(Aspect.COLD, 5).add(Aspect.AIR, 5), Identifier.fromNamespaceAndPath("thaumcraft", "textures/research/cat_auromancy.png"), Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_research_back_2.png"), ConfigResearch.BACK_OVER);
        ResearchCategories.registerCategory("ALCHEMY", "UNLOCKALCHEMY", new AspectList().add(Aspect.ALCHEMY, 30).add(Aspect.FLUX, 10).add(Aspect.MAGIC, 10).add(Aspect.LIFE, 5).add(Aspect.AVERSION, 5).add(Aspect.DESIRE, 5).add(Aspect.WATER, 5), Identifier.fromNamespaceAndPath("thaumcraft", "textures/research/cat_alchemy.png"), Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_research_back_3.png"), ConfigResearch.BACK_OVER);
        ResearchCategories.registerCategory("ARTIFICE", "UNLOCKARTIFICE", new AspectList().add(Aspect.MECHANISM, 10).add(Aspect.CRAFT, 10).add(Aspect.METAL, 10).add(Aspect.TOOL, 10).add(Aspect.ENERGY, 10).add(Aspect.LIGHT, 5).add(Aspect.FLIGHT, 5).add(Aspect.TRAP, 5).add(Aspect.FIRE, 5), Identifier.fromNamespaceAndPath("thaumcraft", "textures/research/cat_artifice.png"), Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_research_back_4.png"), ConfigResearch.BACK_OVER);
        ResearchCategories.registerCategory("INFUSION", "UNLOCKINFUSION", new AspectList().add(Aspect.MAGIC, 30).add(Aspect.PROTECT, 10).add(Aspect.TOOL, 10).add(Aspect.FLUX, 5).add(Aspect.CRAFT, 5).add(Aspect.SOUL, 5).add(Aspect.EARTH, 3), Identifier.fromNamespaceAndPath("thaumcraft", "textures/research/cat_infusion.png"), Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_research_back_7.png"), ConfigResearch.BACK_OVER);
        ResearchCategories.registerCategory("GOLEMANCY", "UNLOCKGOLEMANCY", new AspectList().add(Aspect.MAN, 20).add(Aspect.MOTION, 10).add(Aspect.MIND, 10).add(Aspect.MECHANISM, 10).add(Aspect.EXCHANGE, 5).add(Aspect.SENSES, 5).add(Aspect.BEAST, 5).add(Aspect.ORDER, 5), Identifier.fromNamespaceAndPath("thaumcraft", "textures/research/cat_golemancy.png"), Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_research_back_5.png"), ConfigResearch.BACK_OVER);
        ResearchCategories.registerCategory("ELDRITCH", "UNLOCKELDRITCH", new AspectList().add(Aspect.ELDRITCH, 20).add(Aspect.DARKNESS, 10).add(Aspect.MAGIC, 5).add(Aspect.MIND, 5).add(Aspect.VOID, 5).add(Aspect.DEATH, 5).add(Aspect.UNDEAD, 5).add(Aspect.ENTROPY, 5), Identifier.fromNamespaceAndPath("thaumcraft", "textures/research/cat_eldritch.png"), Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_research_back_6.png"), ConfigResearch.BACK_OVER);
    }
}
