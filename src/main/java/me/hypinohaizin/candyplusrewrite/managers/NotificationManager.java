package me.hypinohaizin.candyplusrewrite.managers;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;

import java.util.stream.Collectors;
import me.hypinohaizin.candyplusrewrite.utils.RenderUtil;
import me.hypinohaizin.candyplusrewrite.utils.ColorUtil;
import java.awt.Color;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.hypinohaizin.candyplusrewrite.event.events.player.PlayerDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.network.play.server.SPacketEntityStatus;
import me.hypinohaizin.candyplusrewrite.event.events.network.PacketEvent;
import me.hypinohaizin.candyplusrewrite.module.render.Notification;
import me.hypinohaizin.candyplusrewrite.CandyPlusRewrite;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import java.util.List;

public class NotificationManager extends Manager
{
    public List<Notif> notifs;
    public List<EntityPlayer> players;
    private final Map<String, Integer> popCounter;
    private int scaledWidth;
    private int scaledHeight;
    private int scaleFactor;

    public NotificationManager() {
        notifs = new ArrayList<>();
        players = new ArrayList<>();
        popCounter = new HashMap<>();
        scaleFactor = 0;
    }

    public void showNotification(final String msg) {
        if (NotificationManager.mc.player == null) {
            return;
        }
        if (!CandyPlusRewrite.m_module.getModuleWithClass(Notification.class).isEnable) {
            return;
        }
        final Notif notif = new Notif(msg);
        for (final Notif notif2 : notifs) {
            final Notif n = notif2;
            notif2.y -= CandyPlusRewrite.m_font.getHeight() + 40;
        }
        updateResolution();
        notif.y = (float)(scaledHeight - 50);
        notif._y = (float)(scaledHeight - 50);
        notifs.add(notif);
    }

    public void onUpdate() {
        if (NotificationManager.mc.world == null) {
            return;
        }
        for (final EntityPlayer player : new ArrayList<>(NotificationManager.mc.world.playerEntities)) {
            if (player == mc.player) continue;
            if (!players.contains(player)) {
                showNotification(player.getName() + " is coming towards you!");
            }
        }
        players = new ArrayList<>(NotificationManager.mc.world.playerEntities);
    }

    @SubscribeEvent
    public void onPacketReceive(final PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketEntityStatus) {
            final SPacketEntityStatus packet = (SPacketEntityStatus)event.getPacket();
            if (packet.getOpCode() == 35 && packet.getEntity(NotificationManager.mc.world) instanceof EntityPlayer) {
                final EntityPlayer player = (EntityPlayer)packet.getEntity(NotificationManager.mc.world);
                final Notification notification = (Notification) CandyPlusRewrite.m_module.getModuleWithClass(Notification.class);
                if (notification.pop.getValue()) {
                    final int pop = countPop(player.getName());
                    if (pop == 1) {
                        showNotification(player.getName() + " popped a totem!");
                    }
                    else {
                        showNotification(player.getName() + " popped " + pop + " totems!");
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerDeath(final PlayerDeathEvent event) {
        final Notification notification = (Notification) CandyPlusRewrite.m_module.getModuleWithClass(Notification.class);
        if (notification.death.getValue()) {
            final EntityPlayer player = event.player;
            if (player == null) {
                return;
            }
            final int pop = getPop(player.getName());
            if (pop == 0) {
                showNotification(ChatFormatting.RED + player.getName() + " dead!");
            }
            else {
                showNotification(ChatFormatting.RED + player.getName() + " dead after " + pop + " pop!");
            }
            popCounter.remove(player.getName());
        }
    }

    public int countPop(final String name) {
        if (!popCounter.containsKey(name)) {
            popCounter.put(name, 1);
            return 1;
        }
        popCounter.replace(name, popCounter.get(name) + 1);
        return popCounter.get(name);
    }

    public int getPop(final String name) {
        if (!popCounter.containsKey(name)) {
            return 0;
        }
        return popCounter.get(name);
    }

    public void onRender2D() {
        try {
            if (NotificationManager.mc.player == null) {
                return;
            }
            for (final Notif notification : notifs) {
                updateResolution();
                final String msg = notification.msg;
                final int width = CandyPlusRewrite.m_font.getWidth(msg);
                RenderUtil.drawRect(scaledWidth - width - 26 + notification.offsetX, notification._y - 21.0f, (float)(width + 27), (float)(CandyPlusRewrite.m_font.getHeight() + 12), ColorUtil.toRGBA(new Color(35, 35, 35, 255)));
                RenderUtil.drawRect(scaledWidth - width - 25 + notification.offsetX, notification._y - 20.0f, (float)(width + 25), (float)(CandyPlusRewrite.m_font.getHeight() + 10), ColorUtil.toRGBA(new Color(45, 45, 45, 255)));
                RenderUtil.drawRect(scaledWidth - width - 26 + notification.offsetX, notification._y - 20.0f + CandyPlusRewrite.m_font.getHeight() + 10.0f, (width + 26) * ((notification.max - notification.ticks) / notification.max), 1.0f, ColorUtil.toRGBA(new Color(170, 170, 170, 255)));
                RenderUtil.drawString(msg, scaledWidth - width - 20 + notification.offsetX, notification._y - 10.0f - 3.0f, ColorUtil.toRGBA(255, 255, 255), false, 1.0f);
                if (notification.ticks <= 0.0f) {
                    final Notif notif = notification;
                    notif.offsetX += (500.0f - notification.offsetX) / 10.0f;
                }
                else {
                    final Notif notif2 = notification;
                    --notif2.ticks;
                    final Notif notif3 = notification;
                    notif3.offsetX += (0.0f - notification.offsetX) / 4.0f;
                    final Notif notif4 = notification;
                    notif4._y += (notification.y - notification._y) / 4.0f;
                }
            }
            notifs = notifs.stream().filter(n -> (n.offsetX < 450.0f || n.ticks != 0.0f) && n._y >= -100.0f).collect(Collectors.toList());
        }
        catch (final Exception ex) {}
    }

    public void updateResolution() {
        scaledWidth = NotificationManager.mc.displayWidth;
        scaledHeight = NotificationManager.mc.displayHeight;
        scaleFactor = 1;
        final boolean flag = NotificationManager.mc.isUnicode();
        int i = NotificationManager.mc.gameSettings.guiScale;
        if (i == 0) {
            i = 1000;
        }
        while (scaleFactor < i && scaledWidth / (scaleFactor + 1) >= 320 && scaledHeight / (scaleFactor + 1) >= 240) {
            ++scaleFactor;
        }
        if (flag && scaleFactor % 2 != 0 && scaleFactor != 1) {
            --scaleFactor;
        }
        final double scaledWidthD = scaledWidth / (double)scaleFactor;
        final double scaledHeightD = scaledHeight / (double)scaleFactor;
        scaledWidth = MathHelper.ceil(scaledWidthD);
        scaledHeight = MathHelper.ceil(scaledHeightD);
    }

    public class Notif
    {
        public String msg;
        public float offsetX;
        public float y;
        public float _y;
        public float ticks;
        public float max;

        public Notif(final String msg) {
            offsetX = 300.0f;
            y = 0.0f;
            _y = 0.0f;
            ticks = 0.0f;
            max = 0.0f;
            this.msg = msg;
            int fps = Minecraft.getDebugFPS();
            if (fps == 0) {
                fps = 60;
            }
            final int seconds = ((Notification) CandyPlusRewrite.m_module.getModuleWithClass(Notification.class)).time.getValue();
            ticks = (float)(seconds * fps);
            max = (float)(seconds * fps);
        }
    }
}