package me.hypinohaizin.candyplusrewrite.module.render;

import me.hypinohaizin.candyplusrewrite.module.Module;

public class HUDEditor extends Module
{
    public static HUDEditor instance;
    
    public HUDEditor() {
        super("HUDEditor", Categories.RENDER, false, false);
        HUDEditor.instance = this;
    }
    
    static {
        HUDEditor.instance = null;
    }
}
