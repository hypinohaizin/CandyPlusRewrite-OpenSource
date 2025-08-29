package me.hypinohaizin.candyplusrewrite.gui.clickguis.clickgui.item;

import me.hypinohaizin.candyplusrewrite.utils.ColorUtil;
import me.hypinohaizin.candyplusrewrite.utils.RenderUtil;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.gui.clickguis.clickgui.Component;

public class FloatSlider extends Component {
    public Setting<? extends Number> setting;
    public float ratio;
    public boolean changing;

    public FloatSlider(final Setting<? extends Number> setting, final float x) {
        this.setting = setting;
        this.x = x;
        this.width = 100.0f;
        this.height = 16.0f;
        Number value = setting.getValue();
        Number max   = setting.maxValue;
        Number min   = setting.minValue;
        ratio = getRatio(value.floatValue(), max.floatValue(), min.floatValue());
        changing = false;
    }

    @Override
    public float doRender(final int mouseX, final int mouseY, final float x, final float y) {
        if (!setting.visible()) return 0.0f;
        this.x = x;
        this.y = y;
        RenderUtil.drawRect(this.x, this.y, width, height, color0);
        float fill = width * ratio;
        RenderUtil.drawRect(this.x, this.y, fill, height, ColorUtil.toRGBA(color));
        float fontY = y + (height - RenderUtil.getStringHeight(1.0f)) / 2.0f;
        RenderUtil.drawString(setting.name, this.x + 6.0f, fontY, ColorUtil.toRGBA(250, 250, 250), false, 1.0f);
        String valStr = setting.getValue().toString();
        RenderUtil.drawString(
                valStr,
                this.x + fill - RenderUtil.getStringWidth(valStr, 1.0f) - 6.0f,
                fontY,
                ColorUtil.toRGBA(250, 250, 250),
                false,
                1.0f
        );
        return height;
    }

    @Override
    public void onMouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        if (isMouseHovering(mouseX, mouseY) && mouseButton == 0) {
            setValue(mouseX);
            changing = true;
        }
    }

    @Override
    public void onMouseClickMove(final int mouseX, final int mouseY, final int button) {
        if (changing && button == 0) {
            setValue(mouseX);
        }
    }

    @Override
    public void onMouseReleased(final int mouseX, final int mouseY, final int state) {
        changing = false;
    }

    private void setValue(final float mouseX) {
        float v = (mouseX - x) / width;
        v = Math.max(0.0f, Math.min(1.0f, v));
        ratio = v;
        Number max = setting.maxValue;
        Number min = setting.minValue;
        float newVal = (max.floatValue() - min.floatValue()) * ratio + min.floatValue();
        float rounded = Math.round(newVal * 10.0f) / 10.0f;
        if (setting.getValue() instanceof Double) {
            setting.setValue((Double) (double) rounded);
        } else {
            setting.setValue(rounded);
        }
    }

    private float getRatio(final float value, final float maxValue, final float minValue) {
        return (value - minValue) / (maxValue - minValue);
    }
}
