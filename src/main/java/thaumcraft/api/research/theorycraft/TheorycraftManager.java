package thaumcraft.api.research.theorycraft;
import java.util.HashMap;


public class TheorycraftManager {
	
	//
	
	public static HashMap<String,ITheorycraftAid> aids = new HashMap<>();
	
	// Bonus Maps for research table aids
	public static HashMap<String, String> aidCategories = new HashMap<>();
	public static HashMap<String, Float> aidCategoryWeights = new HashMap<>();
	public static HashMap<String, Float> aidProgressMultipliers = new HashMap<>();

	public static void registerAid(ITheorycraftAid aid) {
		String key = aid.getClass().getName();
		if (!aids.containsKey(key))
			aids.put(key, aid);
	}
	
	public static void registerAidBonus(String aidKey, String category, float weightMultiplier, float progressMultiplier) {
		aidCategories.put(aidKey, category);
		aidCategoryWeights.put(aidKey, weightMultiplier);
		aidProgressMultipliers.put(aidKey, progressMultiplier);
	}

	//
	
	public static HashMap<String,Class<TheorycraftCard>> cards = new HashMap<>();	
	
	public static void registerCard(Class cardClass) {
		String key = cardClass.getName();
		if (!cards.containsKey(key))
			cards.put(key, cardClass);
	}
	

}
