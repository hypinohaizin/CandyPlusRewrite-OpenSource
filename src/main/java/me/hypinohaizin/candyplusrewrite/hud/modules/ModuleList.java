package me.hypinohaizin.candyplusrewrite.hud.modules;

import java.util.List;
import me.hypinohaizin.candyplusrewrite.utils.ColorUtil;
import java.util.Collections;

import me.hypinohaizin.candyplusrewrite.module.Module;
import java.util.ArrayList;
import me.hypinohaizin.candyplusrewrite.CandyPlusRewrite;
import me.hypinohaizin.candyplusrewrite.utils.RenderUtil;
import java.awt.Color;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.hud.Hud;

public class ModuleList extends Hud
{
    public Setting<Boolean> shadow;
    public Setting<Color> color;
    public Setting<Boolean> background;
    public Setting<Color> backcolor;
    public Setting<Boolean> edge;
    public Setting<Color> edgeColor;
    
    public ModuleList() {
        super("ModuleList", 0.0f, 110.0f);
        shadow = register(new Setting<>("Shadow", false));
        color = register(new Setting<>("Color", new Color(255, 255, 255, 255)));
        background = register(new Setting<>("Background", true));
        backcolor = register(new Setting<>("BGColor", new Color(40, 40, 40, 70), v -> background.getValue()));
        edge = register(new Setting<>("Edge", true));
        edgeColor = register(new Setting<>("EGColor", new Color(255, 255, 255, 150), v -> edge.getValue()));
    }
    
    @Override
    public void onRender() {
        float y = this.y.getValue();
        final float size = 1.0f;
        float _width = 0.0f;
        final float height = RenderUtil.getStringHeight(size);
        final List<Module> sortedModuleList = new ArrayList<Module>(CandyPlusRewrite.m_module.modules);
        Collections.sort(sortedModuleList, (c1, c2) -> c1.name.compareToIgnoreCase(c2.name));
        for (final Module module : sortedModuleList) {
            if (!module.isEnable) {
                continue;
            }
            final String name = module.name;
            final float width = RenderUtil.getStringWidth(name, size);
            if (width > _width) {
                _width = width;
            }
            if (background.getValue()) {
                RenderUtil.drawRect(x.getValue(), y, width + 20.0f * size, height + 10.0f * size, ColorUtil.toRGBA(backcolor.getValue()));
                if (edge.getValue()) {
                    RenderUtil.drawRect(x.getValue(), y, 2.0f * size, height + 10.0f * size, ColorUtil.toRGBA(edgeColor.getValue()));
                }
            }
            RenderUtil.drawString(name, x.getValue() + 10.0f, y + 5.0f, ColorUtil.toRGBA(color.getValue()), shadow.getValue(), size);
            y += height + 10.0f;
        }
        y -= height + 11.0f;
        width = _width + 20.0f;
        this.height = y - this.y.getValue();
    }
}
