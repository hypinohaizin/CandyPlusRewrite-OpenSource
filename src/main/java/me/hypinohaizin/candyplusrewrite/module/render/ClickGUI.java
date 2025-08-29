package me.hypinohaizin.candyplusrewrite.module.render;

import me.hypinohaizin.candyplusrewrite.gui.clickguis.clickgui.CandyGUI;
import me.hypinohaizin.candyplusrewrite.gui.clickguis.CGui;
import java.awt.Color;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.module.Module;

public class ClickGUI extends Module
{
    public Setting<Color> color;
    public Setting<Boolean> outline;
    
    public ClickGUI() {
        super("ClickGUI", Categories.RENDER, 21, false, false);
        color = register(new Setting<>("Color", new Color(210, 0, 130, 255)));
        outline = register(new Setting<>("Outline", false));
    }
    
    @Override
    public void onEnable() {
        if (nullCheck()) {
            return;
        }
        if (!(ClickGUI.mc.currentScreen instanceof CGui)) {
                ClickGUI.mc.displayGuiScreen(new CandyGUI());
        }
    }
    
    @Override
    public void onUpdate() {
        if (!(ClickGUI.mc.currentScreen instanceof CGui) && !HUDEditor.instance.isEnable) {
            disable();
        }
    }
    

}
