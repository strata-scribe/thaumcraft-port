package thaumcraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import thaumcraft.Thaumcraft;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.api.research.ResearchStage;
import thaumcraft.client.ClientHooks;

import java.util.ArrayList;
import java.util.List;

public class GuiResearchPage extends Screen {
    private static final Identifier TEX_BOOK = Identifier.parse(Thaumcraft.MODID + ":textures/gui/gui_researchbook.png");
    private static final int PAGE_WIDTH = 130;

    private final Screen parentScreen;
    private final ResearchEntry entry;
    private int currentStageIdx = 0;
    private int currentSpread = 0;

    public GuiResearchPage(Screen parentScreen, ResearchEntry entry) {
        super(Component.translatable(entry.getName()));
        this.parentScreen = parentScreen;
        this.entry = entry;
    }

    private List<List<FormattedCharSequence>> computePagesForStage(ResearchStage stage) {
        List<List<FormattedCharSequence>> pages = new ArrayList<>();
        if (stage == null) return pages;

        String raw = stage.getTextLocalized();
        if (raw == null) raw = "";

        // Format and clean tags
        raw = raw.replace("<BR>", "\n\n")
                 .replace("<BR/>", "\n\n")
                 .replace("<br>", "\n\n")
                 .replace("<br/>", "\n\n")
                 .replace("<LINE>", "\n")
                 .replace("<LINE/>", "\n")
                 .replace("<DIV>", "\n")
                 .replace("<DIV/>", "\n")
                 .replace("<PAGE>", "\n\n")
                 .replace("<PAGE/>", "\n\n");
        // Remove image tags
        raw = raw.replaceAll("<IMG>.*?</IMG>", "");

        List<FormattedCharSequence> allLines = new ArrayList<>();
        String[] paragraphs = raw.split("\n");
        for (String para : paragraphs) {
            if (para.trim().isEmpty()) {
                allLines.add(FormattedCharSequence.EMPTY);
            } else {
                allLines.addAll(this.font.split(Component.literal(para.trim()), PAGE_WIDTH));
            }
        }

        // Add requirements summary to lines
        boolean hasReqs = false;
        List<FormattedCharSequence> reqLines = new ArrayList<>();
        if (stage.getCraft() != null && stage.getCraft().length > 0) {
            hasReqs = true;
            reqLines.addAll(this.font.split(Component.literal("§6• Craft required item"), PAGE_WIDTH));
        }
        if (stage.getObtain() != null && stage.getObtain().length > 0) {
            hasReqs = true;
            reqLines.addAll(this.font.split(Component.literal("§6• Obtain required item"), PAGE_WIDTH));
        }
        if (stage.getKnow() != null && stage.getKnow().length > 0) {
            hasReqs = true;
            for (ResearchStage.Knowledge k : stage.getKnow()) {
                String catName = k.category != null ? k.category.key : "";
                reqLines.addAll(this.font.split(Component.literal("§5• " + k.type.name() + " (" + catName + "): " + k.amount), PAGE_WIDTH));
            }
        }
        if (stage.getResearch() != null && stage.getResearch().length > 0) {
            hasReqs = true;
            for (String rKey : stage.getResearch()) {
                reqLines.addAll(this.font.split(Component.literal("§1• Research: " + rKey), PAGE_WIDTH));
            }
        }

        if (hasReqs) {
            allLines.add(FormattedCharSequence.EMPTY);
            allLines.addAll(this.font.split(Component.literal("§8§lRequirements:"), PAGE_WIDTH));
            allLines.addAll(reqLines);
        }

        // Paginate lines: Page 0 has title, so fits 14 lines; subsequent pages fit 17 lines
        int lineIdx = 0;
        int pageIndex = 0;
        while (lineIdx < allLines.size() || pageIndex == 0) {
            int maxLines = (pageIndex == 0) ? 14 : 17;
            List<FormattedCharSequence> curPage = new ArrayList<>();
            for (int i = 0; i < maxLines && lineIdx < allLines.size(); i++) {
                curPage.add(allLines.get(lineIdx++));
            }
            pages.add(curPage);
            pageIndex++;
        }

        // Ensure even number of pages so spreads have left and right
        if (pages.size() % 2 != 0) {
            pages.add(new ArrayList<>());
        }

        return pages;
    }

    @Override
    public void extractRenderState(net.minecraft.client.gui.GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTicks) {
        super.extractRenderState(extractor, mouseX, mouseY, partialTicks);

        // Book dimensions scaled: 256 * 1.3 = 332, 181 * 1.3 = 235
        int w = 332;
        int h = 235;
        int x = (this.width - w) / 2;
        int y = (this.height - h) / 2;

        // Draw full book background
        extractor.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEX_BOOK, x, y, 0f, 0f, w, h, 512, 362, 512, 512);

        ResearchStage[] stages = entry.getStages();
        if (stages == null || stages.length == 0) {
            extractor.centeredText(this.font, Component.translatable(entry.getName()), x + 24 + PAGE_WIDTH / 2, y + 20, 0xFF442200);
            return;
        }

        currentStageIdx = Math.max(0, Math.min(stages.length - 1, currentStageIdx));
        ResearchStage stage = stages[currentStageIdx];
        List<List<FormattedCharSequence>> pages = computePagesForStage(stage);
        int totalSpreads = Math.max(1, pages.size() / 2);
        currentSpread = Math.max(0, Math.min(totalSpreads - 1, currentSpread));

        int leftPageIdx = currentSpread * 2;
        int rightPageIdx = currentSpread * 2 + 1;

        List<FormattedCharSequence> leftLines = leftPageIdx < pages.size() ? pages.get(leftPageIdx) : List.of();
        List<FormattedCharSequence> rightLines = rightPageIdx < pages.size() ? pages.get(rightPageIdx) : List.of();

        // Render Left Page
        int leftX = x + 24;
        int leftStartY = y + 20;
        if (currentSpread == 0) {
            // Title on first spread
            extractor.centeredText(this.font, Component.translatable(entry.getName()), leftX + PAGE_WIDTH / 2, y + 20, 0xFF442200);
            extractor.fill(leftX + 10, y + 31, leftX + PAGE_WIDTH - 10, y + 32, 0x44442200);
            leftStartY = y + 36;
        }

        for (int i = 0; i < leftLines.size(); i++) {
            extractor.text(this.font, leftLines.get(i), leftX, leftStartY + i * 9, 0xFF221100, false);
        }

        // Render Right Page
        int rightX = x + 176;
        int rightStartY = y + 20;
        for (int i = 0; i < rightLines.size(); i++) {
            extractor.text(this.font, rightLines.get(i), rightX, rightStartY + i * 9, 0xFF221100, false);
        }

        // Page numbering and stage footer
        int pageNumLeft = currentSpread * 2 + 1;
        int pageNumRight = currentSpread * 2 + 2;
        extractor.centeredText(this.font, Component.literal(String.valueOf(pageNumLeft)), leftX + PAGE_WIDTH / 2, y + 198, 0xFF886644);
        extractor.centeredText(this.font, Component.literal(String.valueOf(pageNumRight)), rightX + PAGE_WIDTH / 2, y + 198, 0xFF886644);

        if (stages.length > 1) {
            String stageText = (currentStageIdx + 1) + " / " + stages.length;
            extractor.text(this.font, Component.literal(stageText), rightX + PAGE_WIDTH - this.font.width(stageText), y + 198, 0xFF886644, false);
        }

        // Page flip arrows
        boolean hasPrev = currentSpread > 0 || currentStageIdx > 0;
        boolean hasNext = currentSpread < totalSpreads - 1 || currentStageIdx < stages.length - 1;

        if (hasPrev) {
            extractor.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEX_BOOK, x + 18, y + 202, 0f, 368f, 18, 12, 24, 16, 512, 512);
        }
        if (hasNext) {
            extractor.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEX_BOOK, x + 296, y + 202, 24f, 368f, 18, 12, 24, 16, 512, 512);
        }
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean isSecondary) {
        double mouseX = event.x();
        double mouseY = event.y();
        int w = 332;
        int h = 235;
        int x = (this.width - w) / 2;
        int y = (this.height - h) / 2;

        if (event.button() == 0) {
            // Check outside book bounds -> close back to parent screen
            if (mouseX < x || mouseX > x + w || mouseY < y || mouseY > y + h) {
                ClientHooks.setScreen(this.parentScreen);
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
                return true;
            }

            ResearchStage[] stages = entry.getStages();
            if (stages != null && stages.length > 0) {
                ResearchStage stage = stages[currentStageIdx];
                List<List<FormattedCharSequence>> pages = computePagesForStage(stage);
                int totalSpreads = Math.max(1, pages.size() / 2);

                // Left Arrow click (Prev page/stage)
                if (mouseX >= x + 10 && mouseX <= x + 45 && mouseY >= y + 195 && mouseY <= y + 220) {
                    if (currentSpread > 0) {
                        currentSpread--;
                        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
                        return true;
                    } else if (currentStageIdx > 0) {
                        currentStageIdx--;
                        List<List<FormattedCharSequence>> prevPages = computePagesForStage(stages[currentStageIdx]);
                        currentSpread = Math.max(0, (prevPages.size() / 2) - 1);
                        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
                        return true;
                    }
                }

                // Right Arrow click (Next page/stage)
                if (mouseX >= x + 285 && mouseX <= x + 325 && mouseY >= y + 195 && mouseY <= y + 220) {
                    if (currentSpread < totalSpreads - 1) {
                        currentSpread++;
                        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
                        return true;
                    } else if (currentStageIdx < stages.length - 1) {
                        currentStageIdx++;
                        currentSpread = 0;
                        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
                        return true;
                    }
                }
            }
        }
        return super.mouseClicked(event, isSecondary);
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyEvent event) {
        if (event.key() == 256) { // ESC
            ClientHooks.setScreen(this.parentScreen);
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
