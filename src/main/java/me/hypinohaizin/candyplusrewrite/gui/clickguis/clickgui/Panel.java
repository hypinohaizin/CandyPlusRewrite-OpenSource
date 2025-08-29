package me.hypinohaizin.candyplusrewrite.gui.clickguis.clickgui;

import me.hypinohaizin.candyplusrewrite.utils.RenderUtil;
import me.hypinohaizin.candyplusrewrite.utils.ColorUtil;
import me.hypinohaizin.candyplusrewrite.utils.StringUtil;
import java.util.concurrent.atomic.AtomicReference;

import me.hypinohaizin.candyplusrewrite.CandyPlusRewrite;
import me.hypinohaizin.candyplusrewrite.module.render.ClickGUI;
import java.util.ArrayList;
import me.hypinohaizin.candyplusrewrite.gui.clickguis.clickgui.item.ModuleButton;
import java.util.List;
import me.hypinohaizin.candyplusrewrite.module.Module;
import java.awt.Color;

public class Panel
{
    public float x;
    public float y;
    public float width;
    public float height;
    public boolean open;
    public Color color;
    public Module.Categories category;
    public List<ModuleButton> modules;
    public boolean moving;
    public float diffx;
    public float diffy;

    public Panel(final float x, final float y, final Module.Categories category) {
        modules = new ArrayList<>();
        moving = false;
        diffy = 0.0f;
        this.x = x;
        this.y = y;
        width = 100.0f;
        height = 15.0f;
        open = true;
        color = ((ClickGUI) CandyPlusRewrite.m_module.getModuleWithClass(ClickGUI.class)).color.getValue();
        this.category = category;
        final List<Module> modules = new ArrayList<Module>(CandyPlusRewrite.m_module.getModulesWithCategories(category));
        modules.sort((c1, c2) -> c1.name.compareToIgnoreCase(c2.name));
        modules.forEach(m -> this.modules.add(new ModuleButton(m, x)));
    }

    public void onRender(final int mouseX, final int mouseY) {
        final AtomicReference<Float> _y = new AtomicReference<Float>(y + 15.0f);
        if (open) {
            modules.forEach(m -> _y.updateAndGet(v -> v + m.onRender(mouseX, mouseY, x, _y.get())));
        }
        final String name = StringUtil.getName(category.name());
        RenderUtil.drawRect(x, y, 100.0f, 15.0f, ColorUtil.toRGBA(30, 30, 30));
        RenderUtil.drawLine(x, y + 15.0f, x + 100.0f, y + 15.0f, 2.0f, ColorUtil.toRGBA(color));
        final float namex = getCenter(x, 100.0f, RenderUtil.getStringWidth(name, 1.0f));
        final float namey = getCenter(y, 15.0f, RenderUtil.getStringHeight(1.0f));
        RenderUtil.drawString(name, namex, namey, ColorUtil.toRGBA(250, 250, 250), false, 1.0f);
        final ClickGUI module = (ClickGUI) CandyPlusRewrite.m_module.getModuleWithClass(ClickGUI.class);
        if (module == null) {
            return;
        }
        if (module.outline.getValue()) {
            RenderUtil.drawLine(x, y, x + 100.0f, y, 1.0f, ColorUtil.toRGBA(color));
            RenderUtil.drawLine(x, _y.get(), x + 100.0f, _y.get(), 1.0f, ColorUtil.toRGBA(color));
            RenderUtil.drawLine(x, y, x, _y.get(), 1.0f, ColorUtil.toRGBA(color));
            RenderUtil.drawLine(x + 100.0f, y, x + 100.0f, _y.get(), 1.0f, ColorUtil.toRGBA(color));
        }
    }

    public void onMouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        if (mouseButton == 0 && isMouseHovering(mouseX, mouseY)) {
            moving = true;
            diffx = x - mouseX;
            diffy = y - mouseY;
        }
        if (mouseButton == 1 && isMouseHovering(mouseX, mouseY)) {
            open = !open;
        }
        if (open) {
            modules.forEach(m -> m.onMouseClicked(mouseX, mouseY, mouseButton));
        }
    }

    public void onMouseReleased(final int mouseX, final int mouseY, final int state) {
        moving = false;
        if (open) {
            modules.forEach(m -> m.onMouseReleased(mouseX, mouseY, state));
        }
    }

    public void onMouseClickMove(final int mouseX, final int mouseY, final int clickedMouseButton) {
        if (clickedMouseButton == 0 && moving) {
            x = mouseX + diffx;
            y = mouseY + diffy;
        }
        if (open) {
            modules.forEach(m -> m.onMouseClickMove(mouseX, mouseY, clickedMouseButton));
        }
    }

    public void keyTyped(final char typedChar, final int keyCode) {
        if (open) {
            modules.forEach(m -> m.onKeyTyped(typedChar, keyCode));
        }
    }

    public Boolean isMouseHovering(final int mouseX, final int mouseY) {
        return x < mouseX && x + width > mouseX && y < mouseY && y + height > mouseY;
    }

    public float getCenter(final float a, final float b, final float c) {
        return a + (b - c) / 2.0f;
    }
}