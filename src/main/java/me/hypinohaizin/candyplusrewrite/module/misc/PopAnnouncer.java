package me.hypinohaizin.candyplusrewrite.module.misc;

import me.hypinohaizin.candyplusrewrite.event.events.player.PlayerDeathEvent;
import me.hypinohaizin.candyplusrewrite.managers.FriendManager;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.entity.player.EntityPlayer;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.module.Module;

import java.util.HashMap;
import java.util.Map;

public class PopAnnouncer extends Module {
    public Setting<Float> range;
    private final Map<String, Integer> popCount = new HashMap<>();

    public PopAnnouncer() {
        super("PopAnnouncer", Categories.MISC, false, false);
        range = register(new Setting<>("Range", 10.0f, 20.0f, 1.0f));
    }

    @Override
    public void onTotemPop(final EntityPlayer player) {
        if (player == null || nullCheck()) return;
        if (mc.player.getDistance(player) > range.getValue()) return;
        String name = player.getName();

        if (player == mc.player) return;
        if (FriendManager.isFriend(name)) return;

        int count = popCount.getOrDefault(name, 0) + 1;
        popCount.put(name, count);

        String msg = "ez pop " + name + " [" + count + "]";
        sendChat(msg);
    }

    @Override
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.player != null) popCount.remove(event.player.getName());
    }

    @Override
    public void onPlayerLogout(EntityPlayer player) {
        if (player != null) popCount.remove(player.getName());
    }

    public void sendChat(final String str) {
        PopAnnouncer.mc.player.connection.sendPacket(new CPacketChatMessage(str));
    }
}
