package me.hypinohaizin.candyplusrewrite.gui.clickguis.clickgui.item;

import me.hypinohaizin.candyplusrewrite.utils.ColorUtil;
import me.hypinohaizin.candyplusrewrite.utils.RenderUtil;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.gui.clickguis.clickgui.Component;

public class EnumButton extends Component
{
    public Setting<Enum> setting;
    
    public EnumButton(final Setting<Enum> setting, final float x) {
        this.setting = setting;
        this.x = x;
        width = 100.0f;
        height = 16.0f;
    }
    
    @Override
    public float doRender(final int mouseX, final int mouseY, final float x, final float y) {
        RenderUtil.drawRect(this.x = x, this.y = y, 100.0f, 16.0f, color0);
        if (isMouseHovering(mouseX, mouseY)) {
            RenderUtil.drawRect(x, y, 100.0f, 16.0f, hovering);
        }
        final float fonty = getCenter(y, 16.0f, RenderUtil.getStringHeight(1.0f));
        RenderUtil.drawString(setting.name + " : " + setting.getValue().name(), x + 6.0f, fonty, ColorUtil.toRGBA(250, 250, 250), false, 1.0f);
        return 16.0f;
    }
    
    @Override
    public void onMouseClicked(final int x, final int y, final int button) {
        if (isMouseHovering(x, y) && button == 0) {
            setting.increaseEnum();
        }
    }
}
