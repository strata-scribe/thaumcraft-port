package thaumcraft.api.research;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.Identifier;
import net.minecraft.client.resources.language.I18n;
import thaumcraft.api.capabilities.IPlayerKnowledge.EnumKnowledgeType;


public class ResearchStage {

	public static final Codec<ResearchStage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.STRING.fieldOf("text").forGetter(ResearchStage::getText),
		Codec.STRING.listOf().optionalFieldOf("recipes", List.of()).forGetter(s -> {
			if (s.getRecipes() == null) return List.of();
			java.util.List<String> list = new java.util.ArrayList<>();
			for (Identifier id : s.getRecipes()) list.add(id.toString());
			return list;
		}),
		Codec.STRING.listOf().optionalFieldOf("required_item", List.of()).forGetter(s -> List.of()),
		Codec.STRING.listOf().optionalFieldOf("required_craft", List.of()).forGetter(s -> List.of()),
		Codec.STRING.listOf().optionalFieldOf("required_knowledge", List.of()).forGetter(s -> {
			if (s.getKnow() == null) return List.of();
			java.util.List<String> list = new java.util.ArrayList<>();
			for (Knowledge k : s.getKnow()) {
				if (k.category == null) list.add(k.type.name() + ";" + k.amount);
				else list.add(k.type.name() + ";" + (k.category.key != null ? k.category.key : "") + ";" + k.amount);
			}
			return list;
		}),
		Codec.STRING.listOf().optionalFieldOf("required_research", List.of()).forGetter(s -> s.getResearch() == null ? List.of() : Arrays.asList(s.getResearch())),
		Codec.INT.optionalFieldOf("warp", 0).forGetter(ResearchStage::getWarp)
	).apply(instance, (text, recipes, reqItem, reqCraft, reqKnow, reqRes, warp) -> {
		ResearchStage stage = new ResearchStage();
		stage.setText(text);
		if (!recipes.isEmpty()) {
			java.util.List<Identifier> ids = new java.util.ArrayList<>();
			for (String r : recipes) {
				try {
					ids.add(Identifier.parse(r.toLowerCase()));
				} catch (Exception ignored) {}
			}
			if (!ids.isEmpty()) stage.setRecipes(ids.toArray(new Identifier[0]));
		}

		if (!reqItem.isEmpty()) stage.setObtain(thaumcraft.api.research.ResearchEntry.parseJsonOreList("", reqItem.toArray(new String[0])));

		if (!reqCraft.isEmpty()) {
			Object[] craftArr = thaumcraft.api.research.ResearchEntry.parseJsonOreList("", reqCraft.toArray(new String[0]));
			stage.setCraft(craftArr);
			if (craftArr != null && craftArr.length > 0) {
				int[] refs = new int[craftArr.length];
				int q = 0;
				for (Object stack2 : craftArr) {
					int code = (stack2 instanceof net.minecraft.world.item.ItemStack) ? thaumcraft.api.research.ResearchEntry.createItemStackHash((net.minecraft.world.item.ItemStack)stack2) : ("oredict:" + stack2).hashCode();
					refs[q++] = code;
				}
				stage.setCraftReference(refs);
			}
		}

		if (!reqKnow.isEmpty()) {
			java.util.List<Knowledge> kl = new java.util.ArrayList<>();
			for (String s : reqKnow) {
				try {
					Knowledge k = Knowledge.parse(s);
					if (k != null) kl.add(k);
				} catch (Exception ignored) {}
			}
			if (!kl.isEmpty()) stage.setKnow(kl.toArray(new Knowledge[0]));
		}

		if (!reqRes.isEmpty()) {
			String[] arr = reqRes.toArray(new String[0]);
			String[] rKey = new String[arr.length];
			String[] rIcn = new String[arr.length];
			for (int a = 0; a < arr.length; ++a) {
				String[] ss = arr[a].split(";");
				rKey[a] = ss[0];
				if (ss.length > 1) {
					rIcn[a] = ss[1];
				} else {
					rIcn[a] = null;
				}
			}
			stage.setResearch(rKey);
			stage.setResearchIcon(rIcn);
		}

		stage.setWarp(warp);
		return stage;
	}));

	String text;
	Identifier[] recipes;
	Object[] obtain;
	Object[] craft;
	int[] craftReference;
	Knowledge[] know;
	String[] research;
	String[] researchIcon;
	int warp;
	
	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}
	
	public String getTextLocalized() {
		return net.minecraft.network.chat.Component.translatable(getText()).getString();
	}
	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}
	/**
	 * @return the recipes
	 */
	public Identifier[] getRecipes() {
		return recipes;
	}
	/**
	 * @param recipes the recipes to set
	 */
	public void setRecipes(Identifier[] recipes) {
		this.recipes = recipes;
	}
	/**
	 * @return the obtain
	 */
	public Object[] getObtain() {
		return obtain;
	}
	/**
	 * @param obtain the obtain to set
	 */
	public void setObtain(Object[] obtain) {
		this.obtain = obtain;
	}
	/**
	 * @return the craft
	 */
	public Object[] getCraft() {
		return craft;
	}
	/**
	 * @param craft the craft to set
	 */
	public void setCraft(Object[] craft) {
		this.craft = craft;
	}	
	/**
	 * @return the craftReference
	 */
	public int[] getCraftReference() {
		return craftReference;
	}
	/**
	 * @param craftReference the craftReference to set
	 */
	public void setCraftReference(int[] craftReference) {
		this.craftReference = craftReference;
	}
	/**
	 * @return the know
	 */
	public Knowledge[] getKnow() {
		return know;
	}
	/**
	 * @param know the know to set
	 */
	public void setKnow(Knowledge[] know) {
		this.know = know;
	}
	
	/**
	 * @return the research
	 */
	public String[] getResearch() {
		return research;
	}
	/**
	 * @param research the research to set
	 */
	public void setResearch(String[] research) {
		this.research = research;
	}
	
	/**
	 * @return the research
	 */
	public String[] getResearchIcon() {
		return researchIcon;
	}
	/**
	 * @param research the research to set
	 */
	public void setResearchIcon(String[] research) {
		researchIcon = research;
	}
	
	/**
	 * @return the warp
	 */
	public int getWarp() {
		return warp;
	}
	/**
	 * @param warp the warp to set
	 */
	public void setWarp(int warp) {
		this.warp = warp;
	}
		
	public static class Knowledge {
		public static final Codec<Knowledge> CODEC = Codec.STRING.xmap(Knowledge::parse, k -> {
			if (k.category == null) {
				return k.type.name() + ";" + k.amount;
			} else {
				return k.type.name() + ";" + k.category.key + ";" + k.amount;
			}
		});

		public EnumKnowledgeType type;
    	public ResearchCategory category; 
    	public int amount = 0;
    	
    	public Knowledge(EnumKnowledgeType type, ResearchCategory category, int num) {
			super();
			this.type = type;
			this.category = category;
			amount = num;
		}

		public static Knowledge parse(String text) {
    		String[] s = text.split(";");
    		if (s.length==2) {
    			int num = 0;
    			try {
    				num = Integer.parseInt(s[1]);
    			} catch (Exception e) {}    			
    			EnumKnowledgeType t = null;
    			try {
    				t = EnumKnowledgeType.valueOf(s[0].toUpperCase());
    			} catch (Exception ignored) {}
    			if (t!=null && !t.hasFields() && num>0) {
    				return new Knowledge(t, null, num);
    			}
    		} else if (s.length==3) {
    			int num = 0;
    			try {
    				num = Integer.parseInt(s[2]);
    			} catch (Exception e) {}    			
    			EnumKnowledgeType t = null;
    			try {
    				t = EnumKnowledgeType.valueOf(s[0].toUpperCase());
    			} catch (Exception ignored) {}
    			ResearchCategory f = ResearchCategories.getResearchCategory(s[1].toUpperCase());
    			if (f == null) {
    				f = new ResearchCategory(s[1].toUpperCase());
    			}
    			if (t!=null && num>0) {
    				return new Knowledge(t,f,num);
    			}
    		}
    		return null;
    	}
    }
	
}
