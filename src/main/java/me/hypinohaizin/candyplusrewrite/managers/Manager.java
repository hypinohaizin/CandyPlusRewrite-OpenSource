package me.hypinohaizin.candyplusrewrite.managers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.hypinohaizin.candyplusrewrite.CandyPlusRewrite;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import me.hypinohaizin.candyplusrewrite.utils.Util;

public class Manager implements Util
{
    public void load() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    public void unload() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }
    
    public boolean nullCheck() {
        return Manager.mc.player == null;
    }
    
    public void sendMessage(final String str) {
        Manager.mc.player.sendMessage(new ChatMessage(TextFormatting.GRAY + "[" + TextFormatting.LIGHT_PURPLE + CandyPlusRewrite.NAME2 + TextFormatting.GRAY + "] " + str));
    }
    
    public static class ChatMessage extends TextComponentBase
    {
        private final String text;
        
        public ChatMessage(final String text) {
            final Pattern pattern = Pattern.compile("&[0123456789abcdefrlosmk]");
            final Matcher matcher = pattern.matcher(text);
            final StringBuffer stringBuffer = new StringBuffer();
            while (matcher.find()) {
                final String replacement = matcher.group().substring(1);
                matcher.appendReplacement(stringBuffer, replacement);
            }
            matcher.appendTail(stringBuffer);
            this.text = stringBuffer.toString();
        }
        
        public String getUnformattedComponentText() {
            return text;
        }
        
        public ITextComponent createCopy() {
            return null;
        }
        
        public ITextComponent shallowCopy() {
            return new ChatMessage(text);
        }
    }
}
