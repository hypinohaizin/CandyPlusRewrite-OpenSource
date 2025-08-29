package me.hypinohaizin.candyplusrewrite.event.events.player;

import net.minecraft.entity.player.EntityPlayer;
import me.hypinohaizin.candyplusrewrite.event.CandyEvent;

public class PlayerDeathEvent extends CandyEvent
{
    public EntityPlayer player;
    
    public PlayerDeathEvent(final EntityPlayer player) {
        this.player = player;
    }
}
