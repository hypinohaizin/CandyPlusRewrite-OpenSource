package me.hypinohaizin.candyplusrewrite.hud;

import net.minecraft.util.math.MathHelper;
import me.hypinohaizin.candyplusrewrite.utils.RenderUtil;
import me.hypinohaizin.candyplusrewrite.utils.ColorUtil;
import java.awt.Color;
import me.hypinohaizin.candyplusrewrite.module.render.HUDEditor;
import me.hypinohaizin.candyplusrewrite.CandyPlusRewrite;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.module.Module;

public class Hud extends Module
{
    public Setting<Float> x;
    public Setting<Float> y;
    public float width;
    public float height;
    public boolean selecting;
    private float diffX;
    private float diffY;
    public float scaledWidth;
    public float scaledHeight;
    public float scaleFactor;
    
    public Hud(final String name, final float x, final float y) {
        super(name, Categories.HUB, false, false);
        height = 0.0f;
        selecting = false;
        diffY = 0.0f;
        scaleFactor = 0.0f;
        this.x = register(new Setting<>("X", x, 2000.0f, 0.0f));
        this.y = register(new Setting<>("Y", x, 2000.0f, 0.0f));
    }
    
    @Override
    public void onRender2D() {
        if (CandyPlusRewrite.m_module.getModuleWithClass(HUDEditor.class).isEnable) {
            final Color color = selecting ? new Color(20, 20, 20, 110) : new Color(20, 20, 20, 80);
            RenderUtil.drawRect(x.getValue() - 10.0f, y.getValue() - 5.0f, width + 20.0f, height + 10.0f, ColorUtil.toRGBA(color));
        }
        onRender();
    }
    
    public void onRender() {
    }
    
    @Override
    public void onUpdate() {
        updateResolution();
        x.maxValue = scaledWidth;
        y.maxValue = scaledHeight;
    }
    
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        if (mouseButton == 0 && isMouseHovering(mouseX, mouseY)) {
            diffX = x.getValue() - mouseX;
            diffY = y.getValue() - mouseY;
            selecting = true;
        }
    }
    
    public void mouseReleased(final int mouseX, final int mouseY, final int state) {
        selecting = false;
    }
    
    public void mouseClickMove(final int mouseX, final int mouseY, final int clickedMouseButton, final long timeSinceLastClick) {
        if (selecting && clickedMouseButton == 0) {
            x.setValue(mouseX + diffX);
            y.setValue(mouseY + diffY);
        }
    }
    
    public Boolean isMouseHovering(final int mouseX, final int mouseY) {
        return x.getValue() - 10.0f < mouseX && x.getValue() + width + 10.0f > mouseX && y.getValue() - 10.0f < mouseY && y.getValue() + height + 10.0f > mouseY;
    }
    
    public void setWidth(final float width) {
        this.width = width;
    }
    
    public void setHeight(final float height) {
        this.height = height;
    }
    
    public void updateResolution() {
        scaledWidth = (float)Hud.mc.displayWidth;
        scaledHeight = (float)Hud.mc.displayHeight;
        scaleFactor = 1.0f;
        final boolean flag = Hud.mc.isUnicode();
        int i = Hud.mc.gameSettings.guiScale;
        if (i == 0) {
            i = 1000;
        }
        while (scaleFactor < i && scaledWidth / (scaleFactor + 1.0f) >= 320.0f && scaledHeight / (scaleFactor + 1.0f) >= 240.0f) {
            ++scaleFactor;
        }
        if (flag && scaleFactor % 2.0f != 0.0f && scaleFactor != 1.0f) {
            --scaleFactor;
        }
        final double scaledWidthD = scaledWidth / (double)scaleFactor;
        final double scaledHeightD = scaledHeight / (double)scaleFactor;
        scaledWidth = (float)MathHelper.ceil(scaledWidthD);
        scaledHeight = (float)MathHelper.ceil(scaledHeightD);
    }
}
