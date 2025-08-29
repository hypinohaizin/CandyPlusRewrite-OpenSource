package me.hypinohaizin.candyplusrewrite.module.misc;

import me.hypinohaizin.candyplusrewrite.CandyPlusRewrite;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.module.Module;

public class DiscordRPC extends Module
{

    public DiscordRPC() {
        super("DiscordRPC", Categories.MISC, false, false);
    }

    @Override
    public void onEnable() {
        CandyPlusRewrite.m_rpc.enable(this);
    }

    @Override
    public void onDisable() {
        CandyPlusRewrite.m_rpc.disable();
    }
}
