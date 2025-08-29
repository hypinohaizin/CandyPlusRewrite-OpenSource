package me.hypinohaizin.candyplusrewrite.event.events.network;

import net.minecraft.network.Packet;
import me.hypinohaizin.candyplusrewrite.event.CandyEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class PacketEvent extends CandyEvent
{
    public final Packet<?> packet;
    
    public PacketEvent(final Packet<?> packet) {
        this.packet = packet;
    }
    
    public Packet<?> getPacket() {
        return packet;
    }
    
    public static class Receive extends PacketEvent
    {
        public Receive(final Packet<?> packet) {
            super(packet);
        }
    }
    
    public static class Send extends PacketEvent
    {
        public Send(final Packet<?> packet) {
            super(packet);
        }
    }
    public class PlayerTravelEvent extends CandyEvent {

    }
}
