package thaumcraft.api.capabilities;

import javax.annotation.Nonnull;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.minecraft.nbt.CompoundTag;
import java.util.function.Supplier;
import thaumcraft.Thaumcraft;
import thaumcraft.common.lib.capabilities.PlayerKnowledge;
import thaumcraft.common.lib.capabilities.PlayerWarp;

public class ThaumcraftCapabilities {

	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Thaumcraft.MODID);

	public static final DeferredHolder<AttachmentType<?>, AttachmentType<IPlayerKnowledge>> KNOWLEDGE_ATTACHMENT = ATTACHMENT_TYPES.register(
            "knowledge",
            () -> AttachmentType.builder(() -> (IPlayerKnowledge) new PlayerKnowledge())
                    .serialize(new net.neoforged.neoforge.attachment.IAttachmentSerializer<IPlayerKnowledge>() {
                        @Override
                        public IPlayerKnowledge read(net.neoforged.neoforge.attachment.IAttachmentHolder holder, net.minecraft.world.level.storage.ValueInput input) {
                            PlayerKnowledge k = new PlayerKnowledge();
                            input.read("data", CompoundTag.CODEC).ifPresent(k::deserializeNBT);
                            return k;
                        }
                        @Override
                        public boolean write(IPlayerKnowledge attachment, net.minecraft.world.level.storage.ValueOutput output) {
                            output.store("data", CompoundTag.CODEC, attachment.serializeNBT());
                            return true;
                        }
                    })
                    .copyOnDeath().build()
    );

	public static final DeferredHolder<AttachmentType<?>, AttachmentType<IPlayerWarp>> WARP_ATTACHMENT = ATTACHMENT_TYPES.register(
            "warp",
            () -> AttachmentType.builder(() -> (IPlayerWarp) new PlayerWarp())
                    .serialize(new net.neoforged.neoforge.attachment.IAttachmentSerializer<IPlayerWarp>() {
                        @Override
                        public IPlayerWarp read(net.neoforged.neoforge.attachment.IAttachmentHolder holder, net.minecraft.world.level.storage.ValueInput input) {
                            PlayerWarp w = new PlayerWarp();
                            input.read("data", CompoundTag.CODEC).ifPresent(w::deserializeNBT);
                            return w;
                        }
                        @Override
                        public boolean write(IPlayerWarp attachment, net.minecraft.world.level.storage.ValueOutput output) {
                            output.store("data", CompoundTag.CODEC, attachment.serializeNBT());
                            return true;
                        }
                    })
                    .copyOnDeath().build()
    );

	// PLAYER RESEARCH
	
	/**
	 * Retrieves the knowledge capability handler for the supplied player
	 */
	public static IPlayerKnowledge getKnowledge(@Nonnull Player player)
	{
		return player.getData(KNOWLEDGE_ATTACHMENT);
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
		return player.getData(WARP_ATTACHMENT);
	}
}
