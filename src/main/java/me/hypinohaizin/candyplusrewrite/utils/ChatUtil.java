package me.hypinohaizin.candyplusrewrite.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import me.hypinohaizin.candyplusrewrite.CandyPlusRewrite;

/**
 * @author h_ypi
 * @since 2025/08/04 15:27
 */
public class ChatUtil {

    public static void sendMessage(String str) {
        Minecraft.getMinecraft().player.sendMessage(
                new TextComponentString(TextFormatting.GRAY + "[" + TextFormatting.LIGHT_PURPLE + CandyPlusRewrite.NAME2 + TextFormatting.GRAY + "] " + str));
    }

    public static void sendServerMessage(String str) {
        Minecraft.getMinecraft().player.sendChatMessage(str);
    }
}
