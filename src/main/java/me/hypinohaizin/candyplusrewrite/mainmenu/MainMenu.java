package me.hypinohaizin.candyplusrewrite.mainmenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.awt.Desktop;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.io.IOException;

public class MainMenu extends GuiScreen {

    private final ResourceLocation background1 = new ResourceLocation("candyplusrewrite", "textures/mainmenu1.png");
    private final ResourceLocation background2 = new ResourceLocation("candyplusrewrite", "textures/mainmenu2.png");
    private final ResourceLocation background3 = new ResourceLocation("candyplusrewrite", "textures/mainmenu3.png");

    private float animatedX, animatedY;
    private List<CustomButton> buttons;
    private int tick, bg;

    private int textX, textY, textWidth;

    public MainMenu() {
    }

    @Override
    public void initGui() {
        tick = 0;
        buttons = new LinkedList<>();
        buttons.add(new CustomButton("SinglePlayer", new ResourceLocation("candyplusrewrite", "textures/icon/singleplayer.png"), new GuiWorldSelection(this)));
        buttons.add(new CustomButton("MultiPlayer", new ResourceLocation("candyplusrewrite", "textures/icon/multiplayer.png"), new GuiMultiplayer(this)));
        buttons.add(new CustomButton("Language", new ResourceLocation("candyplusrewrite", "textures/icon/language.png"), new GuiLanguage(this, mc.gameSettings, mc.getLanguageManager())));
        buttons.add(new CustomButton("Settings", new ResourceLocation("candyplusrewrite", "textures/icon/setting.png"), new GuiOptions(this, mc.gameSettings)));
        buttons.add(new CustomButton("Quit", new ResourceLocation("candyplusrewrite", "textures/icon/quit.png"), null));
        super.initGui();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        switch (Character.toLowerCase(typedChar)) {
            case 's':
                Minecraft.getMinecraft().displayGuiScreen(new GuiWorldSelection(this));
                break;
            case 'm':
                Minecraft.getMinecraft().displayGuiScreen(new GuiMultiplayer(this));
                break;
            case 'l':
                Minecraft.getMinecraft().displayGuiScreen(new GuiLanguage(this, mc.gameSettings, mc.getLanguageManager()));
                break;
            case 'o':
                Minecraft.getMinecraft().displayGuiScreen(new GuiOptions(this, mc.gameSettings));
                break;
            case 'q':
                Minecraft.getMinecraft().shutdown();
                break;
            default:
                break;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.color(1f, 1f, 1f, 1f);
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.disableLighting();

        tick++;
        if (tick % (20 * 30) == 0) {
            bg = (bg + 1) % 3;
        }

        ResourceLocation[] backgrounds = {background1, background2, background3};
        ScaledResolution sr = new ScaledResolution(mc);
        mc.getTextureManager().bindTexture(backgrounds[bg]);

        float bw = sr.getScaledWidth() * 4f / 3f;
        float bh = sr.getScaledHeight() * 4f / 3f;
        drawModalRectWithCustomSizedTexture(
                Math.round(-animatedX / 4f),
                Math.round(-animatedY / 3f),
                0, 0,
                Math.round(bw),
                Math.round(bh),
                bw,
                bh
        );

        int xOffset = sr.getScaledWidth() / 2 - 180;
        int baseY = sr.getScaledHeight() / 2 - 20;
        for (CustomButton cb : buttons) {
            cb.drawScreen(xOffset, baseY, mouseX, mouseY);
            xOffset += 80;
        }

        String credit = "By CandyPlus Rewrite DEV Team";
        textWidth = mc.fontRenderer.getStringWidth(credit);
        textX = sr.getScaledWidth() - textWidth - 4;
        textY = sr.getScaledHeight() - mc.fontRenderer.FONT_HEIGHT - 6;
        mc.fontRenderer.drawString(credit, textX, textY, 0xd0ffffff);

        super.drawScreen(mouseX, mouseY, partialTicks);

        animatedX += (mouseX - animatedX) * 0.12f;
        animatedY += (mouseY - animatedY) * 0.12f;

        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();

        GlStateManager.popMatrix();

        GlStateManager.color(1f, 1f, 1f, 1f);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        for (CustomButton cb : buttons) {
            cb.onClicked(mouseX, mouseY, mouseButton);
        }
        if (mouseX >= textX && mouseX <= textX + textWidth && mouseY >= textY && mouseY <= textY + mc.fontRenderer.FONT_HEIGHT) {
            openLink("https://discord.gg/ktQRKNZzbH");
        }
    }

    private void openLink(String url) {
        try {
            Desktop.getDesktop().browse(URI.create(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class CustomButton {

        private final ResourceLocation resource;
        private final GuiScreen parent;
        private float animatedSize = 25f;
        private int anchorX, anchorY;
        private int renderX, renderY, renderSize;
        private final String name;

        public CustomButton(String name, ResourceLocation resource, GuiScreen parent) {
            this.resource = resource;
            this.parent = parent;
            this.name = name;
        }

        public void drawScreen(int posX, int posY, int mouseX, int mouseY) {
            anchorX = posX;
            anchorY = posY;
            boolean hover = isHovering(mouseX, mouseY);
            animatedSize = animate(animatedSize, hover ? 30f : 25f);

            int size = Math.round(animatedSize * 1.5f);
            int cx = posX + 24;
            int cy = posY + 24;
            int rx = cx - size / 2;
            int ry = cy - size / 2;

            renderX = rx;
            renderY = ry;
            renderSize = size;

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            Minecraft.getMinecraft().getTextureManager().bindTexture(resource);
            Gui.drawModalRectWithCustomSizedTexture(
                    rx,
                    ry,
                    0, 0,
                    size,
                    size,
                    size,
                    size
            );

            if (hover) {
                int nameWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(name);
                int labelY = ry + size + 6;
                Minecraft.getMinecraft().fontRenderer.drawString(name, cx - nameWidth / 2, labelY, 0xFFFFFFFF);
            }
        }

        public void onClicked(int mouseX, int mouseY, int mouseButton) {
            if (mouseButton == 0 && isHovering(mouseX, mouseY)) {
                if (parent == null) Minecraft.getMinecraft().shutdown();
                else Minecraft.getMinecraft().displayGuiScreen(parent);
            }
        }

        private boolean isHovering(int mouseX, int mouseY) {
            return mouseX >= renderX && mouseX <= renderX + renderSize && mouseY >= renderY && mouseY <= renderY + renderSize;
        }

        private static float animate(float current, float target) {
            float speed = 0.18f;
            return current + (target - current) * speed;
        }
    }
}