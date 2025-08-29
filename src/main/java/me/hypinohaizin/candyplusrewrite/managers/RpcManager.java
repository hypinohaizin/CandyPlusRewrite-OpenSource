package me.hypinohaizin.candyplusrewrite.managers;

import club.minnced.discord.rpc.DiscordRichPresence;
import club.minnced.discord.rpc.DiscordEventHandlers;
import me.hypinohaizin.candyplusrewrite.module.misc.DiscordRPC;

public class RpcManager extends Manager
{
    private Thread _thread;

    public RpcManager() {
        _thread = null;
    }

    public void enable(final DiscordRPC module) {
        final club.minnced.discord.rpc.DiscordRPC lib = club.minnced.discord.rpc.DiscordRPC.INSTANCE;
        final String applicationId = "1401898002305650799";
        final String steamId = "";
        final DiscordEventHandlers handlers = new DiscordEventHandlers();

        lib.Discord_Initialize(applicationId, handlers, true, steamId);

        final DiscordRichPresence presence = new DiscordRichPresence();
        presence.startTimestamp = System.currentTimeMillis() / 1000L;
        presence.largeImageText = "";

        lib.Discord_UpdatePresence(presence);

        (_thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                lib.Discord_RunCallbacks();

                presence.largeImageKey = "icon";
                presence.details = "Playing Candy+ Rewrite";
                presence.state = getState();

                lib.Discord_UpdatePresence(presence);

                try {
                    Thread.sleep(3000L);
                } catch (final InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "RPC-Callback-Handler")).start();
    }

    public void disable() {
        if (_thread != null) {
            _thread.interrupt();
            _thread = null;
        }
        club.minnced.discord.rpc.DiscordRPC.INSTANCE.Discord_Shutdown();
    }

    public String getState() {
        if (RpcManager.mc.player == null) {
            return "Main Menu";
        }

        String state = "HP : " + Math.round(RpcManager.mc.player.getHealth() + RpcManager.mc.player.getAbsorptionAmount())
                + " / " + Math.round(RpcManager.mc.player.getMaxHealth() + RpcManager.mc.player.getAbsorptionAmount());
        return state;
    }
}
