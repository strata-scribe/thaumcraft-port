package thaumcraft.api.research;

import com.mojang.serialization.Codec;

public record ResearchItemRequirement(String itemOrTag, int count, boolean isTag) {

    public static final Codec<ResearchItemRequirement> CODEC = Codec.STRING.xmap(ResearchItemRequirement::parse, ResearchItemRequirement::asString);

    public static ResearchItemRequirement parse(String text) {
        String item = text;
        int count = 1;
        if (text.contains(";")) {
            String[] parts = text.split(";", 2);
            item = parts[0];
            try {
                count = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                count = 1;
            }
        }

        boolean isTag = false;
        if (item.startsWith("oredict:")) {
            isTag = true;
            item = item.substring(8);
        }

        return new ResearchItemRequirement(item, count, isTag);
    }

    private String asString() {
        String result = (this.isTag ? "oredict:" : "") + this.itemOrTag;
        if (this.count != 1) {
            result += ";" + this.count;
        }
        return result;
    }
}
