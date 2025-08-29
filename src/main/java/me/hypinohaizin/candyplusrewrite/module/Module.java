package me.hypinohaizin.candyplusrewrite.module;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.hypinohaizin.candyplusrewrite.utils.ChatUtil;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.ITextComponent;
import me.hypinohaizin.candyplusrewrite.managers.Manager;
import me.hypinohaizin.candyplusrewrite.CandyPlusRewrite;
import me.hypinohaizin.candyplusrewrite.module.render.Notification;
import me.hypinohaizin.candyplusrewrite.module.misc.DiscordRPC;
import java.util.Objects;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.Map;
import java.io.FileWriter;
import java.io.File;
import java.awt.Color;
import me.hypinohaizin.candyplusrewrite.setting.EnumConverter;
import java.util.HashMap;
import com.google.gson.Gson;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.entity.player.EntityPlayer;
import me.hypinohaizin.candyplusrewrite.event.events.player.PlayerDeathEvent;
import net.minecraftforge.common.MinecraftForge;
import me.hypinohaizin.candyplusrewrite.event.events.network.PacketEvent;
import java.util.ArrayList;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import java.util.List;
import me.hypinohaizin.candyplusrewrite.setting.Bind;
import me.hypinohaizin.candyplusrewrite.utils.Util;

public class Module implements Util
{
    public String name;
    public Categories category;
    public boolean hide;
    public boolean isEnable;
    public Bind key;
    public List<Setting> settings;
    
    public Module(final String name, final Categories category, final int defaultKey, final boolean hide, final boolean defaultStatus) {
        this.name = "";
        this.category = Categories.MISC;
        this.hide = false;
        isEnable = false;
        key = new Bind();
        settings = new ArrayList<>();
        this.name = name;
        this.category = category;
        setKey(defaultKey);
        this.hide = hide;
        isEnable = defaultStatus;
    }
    
    public Module(final String name, final Categories category, final boolean hide, final boolean defaultStatus) {
        this.name = "";
        this.category = Categories.MISC;
        this.hide = false;
        isEnable = false;
        key = new Bind();
        settings = new ArrayList<>();
        this.name = name;
        this.category = category;
        setKey(-1);
        this.hide = hide;
        isEnable = defaultStatus;
    }
    
    public void onUpdate() {
    }
    
    public void onTick() {
    }
    
    public void onConnect() {
    }
    
    public void onRender2D() {
    }
    
    public void onRender3D() {
    }
    
    public void onRender3D(final float ticks) {
    }
    
    public void onPacketSend(final PacketEvent.Send event) {
    }
    
    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }
    
    public void onPlayerDeath(final PlayerDeathEvent event) {
    }

    public void onPlayerLogout(EntityPlayer player) {
    }

    public void onPacketReceive(final PacketEvent.Receive event) {
    }
    
    public void onTotemPop(final EntityPlayer entity) {
    }

    public boolean isEnable() {
        return isEnable;
    }

    public String getName() {
        return name;
    }

    public boolean toggle() {
        if (!isEnable) {
            enable();
        }
        else {
            disable();
        }
        return isEnable;
    }

    public void enable() {
        if (!isEnable) {
            SendMessage(name + " : " + ChatFormatting.GREEN + "Enabled");
            Notification notification = (Notification) CandyPlusRewrite.m_module.getModuleWithClass(Notification.class);
            if (notification != null && notification.chat.getValue()) {
                ChatUtil.sendMessage(name + " : " + ChatFormatting.GREEN + "Enabled");
            }
            isEnable = true;
            onEnable();
        }
    }

    public void disable() {
        if (isEnable) {
            SendMessage(name + " : " + ChatFormatting.RED + "Disabled");
            Notification notification = (Notification) CandyPlusRewrite.m_module.getModuleWithClass(Notification.class);
            if (notification != null && notification.chat.getValue()) {
                ChatUtil.sendMessage(name + " : " + ChatFormatting.RED + "Disabled");
            }
            isEnable = false;
            onDisable();
        }
    }
    
    public Setting register(final Setting setting) {
        settings.add(setting);
        return setting;
    }
    
    public boolean nullCheck() {
        return Module.mc.player == null || Module.mc.world == null;
    }
    
    public void setKey(final int Nkey) {
        key.setKey(Nkey);
    }
    
    public void saveConfig() throws IOException {
        final Gson gson = new Gson();
        final Map<String, Object> mappedSettings = new HashMap<String, Object>();
        for (final Setting s : settings) {
            if (s.value instanceof Enum) {
                mappedSettings.put(s.name, EnumConverter.currentEnum((Enum)s.value) + "N");
            }
            else if (s.value instanceof Color) {
                final Color color = (Color)s.value;
                mappedSettings.put(s.name, color.getRed() + "," + color.getGreen() + "," + color.getBlue() + "," + color.getAlpha() + "C");
            }
            else if (s.value instanceof Integer) {
                mappedSettings.put(s.name, s.value + "I");
            }
            else {
                mappedSettings.put(s.name, s.value);
            }
        }
        mappedSettings.put("ismoduleenabled", isEnable);
        mappedSettings.put("keybindcode", key.getKey());
        final String json = gson.toJson(mappedSettings);
        final File config = new File("candyplusrewrite/" + category.name().toLowerCase() + "/" + name.toLowerCase() + ".json");
        final FileWriter writer = new FileWriter(config);
        writer.write(json);
        writer.close();
    }
    
    public void loadConfig() throws Exception {
        final Path path = Paths.get("candyplusrewrite/" + category.name().toLowerCase() + "/" + name.toLowerCase() + ".json");
        if (!Files.exists(path)) {
            return;
        }
        final String context = readAll(path);
        final Gson gson = new Gson();
        final Map<String, Object> mappedSettings = (Map<String, Object>)gson.fromJson(context, (Class)Map.class);
        mappedSettings.forEach((name, value) -> setConfig(name, value));
    }
    
    public void setConfig(final String name, final Object value) {
        if (Objects.equals(name, "ismoduleenabled")) {
            isEnable = (boolean)value;
            if (isEnable) {
                MinecraftForge.EVENT_BUS.register(this);
                if (this instanceof DiscordRPC) {
                    onEnable();
                }
            }
            return;
        }
        if (Objects.equals(name, "keybindcode")) {
            key.setKey(((Double)value).intValue());
            return;
        }
        final List<Setting> settings = new ArrayList<>(this.settings);
        for (int i = 0; i < settings.size(); ++i) {
            final Setting setting = settings.get(i);
            final String n = setting.name;
            if (Objects.equals(n, name)) {
                final char c = value.toString().charAt(value.toString().length() - 1);
                if (c == 'N') {
                    final String enumValue = value.toString().replace("N", "");
                    setting.setEnum(Integer.parseInt(enumValue));
                }
                else if (c == 'C') {
                    final String[] color = value.toString().replace("C", "").split(",");
                    setting.setValue(new Color(Integer.parseInt(color[0]), Integer.parseInt(color[1]), Integer.parseInt(color[2]), Integer.parseInt(color[3])));
                }
                else if (c == 'I') {
                    final String intValue = value.toString().replace("I", "");
                    setting.setValue(Double.valueOf(intValue).intValue());
                }
                else if (value instanceof Double) {
                    setting.setValue(((Double)value).floatValue());
                }
                else {
                    setting.setValue(value);
                }
            }
        }
        this.settings = new ArrayList<>(settings);
    }
    
    public static String readAll(final Path path) throws IOException {
        return Files.lines(path).reduce("", (prev, line) -> prev + line + System.getProperty("line.separator"));
    }
    
    public void sendMessage(final String str) {
        final Notification notif = (Notification) CandyPlusRewrite.m_module.getModuleWithClass(Notification.class);
        if (notif.isEnable && notif.message.getValue()) {
            CandyPlusRewrite.m_notif.showNotification(str);
        }
        else {
            ChatUtil.sendMessage(str);
        }
    }
    
    private void SendMessage(final String str) {
        if (GUICheck()) {
            final Notification notif = (Notification) CandyPlusRewrite.m_module.getModuleWithClass(Notification.class);
            if (notif.isEnable && notif.togglE.getValue()) {
                CandyPlusRewrite.m_notif.showNotification(str);
            }
            else {
                ChatUtil.sendMessage(str);
            }
        }
    }
    
    private boolean GUICheck() {
        return !name.equalsIgnoreCase("clickgui");
    }
    
    public enum Categories
    {
        COMBAT, 
        EXPLOIT, 
        MISC, 
        MOVEMENT, 
        RENDER, 
        HUB
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
            return new Manager.ChatMessage(text);
        }
    }
}
