package thaumcraft.client.render;

public class JarRenderMathLogic {

    /**
     * Calculates the Y-scale of the fluid cube (0.0 to 0.75) proportional to the stored essentia amount (0 to 250).
     */
    public static float calculateFluidYScale(int storedEssentia) {
        int amount = Math.max(0, Math.min(250, storedEssentia));
        return (amount / 250.0f) * 0.75f;
    }

    public record BillboardRotation(float yRot, float xRot) {}

    /**
     * Calculates the billboard rotation required for a quad to face the player.
     */
    public static BillboardRotation calculateBillboardRotation(float playerPitch, float playerYaw) {
        return new BillboardRotation(180.0f - playerYaw, -playerPitch);
    }

    public record LabelUV(float minU, float maxU, float minV, float maxV) {}

    /**
     * Calculates the UV texture coordinates for a label quad.
     */
    public static LabelUV calculateLabelUVs(float spriteMinU, float spriteMaxU, float spriteMinV, float spriteMaxV) {
        return new LabelUV(spriteMinU, spriteMaxU, spriteMinV, spriteMaxV);
    }

    /**
     * Returns the hex color for a given aspect tag without needing any Minecraft or Forge imports.
     */
    public static int getAspectColor(String aspectTag) {
        if (aspectTag == null) return 0xFFFFFF;

        switch (aspectTag.toLowerCase()) {
            // Primal
            case "aer": return 0xffff7e;
            case "terra": return 0x56c000;
            case "ignis": return 0xff5a01;
            case "aqua": return 0x3cd4fc;
            case "ordo": return 0xd5d4ec;
            case "perditio": return 0x404040;
            // Compound (Subset of core aspects often used)
            case "vacuos": return 0x888888;
            case "lux": return 0xffffc0;
            case "motus": return 0xcdccf4;
            case "gelum": return 0xe1ffff;
            case "vitreus": return 0x80ffff;
            case "metallum": return 0xb5b5cd;
            case "victus": return 0xde0005;
            case "mortuus": return 0x6a0005;
            case "potentia": return 0xc0ffff;
            case "permutatio": return 0x578357;
            case "praecantatio": return 0xcf00ff;
            case "auram": return 0xffc0ff;
            case "alkimia": return 0x23ac9d;
            case "vitium": return 0x800080;
            case "tenebrae": return 0x222222;
            case "alienis": return 0x805080;
            case "volatus": return 0xe7e7d7;
            case "herba": return 0x01ac00;
            case "instrumentum": return 0x4040ee;
            case "fabrico": return 0x809d80;
            case "machina": return 0x8080a0;
            case "vinculum": return 0x9a8080;
            case "spiritus": return 0xebebfb;
            case "cognitio": return 0xf9967f;
            case "sensus": return 0xc0ffc0;
            case "aversio": return 0xc05050;
            case "praemunio": return 0x00c0c0;
            case "desiderium": return 0xe6be44;
            case "exanimis": return 0x3a4000;
            case "bestia": return 0x9f6409;
            case "humanus": return 0xffd7c0;
            default: return 0xFFFFFF;
        }
    }
}
