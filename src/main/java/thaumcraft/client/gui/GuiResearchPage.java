package thaumcraft.client.gui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.Identifier;
import thaumcraft.Thaumcraft;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.api.research.ResearchStage;

public class GuiResearchPage extends Screen {
    private static final Identifier TEX_BOOK = Identifier.parse(Thaumcraft.MODID + ":textures/gui/gui_researchbook.png");
    private final Screen parentScreen;
    private final ResearchEntry entry;
    private int page = 0;

    public GuiResearchPage(Screen parentScreen, ResearchEntry entry) {
        super(net.minecraft.network.chat.Component.translatable(entry.getName()));
        this.parentScreen = parentScreen;
        this.entry = entry;
    }

    @Override
    public void extractRenderState(net.minecraft.client.gui.GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTicks) {
        super.extractRenderState(extractor, mouseX, mouseY, partialTicks);
        
        // Draw the book background (centered)
        int w = 256;
        int h = 180;
        int x = (this.width - w) / 2;
        int y = (this.height - h) / 2;
        
        extractor.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEX_BOOK, x, y, 0f, 0f, w, h, 256, 256);
        
        // Draw title
        extractor.centeredText(this.font, net.minecraft.network.chat.Component.translatable(entry.getName()), this.width / 2, y + 10, 0xFF442200);
        
        // Draw stage description text if stages are present
        ResearchStage[] stages = entry.getStages();
        if (stages != null && stages.length > 0) {
            int currentStageIdx = Math.max(0, Math.min(stages.length - 1, page));
            ResearchStage stage = stages[currentStageIdx];
            
            String text = stage.getTextLocalized();
            
            // Draw left page text wrapped
            extractor.textWithWordWrap(this.font, net.minecraft.network.chat.Component.literal(text), x + 20, y + 30, 95, 0xFF331100);
            
            // Draw stage page indicator/navigation
            String stageIndicator = (currentStageIdx + 1) + " / " + stages.length;
            extractor.centeredText(this.font, net.minecraft.network.chat.Component.literal(stageIndicator), this.width / 2, y + 160, 0xFF553311);
        }
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean isSecondary) {
        double mouseX = event.x();
        double mouseY = event.y();
        int w = 256;
        int h = 180;
        int x = (this.width - w) / 2;
        int y = (this.height - h) / 2;
        
        if (event.button() == 0) {
            // Check if clicked close / back outside book boundary
            if (mouseX < x || mouseX > x + w || mouseY < y || mouseY > y + h) {
                net.minecraft.client.Minecraft.getInstance().setScreen(this.parentScreen);
                net.minecraft.client.Minecraft.getInstance().getSoundManager().play(net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(net.minecraft.sounds.SoundEvents.BOOK_PAGE_TURN, 1.0F));
                return true;
            }
            
            // Handle clicking left/right navigation region within the book
            ResearchStage[] stages = entry.getStages();
            if (stages != null && stages.length > 1) {
                if (mouseX >= x + w / 2 && mouseX <= x + w - 20 && mouseY >= y + h - 30 && mouseY <= y + h - 10) {
                    if (page < stages.length - 1) {
                        page++;
                        net.minecraft.client.Minecraft.getInstance().getSoundManager().play(net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(net.minecraft.sounds.SoundEvents.BOOK_PAGE_TURN, 1.0F));
                        return true;
                    }
                } else if (mouseX >= x + 20 && mouseX <= x + w / 2 && mouseY >= y + h - 30 && mouseY <= y + h - 10) {
                    if (page > 0) {
                        page--;
                        net.minecraft.client.Minecraft.getInstance().getSoundManager().play(net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(net.minecraft.sounds.SoundEvents.BOOK_PAGE_TURN, 1.0F));
                        return true;
                    }
                }
            }
        }
        return super.mouseClicked(event, isSecondary);
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyEvent event) {
        if (event.key() == 256) { // ESC key
            net.minecraft.client.Minecraft.getInstance().setScreen(this.parentScreen);
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
