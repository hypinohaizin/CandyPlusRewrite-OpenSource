package me.hypinohaizin.candyplusrewrite.gui.clickguis.clickgui.item;

import me.hypinohaizin.candyplusrewrite.utils.ColorUtil;
import me.hypinohaizin.candyplusrewrite.utils.RenderUtil;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.gui.clickguis.clickgui.Component;

public class IntegerSlider extends Component
{
    public Setting<Integer> setting;
    public float ratio;
    public boolean changing;
    
    public IntegerSlider(final Setting<Integer> setting, final float x) {
        this.setting = setting;
        this.x = x;
        width = 100.0f;
        height = 16.0f;
        ratio = getRatio(setting.getValue(), setting.maxValue, setting.minValue);
        changing = false;
    }
    
    @Override
    public float doRender(final int mouseX, final int mouseY, final float x, final float y) {
        if (setting.visible()) {
            RenderUtil.drawRect(this.x = x, this.y = y, 100.0f, 16.0f, color0);
            final float width = this.width * ratio;
            RenderUtil.drawRect(x, y, width, 16.0f, ColorUtil.toRGBA(color));
            final float fonty = y + (height - RenderUtil.getStringHeight(1.0f)) / 2.0f;
            RenderUtil.drawString(setting.name, x + 6.0f, fonty, ColorUtil.toRGBA(250, 250, 250), false, 1.0f);
            RenderUtil.drawString(setting.getValue().toString(), x + width - RenderUtil.getStringWidth(setting.getValue().toString(), 1.0f) - 6.0f, fonty, ColorUtil.toRGBA(250, 250, 250), false, 1.0f);
            return 16.0f;
        }
        return 0.0f;
    }
    
    @Override
    public void onMouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        if (isMouseHovering(mouseX, mouseY) && mouseButton == 0) {
            setValue((float)mouseX);
            changing = true;
        }
    }
    
    @Override
    public void onMouseClickMove(final int mouseX, final int mouseY, final int clickedMouseButton) {
        if (changing && clickedMouseButton == 0) {
            setValue((float)mouseX);
        }
    }
    
    @Override
    public void onMouseReleased(final int mouseX, final int mouseY, final int state) {
        changing = false;
    }
    
    public void setValue(final float mouseX) {
        float v = (mouseX - x) / width;
        if (v > 1.0f) {
            v = 1.0f;
        }
        if (v < 0.0f) {
            v = 0.0f;
        }
        ratio = v;
        final float newValue = (setting.maxValue - setting.minValue) * ratio + setting.minValue;
        setting.setValue(Math.round(newValue));
    }
    
    public float getRatio(final float value, final float maxValue, final float minValue) {
        return (value - minValue) / maxValue;
    }
}
