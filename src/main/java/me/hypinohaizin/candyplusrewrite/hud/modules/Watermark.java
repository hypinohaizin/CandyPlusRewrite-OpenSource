package me.hypinohaizin.candyplusrewrite.hud.modules;

import me.hypinohaizin.candyplusrewrite.CandyPlusRewrite;
import me.hypinohaizin.candyplusrewrite.utils.ColorUtil;
import me.hypinohaizin.candyplusrewrite.utils.RenderUtil;
import java.awt.Color;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.hud.Hud;

public class Watermark extends Hud
{
    public Setting<Float> speed;
    public Setting<Float> size;
    public Setting<Boolean> shadow;
    public Setting<Color> color;
    public Setting<Boolean> background;
    public Setting<Color> backcolor;
    private float offsetx;

    public Watermark() {
        super("Watermark", 10.0f, 10.0f);
        speed = register(new Setting<>("Speed", 4.0f, 10.0f, 0.1f));
        size = register(new Setting<>("Size", 1.0f, 5.0f, 0.5f));
        shadow = register(new Setting<>("Shadow", false));
        color = register(new Setting<>("Color", new Color(255, 255, 255, 255)));
        background = register(new Setting<>("Background", false));
        backcolor = register(new Setting<>("BGColor", new Color(40, 40, 40, 60), v -> background.getValue()));
        offsetx = 0.0f;
    }

    @Override
    public void onRender() {
        final String message = CandyPlusRewrite.NV;
        final float size = this.size.getValue();
        final float width = RenderUtil.getStringWidth(message, size);
        final float height = RenderUtil.getStringHeight(size);
        if (background.getValue()) {
            RenderUtil.drawRect(x.getValue() + offsetx, y.getValue(), width + 20.0f * size, height + 10.0f * size, ColorUtil.toRGBA(backcolor.getValue()));
        }
        RenderUtil.drawString(message, x.getValue() + 10.0f + offsetx, y.getValue() + 5.0f, ColorUtil.toRGBA(color.getValue()), shadow.getValue(), size);
        if (scaledWidth + RenderUtil.getStringWidth(message, size) + 10.0f < offsetx) {
            offsetx = RenderUtil.getStringWidth(message, size) * -1.0f - 10.0f;
        }
        this.width = width + 5.0f * size;
        this.height = height + 5.0f * size;
    }
}
