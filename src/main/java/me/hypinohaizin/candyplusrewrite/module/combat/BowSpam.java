package me.hypinohaizin.candyplusrewrite.module.combat;

import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.item.ItemBow;
import me.hypinohaizin.candyplusrewrite.module.Module;

public class BowSpam extends Module
{
    public BowSpam() {
        super("BowSpam", Categories.COMBAT, false, false);
    }
    
    @Override
    public void onTick() {
        try {
            if (nullCheck()) {
                return;
            }
            if (BowSpam.mc.player.getHeldItemMainhand().getItem() instanceof ItemBow) {
                BowSpam.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, BowSpam.mc.player.getHorizontalFacing()));
                BowSpam.mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(BowSpam.mc.player.getActiveHand()));
                BowSpam.mc.player.stopActiveHand();
            }
        }
        catch (final Exception ex) {}
    }
}
