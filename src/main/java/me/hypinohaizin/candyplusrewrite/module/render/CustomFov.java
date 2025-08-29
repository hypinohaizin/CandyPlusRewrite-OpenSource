package me.hypinohaizin.candyplusrewrite.module.render;

import net.minecraft.client.settings.GameSettings;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.module.Module;

public class CustomFov extends Module
{
    public Setting<Float> cfov;
    
    public CustomFov() {
        super("CustomFov", Categories.RENDER, false, false);
        cfov = register(new Setting<>("Fov", 0.0f, 180.0f, 1.0f));
    }
    
    @Override
    public void onUpdate() {
        CustomFov.mc.gameSettings.setOptionFloatValue(GameSettings.Options.FOV, cfov.getValue());
    }
}
