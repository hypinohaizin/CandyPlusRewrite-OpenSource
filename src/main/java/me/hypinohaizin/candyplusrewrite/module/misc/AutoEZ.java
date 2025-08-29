package me.hypinohaizin.candyplusrewrite.module.misc;

import me.hypinohaizin.candyplusrewrite.managers.FriendManager;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.entity.player.EntityPlayer;
import me.hypinohaizin.candyplusrewrite.event.events.player.PlayerDeathEvent;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.module.Module;

public class AutoEZ extends Module {
    public Setting<Float> range;

    public AutoEZ() {
        super("AutoEZ", Categories.MISC, false, false);
        range = register(new Setting<>("Range", 10.0f, 50.0f, 1.0f));
    }

    @Override
    public void onPlayerDeath(final PlayerDeathEvent event) {
        if (nullCheck()) return;
        final EntityPlayer player = event.player;
        if (player.getHealth() > 0.0f) return;
        if (mc.player.getDistance(player) > range.getValue()) return;

        if (player == mc.player) return;
        if (FriendManager.isFriend(player.getName())) return;

        EZ();
    }

    public void EZ() {
            sendChat("you just got ez'd by candy+ rewrite");
    }

    public void sendChat(final String str) {
        AutoEZ.mc.player.connection.sendPacket(new CPacketChatMessage(str));
    }
}
