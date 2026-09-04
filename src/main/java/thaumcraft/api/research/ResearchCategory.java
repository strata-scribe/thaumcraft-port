package thaumcraft.api.research;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;



public class ResearchCategory {

	public static final Codec<ResearchCategory> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("key").forGetter(c -> c.key),
			Codec.STRING.optionalFieldOf("researchKey", "").forGetter(c -> c.researchKey != null ? c.researchKey : ""),
			AspectList.CODEC.fieldOf("formula").forGetter(c -> c.formula),
			Identifier.CODEC.fieldOf("icon").forGetter(c -> c.icon),
			Identifier.CODEC.fieldOf("background").forGetter(c -> c.background),
			Identifier.CODEC.optionalFieldOf("background2").forGetter(c -> Optional.ofNullable(c.background2))
	).apply(instance, (key, researchKey, formula, icon, background, background2) -> {
		return new ResearchCategory(key, researchKey.isEmpty() ? null : researchKey, formula, icon, background, background2.orElse(null));
	}));

	
	/** Is the smallest column used on the GUI. */
    public int minDisplayColumn;

    /** Is the smallest row used on the GUI. */
    public int minDisplayRow;

    /** Is the biggest column used on the GUI. */
    public int maxDisplayColumn;

    /** Is the biggest row used on the GUI. */
    public int maxDisplayRow;
    
    /** display variables **/
    public Identifier icon;
    public Identifier background;
    public Identifier background2;
    
    public String researchKey;
    public String key;
    
    public AspectList formula;
	
	public ResearchCategory(String key, String researchkey, AspectList formula, Identifier icon, Identifier background) {
		this.key = key;
		researchKey = researchkey;
		this.icon = icon;
		this.background = background;
		background2 = null;
		this.formula = formula;
	}
	
	public ResearchCategory(String key, String researchKey, AspectList formula, Identifier icon, Identifier background, Identifier background2) {
		this.key = key;
		this.researchKey = researchKey;
		this.icon = icon;
		this.background = background;
		this.background2 = background2;
		this.formula = formula;
	}
	
	/**
	 * For a given list of aspects this method will calculate the amount of raw knowledge you will be able to gain for the knowledge field.
	 * @param as
	 * @return
	 */
	public int applyFormula(AspectList as) {		
		return applyFormula(as,1);
	}
	
	/**
	 * This version of the method accepts a multiplier for the total - should usually not be needed by addon mods
	 * @param as
	 * @param mod multiplier to total
	 * @return
	 */
	public int applyFormula(AspectList as, double mod) {			
		if (formula==null) return 0;
		double total=0;
		for (Aspect aspect:formula.getAspects()) {
			total += (mod * mod) * as.getAmount(aspect) * (formula.getAmount(aspect) / 10d);
		}
		if (total>0) total = Math.sqrt(total); 
		return Mth.ceil( total );
	}

	//Research
	public Map<String, ResearchEntry> research = new HashMap<String,ResearchEntry>();	
	
}
