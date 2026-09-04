package thaumcraft.api.capabilities;

import javax.annotation.Nonnull;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import java.util.function.Supplier;

public class ThaumcraftCapabilities {

	// PLAYER RESEARCH
	
	/**
	 * Retrieves the knowledge capability handler for the supplied player
	 */
	public static IPlayerKnowledge getKnowledge(@Nonnull Player player)
	{
		return player.getData(thaumcraft.common.lib.capabilities.PlayerKnowledge.KNOWLEDGE_ATTACHMENT);
	}
	
	/**
	 * Shortcut method to check if player knows the passed research entries. All must be true
	 */
	public static boolean knowsResearch(@Nonnull Player player, @Nonnull String... research) {
		IPlayerKnowledge knowledge = getKnowledge(player);
		if (knowledge == null) return false;
		for (String r : research) {
			if (r.contains("&&")) {
				String[] rr = r.split("&&");
				if (!knowsResearch(player,rr)) return false;
			} else
			if (r.contains("||")) {
				String[] rr = r.split("\\|\\|"); // Escape the pipes for regex split
				for (String str : rr)
					if (knowsResearch(player,str)) return true;
			} else
			if (!knowledge.isResearchKnown(r)) return false;
		}
		return true;
	}
	
	/**
	 * Shortcut method to check if player knows all the passed research entries. 
	 */
	public static boolean knowsResearchStrict(@Nonnull Player player, @Nonnull String... research) {
		IPlayerKnowledge knowledge = getKnowledge(player);
		if (knowledge == null) return false;
		for (String r : research) {
			if (r.contains("&&")) {
				String[] rr = r.split("&&");
				if (!knowsResearchStrict(player,rr)) return false;
			} else
			if (r.contains("||")) {
				String[] rr = r.split("\\|\\|"); // Escape pipes
				for (String str : rr)
					if (knowsResearchStrict(player,str)) return true;
			} else
			if (r.contains("@")) {
				if (!knowledge.isResearchKnown(r)) return false;
			} else {
				if (!knowledge.isResearchComplete(r)) return false; 
			}
		}
		return true;
	}
	
	// PLAYER WARP

	/**
	 * Retrieves the warp capability handler for the supplied player
	 */
	public static IPlayerWarp getWarp(@Nonnull Player player)
	{
		return player.getData(thaumcraft.common.lib.capabilities.PlayerWarp.WARP_ATTACHMENT);
	}
}
