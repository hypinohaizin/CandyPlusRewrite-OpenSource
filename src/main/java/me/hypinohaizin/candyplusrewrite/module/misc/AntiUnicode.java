package me.hypinohaizin.candyplusrewrite.module.misc;

import me.hypinohaizin.candyplusrewrite.module.Module;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.TextComponentString;
import me.hypinohaizin.candyplusrewrite.event.events.network.PacketEvent;

/**
 * @author h_ypi
 * @since 2025/08/04 14:18
 */
public class AntiUnicode extends Module {
    public final String[] list = {
            "\u263b", "\u2465", "\u267b", "\u2525", "\ub9a2", "\uc384", "\u7e3a", "\u2c37", "\u84b9", "\ua225",
            "\u84b9", "\u3ac3", "\u317e", "\u2c36", "\u3ac3", "\u347e", "\u2673", "\u7340", "\u7465", "\u2220",
            "\ub9a2", "\uc384", "\u773d", "\u656b", "\u3459", "\u6f4d", "\u5262", "\u4a30", "\u7156", "\u6173",
            "\u374f", "\u7257", "\u4439", "\u4954", "\u7a42", "\u4336", "\u5546", "\u644b", "\u675a", "\u6970",
            "\u3347", "\u3151", "\u5838", "\u756c", "\u6a6e", "\u3268", "\u4439", "\u4579", "\u5076", "\u4c74",
            "\u7835", "\u4840", "\u536d", "\u2266", "\u250a", "\ub9a2", "\uc384", "\u7e3a", "\u3935", "\u3535",
            "\u0101", "\u0201", "\u0301", "\u0401", "\u0501", "\u0601", "\u0701", "\u0801", "\u0901", "\u0a01",
            "\u0b01", "\u0e01", "\u0f01", "\u1001", "\u1101", "\u1201", "\u1301", "\u1401", "\u1501", "\u1601",
            "\u1701", "\u1801", "\u1901", "\u1a01", "\u1b01", "\u1c01", "\u1d01", "\u1e01", "\u7001", "\u7101",
            "\u7201", "\u7301", "\u7401", "\u7501", "\u7601", "\u7701", "\u7801", "\u7901", "\u7a01", "\u7b01",
            "\u7c01", "\u7d01", "\u7e01", "\u7f01", "\u8001", "\u8101", "\u8201", "\u8301", "\u8401", "\u8501",
            "\u8601", "\u8701", "\u8801", "\u8901", "\u8a01", "\u8b01", "\u8c01", "\u8d01", "\u8e01", "\u8f01",
            "\u9001", "\u9101", "\u9201", "\u9301", "\u9401", "\u9501", "\u9601", "\u9701", "\u9801", "\u9901",
            "\u9a01", "\u9b01", "\u9c01", "\u9d01", "\u9e01", "\u9f01", "\ua001", "\ua101", "\ua201", "\ua301",
            "\ua401", "\ua501", "\ua601", "\ua701", "\ua801", "\ua901", "\uaa01", "\uac01", "\uad01", "\uae01",
            "\uaf01", "\ub001", "\ub101", "\ub201", "\ub301", "\ub401", "\ub501", "\ub601", "\ub701", "\ub801",
            "\ub901", "\uba01", "\ubb01", "\ubc01", "\ubd01", "\u6500", "\u7300", "\u7400", "\u7200", "\u7500"
    };

    public AntiUnicode() {
        super("AntiUnicode", Categories.MISC, false, false);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketChat) {
            String message = ((SPacketChat) event.getPacket()).getChatComponent().getUnformattedText();
            for (String unicode : list) {
                if (message.contains(unicode)) {
                    ((SPacketChat) event.getPacket()).chatComponent = new TextComponentString("(lag message)");
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }
}
