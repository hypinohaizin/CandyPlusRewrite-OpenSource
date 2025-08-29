package me.hypinohaizin.candyplusrewrite.module.combat;

import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.network.play.server.SPacketEntityStatus;
import me.hypinohaizin.candyplusrewrite.event.events.network.PacketEvent;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.module.Module;

public class Velocity extends Module
{
    Setting<Integer> horizontal_vel;
    Setting<Integer> vertical_vel;
    Setting<Boolean> explosions;
    Setting<Boolean> bobbers;
    
    public Velocity() {
        super("Velocity", Categories.COMBAT, false, false);
        horizontal_vel = register(new Setting<>("Horizontal", 0, 100, 0));
        vertical_vel = register(new Setting<>("Vertical", 0, 100, 0));
        explosions = register(new Setting<>("Explosions", true));
        bobbers = register(new Setting<>("Bobbers", true));
    }
    
    @Override
    public void onPacketReceive(final PacketEvent.Receive event) {
        if (Velocity.mc.player == null) {
            return;
        }
        if (event.packet instanceof SPacketEntityStatus && bobbers.getValue()) {
            final SPacketEntityStatus packet = (SPacketEntityStatus)event.packet;
            if (packet.getOpCode() == 31) {
                final Entity entity = packet.getEntity(Velocity.mc.world);
                if (entity != null && entity instanceof EntityFishHook) {
                    final EntityFishHook fishHook = (EntityFishHook)entity;
                    if (fishHook.caughtEntity == Velocity.mc.player) {
                        event.cancel();
                    }
                }
            }
        }
        if (event.packet instanceof SPacketEntityVelocity) {
            final SPacketEntityVelocity packet2 = (SPacketEntityVelocity)event.packet;
            if (packet2.getEntityID() == Velocity.mc.player.getEntityId()) {
                if (horizontal_vel.getValue() == 0 && vertical_vel.getValue() == 0) {
                    event.cancel();
                    return;
                }
                if (horizontal_vel.getValue() != 100) {}
                if (vertical_vel.getValue() != 100) {}
            }
        }
        if (event.packet instanceof SPacketExplosion && explosions.getValue()) {
            final SPacketExplosion packet3 = (SPacketExplosion)event.packet;
            if (horizontal_vel.getValue() == 0 && vertical_vel.getValue() == 0) {
                event.cancel();
                return;
            }
            if (horizontal_vel.getValue() != 100) {}
            if (vertical_vel.getValue() != 100) {}
        }
    }
}
