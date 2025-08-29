package me.hypinohaizin.candyplusrewrite.module.render;

import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.module.Module;

public class Notification extends Module
{
    public Setting<Integer> time;
    public Setting<Boolean> togglE;
    public Setting<Boolean> chat;
    public Setting<Boolean> message;
    public Setting<Boolean> player;
    public Setting<Boolean> pop;
    public Setting<Boolean> death;

    public Notification() {
        super("Notification", Categories.RENDER, false, true);
        time = register(new Setting<>("Time", 2, 5, 1));
        togglE = register(new Setting<>("Toggle", false));
        chat = register(new Setting<>("ChatToggle", false));
        message = register(new Setting<>("Message", true));
        player = register(new Setting<>("Player", true));
        pop = register(new Setting<>("Totem", true));
        death = register(new Setting<>("Death", true));
    }
}