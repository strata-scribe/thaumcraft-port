package thaumcraft.api.research;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.List;

import net.minecraft.resources.Identifier;
import net.minecraft.client.resources.language.I18n;


public class ResearchAddendum {

	public static final Codec<ResearchAddendum> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.STRING.fieldOf("text").forGetter(ResearchAddendum::getText),
		Identifier.CODEC.listOf().optionalFieldOf("recipes", List.of()).forGetter(a -> a.getRecipes() == null ? List.of() : Arrays.asList(a.getRecipes())),
		Codec.STRING.listOf().optionalFieldOf("required_research", List.of()).forGetter(a -> a.getResearch() == null ? List.of() : Arrays.asList(a.getResearch()))
	).apply(instance, (text, recipes, research) -> {
		ResearchAddendum a = new ResearchAddendum();
		a.setText(text);
		if (!recipes.isEmpty()) a.setRecipes(recipes.toArray(new Identifier[0]));
		if (!research.isEmpty()) a.setResearch(research.toArray(new String[0]));
		return a;
	}));

	String text;
	Identifier[] recipes;
	String[] research;
	
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
	
}
