package me.hypinohaizin.candyplusrewrite.gui.clickguis.clickgui.item;

import me.hypinohaizin.candyplusrewrite.utils.RenderUtil;
import me.hypinohaizin.candyplusrewrite.utils.ColorUtil;
import java.awt.Color;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.gui.clickguis.clickgui.Component;

public class ColorSlider extends Component
{
    private final Setting<Color> setting;
    private boolean isOpening;
    private boolean redChanging;
    private boolean greenChanging;
    private boolean blueChanging;
    private boolean alphaChanging;
    private float rLx;
    private float gLx;
    private float bLx;
    private float aLx;
    
    public ColorSlider(final Setting<Color> setting, final float x) {
        isOpening = false;
        alphaChanging = false;
        aLx = 0.0f;
        this.setting = setting;
        this.x = x;
        width = 100.0f;
        height = 16.0f;
        rLx = setting.getValue().getRed() / 255.0f;
        gLx = setting.getValue().getGreen() / 255.0f;
        bLx = setting.getValue().getBlue() / 255.0f;
        aLx = setting.getValue().getAlpha() / 255.0f;
    }
    
    @Override
    public float doRender(final int mouseX, final int mouseY, final float x, final float y) {
        if (!setting.visible()) {
            return 0.0f;
        }
        RenderUtil.drawRect(x, this.y = y, width, height, ColorUtil.toRGBA(color));
        final float centeredY = y + (height - RenderUtil.getStringHeight(1.0f)) / 2.0f;
        final float rcy1 = y + (height - 11.0f) / 2.0f;
        final float rcy2 = y + (height - 9.0f) / 2.0f;
        RenderUtil.drawRect(x + width - 21.0f, rcy1, 12.0f, 12.0f, color0);
        RenderUtil.drawRect(x + width - 20.0f, rcy2, 10.0f, 10.0f, ColorUtil.toRGBA(new Color(setting.getValue().getRed(), setting.getValue().getGreen(), setting.getValue().getBlue(), setting.getValue().getAlpha())));
        RenderUtil.drawString(setting.name, x + 5.0f, centeredY, ColorUtil.toRGBA(250, 250, 250), false, 1.0f);
        if (isOpening) {
            final float colorFieldHeight = 93.0f;
            final float colorFieldY = y + height;
            final float colorFieldHeightY = colorFieldY + colorFieldHeight;
            RenderUtil.drawRect(x, colorFieldY, width, colorFieldHeight, color0);
            final int gray = ColorUtil.toRGBA(50, 50, 50);
            RenderUtil.drawString("Red", x + 5.0f, colorFieldY + 5.0f, ColorUtil.toRGBA(250, 250, 250), false, 1.0f);
            RenderUtil.drawRect(x + 10.0f, colorFieldY + 15.0f, width - 20.0f, 5.0f, gray);
            RenderUtil.drawRect(x + 10.0f + (width - 20.0f) * rLx, colorFieldY + 13.0f, 3.0f, 9.0f, ColorUtil.toRGBA(255, 255, 255));
            RenderUtil.drawString("Green", x + 5.0f, colorFieldY + 27.0f, ColorUtil.toRGBA(250, 250, 250), false, 1.0f);
            RenderUtil.drawRect(x + 10.0f, colorFieldY + 37.0f, width - 20.0f, 5.0f, gray);
            RenderUtil.drawRect(x + 10.0f + (width - 20.0f) * gLx, colorFieldY + 35.0f, 3.0f, 9.0f, ColorUtil.toRGBA(255, 255, 255));
            RenderUtil.drawString("Blue", x + 5.0f, colorFieldY + 49.0f, ColorUtil.toRGBA(250, 250, 250), false, 1.0f);
            RenderUtil.drawRect(x + 10.0f, colorFieldY + 59.0f, width - 20.0f, 5.0f, gray);
            RenderUtil.drawRect(x + 10.0f + (width - 20.0f) * bLx, colorFieldY + 57.0f, 3.0f, 9.0f, ColorUtil.toRGBA(255, 255, 255));
            RenderUtil.drawString("Alpha", x + 5.0f, colorFieldY + 71.0f, ColorUtil.toRGBA(250, 250, 250), false, 1.0f);
            RenderUtil.drawRect(x + 10.0f, colorFieldY + 81.0f, width - 20.0f, 5.0f, gray);
            RenderUtil.drawRect(x + 10.0f + (width - 20.0f) * aLx, colorFieldY + 79.0f, 3.0f, 9.0f, ColorUtil.toRGBA(255, 255, 255));
            return colorFieldHeight + height;
        }
        return height;
    }
    
    @Override
    public void onMouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        final float colorFieldHeight = 70.0f;
        final float colorFieldY = y + height;
        final float colorFieldHeightY = colorFieldY + colorFieldHeight;
        if (isMouseHovering(mouseX, mouseY) && mouseButton == 1) {
            isOpening = !isOpening;
        }
        if (!isOpening) {
            return;
        }
        if (isMouseHovering((float)mouseX, (float)mouseY, x + 8.0f, (float)(int)(colorFieldY + 7.0f), width - 12.0f, 15.0f)) {
            redChanging = true;
        }
        if (isMouseHovering((float)mouseX, (float)mouseY, x + 8.0f, (float)(int)(colorFieldY + 29.0f), width - 12.0f, 15.0f)) {
            greenChanging = true;
        }
        if (isMouseHovering((float)mouseX, (float)mouseY, x + 8.0f, (float)(int)(colorFieldY + 51.0f), width - 12.0f, 15.0f)) {
            blueChanging = true;
        }
        if (isMouseHovering((float)mouseX, (float)mouseY, x + 8.0f, (float)(int)(colorFieldY + 73.0f), width - 12.0f, 15.0f)) {
            alphaChanging = true;
        }
    }
    
    @Override
    public void onMouseReleased(final int mouseX, final int mouseY, final int state) {
        redChanging = false;
        greenChanging = false;
        blueChanging = false;
        alphaChanging = false;
    }
    
    @Override
    public void onMouseClickMove(final int mouseX, final int mouseY, final int clickedMouseButton) {
        if (!isOpening) {
            return;
        }
        final float colorFieldHeight = 70.0f;
        final float colorFieldY = y + height;
        final float colorFieldHeightY = colorFieldY + colorFieldHeight;
        if (x + 8.0f < mouseX && x + width - 12.0f > mouseX && clickedMouseButton == 0 && redChanging) {
            rLx = (mouseX - (x + 10.0f)) / (width - 20.0f);
            if (rLx > 1.0f) {
                rLx = 1.0f;
            }
            if (rLx < 0.0f) {
                rLx = 0.0f;
            }
            setting.setValue(new Color((int)(Math.round(255.0f * rLx * 10.0f) / 10.0f), setting.getValue().getGreen(), setting.getValue().getBlue(), setting.getValue().getAlpha()));
        }
        if (x + 8.0f < mouseX && x + width - 12.0f > mouseX && clickedMouseButton == 0 && greenChanging) {
            gLx = (mouseX - (x + 10.0f)) / (width - 20.0f);
            if (gLx > 1.0f) {
                gLx = 1.0f;
            }
            if (gLx < 0.0f) {
                gLx = 0.0f;
            }
            setting.setValue(new Color(setting.getValue().getRed(), (int)(Math.round(255.0f * gLx * 10.0f) / 10.0f), setting.getValue().getBlue(), setting.getValue().getAlpha()));
        }
        if (x + 8.0f < mouseX && x + width - 12.0f > mouseX && clickedMouseButton == 0 && blueChanging) {
            bLx = (mouseX - (x + 10.0f)) / (width - 20.0f);
            if (bLx > 1.0f) {
                bLx = 1.0f;
            }
            if (bLx < 0.0f) {
                bLx = 0.0f;
            }
            setting.setValue(new Color(setting.getValue().getRed(), setting.getValue().getGreen(), (int)(Math.round(255.0f * bLx * 10.0f) / 10.0f), setting.getValue().getAlpha()));
        }
        if (x + 8.0f < mouseX && x + width - 12.0f > mouseX && clickedMouseButton == 0 && alphaChanging) {
            aLx = (mouseX - (x + 10.0f)) / (width - 20.0f);
            if (aLx > 1.0f) {
                aLx = 1.0f;
            }
            if (aLx < 0.0f) {
                aLx = 0.0f;
            }
            setting.setValue(new Color(setting.getValue().getRed(), setting.getValue().getGreen(), setting.getValue().getBlue(), (int)(Math.round(255.0f * aLx * 10.0f) / 10.0f)));
        }
    }
}
