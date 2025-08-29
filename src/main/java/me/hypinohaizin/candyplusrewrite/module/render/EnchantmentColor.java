package me.hypinohaizin.candyplusrewrite.module.render;

import java.awt.Color;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.module.Module;

public class EnchantmentColor extends Module
{
    public Setting<Color> color;
    public static EnchantmentColor INSTANCE;
    
    public EnchantmentColor() {
        super("EnchantmentColor", Categories.RENDER, false, false);
        color = register(new Setting<>("Color", new Color(255, 255, 255, 50)));
        EnchantmentColor.INSTANCE = this;
    }
}
