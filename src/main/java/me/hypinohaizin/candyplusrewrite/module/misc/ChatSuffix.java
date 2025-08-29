package me.hypinohaizin.candyplusrewrite.module.misc;

import me.hypinohaizin.candyplusrewrite.command.CommandManager;
import net.minecraft.network.play.client.CPacketChatMessage;
import me.hypinohaizin.candyplusrewrite.event.events.network.PacketEvent;
import me.hypinohaizin.candyplusrewrite.module.Module;

public class ChatSuffix extends Module
{

    public ChatSuffix() {
        super("ChatSuffix", Categories.MISC, false, false);
    }

    @Override
    public void onPacketSend(final PacketEvent.Send event) {
        if (event.packet instanceof CPacketChatMessage) {
            CPacketChatMessage p = (CPacketChatMessage) event.packet;
            String msg = p.getMessage();
            if (msg.startsWith("/") || (msg.startsWith(CommandManager.getCommandPrefix()))) return;

            String suffixCheck = "Candy+ Rewrite".toLowerCase();
            if (msg.contains(" || " + toUnicode(suffixCheck))) return;

            String suffix = toUnicode(" || " + suffixCheck);
            String newMsg = msg + suffix;

            if (newMsg.length() > 255) return;

            event.cancel();
            mc.player.connection.sendPacket(new CPacketChatMessage(newMsg));
        }
    }

    public String toUnicode(final String s) {
        return s.toLowerCase().replace("a", "\u1d00").replace("b", "\u0299").replace("c", "\u1d04").replace("d", "\u1d05").replace("e", "\u1d07").replace("f", "\ua730").replace("g", "\u0262").replace("h", "\u029c").replace("i", "\u026a").replace("j", "\u1d0a").replace("k", "\u1d0b").replace("l", "\u029f").replace("m", "\u1d0d").replace("n", "\u0274").replace("o", "\u1d0f").replace("p", "\u1d18").replace("q", "\u01eb").replace("r", "\u0280").replace("s", "\ua731").replace("t", "\u1d1b").replace("u", "\u1d1c").replace("v", "\u1d20").replace("w", "\u1d21").replace("x", "\u02e3").replace("y", "\u028f").replace("z", "\u1d22");
    }
}