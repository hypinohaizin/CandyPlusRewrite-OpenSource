package me.hypinohaizin.candyplusrewrite.module.render;

import me.hypinohaizin.candyplusrewrite.CandyPlusRewrite;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.module.Module;

public class SmallShield extends Module
{
    public Setting<Boolean> normalOffset;
    public Setting<Float> offset;
    public Setting<Float> offX;
    public Setting<Float> offY;
    public Setting<Float> mainX;
    public Setting<Float> mainY;
    
    public SmallShield() {
        super("SmallShield", Categories.RENDER, false, false);
        normalOffset = register(new Setting<>("OffNormal", false));
        offset = register(new Setting<>("Offset", 0.7f, 1.0f, 0.0f, v -> normalOffset.getValue()));
        offX = register(new Setting<>("OffX", 0.0f, 1.0f, (-1.0f), v -> !normalOffset.getValue()));
        offY = register(new Setting<>("OffY", 0.0f, 1.0f, (-1.0f), v -> !normalOffset.getValue()));
        mainX = register(new Setting<>("MainX", 0.0f, 1.0f, (-1.0f)));
        mainY = register(new Setting<>("MainY", 0.0f, 1.0f, (-1.0f)));
    }
    
    public static SmallShield getINSTANCE() {
        return (SmallShield) CandyPlusRewrite.m_module.getModuleWithClass(SmallShield.class);
    }
    
    @Override
    public void onUpdate() {
        if (nullCheck()) {
            return;
        }
        if (normalOffset.getValue()) {}
    }
}
