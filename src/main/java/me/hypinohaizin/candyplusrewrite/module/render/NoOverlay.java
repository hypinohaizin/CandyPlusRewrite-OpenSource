package me.hypinohaizin.candyplusrewrite.module.render;

import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketSpawnExperienceOrb;
import me.hypinohaizin.candyplusrewrite.event.events.network.PacketEvent;
import net.minecraft.init.MobEffects;
import me.hypinohaizin.candyplusrewrite.module.Module;

public class NoOverlay extends Module
{
    public NoOverlay() {
        super("NoOverlay", Categories.RENDER, true, true);
    }
    
    @Override
    public void onRender3D() {
        if (NoOverlay.mc.player == null) {
            return;
        }
        NoOverlay.mc.player.removeActivePotionEffect(MobEffects.BLINDNESS);
        NoOverlay.mc.player.removeActivePotionEffect(MobEffects.NAUSEA);
    }
    
    @Override
    public void onPacketReceive(final PacketEvent.Receive event) {
        final Packet<?> packet = event.packet;
        if (packet instanceof SPacketSpawnExperienceOrb || packet instanceof SPacketExplosion) {
            event.cancel();
        }
    }
}
