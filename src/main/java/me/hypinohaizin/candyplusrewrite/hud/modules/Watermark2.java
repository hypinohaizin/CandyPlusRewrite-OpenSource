package me.hypinohaizin.candyplusrewrite.hud.modules;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.IOException;
import java.awt.Color;
import javax.imageio.ImageIO;

import me.hypinohaizin.candyplusrewrite.utils.RenderUtil;
import me.hypinohaizin.candyplusrewrite.utils.CandyDynamicTexture;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.hud.Hud;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;

public class Watermark2 extends Hud {
    public Setting<Float> scale;
    public Setting<Boolean> rainbow;
    public Setting<Integer> saturation;
    public Setting<Integer> brightness;
    public Setting<Integer> speed;
    public CandyDynamicTexture watermark;

    public Watermark2() {
        super("Watermark2", 10.0f, 10.0f);
        scale = register(new Setting<>("Scale", 0.4f, 1.0f, 0.1f));
        rainbow = register(new Setting<>("Rainbow", false));
        saturation = register(new Setting<>("Saturation", 50, 100, 0, v -> rainbow.getValue()));
        brightness = register(new Setting<>("Brightness", 100, 100, 0, v -> rainbow.getValue()));
        speed = register(new Setting<>("Speed", 40, 100, 1, v -> rainbow.getValue()));
        loadLogo();
    }

    @Override
    public void onRender() {
        if (watermark == null) {
            loadLogo();
            return;
        }

        float width = watermark.GetWidth() * scale.getValue();
        float height = watermark.GetHeight() * scale.getValue();
        GlStateManager.enableTexture2D();
        GlStateManager.disableAlpha();
        RenderHelper.enableGUIStandardItemLighting();
        mc.renderEngine.bindTexture(watermark.GetResourceLocation());
        GlStateManager.glTexParameteri(3553, 10240, 9729);
        GlStateManager.glTexParameteri(3553, 10241, 9729);

        if (rainbow.getValue()) {
            Color color = new Color(RenderUtil.getRainbow(speed.getValue() * 100, 0, saturation.getValue() / 100.0f, brightness.getValue() / 100.0f));
            GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, 1.0f);
        } else {
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        }

        GlStateManager.pushMatrix();
        RenderUtil.drawTexture(x.getValue(), y.getValue(), width, height);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
        this.width = width;
        this.height = height;
    }

    public void loadLogo() {
        try (InputStream stream = Watermark2.class.getResourceAsStream("/assets/candyplusrewrite/textures/watermark2.png")) {
            BufferedImage image = ImageIO.read(stream);
            int height = image.getHeight();
            int width = image.getWidth();
            watermark = new CandyDynamicTexture(image, height, width);
            watermark.SetResourceLocation(Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("candyplusrewrite/textures", watermark));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
