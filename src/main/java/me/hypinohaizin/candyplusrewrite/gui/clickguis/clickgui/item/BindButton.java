package me.hypinohaizin.candyplusrewrite.gui.clickguis.clickgui.item;

import org.lwjgl.input.Keyboard;
import me.hypinohaizin.candyplusrewrite.utils.ColorUtil;
import me.hypinohaizin.candyplusrewrite.utils.RenderUtil;
import me.hypinohaizin.candyplusrewrite.module.Module;
import me.hypinohaizin.candyplusrewrite.gui.clickguis.clickgui.Component;

public class BindButton extends Component
{
    public Module module;
    public boolean keyWaiting;
    
    public BindButton(final Module module, final float x) {
        keyWaiting = false;
        this.module = module;
        this.x = x;
        width = 100.0f;
        height = 16.0f;
    }
    
    @Override
    public float doRender(final int mouseX, final int mouseY, final float x, final float y) {
        RenderUtil.drawRect(this.x = x, this.y = y, 100.0f, 16.0f, color0);
        if (keyWaiting) {
            RenderUtil.drawRect(x, y, 100.0f, 16.0f, ColorUtil.toRGBA(color));
        }
        if (isMouseHovering(mouseX, mouseY)) {
            RenderUtil.drawRect(x, y, 100.0f, 16.0f, hovering);
        }
        final float bindy = getCenter(y, 16.0f, RenderUtil.getStringHeight(1.0f));
        RenderUtil.drawString("Bind : " + (keyWaiting ? "..." : ((module.key.key == -1) ? "NONE" : Keyboard.getKeyName(module.key.key))), x + 6.0f, bindy, ColorUtil.toRGBA(250, 250, 250), false, 1.0f);
        return 16.0f;
    }
    
    @Override
    public void onMouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        if (mouseButton == 0 && isMouseHovering(mouseX, mouseY)) {
            keyWaiting = !keyWaiting;
        }
    }
    
    @Override
    public void onKeyTyped(final char typedChar, final int keyCode) {
        if (keyWaiting) {
            if (keyCode == 14 || keyCode == 1) {
                module.setKey(-1);
            }
            else {
                module.setKey(keyCode);
            }
            keyWaiting = false;
        }
    }
}
