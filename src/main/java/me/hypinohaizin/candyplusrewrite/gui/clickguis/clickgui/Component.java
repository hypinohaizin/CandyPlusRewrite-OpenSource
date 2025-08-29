package me.hypinohaizin.candyplusrewrite.gui.clickguis.clickgui;

import me.hypinohaizin.candyplusrewrite.utils.ColorUtil;
import me.hypinohaizin.candyplusrewrite.CandyPlusRewrite;
import me.hypinohaizin.candyplusrewrite.module.render.ClickGUI;
import java.awt.Color;
import me.hypinohaizin.candyplusrewrite.module.Module;

public class Component
{
    public Module module;
    public float x;
    public float y;
    public float width;
    public float height;
    public Color color;
    public int color0;
    public int hovering;

    public Component() {
        color = ((ClickGUI) CandyPlusRewrite.m_module.getModuleWithClass(ClickGUI.class)).color.getValue();
        color0 = ColorUtil.toRGBA(30, 35, 30);
        hovering = ColorUtil.toRGBA(170, 170, 170, 100);
    }

    public float doRender(final int mouseX, final int mouseY, final float x, final float y) {
        return 0.0f;
    }

    public void onMouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
    }

    public void onMouseReleased(final int mouseX, final int mouseY, final int state) {
    }

    public void onMouseClickMove(final int mouseX, final int mouseY, final int clickedMouseButton) {
    }

    public void onKeyTyped(final char typedChar, final int keyCode) {
    }

    public float getCenter(final float a, final float b, final float c) {
        return a + (b - c) / 2.0f;
    }

    public Boolean isMouseHovering(final int mouseX, final int mouseY) {
        return x < mouseX && x + width > mouseX && y < mouseY && y + height > mouseY;
    }

    public Boolean isMouseHovering(final float mouseX, final float mouseY, final float cx, final float cy, final float cw, final float ch) {
        return cx < mouseX && cx + cw > mouseX && cy < mouseY && cy + ch > mouseY;
    }
}