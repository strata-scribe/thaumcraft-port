package thaumcraft.api.golems;

import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import java.util.function.Supplier;

public class EnumGolemTrait {

	public static final DeferredHolder<EnumGolemTrait, EnumGolemTrait> SMART = register("smart");
	public static final DeferredHolder<EnumGolemTrait, EnumGolemTrait> DEFT = register("deft");
	public static final DeferredHolder<EnumGolemTrait, EnumGolemTrait> CLUMSY = register("clumsy", () -> DEFT);
	public static final DeferredHolder<EnumGolemTrait, EnumGolemTrait> FIGHTER = register("fighter");
	public static final DeferredHolder<EnumGolemTrait, EnumGolemTrait> WHEELED = register("wheeled");
	public static final DeferredHolder<EnumGolemTrait, EnumGolemTrait> FLYER = register("flyer");
	public static final DeferredHolder<EnumGolemTrait, EnumGolemTrait> CLIMBER = register("climber");
	public static final DeferredHolder<EnumGolemTrait, EnumGolemTrait> HEAVY = register("heavy");
	public static final DeferredHolder<EnumGolemTrait, EnumGolemTrait> LIGHT = register("light", () -> HEAVY);
	public static final DeferredHolder<EnumGolemTrait, EnumGolemTrait> FRAGILE = register("fragile");
	public static final DeferredHolder<EnumGolemTrait, EnumGolemTrait> REPAIR = register("repair");
	public static final DeferredHolder<EnumGolemTrait, EnumGolemTrait> SCOUT = register("scout");
	public static final DeferredHolder<EnumGolemTrait, EnumGolemTrait> ARMORED = register("armored", () -> FRAGILE);
	public static final DeferredHolder<EnumGolemTrait, EnumGolemTrait> BRUTAL = register("brutal");
	public static final DeferredHolder<EnumGolemTrait, EnumGolemTrait> FIREPROOF = register("fireproof");
	public static final DeferredHolder<EnumGolemTrait, EnumGolemTrait> BREAKER = register("breaker");
	public static final DeferredHolder<EnumGolemTrait, EnumGolemTrait> HAULER = register("hauler");
	public static final DeferredHolder<EnumGolemTrait, EnumGolemTrait> RANGED = register("ranged");
	public static final DeferredHolder<EnumGolemTrait, EnumGolemTrait> BLASTPROOF = register("blastproof");
	
	private static DeferredHolder<EnumGolemTrait, EnumGolemTrait> register(String name) {
		return register(name, null);
	}
	
	private static DeferredHolder<EnumGolemTrait, EnumGolemTrait> register(String name, Supplier<DeferredHolder<EnumGolemTrait, EnumGolemTrait>> oppositeSupplier) {
		return ThaumcraftGolemRegistries.GOLEM_TRAITS.register(name, () -> new EnumGolemTrait(name, oppositeSupplier));
	}

	public Identifier icon;
	private Supplier<DeferredHolder<EnumGolemTrait, EnumGolemTrait>> oppositeSupplier;
	private String name;

	public EnumGolemTrait(String name, Supplier<DeferredHolder<EnumGolemTrait, EnumGolemTrait>> oppositeSupplier) {
		this.name = name;
		this.icon = Identifier.fromNamespaceAndPath("thaumcraft","textures/misc/golem/tag_" + name + ".png");
		this.oppositeSupplier = oppositeSupplier;
	}

	public static void init() {
	}

	public String name() {
		return name.toUpperCase();
	}
	
	public EnumGolemTrait getOpposite() {
		if (oppositeSupplier != null && oppositeSupplier.get() != null) {
			return oppositeSupplier.get().get();
		}
		if (this == DEFT.get()) return CLUMSY.get();
		if (this == HEAVY.get()) return LIGHT.get();
		if (this == FRAGILE.get()) return ARMORED.get();
		return null;
	}
	
	public String getLocalizedName() {
		return net.minecraft.network.chat.Component.translatable("golem.trait."+ name.toLowerCase()).getString();
	}
	
	public String getLocalizedDescription() {
		return net.minecraft.network.chat.Component.translatable("golem.trait.text."+ name().toLowerCase()).getString();
	}
}