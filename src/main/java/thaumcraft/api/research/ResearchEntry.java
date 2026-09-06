package thaumcraft.api.research;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import net.minecraft.util.StringRepresentable;

import java.util.Arrays;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.resources.language.I18n;
import thaumcraft.api.research.ResearchStage.Knowledge;



public class ResearchEntry {

	public static final Codec<ResearchEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.STRING.fieldOf("key").forGetter(ResearchEntry::getKey),
		Codec.STRING.fieldOf("name").forGetter(ResearchEntry::getName),
		Codec.STRING.fieldOf("category").forGetter(ResearchEntry::getCategory),
		Codec.STRING.listOf().optionalFieldOf("icons", List.of()).forGetter(e -> List.of()),
		Codec.STRING.listOf().optionalFieldOf("parents", List.of()).forGetter(e -> e.getParents() == null ? List.of() : Arrays.asList(e.getParents())),
		Codec.STRING.listOf().optionalFieldOf("siblings", List.of()).forGetter(e -> e.getSiblings() == null ? List.of() : Arrays.asList(e.getSiblings())),
		Codec.STRING.listOf().optionalFieldOf("meta", List.of()).forGetter(e -> {
			if (e.getMeta() == null) return List.of();
			List<String> list = new ArrayList<>();
			for (EnumResearchMeta m : e.getMeta()) list.add(m.name());
			return list;
		}),
		Codec.INT.listOf().optionalFieldOf("location", List.of()).forGetter(e -> List.of(e.getDisplayColumn(), e.getDisplayRow())),
		Codec.STRING.listOf().optionalFieldOf("reward_item", List.of()).forGetter(e -> List.of()),
		Codec.STRING.listOf().optionalFieldOf("reward_knowledge", List.of()).forGetter(e -> List.of()),
		ResearchStage.CODEC.listOf().optionalFieldOf("stages", List.of()).forGetter(e -> e.getStages() == null ? List.of() : Arrays.asList(e.getStages())),
		ResearchAddendum.CODEC.listOf().optionalFieldOf("addenda", List.of()).forGetter(e -> e.getAddenda() == null ? List.of() : Arrays.asList(e.getAddenda()))
	).apply(instance, (key, name, category, icons, parents, siblings, meta, location, rewardItem, rewardKnow, stages, addenda) -> {
		ResearchEntry entry = new ResearchEntry();
		entry.setKey(key);
		entry.setName(name);
		entry.setCategory(category);

		if (!icons.isEmpty()) {
			Object[] ir = new Object[icons.size()];
			for (int a = 0; a < icons.size(); ++a) {
				String ic = icons.get(a);
				ItemStack stack = parseJSONtoItemStack(ic);
				if (stack != null && !stack.isEmpty()) {
					ir[a] = stack;
				} else if (ic.startsWith("item:") || ic.startsWith("oredict:") || ic.contains(";")) {
					ir[a] = ic;
				} else {
					ir[a] = Identifier.parse(ic.toLowerCase());
				}
			}
			entry.setIcons(ir);
		}

		if (!parents.isEmpty()) entry.setParents(parents.toArray(new String[0]));
		if (!siblings.isEmpty()) entry.setSiblings(siblings.toArray(new String[0]));

		if (!meta.isEmpty()) {
			List<EnumResearchMeta> metas = new ArrayList<>();
			for (String s : meta) {
				metas.add(EnumResearchMeta.valueOf(s.toUpperCase()));
			}
			entry.setMeta(metas.toArray(new EnumResearchMeta[0]));
		}

		if (location.size() == 2) {
			entry.setDisplayColumn(location.get(0));
			entry.setDisplayRow(location.get(1));
		}

		if (!rewardItem.isEmpty()) entry.setRewardItem(parseJsonItemList(key, rewardItem.toArray(new String[0])));

		if (!rewardKnow.isEmpty()) {
			List<ResearchStage.Knowledge> kl = new ArrayList<>();
			for (String s : rewardKnow) {
				ResearchStage.Knowledge k = ResearchStage.Knowledge.parse(s);
				if (k != null) kl.add(k);
			}
			entry.setRewardKnow(kl.toArray(new ResearchStage.Knowledge[0]));
		}

		if (!stages.isEmpty()) entry.setStages(stages.toArray(new ResearchStage[0]));
		if (!addenda.isEmpty()) entry.setAddenda(addenda.toArray(new ResearchAddendum[0]));

		return entry;
	}));

	public static int createItemStackHash(ItemStack stack) {
		if (stack == null || stack.isEmpty()) {
			return 0;
		}
		stack.setCount(1);
		return stack.toString().hashCode();
	}

	public static ItemStack[] parseJsonItemList(String key, String[] stacks) {
		if (stacks == null || stacks.length == 0) {
			return null;
		}
		ItemStack[] work = new ItemStack[stacks.length];
		int idx = 0;
		for (String s : stacks) {
			s = s.replace("'", "\"");
			ItemStack stack = parseJSONtoItemStack(s);
			if (stack != null && !stack.isEmpty()) {
				work[idx] = stack;
				++idx;
			}
		}
		ItemStack[] out = null;
		if (idx > 0) {
			out = Arrays.copyOf(work, idx);
		}
		return out;
	}

	public static Object[] parseJsonOreList(String key, String[] stacks) {
		if (stacks == null || stacks.length == 0) {
			return null;
		}
		Object[] work = new Object[stacks.length];
		int idx = 0;
		for (String s : stacks) {
			s = s.replace("'", "\"");
			if (s.startsWith("oredict:")) {
				String[] st = s.split(":");
				if (st.length > 1) {
					work[idx] = st[1];
					++idx;
				}
			}
			else {
				ItemStack stack = parseJSONtoItemStack(s);
				if (stack != null && !stack.isEmpty()) {
					work[idx] = stack;
					++idx;
				}
			}
		}
		Object[] out = null;
		if (idx > 0) {
			out = Arrays.copyOf(work, idx);
		}
		return out;
	}

	public static ItemStack parseJSONtoItemStack(String entry) {
		if (entry == null) {
			return null;
		}
		String[] split = entry.split(";");
		String name = split[0];
		int num = -1;
		int dam = -1;
		String nbt = null;
		for (int a = 1; a < split.length; ++a) {
			if (split[a].startsWith("{")) {
				nbt = split[a].replace("'", "\"");
				break;
			}
			int q = -1;
			try {
				q = Integer.parseInt(split[a]);
			}
			catch (NumberFormatException e) {
				continue;
			}
			if (q >= 0 && num < 0) {
				num = q;
			}
			else if (q >= 0 && dam < 0) {
				dam = q;
			}
		}
		if (num < 0) {
			num = 1;
		}
		if (dam < 0) {
			dam = 0;
		}
		ItemStack stack = ItemStack.EMPTY;
		try {
			Item it = net.minecraft.core.registries.BuiltInRegistries.ITEM.getOptional(Identifier.parse(name.toLowerCase())).orElse(net.minecraft.world.item.Items.AIR);
			if (it != null) {
				stack = new ItemStack(it, num);
				if (stack.isEmpty()) {
					System.out.println("PARSED ITEM IS EMPTY: " + name + " -> " + it);
				} else {
					System.out.println("PARSED ITEM SUCCESS: " + name + " -> " + stack);
				}
				if (nbt != null) {
					// stack.setTagCompound(new net.minecraft.nbt.CompoundTag() /* TODO: parse string nbt */);
				}
			}
		}
		catch (Throwable ex) {
			// Registry uninitialized or item not found
		}
		return stack;
	}

	

	/**
	 * A short string used as a key for this research. Must be unique
	 */
	String key;
	
	/**
	 * A short string used as a reference to the research category to which this must be added.
	 */
	String category;
	
	/**
	 * A text name of the research entry. Can be a localizable string.
	 */
	String name;
	
	/**
     * This links to any research that needs to be completed before this research can be discovered or learnt.
     */
    String[] parents;
        
    /**
     * any research linked to this that will be unlocked automatically when this research is complete
     */
    String[] siblings;
    
	
    /**
     * the horizontal position of the research icon
     */
    int displayColumn;

    /**
     * the vertical position of the research icon
     */
    int displayRow;
    
    /**
     * the icon to be used for this research 
     */
    Object[] icons;    

    /**
     * special meta-data tags that indicate how this research must be handled
     */
    EnumResearchMeta[] meta;
    
    /**
     * items the player will receive on completion of this research
     */
    ItemStack[] rewardItem;
    
    /**
     * knowledge the player will receive on completion of this research
     */
    Knowledge[] rewardKnow;
    
    
    
    public enum EnumResearchMeta implements StringRepresentable {
    	ROUND,
    	SPIKY,//these also grant .5 bonus inspiration for theorycrafting
    	REVERSE,
    	HIDDEN,//these also grant .1 bonus inspiration for theorycrafting
    	AUTOUNLOCK,
    	HEX;

		public static final Codec<EnumResearchMeta> CODEC = StringRepresentable.fromEnum(EnumResearchMeta::values);
		@Override
		public String getSerializedName() {
			return this.name();
		}
	}
    
    /**
     * The various stages present in this research entry
     */
    ResearchStage[] stages;
    
    /**
     * The various addena present in this research entry
     */
    ResearchAddendum[] addenda;
    
	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return the name
	 */
	public String getLocalizedName() {
		return net.minecraft.network.chat.Component.translatable(getName()).getString();
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the parents
	 */
	public String[] getParents() {
		return parents;
	}
	
	/**
	 * @return return parents with ALL prefixes and postfixes stripped away
	 */
	public String[] getParentsClean() {
		String[] out = null;
		if (parents!=null) { 
			out = getParentsStripped();
			for (int q=0;q<out.length;q++) {
				if (out[q].contains("@")) 
					out[q] = out[q].substring(0,out[q].indexOf("@"));
			}
		}
		return out;
	}
	
	
	/**
	 * @return return parents with prefixes stripped away
	 */
	public String[] getParentsStripped() {
		String[] out = null;
		if (parents!=null) { 
			out = new String[parents.length];
			for (int q=0;q<out.length;q++) {
				out[q] = ""+parents[q];
				if (out[q].startsWith("~")) 
					out[q] = out[q].substring(1);
			}
		}
		return out;
	}

	/**
	 * @param parents the parents to set
	 */
	public void setParents(String[] parents) {
		this.parents = parents;
	}

	/**
	 * @return the siblings
	 */
	public String[] getSiblings() {
		return siblings;
	}

	/**
	 * @param siblings the siblings to set
	 */
	public void setSiblings(String[] siblings) {
		this.siblings = siblings;
	}

	/**
	 * @return the displayColumn
	 */
	public int getDisplayColumn() {
		return displayColumn;
	}

	/**
	 * @param displayColumn the displayColumn to set
	 */
	public void setDisplayColumn(int displayColumn) {
		this.displayColumn = displayColumn;
	}

	/**
	 * @return the displayRow
	 */
	public int getDisplayRow() {
		return displayRow;
	}

	/**
	 * @param displayRow the displayRow to set
	 */
	public void setDisplayRow(int displayRow) {
		this.displayRow = displayRow;
	}

	/**
	 * @return the icons
	 */
	public Object[] getIcons() {
		return icons;
	}

	/**
	 * @param icons the icons to set
	 */
	public void setIcons(Object[] icons) {
		this.icons = icons;
	}

	/**
	 * @return the meta
	 */
	public EnumResearchMeta[] getMeta() {
		return meta;
	}
	
	public boolean hasMeta(EnumResearchMeta me) {
		return meta==null ? false : Arrays.asList(meta).contains(me);
	}

	/**
	 * @param meta the meta to set
	 */
	public void setMeta(EnumResearchMeta[] meta) {
		this.meta = meta;
	}

	/**
	 * @return the stages
	 */
	public ResearchStage[] getStages() {
		return stages;
	}

	/**
	 * @param stages the stages to set
	 */
	public void setStages(ResearchStage[] stages) {
		this.stages = stages;
	}

	/**
	 * @return the rewardItem
	 */
	public ItemStack[] getRewardItem() {
		return rewardItem;
	}

	/**
	 * @param rewardItem the rewardItem to set
	 */
	public void setRewardItem(ItemStack[] rewardItem) {
		this.rewardItem = rewardItem;
	}

	/**
	 * @return the rewardKnow
	 */
	public Knowledge[] getRewardKnow() {
		return rewardKnow;
	}

	/**
	 * @param rewardKnow the rewardKnow to set
	 */
	public void setRewardKnow(Knowledge[] rewardKnow) {
		this.rewardKnow = rewardKnow;
	}

	/**
	 * @return the addenda
	 */
	public ResearchAddendum[] getAddenda() {
		return addenda;
	}

	/**
	 * @param addenda the addenda to set
	 */
	public void setAddenda(ResearchAddendum[] addenda) {
		this.addenda = addenda;
	}
    
    
	
}
