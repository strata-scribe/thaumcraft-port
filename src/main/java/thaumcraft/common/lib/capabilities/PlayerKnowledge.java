package thaumcraft.common.lib.capabilities;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import thaumcraft.Thaumcraft;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketSyncKnowledge;

public class PlayerKnowledge implements IPlayerKnowledge {
    
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

    private HashSet<String> research;
    private Map<String, Integer> stages;
    private Map<String, HashSet<EnumResearchFlag>> flags;
    private Map<String, Integer> knowledge;
    
    public PlayerKnowledge() {
        research = new HashSet<String>();
        stages = new HashMap<String, Integer>();
        flags = new HashMap<String, HashSet<EnumResearchFlag>>();
        knowledge = new HashMap<String, Integer>();
    }
    
    @Override
    public void clear() {
        research.clear();
        flags.clear();
        stages.clear();
        knowledge.clear();
    }
    
    @Override
    public EnumResearchStatus getResearchStatus(@Nonnull String res) {
        if (!isResearchKnown(res)) {
            return EnumResearchStatus.UNKNOWN;
        }
        ResearchEntry entry = ResearchCategories.getResearch(res);
        if (entry == null || entry.getStages() == null || getResearchStage(res) > entry.getStages().length) {
            return EnumResearchStatus.COMPLETE;
        }
        return EnumResearchStatus.IN_PROGRESS;
    }
    
    @Override
    public boolean isResearchKnown(String res) {
        if (res == null) {
            return false;
        }
        if (res.equals("")) {
            return true;
        }
        String[] ss = res.split("@");
        return (ss.length <= 1 || getResearchStage(ss[0]) >= Mth.getInt(ss[1], 0)) && research.contains(ss[0]);
    }
    
    @Override
    public boolean isResearchComplete(String res) {
        return getResearchStatus(res) == EnumResearchStatus.COMPLETE;
    }
    
    @Override
    public int getResearchStage(String res) {
        if (res == null || !research.contains(res)) {
            return -1;
        }
        Integer stage = stages.get(res);
        return (stage == null) ? 0 : stage;
    }
    
    @Override
    public boolean setResearchStage(String res, int stage) {
        if (res == null || !research.contains(res) || stage <= 0) {
            return false;
        }
        stages.put(res, stage);
        return true;
    }
    
    @Override
    public boolean addResearch(@Nonnull String res) {
        if (!isResearchKnown(res)) {
            research.add(res);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean removeResearch(@Nonnull String res) {
        if (isResearchKnown(res)) {
            research.remove(res);
            return true;
        }
        return false;
    }
    
    @Nonnull
    @Override
    public Set<String> getResearchList() {
        return Collections.unmodifiableSet(research);
    }
    
    @Override
    public boolean setResearchFlag(@Nonnull String res, @Nonnull EnumResearchFlag flag) {
        HashSet<EnumResearchFlag> list = flags.get(res);
        if (list == null) {
            list = new HashSet<EnumResearchFlag>();
            flags.put(res, list);
        }
        if (list.contains(flag)) {
            return false;
        }
        list.add(flag);
        return true;
    }
    
    @Override
    public boolean clearResearchFlag(@Nonnull String res, @Nonnull EnumResearchFlag flag) {
        HashSet<EnumResearchFlag> list = flags.get(res);
        if (list != null) {
            boolean b = list.remove(flag);
            if (list.isEmpty()) {
                flags.remove(res);
            }
            return b;
        }
        return false;
    }
    
    @Override
    public boolean hasResearchFlag(@Nonnull String res, @Nonnull EnumResearchFlag flag) {
        return flags.get(res) != null && flags.get(res).contains(flag);
    }
    
    private String getKey(EnumKnowledgeType type, ResearchCategory category) {
        return type.getAbbreviation() + "_" + ((category == null) ? "" : category.key);
    }
    
    @Override
    public boolean addKnowledge(EnumKnowledgeType type, ResearchCategory category, int amount) {
        String key = getKey(type, category);
        int c = getKnowledgeRaw(type, category);
        if (c + amount < 0) {
            return false;
        }
        c += amount;
        knowledge.put(key, c);
        return true;
    }
    
    @Override
    public int getKnowledge(EnumKnowledgeType type, ResearchCategory category) {
        String key = getKey(type, category);
        int c = knowledge.containsKey(key) ? knowledge.get(key) : 0;
        return (int)Math.floor(c / (double)type.getProgression());
    }
    
    @Override
    public int getKnowledgeRaw(EnumKnowledgeType type, ResearchCategory category) {
        String key = getKey(type, category);
        return knowledge.containsKey(key) ? knowledge.get(key) : 0;
    }
    
    @Override
    public void sync(ServerPlayer player) {
        net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player, new PacketSyncKnowledge(this.serializeNBT()));
    }
    
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag rootTag = new CompoundTag();
        ListTag researchList = new ListTag();
        for (String resKey : research) {
            CompoundTag tag = new CompoundTag();
            tag.putString("key", resKey);
            if (stages.containsKey(resKey)) {
                tag.putInt("stage", stages.get(resKey));
            }
            if (flags.containsKey(resKey)) {
                HashSet<EnumResearchFlag> list = flags.get(resKey);
                if (list != null) {
                    String fs = "";
                    for (EnumResearchFlag flag : list) {
                        if (fs.length() > 0) {
                            fs += ",";
                        }
                        fs += flag.name();
                    }
                    tag.putString("flags", fs);
                }
            }
            researchList.add(tag);
        }
        rootTag.put("research", researchList);
        ListTag knowledgeList = new ListTag();
        for (String key : knowledge.keySet()) {
            Integer c = knowledge.get(key);
            if (c != null && c > 0 && key != null && !key.isEmpty()) {
                CompoundTag tag2 = new CompoundTag();
                tag2.putString("key", key);
                tag2.putInt("amount", c);
                knowledgeList.add(tag2);
            }
        }
        rootTag.put("knowledge", knowledgeList);
        return rootTag;
    }
    
    @Override
    public void deserializeNBT(CompoundTag rootTag) {
        if (rootTag == null) {
            return;
        }
        clear();
        ListTag researchList = rootTag.getListOrEmpty("research");
        for (int i = 0; i < researchList.size(); ++i) {
            CompoundTag tag = researchList.getCompoundOrEmpty(i);
            String know = tag.getStringOr("key", "");
            if (!know.isEmpty() && !isResearchKnown(know)) {
                research.add(know);
                int stage = tag.getIntOr("stage", 0);
                if (stage > 0) {
                    stages.put(know, stage);
                }
                String fs = tag.getStringOr("flags", "");
                if (fs.length() > 0) {
                    String[] split;
                    String[] ss = split = fs.split(",");
                    for (String s : split) {
                        EnumResearchFlag flag = null;
                        try {
                            flag = EnumResearchFlag.valueOf(s);
                        }
                        catch (Exception ex) {}
                        if (flag != null) {
                            setResearchFlag(know, flag);
                        }
                    }
                }
            }
        }
        ListTag knowledgeList = rootTag.getListOrEmpty("knowledge");
        for (int j = 0; j < knowledgeList.size(); ++j) {
            CompoundTag tag2 = knowledgeList.getCompoundOrEmpty(j);
            String key = tag2.getStringOr("key", "");
            int amount = tag2.getIntOr("amount", 0);
            knowledge.put(key, amount);
        }
        addAutoUnlockResearch();
    }
    
    private void addAutoUnlockResearch() {
        for (ResearchCategory cat : ResearchCategories.researchCategories.values()) {
            for (ResearchEntry ri : cat.research.values()) {
                if (ri.hasMeta(ResearchEntry.EnumResearchMeta.AUTOUNLOCK)) {
                    addResearch(ri.getKey());
                }
            }
        }
    }
}
