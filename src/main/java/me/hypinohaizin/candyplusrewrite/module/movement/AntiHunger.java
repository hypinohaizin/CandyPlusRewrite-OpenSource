package me.hypinohaizin.candyplusrewrite.module.movement;

import me.hypinohaizin.candyplusrewrite.module.Module;
import me.hypinohaizin.candyplusrewrite.event.events.network.PacketEvent;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.Packet;

/**
 * @author h_ypi
 * @since 2025/08/06 4:39
 */
public class AntiHunger extends Module {

    public AntiHunger() {
        super("AntiHunger", Categories.MOVEMENT, false, false);
    }

    @Override
    public void onEnable() {
        if (mc.player != null && mc.getConnection() != null) {
            mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (mc.player != null && mc.getConnection() != null && mc.player.isSprinting()) {
            mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
        }
        super.onDisable();
    }

    @Override
    public void onPacketSend(PacketEvent.Send event) {
        Packet<?> packet = event.getPacket();
        if (packet instanceof CPacketEntityAction) {
            CPacketEntityAction action = (CPacketEntityAction) packet;
            if (action.getAction() == CPacketEntityAction.Action.START_SPRINTING
                    || action.getAction() == CPacketEntityAction.Action.STOP_SPRINTING) {
                event.cancel();
            }
        }
    }
}
