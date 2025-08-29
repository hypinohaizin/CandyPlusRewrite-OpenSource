package me.hypinohaizin.candyplusrewrite.module.movement;

import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketChatMessage;
import me.hypinohaizin.candyplusrewrite.event.events.network.PacketEvent;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import java.util.ArrayList;
import net.minecraft.network.Packet;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.module.Module;

public class Blink extends Module
{
    public Setting<Boolean> noEntity;
    public Setting<Boolean> limit;
    public Setting<Integer> maxPackets;
    public Setting<Boolean> all;
    public Setting<Integer> skip;
    public EntityPlayer entity;
    public BlockPos startPos;
    public List<Packet<?>> packets;
    
    public Blink() {
        super("Blink", Categories.MOVEMENT, false, false);
        noEntity = register(new Setting<>("NoEntity", false));
        limit = register(new Setting<>("Limit", false));
        maxPackets = register(new Setting<>("MaxPackets", 20, 70, 10, s -> limit.getValue()));
        all = register(new Setting<>("Cancel All", false));
        skip = register(new Setting<>("Skip", 0, 3, 0));
        entity = null;
        startPos = null;
        packets = null;
    }
    
    @Override
    public void onEnable() {
        if (nullCheck()) {
            disable();
            return;
        }
        packets = new ArrayList<Packet<?>>();
        if (!noEntity.getValue()) {
            (entity = new EntityOtherPlayerMP(Blink.mc.world, Blink.mc.getSession().getProfile())).copyLocationAndAnglesFrom(Blink.mc.player);
            entity.rotationYaw = Blink.mc.player.rotationYaw;
            entity.rotationYawHead = Blink.mc.player.rotationYawHead;
            entity.inventory.copyInventory(Blink.mc.player.inventory);
            Blink.mc.world.addEntityToWorld(6942069, entity);
            startPos = Blink.mc.player.getPosition();
        }
    }
    
    @Override
    public void onDisable() {
        if (nullCheck() || packets == null) {
            return;
        }
        int counter = 0;
        for (final Packet packet : packets) {
            if (skip.getValue() <= counter) {
                Blink.mc.player.connection.sendPacket(packet);
                counter = 0;
            }
            ++counter;
        }
        Blink.mc.world.removeEntityFromWorld(entity.getEntityId());
    }
    
    @Override
    public void onUpdate() {
        if (nullCheck() || packets == null) {
            return;
        }
        if (limit.getValue() && packets.size() > maxPackets.getValue()) {
            sendMessage("Packets size has reached the limit! disabling...");
            packets = new ArrayList<Packet<?>>();
            disable();
        }
    }
    
    @Override
    public void onPacketSend(final PacketEvent.Send event) {
        if (nullCheck() || packets == null) {
            return;
        }
        final Packet<?> packet = event.packet;
        if (!all.getValue()) {
            if (packet instanceof CPacketChatMessage || packet instanceof CPacketConfirmTeleport || packet instanceof CPacketKeepAlive || packet instanceof CPacketTabComplete || packet instanceof CPacketClientStatus) {
                return;
            }
            packets.add(packet);
            event.cancel();
        }
        else if (packet instanceof CPacketPlayer) {
            packets.add(packet);
            event.cancel();
        }
    }
}
