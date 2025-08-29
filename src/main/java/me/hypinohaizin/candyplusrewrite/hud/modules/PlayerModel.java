package me.hypinohaizin.candyplusrewrite.hud.modules;

import me.hypinohaizin.candyplusrewrite.utils.RenderUtil;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.hud.Hud;

public class PlayerModel extends Hud
{
    public Setting<Float> scale;
    
    public PlayerModel() {
        super("PlayerModel", 100.0f, 150.0f);
        scale = register(new Setting<>("Scale", 50.0f, 100.0f, 30.0f));
    }
    
    @Override
    public void onRender() {
        width = (PlayerModel.mc.player.width + 0.5f) * scale.getValue() + 10.0f;
        height = (PlayerModel.mc.player.height + 0.5f) * scale.getValue();
        RenderUtil.renderEntity(PlayerModel.mc.player, x.getValue() + width - 30.0f, y.getValue() + height - 20.0f, scale.getValue());
    }
}
