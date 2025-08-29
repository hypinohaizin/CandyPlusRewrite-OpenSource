package me.hypinohaizin.candyplusrewrite.gui.clickguis.clickgui.item;

import me.hypinohaizin.candyplusrewrite.utils.ColorUtil;
import me.hypinohaizin.candyplusrewrite.utils.RenderUtil;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.gui.clickguis.clickgui.Component;

public class BooleanButton extends Component
{
    public Setting<Boolean> setting;
    
    public BooleanButton(final Setting<Boolean> setting, final float x) {
        this.setting = setting;
        this.x = x;
        width = 100.0f;
        height = 16.0f;
    }
    
    @Override
    public float doRender(final int mouseX, final int mouseY, final float x, final float y) {
        if (setting.visible()) {
            RenderUtil.drawRect(this.x = x, this.y = y, 100.0f, 16.0f, color0);
            if (setting.getValue()) {
                RenderUtil.drawRect(x, y, 100.0f, 16.0f, ColorUtil.toRGBA(color));
            }
            if (isMouseHovering(mouseX, mouseY)) {
                RenderUtil.drawRect(x, y, 100.0f, 16.0f, hovering);
            }
            final String name = setting.name;
            final float namey = getCenter(y, 16.0f, RenderUtil.getStringHeight(1.0f));
            RenderUtil.drawString(name, x + 6.0f, namey, ColorUtil.toRGBA(250, 250, 250), false, 1.0f);
            return 16.0f;
        }
        return 0.0f;
    }
    
    @Override
    public void onMouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        if (mouseButton == 0 && isMouseHovering(mouseX, mouseY)) {
            setting.setValue(!setting.getValue());
        }
    }
}
