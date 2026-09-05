package thaumcraft.client.gui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import thaumcraft.Thaumcraft;

public class GuiThaumonomicon extends Screen {
    private static final Identifier TEX_OVERLAY = Identifier.parse(Thaumcraft.MODID + ":textures/gui/gui_research_browser.png");

    private double mapX = 0;
    private double mapY = 0;
    private double zoom = 1.0;
    private String selectedCategory = "BASICS";

    public GuiThaumonomicon() {
        super(net.minecraft.network.chat.Component.translatable("item.thaumcraft.thaumonomicon"));
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public boolean mouseDragged(net.minecraft.client.input.MouseButtonEvent event, double dragX, double dragY) {
        if (event.button() == 0) {
            this.mapX -= dragX / this.zoom;
            this.mapY -= dragY / this.zoom;
            return true;
        }
        return super.mouseDragged(event, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        this.zoom += scrollY * 0.15;
        this.zoom = Math.max(0.5, Math.min(2.0, this.zoom));
        return true;
    }

    @Override
    public void extractRenderState(net.minecraft.client.gui.GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTicks) {
        super.extractRenderState(extractor, mouseX, mouseY, partialTicks);

        // Calculate map coordinates from screen coordinates
        double mx = (mouseX - this.width / 2.0) / this.zoom + this.mapX;
        double my = (mouseY - this.height / 2.0) / this.zoom + this.mapY;

        thaumcraft.api.research.ResearchEntry hoveredEntry = null;

        // Draw tiling background and nodes in translated map space
        extractor.pose().pushMatrix();
        extractor.pose().translate(this.width / 2.0f, this.height / 2.0f);
        extractor.pose().scale((float)this.zoom, (float)this.zoom);
        extractor.pose().translate((float)-this.mapX, (float)-this.mapY);

        if (this.selectedCategory != null) {
            thaumcraft.api.research.ResearchCategory cat = thaumcraft.api.research.ResearchCategories.getResearchCategory(this.selectedCategory);
            if (cat != null) {
                Identifier bg = cat.background;
                // draw tiling map
                for (int x = -10; x <= 10; x++) {
                    for (int y = -10; y <= 10; y++) {
                        extractor.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, bg, x * 256, y * 256, 0f, 0f, 256, 256, 256, 256);
                    }
                }

                // Draw connectivity lines
                net.minecraft.world.entity.player.Player localPlayer = net.minecraft.client.Minecraft.getInstance().player;
                for (thaumcraft.api.research.ResearchEntry entry : cat.research.values()) {
                    int nodeX = entry.getDisplayColumn() * 24;
                    int nodeY = entry.getDisplayRow() * 24;
                    
                    String[] parentsClean = entry.getParentsClean();
                    String[] parentsRaw = entry.getParents();
                    if (parentsClean != null) {
                        for (int i = 0; i < parentsClean.length; i++) {
                            String parentKey = parentsClean[i];
                            String parentRaw = parentsRaw[i];
                            
                            thaumcraft.api.research.ResearchEntry parentEntry = cat.research.get(parentKey);
                            if (parentEntry != null) {
                                int parentX = parentEntry.getDisplayColumn() * 24;
                                int parentY = parentEntry.getDisplayRow() * 24;
                                
                                boolean parentUnlocked = localPlayer != null && thaumcraft.api.capabilities.ThaumcraftCapabilities.knowsResearch(localPlayer, parentKey);
                                boolean childUnlocked = localPlayer != null && thaumcraft.api.capabilities.ThaumcraftCapabilities.knowsResearch(localPlayer, entry.getKey());
                                
                                int color = 0xFF222222; // default very dark/locked
                                if (parentUnlocked) {
                                    if (childUnlocked) {
                                        color = 0xFF55DDFF; // bright aqua/blue
                                    } else {
                                        color = 0xFF225588; // dark blue
                                    }
                                }
                                
                                boolean isSoft = parentRaw.startsWith("~");
                                if (isSoft) {
                                    color = (color & 0x00FFFFFF) | 0x88000000; // make transparent
                                }
                                
                                // Draw horizontal and vertical segments
                                if (parentX == nodeX) {
                                    extractor.fill(parentX - 1, Math.min(parentY, nodeY), parentX + 1, Math.max(parentY, nodeY), color);
                                } else if (parentY == nodeY) {
                                    extractor.fill(Math.min(parentX, nodeX), parentY - 1, Math.max(parentX, nodeX), parentY + 1, color);
                                } else {
                                    // L-shape line
                                    extractor.fill(Math.min(parentX, nodeX), parentY - 1, Math.max(parentX, nodeX), parentY + 1, color);
                                    extractor.fill(nodeX - 1, Math.min(parentY, nodeY), nodeX + 1, Math.max(parentY, nodeY), color);
                                }
                            }
                        }
                    }
                }

                // Draw nodes
                for (thaumcraft.api.research.ResearchEntry entry : cat.research.values()) {
                    int nodeX = entry.getDisplayColumn() * 24;
                    int nodeY = entry.getDisplayRow() * 24;
                    
                    if (mx >= nodeX - 12 && mx <= nodeX + 12 && my >= nodeY - 12 && my <= nodeY + 12) {
                        hoveredEntry = entry;
                    }

                    // Choose frame texture based on EnumResearchMeta
                    Identifier frameTex = Identifier.parse("thaumcraft:textures/gui/hex1.png"); // default standard hex
                    int frameSize = 32;
                    if (entry.hasMeta(thaumcraft.api.research.ResearchEntry.EnumResearchMeta.ROUND)) {
                        frameTex = Identifier.parse("thaumcraft:textures/gui/hex2.png");
                    } else if (entry.hasMeta(thaumcraft.api.research.ResearchEntry.EnumResearchMeta.SPIKY)) {
                        frameTex = Identifier.parse("thaumcraft:textures/gui/hex3.png");
                        frameSize = 48;
                    } else if (entry.hasMeta(thaumcraft.api.research.ResearchEntry.EnumResearchMeta.REVERSE)) {
                        frameTex = Identifier.parse("thaumcraft:textures/gui/hex4.png");
                        frameSize = 48;
                    }
                    
                    int offset = frameSize / 2;
                    extractor.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, frameTex, nodeX - offset, nodeY - offset, 0f, 0f, frameSize, frameSize, frameSize, frameSize);

                    if (entry.getIcons() != null && entry.getIcons().length > 0) {
                        Object icon = entry.getIcons()[0];
                        if (icon instanceof net.minecraft.world.item.ItemStack) {
                            extractor.item((net.minecraft.world.item.ItemStack)icon, nodeX - 8, nodeY - 8, 0);
                        } else if (icon instanceof Identifier) {
                            Identifier id = (Identifier)icon;
                            if (id.getPath().contains("textures/")) {
                                // Fix common old thaumcraft syntax for texture paths
                                if (id.getPath().startsWith("textures/items/")) {
                                    id = Identifier.fromNamespaceAndPath(id.getNamespace(), id.getPath().replace("textures/items/", "textures/item/"));
                                }
                                if (id.getPath().startsWith("textures/blocks/")) {
                                    id = Identifier.fromNamespaceAndPath(id.getNamespace(), id.getPath().replace("textures/blocks/", "textures/block/"));
                                }
                                extractor.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, id, nodeX - 8, nodeY - 8, 0f, 0f, 16, 16, 32, 32);
                            } else {
                                net.minecraft.world.item.Item it = net.minecraft.core.registries.BuiltInRegistries.ITEM.getOptional(id).orElse(net.minecraft.world.item.Items.AIR);
                                if (it != net.minecraft.world.item.Items.AIR) {
                                    extractor.item(new net.minecraft.world.item.ItemStack(it), nodeX - 8, nodeY - 8, 0);
                                } else {
                                    id = Identifier.fromNamespaceAndPath(id.getNamespace(), "textures/item/" + id.getPath() + ".png");
                                    extractor.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, id, nodeX - 8, nodeY - 8, 0f, 0f, 16, 16, 16, 16);
                                }
                            }
                        }
                    }
                }
            }
        }

        extractor.pose().popMatrix();

        // Draw solid sidebar background on the left to cover nodes sliding underneath
        extractor.fill(0, 0, 36, this.height, 0xFF0D0D12); // Premium near-black sidebar
        extractor.fill(36, 0, 37, this.height, 0xFF252530); // Clean vertical separator border

        // Draw category tabs on the left (unaffected by map zoom/pan)
        int tabY = 30;
        int tabWidth = 28;
        int tabHeight = 28;
        thaumcraft.api.research.ResearchCategory hoveredCategory = null;
        java.util.Collection<thaumcraft.api.research.ResearchCategory> categories = thaumcraft.api.research.ResearchCategories.researchCategories.values();
        for (thaumcraft.api.research.ResearchCategory cat : categories) {
            boolean isSelected = cat.key.equals(this.selectedCategory);
            
            // Draw hover highlight if mouse is over this tab
            boolean isHovered = mouseX >= 5 && mouseX <= 33 && mouseY >= tabY && mouseY <= tabY + tabHeight;
            if (isHovered) {
                extractor.fill(5, tabY, 33, tabY + tabHeight, 0x1AFFFFFF); // Subtle white overlay
                hoveredCategory = cat;
            }
            
            // Draw elegant selected indicator dot/bar on the left
            if (isSelected) {
                extractor.fill(2, tabY + 4, 4, tabY + tabHeight - 4, 0xFF9061F9); // Royal violet accent bar
            }
            
            int iconX = 11;
            int iconY = tabY + 6;
            
            Identifier iconId = cat.icon;
            if (iconId != null) {
                if (iconId.getPath().contains("textures/")) {
                    Identifier cleanIconId = iconId;
                    if (cleanIconId.getPath().startsWith("textures/items/")) {
                        cleanIconId = Identifier.fromNamespaceAndPath(cleanIconId.getNamespace(), cleanIconId.getPath().replace("textures/items/", "textures/item/"));
                    }
                    if (cleanIconId.getPath().startsWith("textures/blocks/")) {
                        cleanIconId = Identifier.fromNamespaceAndPath(cleanIconId.getNamespace(), cleanIconId.getPath().replace("textures/blocks/", "textures/block/"));
                    }
                    extractor.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, cleanIconId, iconX, iconY, 0f, 0f, 16, 16, 16, 16);
                } else {
                    net.minecraft.world.item.Item it = net.minecraft.core.registries.BuiltInRegistries.ITEM.getOptional(iconId).orElse(net.minecraft.world.item.Items.AIR);
                    if (it != net.minecraft.world.item.Items.AIR) {
                        extractor.item(new net.minecraft.world.item.ItemStack(it), iconX, iconY, 0);
                    } else {
                        Identifier tryId = Identifier.fromNamespaceAndPath(iconId.getNamespace(), "textures/item/" + iconId.getPath() + ".png");
                        extractor.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, tryId, iconX, iconY, 0f, 0f, 16, 16, 16, 16);
                    }
                }
            }
            
            tabY += tabHeight + 4;
        }
        
        if (hoveredCategory != null) {
            String name = hoveredCategory.key;
            if (name.length() > 0) {
                name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
            }
            extractor.setTooltipForNextFrame(net.minecraft.network.chat.Component.literal(name), mouseX, mouseY);
        } else if (hoveredEntry != null) {
            extractor.setTooltipForNextFrame(net.minecraft.network.chat.Component.translatable(hoveredEntry.getName()), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean isSecondary) {
        double mouseX = event.x();
        double mouseY = event.y();
        if (event.button() == 0) {
            // Check Category tabs clicks
            int tabY = 30;
            int tabWidth = 28;
            int tabHeight = 28;
            java.util.Collection<thaumcraft.api.research.ResearchCategory> categories = thaumcraft.api.research.ResearchCategories.researchCategories.values();
            for (thaumcraft.api.research.ResearchCategory cat : categories) {
                if (mouseX >= 5 && mouseX <= 33 && mouseY >= tabY && mouseY <= tabY + tabHeight) {
                    this.selectedCategory = cat.key;
                    net.minecraft.client.Minecraft.getInstance().getSoundManager().play(net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    return true;
                }
                tabY += tabHeight + 4;
            }

            // Check Node clicks
            if (this.selectedCategory != null) {
                thaumcraft.api.research.ResearchCategory cat = thaumcraft.api.research.ResearchCategories.getResearchCategory(this.selectedCategory);
                if (cat != null) {
                    double mx = (mouseX - this.width / 2.0) / this.zoom + this.mapX;
                    double my = (mouseY - this.height / 2.0) / this.zoom + this.mapY;
                    for (thaumcraft.api.research.ResearchEntry entry : cat.research.values()) {
                        int nodeX = entry.getDisplayColumn() * 24;
                        int nodeY = entry.getDisplayRow() * 24;
                        if (mx >= nodeX - 12 && mx <= nodeX + 12 && my >= nodeY - 12 && my <= nodeY + 12) {
                            net.minecraft.client.Minecraft.getInstance().getSoundManager().play(net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(net.minecraft.sounds.SoundEvents.BOOK_PAGE_TURN, 1.0F));
                            thaumcraft.client.ClientHooks.setScreen(new GuiResearchPage(this, entry));
                            return true;
                        }
                    }
                }
            }
        }
        return super.mouseClicked(event, isSecondary);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
