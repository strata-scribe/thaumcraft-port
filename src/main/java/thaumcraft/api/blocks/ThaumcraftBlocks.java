package thaumcraft.api.blocks;
import java.util.HashMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;




public class ThaumcraftBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Thaumcraft.MODID);



	// Level
	public static DeferredBlock<Block> oreAmber = BLOCKS.registerSimpleBlock("ore_amber", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> oreCinnabar = BLOCKS.registerSimpleBlock("ore_cinnabar", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> oreQuartz = BLOCKS.registerSimpleBlock("ore_quartz", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> logGreatwood = BLOCKS.registerSimpleBlock("log_greatwood", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> logSilverwood = BLOCKS.registerSimpleBlock("log_silverwood", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> leafGreatwood = BLOCKS.registerSimpleBlock("leaf_greatwood", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> leafSilverwood = BLOCKS.registerSimpleBlock("leaf_silverwood", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> saplingGreatwood = BLOCKS.registerSimpleBlock("sapling_greatwood", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> saplingSilverwood = BLOCKS.registerSimpleBlock("sapling_silverwood", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> shimmerleaf = BLOCKS.registerSimpleBlock("shimmerleaf", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> cinderpearl = BLOCKS.registerSimpleBlock("cinderpearl", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> vishroom = BLOCKS.registerSimpleBlock("vishroom", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> plankGreatwood = BLOCKS.registerSimpleBlock("plank_greatwood", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> plankSilverwood = BLOCKS.registerSimpleBlock("plank_silverwood", () -> BlockBehaviour.Properties.of());

	public static DeferredBlock<Block> stoneArcane = BLOCKS.registerSimpleBlock("stone_arcane", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> stoneArcaneBrick = BLOCKS.registerSimpleBlock("stone_arcane_brick", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> stoneAncient = BLOCKS.registerSimpleBlock("stone_ancient", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> stoneAncientTile = BLOCKS.registerSimpleBlock("stone_ancient_tile", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> stoneAncientRock = BLOCKS.registerSimpleBlock("stone_ancient_rock", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> stoneAncientDoorway = BLOCKS.registerSimpleBlock("stone_ancient_doorway", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> stoneAncientGlyphed = BLOCKS.registerSimpleBlock("stone_ancient_glyphed", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> stoneEldritchTile = BLOCKS.registerSimpleBlock("stone_eldritch_tile", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> stonePorous = BLOCKS.registerSimpleBlock("stone_porous", () -> BlockBehaviour.Properties.of());

	public static DeferredBlock<Block> empty = BLOCKS.registerSimpleBlock("empty", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> amberBlock = BLOCKS.registerSimpleBlock("amber_block", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> amberBrick = BLOCKS.registerSimpleBlock("amber_brick", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> stairsArcane = BLOCKS.registerSimpleBlock("stairs_arcane", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> stairsArcaneBrick = BLOCKS.registerSimpleBlock("stairs_arcane_brick", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> stairsAncient = BLOCKS.registerSimpleBlock("stairs_ancient", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> stairsSilverwood = BLOCKS.registerSimpleBlock("stairs_silverwood", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> stairsGreatwood = BLOCKS.registerSimpleBlock("stairs_greatwood", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> stairsEldritch = BLOCKS.registerSimpleBlock("stairs_eldritch", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<SlabBlock> slabGreatwood = BLOCKS.registerBlock("slab_greatwood", SlabBlock::new, () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<SlabBlock> slabSilverwood = BLOCKS.registerBlock("slab_silverwood", SlabBlock::new, () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<SlabBlock> slabArcaneStone = BLOCKS.registerBlock("slab_arcane_stone", SlabBlock::new, () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<SlabBlock> slabArcaneBrick = BLOCKS.registerBlock("slab_arcane_brick", SlabBlock::new, () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<SlabBlock> slabAncient = BLOCKS.registerBlock("slab_ancient", SlabBlock::new, () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<SlabBlock> slabEldritch = BLOCKS.registerBlock("slab_eldritch", SlabBlock::new, () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<SlabBlock> doubleSlabGreatwood = BLOCKS.registerBlock("double_slab_greatwood", SlabBlock::new, () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<SlabBlock> doubleSlabSilverwood = BLOCKS.registerBlock("double_slab_silverwood", SlabBlock::new, () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<SlabBlock> doubleSlabArcaneStone = BLOCKS.registerBlock("double_slab_arcane_stone", SlabBlock::new, () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<SlabBlock> doubleSlabArcaneBrick = BLOCKS.registerBlock("double_slab_arcane_brick", SlabBlock::new, () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<SlabBlock> doubleSlabAncient = BLOCKS.registerBlock("double_slab_ancient", SlabBlock::new, () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<SlabBlock> doubleSlabEldritch = BLOCKS.registerBlock("double_slab_eldritch", SlabBlock::new, () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> taintCrust = BLOCKS.registerSimpleBlock("taint_crust", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> taintSoil = BLOCKS.registerSimpleBlock("taint_soil", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> taintRock = BLOCKS.registerSimpleBlock("taint_rock", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> taintGeyser = BLOCKS.registerSimpleBlock("taint_geyser", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> taintFibre = BLOCKS.registerSimpleBlock("taint_fibre", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> taintLog = BLOCKS.registerSimpleBlock("taint_log", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> taintFeature = BLOCKS.registerSimpleBlock("taint_feature", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> lootCrateCommon = BLOCKS.registerSimpleBlock("loot_crate_common", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> lootCrateUncommon = BLOCKS.registerSimpleBlock("loot_crate_uncommon", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> lootCrateRare = BLOCKS.registerSimpleBlock("loot_crate_rare", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> lootUrnCommon = BLOCKS.registerSimpleBlock("loot_urn_common", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> lootUrnUncommon = BLOCKS.registerSimpleBlock("loot_urn_uncommon", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> lootUrnRare = BLOCKS.registerSimpleBlock("loot_urn_rare", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> eldritch = BLOCKS.registerSimpleBlock("eldritch", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> crystalAir = BLOCKS.registerSimpleBlock("crystal_air", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> crystalFire = BLOCKS.registerSimpleBlock("crystal_fire", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> crystalWater = BLOCKS.registerSimpleBlock("crystal_water", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> crystalEarth = BLOCKS.registerSimpleBlock("crystal_earth", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> crystalOrder = BLOCKS.registerSimpleBlock("crystal_order", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> crystalEntropy = BLOCKS.registerSimpleBlock("crystal_entropy", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> crystalTaint = BLOCKS.registerSimpleBlock("crystal_taint", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> grassAmbient = BLOCKS.registerSimpleBlock("grass_ambient", () -> BlockBehaviour.Properties.of());

	// Doodads
	public static DeferredBlock<Block> tableWood = BLOCKS.registerSimpleBlock("table_wood", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> tableStone = BLOCKS.registerSimpleBlock("table_stone", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> metalBlockThaumium = BLOCKS.registerSimpleBlock("metal_block_thaumium", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> metalBlockVoid = BLOCKS.registerSimpleBlock("metal_block_void", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> metalBlockBrass = BLOCKS.registerSimpleBlock("metal_block_brass", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> metalAlchemical = BLOCKS.registerSimpleBlock("metal_alchemical", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> metalAlchemicalAdvanced = BLOCKS.registerSimpleBlock("metal_alchemical_advanced", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> bloom = BLOCKS.registerSimpleBlock("bloom", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> pedestalArcane = BLOCKS.registerSimpleBlock("pedestal_arcane", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> pedestalAncient = BLOCKS.registerSimpleBlock("pedestal_ancient", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> pedestalEldritch = BLOCKS.registerSimpleBlock("pedestal_eldritch", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> fleshBlock = BLOCKS.registerSimpleBlock("flesh_block", () -> BlockBehaviour.Properties.of());

	public static DeferredBlock<Block> pavingStoneTravel = BLOCKS.registerSimpleBlock("paving_stone_travel", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> pavingStoneBarrier = BLOCKS.registerSimpleBlock("paving_stone_barrier", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> pillarArcane = BLOCKS.registerSimpleBlock("pillar_arcane", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> pillarAncient = BLOCKS.registerSimpleBlock("pillar_ancient", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> pillarEldritch = BLOCKS.registerSimpleBlock("pillar_eldritch", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> redstoneRelay = BLOCKS.registerSimpleBlock("redstone_relay", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> matrixSpeed = BLOCKS.registerSimpleBlock("matrix_speed", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> matrixCost = BLOCKS.registerSimpleBlock("matrix_cost", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> visBattery = BLOCKS.registerSimpleBlock("vis_battery", () -> BlockBehaviour.Properties.of());

	public static HashMap<DyeColor, DeferredBlock<Block>> candles = new HashMap<>();
	public static HashMap<DyeColor, DeferredBlock<Block>> banners = new HashMap<>();
	public static HashMap<DyeColor, DeferredBlock<Block>> nitor = new HashMap<>();

	public static DeferredBlock<Block> bannerCrimsonCult = BLOCKS.registerSimpleBlock("banner_crimson_cult", () -> BlockBehaviour.Properties.of());

	public static DeferredBlock<Block> inlay = BLOCKS.registerSimpleBlock("inlay", () -> BlockBehaviour.Properties.of());

	// Machines
	public static DeferredBlock<Block> arcaneEar = BLOCKS.registerSimpleBlock("arcane_ear", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> arcaneEarToggle = BLOCKS.registerSimpleBlock("arcane_ear_toggle", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> levitator = BLOCKS.registerSimpleBlock("levitator", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> dioptra = BLOCKS.registerSimpleBlock("dioptra", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> crucible = BLOCKS.registerSimpleBlock("crucible", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> arcaneWorkbench = BLOCKS.registerSimpleBlock("arcane_workbench", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> arcaneWorkbenchCharger = BLOCKS.registerSimpleBlock("arcane_workbench_charger", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> wandWorkbench = BLOCKS.registerSimpleBlock("wand_workbench", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> rechargePedestal = BLOCKS.registerSimpleBlock("recharge_pedestal", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> researchTable = BLOCKS.registerSimpleBlock("research_table", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> tube = BLOCKS.registerSimpleBlock("tube", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> tubeValve = BLOCKS.registerSimpleBlock("tube_valve", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> tubeRestrict = BLOCKS.registerSimpleBlock("tube_restrict", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> tubeOneway = BLOCKS.registerSimpleBlock("tube_oneway", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> tubeFilter = BLOCKS.registerSimpleBlock("tube_filter", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> tubeBuffer = BLOCKS.registerSimpleBlock("tube_buffer", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> centrifuge = BLOCKS.registerSimpleBlock("centrifuge", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> hungryChest = BLOCKS.registerSimpleBlock("hungry_chest", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> jarNormal = BLOCKS.registerSimpleBlock("jar_normal", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> jarVoid = BLOCKS.registerSimpleBlock("jar_void", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> jarBrain = BLOCKS.registerSimpleBlock("jar_brain", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> bellows = BLOCKS.registerSimpleBlock("bellows", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> smelterBasic = BLOCKS.registerSimpleBlock("smelter_basic", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> smelterThaumium = BLOCKS.registerSimpleBlock("smelter_thaumium", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> smelterVoid = BLOCKS.registerSimpleBlock("smelter_void", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> smelterAux = BLOCKS.registerSimpleBlock("smelter_aux", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> smelterVent = BLOCKS.registerSimpleBlock("smelter_vent", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> fluxScrubber = BLOCKS.registerSimpleBlock("flux_scrubber", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> alembic = BLOCKS.registerSimpleBlock("alembic", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> infusionMatrix = BLOCKS.registerSimpleBlock("infusion_matrix", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> infernalFurnace = BLOCKS.registerSimpleBlock("infernal_furnace", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> thaumatorium = BLOCKS.registerSimpleBlock("thaumatorium", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> thaumatoriumTop = BLOCKS.registerSimpleBlock("thaumatorium_top", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> brainBox = BLOCKS.registerSimpleBlock("brain_box", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> mirror = BLOCKS.registerSimpleBlock("mirror", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> mirrorEssentia = BLOCKS.registerSimpleBlock("mirror_essentia", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> auraTotem = BLOCKS.registerSimpleBlock("aura_totem", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> spa = BLOCKS.registerSimpleBlock("spa", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> everfullUrn = BLOCKS.registerSimpleBlock("everfull_urn", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> lampArcane = BLOCKS.registerSimpleBlock("lamp_arcane", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> lampFertility = BLOCKS.registerSimpleBlock("lamp_fertility", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> lampGrowth = BLOCKS.registerSimpleBlock("lamp_growth", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> golemBuilder = BLOCKS.registerSimpleBlock("golem_builder", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> essentiaTransportInput = BLOCKS.registerSimpleBlock("essentia_transport_input", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> essentiaTransportOutput = BLOCKS.registerSimpleBlock("essentia_transport_output", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> patternCrafter = BLOCKS.registerSimpleBlock("pattern_crafter", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> activatorRail = BLOCKS.registerSimpleBlock("activator_rail", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> potionSprayer = BLOCKS.registerSimpleBlock("potion_sprayer", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> stabilizer = BLOCKS.registerSimpleBlock("stabilizer", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> visGenerator = BLOCKS.registerSimpleBlock("vis_generator", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> condenser = BLOCKS.registerSimpleBlock("condenser", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> condenserlattice = BLOCKS.registerSimpleBlock("condenserlattice", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> condenserlatticeDirty = BLOCKS.registerSimpleBlock("condenserlattice_dirty", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> voidSiphon = BLOCKS.registerSimpleBlock("void_siphon", () -> BlockBehaviour.Properties.of());

	// Fluids
	public static DeferredBlock<Block> fluxGoo = BLOCKS.registerSimpleBlock("flux_goo", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> purifyingFluid = BLOCKS.registerSimpleBlock("purifying_fluid", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> liquidDeath = BLOCKS.registerSimpleBlock("liquid_death", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> taintDust = BLOCKS.registerSimpleBlock("taint_dust", () -> BlockBehaviour.Properties.of());

	// Misc
	public static DeferredBlock<Block> hole = BLOCKS.registerSimpleBlock("hole", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> effectShock = BLOCKS.registerSimpleBlock("effect_shock", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> effectSap = BLOCKS.registerSimpleBlock("effect_sap", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> effectGlimmer = BLOCKS.registerSimpleBlock("effect_glimmer", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> barrier = BLOCKS.registerSimpleBlock("barrier", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> placeholderNetherbrick = BLOCKS.registerSimpleBlock("placeholder_netherbrick", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> placeholderObsidian = BLOCKS.registerSimpleBlock("placeholder_obsidian", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> placeholderBars = BLOCKS.registerSimpleBlock("placeholder_bars", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> placeholderAnvil = BLOCKS.registerSimpleBlock("placeholder_anvil", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> placeholderCauldron = BLOCKS.registerSimpleBlock("placeholder_cauldron", () -> BlockBehaviour.Properties.of());
	public static DeferredBlock<Block> placeholderTable = BLOCKS.registerSimpleBlock("placeholder_table", () -> BlockBehaviour.Properties.of());





































}
