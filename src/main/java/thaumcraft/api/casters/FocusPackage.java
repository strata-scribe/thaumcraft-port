package thaumcraft.api.casters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class FocusPackage implements IFocusElement {
	
	@Override
	public String getResearch() {
		return null;
	}
	
	public Level world;
	private LivingEntity caster;	
	private UUID casterUUID;
	
	private float power = 1;
	private int complexity = 0;
	
	int index;
	UUID uid;
	
	public List<IFocusElement> nodes = Collections.synchronizedList(new ArrayList<>());	
	
	public FocusPackage() {	}

	public FocusPackage(LivingEntity caster) {
		super();
		world = caster.level();
		this.caster = caster;
		casterUUID = caster.getUUID();
	}	
		
	@Override
	public String getKey() {
		return "thaumcraft.PACKAGE";
	}

	@Override
	public EnumUnitType getType() {
		return EnumUnitType.PACKAGE;
	}

	public UUID getUid() {
		return uid;
	}

	public void setUid(UUID uid) {
		this.uid = uid;
	}

	public UUID getUniqueID() {
		return uid;
	}

	public void setUniqueID(UUID uid) {
		this.uid = uid;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getExecutionIndex() {
		return index;
	}

	public void setExecutionIndex(int index) {
		this.index = index;
	}
	
	public int getComplexity() {
		return complexity;
	}

	public void setComplexity(int complexity) {
		this.complexity = complexity;
	}

	public void addNode(IFocusElement node) {
		nodes.add(node);
	}
	
	public void addNode(int idx, IFocusElement node) {
		nodes.add(idx, node);
	}
	
	public void removeNode(int idx) {
		nodes.remove(idx);
	}

	public UUID getCasterUUID() {
		return casterUUID;
	}

	public void setCasterUUID(UUID casterUUID) {
		this.casterUUID = casterUUID;
	}	
	
	public LivingEntity getCaster() {
		try {
			if (caster==null) {
				caster = (LivingEntity) world.getPlayerByUUID(getCasterUUID());
			}
			if (caster==null) {
				if (world instanceof net.minecraft.server.level.ServerLevel serverLevel) {
					net.minecraft.world.entity.Entity ent = serverLevel.getEntity(getCasterUUID());
					if (ent instanceof LivingEntity) {
						caster = (LivingEntity) ent;
					}
				}
			}
		} catch (Exception e) {}
		return caster;
	}
	
	public FocusEffect[] getFocusEffects() {		
		return getFocusEffectsPackage(this);
	}
	
	private FocusEffect[] getFocusEffectsPackage(FocusPackage fp) {
		ArrayList<FocusEffect> out = new ArrayList<>();
		for (IFocusElement el:fp.nodes) {
			if (el instanceof FocusEffect) out.add((FocusEffect)el);
			if (el instanceof FocusModSplit) {
				for (FocusPackage fsp:((FocusModSplit)el).getSplitPackages())
					for (FocusEffect fep:getFocusEffectsPackage(fsp))
						out.add(fep);
			}
		}
		return out.toArray(new FocusEffect[]{});
	}

	public void deserialize(CompoundTag nbt) {
		uid = nbt.read("uid", net.minecraft.core.UUIDUtil.CODEC).orElse(null);		
		index = nbt.getInt("index").orElse(0);
		if (nbt.contains("dim")) {
			String dimString = nbt.getString("dim").orElse("");
			try {
				ResourceKey<Level> dimKey = ResourceKey.create(
						Registries.DIMENSION,
						Identifier.parse(dimString)
				);
				net.minecraft.server.MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
				if (server != null) {
					world = server.getLevel(dimKey);
				}
			} catch (Exception e) {}
		}
		setCasterUUID(nbt.read("casterUUID", net.minecraft.core.UUIDUtil.CODEC).orElse(null));
		power = nbt.getFloat("power").orElse(0.0f);
		complexity = nbt.getInt("complexity").orElse(0);
				
		ListTag nodelist = nbt.getList("nodes").orElse(new ListTag());
		nodes.clear();
		for (int x=0;x<nodelist.size();x++) {
			CompoundTag nodenbt = nodelist.getCompound(x).orElse(new CompoundTag());
			EnumUnitType ut = EnumUnitType.valueOf(nodenbt.getString("type").orElse(""));
			if (ut!=null) {
				if (ut==EnumUnitType.PACKAGE) {
					FocusPackage fp = new FocusPackage();
					fp.deserialize(nodenbt.getCompound("package").orElse(new CompoundTag()));
					nodes.add(fp);
					break;
				} else {
					IFocusElement fn = FocusEngine.getElement(nodenbt.getString("key").orElse("")); 
					if (fn!=null) {						
						if (fn instanceof FocusNode) {
							((FocusNode)fn).initialize();
							if (((FocusNode)fn).getSettingList()!=null)
								for (String ns : ((FocusNode)fn).getSettingList()) {
									((FocusNode)fn).getSetting(ns).setValue(nodenbt.getInt("setting."+ns).orElse(0));
								}
							
							if (fn instanceof FocusModSplit) {								
								((FocusModSplit)fn).deserialize(nodenbt.getCompound("packages").orElse(new CompoundTag()));		
							}
						}
						addNode(fn);
					}
				}
			}
		}
	}

	public CompoundTag serialize() {
		CompoundTag nbt = new CompoundTag();
		if (uid!=null) nbt.store("uid", net.minecraft.core.UUIDUtil.CODEC, uid);
		nbt.putInt("index", index);
		if (getCasterUUID() != null) nbt.store("casterUUID", net.minecraft.core.UUIDUtil.CODEC, getCasterUUID());
		if (world!=null) nbt.putString("dim", world.dimension().identifier().toString());
		nbt.putFloat("power", power);
		nbt.putInt("complexity", complexity);
		
		//nodes
		ListTag nodelist = new ListTag();
		synchronized (nodes) {
			for (IFocusElement node:nodes) {
				if (node==null || node.getType()==null) continue;
				CompoundTag nodenbt = new CompoundTag();
				nodenbt.putString("type", node.getType().name());
				nodenbt.putString("key", node.getKey());
				if (node.getType()==EnumUnitType.PACKAGE) {
					nodenbt.put("package", ((FocusPackage)node).serialize());
					nodelist.add(nodenbt);
					break;
				} else {				
					if (node instanceof FocusNode && ((FocusNode)node).getSettingList()!=null)
						for (String ns : ((FocusNode)node).getSettingList()) {
							nodenbt.putInt("setting."+ns, ((FocusNode)node).getSettingValue(ns));
						}
					if (node instanceof FocusModSplit) {	
						nodenbt.put("packages", ((FocusModSplit)node).serialize());	
					}
					nodelist.add(nodenbt);
				}			
			}
		}
		nbt.put("nodes", nodelist);					
		
		return nbt;
	}

	public float getPower() {
		return power;
	}

	public void multiplyPower(float pow) {
		power *= pow;
	}

	public FocusPackage copy(LivingEntity caster) {
		FocusPackage fp = new FocusPackage(caster);
		fp.deserialize(serialize());
		return fp;
	}
	
	public void initialize(LivingEntity caster) {
		world=caster.level();
		IFocusElement node = nodes.get(0);
		if (node instanceof FocusMediumRoot && ((FocusMediumRoot)node).supplyTargets()==null) {
			((FocusMediumRoot)node).setupFromCaster(caster);
		}
	}

	public int getSortingHelper() {
		String s="";
		for (IFocusElement k: nodes) {
			s+=k.getKey();
			if (k instanceof FocusNode && ((FocusNode)k).getSettingList()!=null)
				for (String ns : ((FocusNode)k).getSettingList()) {
					s += ""+((FocusNode)k).getSettingValue(ns);
				}
		}		
		return s.hashCode();
	}
}
