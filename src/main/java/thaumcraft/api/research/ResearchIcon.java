package thaumcraft.api.research;

import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;

public record ResearchIcon(String rawText, Identifier textureLocation, boolean isTexture) {

    public static final Codec<ResearchIcon> CODEC = Codec.STRING.xmap(ResearchIcon::parse, ResearchIcon::rawText);

    public static ResearchIcon parse(String text) {
        if (text.endsWith(".png") || text.contains("textures/")) {
            return new ResearchIcon(text, Identifier.tryParse(text), true);
        }
        return new ResearchIcon(text, null, false);
    }
}
