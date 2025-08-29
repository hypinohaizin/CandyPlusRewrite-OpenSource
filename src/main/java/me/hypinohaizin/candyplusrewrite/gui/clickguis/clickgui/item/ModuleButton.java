package me.hypinohaizin.candyplusrewrite.gui.clickguis.clickgui.item;

import java.awt.Color;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import java.util.concurrent.atomic.AtomicReference;
import me.hypinohaizin.candyplusrewrite.utils.ColorUtil;
import me.hypinohaizin.candyplusrewrite.utils.RenderUtil;

import java.util.ArrayList;
import me.hypinohaizin.candyplusrewrite.module.Module;
import java.util.List;
import me.hypinohaizin.candyplusrewrite.gui.clickguis.clickgui.Component;

public class ModuleButton extends Component
{
    public List<Component> componentList;
    public boolean open;
    
    public ModuleButton(final Module module, final float x) {
        componentList = new ArrayList<Component>();
        this.module = module;
        this.x = x;
        width = 100.0f;
        open = false;
        height = 16.0f;
        module.settings.forEach(this::addSetting);
        add(new BindButton(module, x));
    }
    
    public float onRender(final int mouseX, final int mouseY, final float x, final float y) {
        RenderUtil.drawRect(this.x = x, this.y = y, 100.0f, 16.0f, color0);
        if (module.isEnable) {
            RenderUtil.drawRect(x, y, 100.0f, 16.0f, ColorUtil.toRGBA(color));
        }
        if (isMouseHovering(mouseX, mouseY)) {
            RenderUtil.drawRect(x, y, 100.0f, 16.0f, hovering);
        }
        final String name = module.name;
        final float namey = getCenter(y, 16.0f, RenderUtil.getStringHeight(1.0f));
        RenderUtil.drawString(name, x + 3.0f, namey, ColorUtil.toRGBA(250, 250, 250), false, 1.0f);
        RenderUtil.drawString("...", x + 100.0f - RenderUtil.getStringWidth("...", 1.0f) - 3.0f, namey - 1.0f, ColorUtil.toRGBA(250, 250, 250), false, 1.0f);
        final AtomicReference<Float> height = new AtomicReference<Float>(16.0f);
        if (open) {
            componentList.forEach(c -> {
                final float h = c.doRender(mouseX, mouseY, x, y + height.get());
                RenderUtil.drawRect(x, y + height.get(), 2.0f, h, ColorUtil.toRGBA(color));
                height.updateAndGet(v -> v + h);
            });
        }
        return height.get();
    }
    
    @Override
    public void onMouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        if (mouseButton == 0 && isMouseHovering(mouseX, mouseY)) {
            module.toggle();
        }
        if (mouseButton == 1 && isMouseHovering(mouseX, mouseY)) {
            open = !open;
        }
        if (open) {
            componentList.forEach(c -> c.onMouseClicked(mouseX, mouseY, mouseButton));
        }
    }
    
    @Override
    public void onMouseReleased(final int mouseX, final int mouseY, final int state) {
        if (open) {
            componentList.forEach(c -> c.onMouseReleased(mouseX, mouseY, state));
        }
    }
    
    @Override
    public void onMouseClickMove(final int mouseX, final int mouseY, final int clickedMouseButton) {
        if (open) {
            componentList.forEach(c -> c.onMouseClickMove(mouseX, mouseY, clickedMouseButton));
        }
    }
    
    @Override
    public void onKeyTyped(final char typedChar, final int keyCode) {
        if (open) {
            componentList.forEach(c -> c.onKeyTyped(typedChar, keyCode));
        }
    }
    
    public void addSetting(final Setting setting) {
        final Object value = setting.value;
        if (value instanceof Boolean) {
            add(new BooleanButton(setting, x));
        }
        else if (value instanceof Integer) {
            add(new IntegerSlider(setting, x));
        }
        else if (value instanceof Float) {
            add(new FloatSlider(setting, x));
        }
        else if (value instanceof Color) {
            add(new ColorSlider(setting, x));
        }
        else {
            add(new EnumButton(setting, x));
        }
    }
    
    private void add(final Component component) {
        componentList.add(component);
    }
}
