package me.hypinohaizin.candyplusrewrite.managers;

import me.hypinohaizin.candyplusrewrite.command.CommandManager;
import me.hypinohaizin.candyplusrewrite.event.events.player.PlayerDeathEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import me.hypinohaizin.candyplusrewrite.event.events.network.PacketEvent;
import me.hypinohaizin.candyplusrewrite.utils.RenderUtil3D;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import org.lwjgl.input.Keyboard;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.hypinohaizin.candyplusrewrite.CandyPlusRewrite;
import net.minecraftforge.event.entity.living.LivingEvent;

public class EventManager extends Manager
{
    @SubscribeEvent
    public void onUpdate(final LivingEvent.LivingUpdateEvent event) {
        if (!nullCheck()) {
            CandyPlusRewrite.m_notif.onUpdate();
            CandyPlusRewrite.m_rotate.updateRotations();
            CandyPlusRewrite.m_hole.update();
            CandyPlusRewrite.m_module.onUpdate();
        }
    }

    @SubscribeEvent
    public void onConnect(final FMLNetworkEvent.ClientConnectedToServerEvent event) {
        CandyPlusRewrite.m_module.onConnect();
    }

    @SubscribeEvent
    public void onDisconnect(final FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        CandyPlusRewrite.Info("Saving configs...");
        ConfigManager.saveConfigs();
        CandyPlusRewrite.Info("Successfully save configs!");
    }

    @SubscribeEvent
    public void onTick(final TickEvent.ClientTickEvent event) {
        CandyPlusRewrite.m_module.onTick();
    }

    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public void onKeyInput(final InputEvent.KeyInputEvent event) {
        if (Keyboard.getEventKeyState()) {
            CandyPlusRewrite.m_module.onKeyInput(Keyboard.getEventKey());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatSent(final ClientChatEvent event) {
        String msg = event.getMessage();
        String prefix = CommandManager.getCommandPrefix();
        if (msg.startsWith(prefix)) {
            event.setCanceled(true);
            Minecraft mc = Minecraft.getMinecraft();
            mc.ingameGUI.getChatGUI().addToSentMessages(msg);
            try {
                CommandManager.callCommand(msg.substring(prefix.length()), false);
            } catch (Exception e) {
                e.printStackTrace();
                if (mc.player != null) {
                    mc.player.sendMessage(new TextComponentString(
                            TextFormatting.DARK_RED + "Error: " + e.getMessage()
                    ));
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRenderGameOverlayEvent(final RenderGameOverlayEvent.Text event) {
        if (event.getType().equals(RenderGameOverlayEvent.ElementType.TEXT)) {
            final ScaledResolution resolution = new ScaledResolution(EventManager.mc);
            CandyPlusRewrite.m_module.onRender2D();
            CandyPlusRewrite.m_notif.onRender2D();
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }

    @SubscribeEvent
    public void onWorldRender(final RenderWorldLastEvent event) {
        if (event.isCanceled()) {
            return;
        }
        if (EventManager.mc.player == null || EventManager.mc.world == null) {
            return;
        }
        EventManager.mc.profiler.startSection("candyplusrewrite");
        EventManager.mc.profiler.startSection("setup");
        RenderUtil3D.prepare();
        EventManager.mc.profiler.endSection();
        CandyPlusRewrite.m_module.onRender3D();
        CandyPlusRewrite.m_module.onRender3D(event.getPartialTicks());
        EventManager.mc.profiler.startSection("release");
        RenderUtil3D.release();
        EventManager.mc.profiler.endSection();
        EventManager.mc.profiler.endSection();
    }

    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        CandyPlusRewrite.m_module.onPacketSend(event);
    }

    @SubscribeEvent
    public void onPacketReceive(final PacketEvent.Receive event) {
        CandyPlusRewrite.m_module.onPacketReceive(event);
        if (event.getPacket() instanceof SPacketEntityStatus) {
            final SPacketEntityStatus packet = (SPacketEntityStatus)event.getPacket();
            if (packet.getOpCode() == 35 && packet.getEntity(EventManager.mc.world) instanceof EntityPlayer) {
                final EntityPlayer player = (EntityPlayer)packet.getEntity(EventManager.mc.world);
                CandyPlusRewrite.m_module.onTotemPop(player);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerDeath(final PlayerDeathEvent event) {
        CandyPlusRewrite.m_module.onPlayerDeath(event);
    }
}
